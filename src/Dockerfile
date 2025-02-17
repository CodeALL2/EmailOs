# 使用官方 OpenJDK 作为基础镜像
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-jdk
# 设置工作目录
WORKDIR /app

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 将 JAR 包复制到容器中
COPY emaildispatcher-0.0.1-SNAPSHOT.jar app.jar

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]
