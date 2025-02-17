FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-jdk

# 设置工作目录
WORKDIR /app2

ENV TZ=Asina/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 将 JAR 包复制到容器中
COPY sendmail-0.0.1-SNAPSHOT.jar app2.jar

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app2.jar"]