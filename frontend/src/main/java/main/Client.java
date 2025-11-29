package main;

import jakarta.websocket.*;
import java.net.URI;
import javax.swing.SwingUtilities;

@ClientEndpoint
public class Client {

    private Session session;
    private frame ui; // 直接持有主視窗參考
    private String server_URI; // WebSocket URL，從配置文件讀取
    
    public Client(frame ui) {
        this.ui = ui;
        // 從配置文件讀取 WebSocket URL
        this.server_URI = ConfigManager.getWebSocketUrl();
        System.out.println("WebSocket 服務器地址: " + server_URI);
    }

    /**
     * ✅ 主動建立 WebSocket 連線
     */
    public boolean connect() {
        try {
            if (session != null && session.isOpen()) {
                System.out.println("⚠️ 已有 WebSocket 連線存在，不需重複連線。");
                return true;
            }

            System.out.println("🚀 嘗試建立 WebSocket 連線...");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxSessionIdleTimeout(0);
            session = container.connectToServer(this, URI.create(server_URI));
            System.out.println("✅ 已成功連線到伺服器");
            return true;
        } catch (Exception e) {
            System.out.println("❌ WebSocket 連線失敗：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ 主動關閉 WebSocket 連線
     */
    public void disconnect() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
                System.out.println("🔴 WebSocket 已關閉");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== WebSocket callback =====================

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("🟢 WebSocket 已開啟");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("📩 收到伺服器訊息：" + message);

        SwingUtilities.invokeLater(() -> {
            if (message.contains("update_comments")) {
                System.out.println("🔁 收到留言更新通知 → 重新整理留言區");
                ui.reset_ArticleReadPanel(ui.getIdxNowArticle());
                ui.reset_BrowserPanel();
            }
            else if (message.contains("update_likes")) {
                System.out.println("❤️ 收到按讚更新通知 → 重新整理讚數");
                ui.reset_ArticleReadPanel(ui.getIdxNowArticle());
                ui.reset_BrowserPanel();
            }
            else if (message.contains("update_browser")) {
                System.out.println("📰 發表新文章 → 重新整理 browser");
                ui.reset_BrowserPanel();
            }
            else if (message.contains("delete_article")) {
                System.out.println("🗑️ 刪除文章 → 重新整理 browser");
                ui.reset_BrowserPanel();
            }
        });
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("🔴 WebSocket 連線關閉：" + reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("⚠️ 發生錯誤：" + throwable.getMessage());
        throwable.printStackTrace();
    }

    // ===================== 發送訊息 =====================

    public void sendMessage(String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getAsyncRemote().sendText(message);
                System.out.println("📤 已發送訊息：" + message);
            } else {
                System.out.println("⚠️ 無法發送訊息，連線尚未建立。");
            }
        } catch (Exception e) {
            System.out.println("❌ 發送失敗：" + e.getMessage());
        }
    }
}
