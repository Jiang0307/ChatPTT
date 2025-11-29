-- ChatPTT 資料庫初始化腳本
-- Spring Boot 會在啟動時自動執行此文件（如果配置了 spring.sql.init.mode）

-- 創建 Users 表
CREATE TABLE IF NOT EXISTS Users (
    Username   VARCHAR(50) PRIMARY KEY,
    Passwords  VARCHAR(255) NOT NULL,
    Nickname   VARCHAR(50)
);

-- 創建 Articles 表
CREATE TABLE IF NOT EXISTS Articles (
    Article_ID     INT AUTO_INCREMENT PRIMARY KEY,
    Username       VARCHAR(50) NOT NULL,
    Title          VARCHAR(100) NOT NULL,
    Class          VARCHAR(50),
    Content        TEXT,
    Times          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Username) REFERENCES Users(Username)
      ON DELETE CASCADE
);

-- 創建 Comments 表
CREATE TABLE IF NOT EXISTS Comments (
    Comment_ID  INT AUTO_INCREMENT PRIMARY KEY,
    Username    VARCHAR(50) NOT NULL,
    Article_ID  INT NOT NULL,
    Content     TEXT NOT NULL,
    Times       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Username) REFERENCES Users(Username)
      ON DELETE CASCADE,
    FOREIGN KEY (Article_ID) REFERENCES Articles(Article_ID)
      ON DELETE CASCADE
);

-- 創建 Likes 表
CREATE TABLE IF NOT EXISTS Likes (
    Username   VARCHAR(50) NOT NULL,
    Article_ID INT NOT NULL,
    PRIMARY KEY (Username, Article_ID),
    FOREIGN KEY (Username) REFERENCES Users(Username)
      ON DELETE CASCADE,
    FOREIGN KEY (Article_ID) REFERENCES Articles(Article_ID)
      ON DELETE CASCADE
);

