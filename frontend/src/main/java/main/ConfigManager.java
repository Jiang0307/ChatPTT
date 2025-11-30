package main;

import java.io.*;
import java.util.Properties;

/**
 * 配置管理器，用於讀取和保存配置文件
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    // 預設值僅用於開發環境或配置文件讀取失敗時的後備
    private static final String DEFAULT_SERVER_URL = "http://localhost:8080";
    private static final String DEFAULT_WEBSOCKET_URL = "ws://localhost:8080/chat";
    
    private static Properties properties;
    
    /**
     * 載入配置文件
     */
    private static void loadConfig() {
        if (properties != null) {
            return; // 已經載入過
        }
        
        properties = new Properties();
        File configFile = new File(CONFIG_FILE);
        
        // 如果配置文件不存在，創建預設配置文件
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }
        
        // 讀取配置文件
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("無法讀取配置文件，使用預設值: " + e.getMessage());
            properties = new Properties();
            properties.setProperty("server.url", DEFAULT_SERVER_URL);
            properties.setProperty("websocket.url", DEFAULT_WEBSOCKET_URL);
        }
    }
    
    /**
     * 創建預設配置文件
     */
    private static void createDefaultConfig(File configFile) {
        try (FileOutputStream fos = new FileOutputStream(configFile);
             PrintWriter writer = new PrintWriter(fos)) {
            
            writer.println("# ChatPTT 前端配置文件");
            writer.println("# 請設置您的後端服務器地址");
            writer.println();
            writer.println("# 後端 API 地址");
            writer.println("# 開發環境: http://localhost:8080");
            writer.println("# 生產環境: https://chatptt.up.railway.app");
            writer.println("server.url=" + DEFAULT_SERVER_URL);
            writer.println();
            writer.println("# WebSocket 地址");
            writer.println("# 開發環境: ws://localhost:8080/chat");
            writer.println("# 生產環境: wss://chatptt.up.railway.app/chat");
            writer.println("websocket.url=" + DEFAULT_WEBSOCKET_URL);
            
            System.out.println("已創建預設配置文件: " + configFile.getAbsolutePath());
            System.out.println("請編輯配置文件設置後端服務器地址");
        } catch (IOException e) {
            System.err.println("無法創建配置文件: " + e.getMessage());
        }
    }
    
    /**
     * 獲取後端服務器 URL
     */
    public static String getServerUrl() {
        loadConfig();
        return properties.getProperty("server.url", DEFAULT_SERVER_URL);
    }
    
    /**
     * 獲取 WebSocket URL
     */
    public static String getWebSocketUrl() {
        loadConfig();
        return properties.getProperty("websocket.url", DEFAULT_WEBSOCKET_URL);
    }
    
    /**
     * 設置後端服務器 URL
     */
    public static void setServerUrl(String url) {
        loadConfig();
        properties.setProperty("server.url", url);
        saveConfig();
    }
    
    /**
     * 設置 WebSocket URL
     */
    public static void setWebSocketUrl(String url) {
        loadConfig();
        properties.setProperty("websocket.url", url);
        saveConfig();
    }
    
    /**
     * 保存配置文件
     */
    private static void saveConfig() {
        File configFile = new File(CONFIG_FILE);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "ChatPTT Configuration");
        } catch (IOException e) {
            System.err.println("無法保存配置文件: " + e.getMessage());
        }
    }
}