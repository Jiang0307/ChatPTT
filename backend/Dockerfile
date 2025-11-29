# 使用 OpenJDK 21 作為基礎映像
FROM openjdk:21-jdk-slim

# 設置工作目錄
WORKDIR /app

# 複製 Maven 構建的 JAR 檔案
# 注意：需要先執行 mvn package 構建 JAR
COPY target/chatptt-backend-1.0.0.jar app.jar

# 暴露端口（Railway 會自動設置 PORT 環境變數）
EXPOSE 8080

# 啟動應用
# Railway 會自動設置 PORT 環境變數，Spring Boot 會自動讀取
ENTRYPOINT ["java", "-jar", "app.jar"]

