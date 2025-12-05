package chatptt.controller;

import chatptt.model.Users;
import chatptt.service.DBConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private DBConnectionService dbService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Map<String, Object> response = new HashMap<>();
        
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.put("success", false);
            response.put("message", "請正確輸入帳號與密碼");
            return ResponseEntity.badRequest().body(response);
        }

        Users user = dbService.verifyLogin(username, password);
        if (user != null) {
            response.put("success", true);
            response.put("user", user);
            response.put("message", "登入成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "登入失敗：使用者帳號或密碼錯誤。");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String nickname = request.get("nickname");

        Map<String, Object> response = new HashMap<>();
        
        if (username == null || password == null || nickname == null || 
            username.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
            response.put("success", false);
            response.put("message", "請填寫所有欄位");
            return ResponseEntity.badRequest().body(response);
        }

        boolean result = dbService.signUp(username, password, nickname);
        if (result) {
            response.put("success", true);
            response.put("message", "註冊成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "註冊失敗：用戶名已存在");
            return ResponseEntity.status(409).body(response);
        }
    }
}
