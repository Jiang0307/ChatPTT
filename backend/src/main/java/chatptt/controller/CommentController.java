package chatptt.controller;

import chatptt.model.Comments;
import chatptt.service.DBConnectionService;
import chatptt.websocket.ChatWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
public class CommentController {

    @Autowired
    private DBConnectionService dbService;

    @GetMapping
    public ResponseEntity<List<Comments>> getComments(@PathVariable int articleId) {
        List<Comments> comments = dbService.getComments(articleId);
        if (comments != null) {
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadComment(
            @PathVariable int articleId,
            @RequestBody Map<String, String> request) {
        String username = request.get("username");
        String content = request.get("content");

        Map<String, Object> response = new HashMap<>();

        if (username == null || content == null || username.isEmpty() || content.isEmpty()) {
            response.put("success", false);
            response.put("message", "請填寫留言內容");
            return ResponseEntity.badRequest().body(response);
        }

        // 檢查文章是否存在
        if (!dbService.checkArticleAlive(articleId)) {
            response.put("success", false);
            response.put("message", "留言發送失敗，文章已被刪除。");
            return ResponseEntity.status(404).body(response);
        }

        boolean result = dbService.uploadComment(username, articleId, content);
        if (result) {
            // 廣播通知所有客戶端
            ChatWebSocketServer.broadcast("update_comments");
            response.put("success", true);
            response.put("message", "留言發送成功！");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "留言發送失敗，資料庫執行異常。");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCommentNumber(@PathVariable int articleId) {
        Map<String, Object> response = new HashMap<>();
        int count = dbService.getCommentNumber(articleId);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}
