# 使用预装的Maven 和 JDK21 的镜像
FROM maven:3.9-amazoncorretto-21
WORKDIR /app

#只复制必要的源代码和配置文件
COPY pom.xml .
COPY src ./src

#使用Maven执行打包
RUN mvn clean package -DskipTests

#暴露应用接口
EXPOSE 8123

#使用生产环境配置启动应用
CMD ["java","-jar","/app/target/dou-ai-agent-0.0.1-SNAPSHOT.jar","--spring.profiles.active=pord"]


