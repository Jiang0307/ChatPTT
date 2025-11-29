# 多階段構建：第一階段 - 編譯
FROM maven:3.9-eclipse-temurin-21 AS build

# 設置工作目錄
WORKDIR /app

# 複製 pom.xml 和源代碼
# 注意：如果 Railway 設置根目錄為 backend，使用相對路徑
# 如果 Railway 在根目錄執行，需要修改為 COPY backend/pom.xml
COPY pom.xml .
COPY src ./src

# 構建應用（跳過測試以加快構建速度）
RUN mvn clean package -DskipTests

# 第二階段 - 運行
FROM openjdk:21-jdk-slim

# 設置工作目錄
WORKDIR /app

# 從構建階段複製 JAR 檔案
COPY --from=build /app/target/chatptt-backend-1.0.0.jar app.jar

# 暴露端口（Railway 會自動設置 PORT 環境變數）
EXPOSE 8080

# 啟動應用
# Railway 會自動設置 PORT 環境變數，Spring Boot 會自動讀取
ENTRYPOINT ["java", "-jar", "app.jar"]

