# https://github.com/ringcentral-docker/jdk/blob/master/jdk8/Dockerfile
FROM ringcentral/jdk

#VOLUME /logs
# 将构建的包扔到容器里，版本在pom.xml中定义，8088端口用于远程调试
ADD pb-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8088,suspend=n", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]

EXPOSE 8084
EXPOSE 8088
