
package com.dou.douaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

/**
 * A simple logger advisor that logs the request and response messages.
 *
 * @author Christian Tzolov
 */
@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {


	@Override
	public String getName() {
		return "逗哥的日志advisor";
	}

	@Override
	public int getOrder() {
		return 1;
	}

	private ChatClientRequest before(ChatClientRequest chatClientRequest) {
	    log.info("AI Request: {}", chatClientRequest.prompt());
		return chatClientRequest;
	}

	private void observeAfter(ChatClientResponse chatClientResponse) {

		log.info("AI Response: {}, 消耗的Token: {}", chatClientResponse.chatResponse().getResult().getOutput().getText(), chatClientResponse.chatResponse().getMetadata().getUsage());

	}


	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
		chatClientRequest = before(chatClientRequest);

		ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

		observeAfter(chatClientResponse);

		return chatClientResponse;
	}

	@Override
	public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
		chatClientRequest = before(chatClientRequest);

		Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);
		return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponseFlux, this::observeAfter);
	}
}
