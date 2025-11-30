package com.chatptt.controller;

import com.chatptt.service.DBConnectionService;
import com.chatptt.websocket.ChatWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/articles/{articleId}/likes")
public class LikeController {

    @Autowired
    private DBConnectionService dbService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadLike(
            @PathVariable int articleId,
            @RequestBody Map<String, String> request) {
        String username = request.get("username");

        Map<String, Object> response = new HashMap<>();

        if (username == null || username.isEmpty()) {
            response.put("success", false);
            response.put("message", "請提供用戶名");
            return ResponseEntity.badRequest().body(response);
        }

        int result = dbService.uploadLike(username, articleId);
        // 廣播通知所有客戶端
        ChatWebSocketServer.broadcast("update_likes");
        
        response.put("success", true);
        response.put("result", result); // 0: 取消讚, 1: 按讚, 2: 錯誤
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getLikeNumber(@PathVariable int articleId) {
        Map<String, Object> response = new HashMap<>();
        int count = dbService.getLikeNumber(articleId);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkLiked(
            @PathVariable int articleId,
            @RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        boolean liked = dbService.checkLiked(username, articleId);
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }
}



