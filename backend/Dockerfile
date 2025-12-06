# 使用 Maven 映像來構建和運行
FROM maven:3.9-eclipse-temurin-21

# 設置工作目錄
WORKDIR /app

# 複製 pom.xml 和源代碼
# 支援從根目錄或 backend 目錄執行
COPY pom.xml ./pom.xml
COPY src ./src

# 暴露端口（Railway 會自動設置 PORT 環境變數）
EXPOSE 8080

# 啟動應用
# Railway 會自動設置 PORT 環境變數，Spring Boot 會自動讀取
ENTRYPOINT ["mvn", "spring-boot:run"]