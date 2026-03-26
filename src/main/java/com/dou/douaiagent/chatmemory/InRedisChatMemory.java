package com.dou.douaiagent.chatmemory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InRedisChatMemory implements ChatMemory {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<AssistantMessage.ToolCall>> TOOL_CALLS_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<ToolResponseMessage.ToolResponse>> TOOL_RESPONSES_TYPE =
            new TypeReference<>() {};
    private static final TypeReference<List<Media>> MEDIA_LIST_TYPE = new TypeReference<>() {};

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "chat:memory:";

    @Override
    public void add(String conversationId, Message message) {
        try {
            String value = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().rightPush(KEY_PREFIX + conversationId, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        try {
            for (Message message : messages) {
                String value = objectMapper.writeValueAsString(message);
                redisTemplate.opsForList().rightPush(KEY_PREFIX + conversationId, value);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<String> values = redisTemplate.opsForList().range(KEY_PREFIX + conversationId, 0, -1);
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        List<Message> messages = values.stream()
                .map(this::deserializeMessage)
                .filter(m -> m != null)
                .toList();
        int size = messages.size();
        int skip = Math.max(0, size - lastN);
        return messages.stream().skip(skip).toList();
    }

    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(KEY_PREFIX + conversationId);
    }

    private Message deserializeMessage(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            MessageType type = parseMessageType(root);
            Map<String, Object> metadata = readMetadataMap(root.get("metadata"));
            String text = readText(root);

            return switch (type) {
                case USER -> {
                    List<Media> media = readMediaList(root.get("media"));
                    yield new UserMessage(text, media, metadata);
                }
                case ASSISTANT -> {
                    List<AssistantMessage.ToolCall> toolCalls = readToolCalls(root.get("toolCalls"));
                    List<Media> media = readMediaList(root.get("media"));
                    yield new AssistantMessage(text, metadata, toolCalls, media);
                }
                case SYSTEM -> new SystemMessage(text);
                case TOOL -> {
                    List<ToolResponseMessage.ToolResponse> responses = readToolResponses(root.get("responses"));
                    yield new ToolResponseMessage(responses, metadata);
                }
            };
        } catch (Exception ignored) {
            return null;
        }
    }

    private static MessageType parseMessageType(JsonNode root) {
        JsonNode n = root.get("messageType");
        if (n == null || n.isNull()) {
            throw new IllegalArgumentException("missing messageType");
        }
        String s = n.asText();
        for (MessageType mt : MessageType.values()) {
            if (mt.name().equals(s) || mt.getValue().equalsIgnoreCase(s)) {
                return mt;
            }
        }
        return MessageType.valueOf(s);
    }

    private static String readText(JsonNode root) {
        JsonNode t = root.get("text");
        if (t != null && !t.isNull()) {
            return t.asText("");
        }
        JsonNode tc = root.get("textContent");
        if (tc != null && !tc.isNull()) {
            return tc.asText("");
        }
        return "";
    }

    private Map<String, Object> readMetadataMap(JsonNode metadataNode) {
        if (metadataNode == null || metadataNode.isNull() || metadataNode.isMissingNode()) {
            return Map.of();
        }
        Map<String, Object> m = objectMapper.convertValue(metadataNode, MAP_TYPE);
        return m != null ? m : Map.of();
    }

    private List<Media> readMediaList(JsonNode node) {
        if (node == null || !node.isArray() || node.isEmpty()) {
            return List.of();
        }
        List<Media> list = objectMapper.convertValue(node, MEDIA_LIST_TYPE);
        return list != null ? list : List.of();
    }

    private List<AssistantMessage.ToolCall> readToolCalls(JsonNode node) {
        if (node == null || !node.isArray() || node.isEmpty()) {
            return Collections.emptyList();
        }
        List<AssistantMessage.ToolCall> list = objectMapper.convertValue(node, TOOL_CALLS_TYPE);
        return list != null ? list : Collections.emptyList();
    }

    private List<ToolResponseMessage.ToolResponse> readToolResponses(JsonNode node) {
        if (node == null || !node.isArray() || node.isEmpty()) {
            return Collections.emptyList();
        }
        List<ToolResponseMessage.ToolResponse> list = objectMapper.convertValue(node, TOOL_RESPONSES_TYPE);
        return list != null ? list : Collections.emptyList();
    }
}
