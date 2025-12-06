# 使用 Maven 映像來構建和運行
FROM maven:3.9-eclipse-temurin-21

# 設置工作目錄
WORKDIR /app

# 複製 pom.xml 和源代碼
# Root directory 已設置為 backend，所以直接複製當前目錄的文件
COPY pom.xml ./pom.xml
COPY src ./src

# 暴露端口（Railway 會自動設置 PORT 環境變數）
EXPOSE 8080

# 啟動應用
# Railway 會自動設置 PORT 環境變數，Spring Boot 會自動讀取
ENTRYPOINT ["mvn", "spring-boot:run"]