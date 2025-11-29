package com.chatptt.controller;

import com.chatptt.model.Articles;
import com.chatptt.service.DBConnectionService;
import com.chatptt.websocket.ChatWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private DBConnectionService dbService;

    @GetMapping
    public ResponseEntity<List<Articles>> getArticles(@RequestParam(required = false, defaultValue = "全部") String classParam) {
        List<Articles> articles = dbService.getArticles(classParam);
        if (articles != null) {
            return ResponseEntity.ok(articles);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadArticle(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String title = request.get("title");
        String content = request.get("content");
        String uploadClass = request.get("uploadClass");

        Map<String, Object> response = new HashMap<>();

        if (username == null || title == null || content == null || uploadClass == null ||
            username.isEmpty() || title.isEmpty() || content.isEmpty() || uploadClass.isEmpty()) {
            response.put("success", false);
            response.put("message", "請填寫所有欄位");
            return ResponseEntity.badRequest().body(response);
        }

        boolean result = dbService.uploadArticle(username, title, content, uploadClass);
        if (result) {
            // 廣播通知所有客戶端
            ChatWebSocketServer.broadcast("update_browser");
            response.put("success", true);
            response.put("message", "文章上傳成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "文章上傳失敗");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteArticle(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean result = dbService.deleteArticle(id);
        if (result) {
            // 廣播通知所有客戶端
            ChatWebSocketServer.broadcast("delete_article");
            response.put("success", true);
            response.put("message", "文章刪除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "文章刪除失敗");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}/alive")
    public ResponseEntity<Map<String, Object>> checkArticleAlive(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean alive = dbService.checkArticleAlive(id);
        response.put("alive", alive);
        return ResponseEntity.ok(response);
    }
}


