# 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 设置编码
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# 复制pom.xml和源码
COPY pom.xml .
COPY src ./src

# 直接构建（跳过测试）
RUN mvn package -DskipTests -B -Dfile.encoding=UTF-8

# 运行阶段
FROM eclipse-temurin:17-jre

WORKDIR /app

# 设置编码
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

# 从构建阶段复制jar包
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
