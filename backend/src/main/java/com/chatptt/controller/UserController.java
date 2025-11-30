package com.chatptt.controller;

import com.chatptt.service.DBConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private DBConnectionService dbService;

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String newPasswords = request.get("newPasswords");
        String newNickname = request.get("newNickname");

        Map<String, Object> response = new HashMap<>();

        if (username == null || username.isEmpty()) {
            response.put("success", false);
            response.put("message", "請提供用戶名");
            return ResponseEntity.badRequest().body(response);
        }

        if ((newPasswords == null || newPasswords.isEmpty()) && 
            (newNickname == null || newNickname.isEmpty())) {
            response.put("success", false);
            response.put("message", "請至少更新一項！");
            return ResponseEntity.badRequest().body(response);
        }

        if (newNickname != null && newNickname.length() > 7) {
            response.put("success", false);
            response.put("message", "暱稱不可以超過7個字！");
            return ResponseEntity.badRequest().body(response);
        }

        boolean result = dbService.updateUser(username, 
            newPasswords != null ? newPasswords : "", 
            newNickname != null ? newNickname : "");
        
        if (result) {
            response.put("success", true);
            response.put("message", "更新成功。");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "更新失敗，可能是伺服器連線異常。");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String passwords = request.get("passwords");

        Map<String, Object> response = new HashMap<>();

        if (username == null || passwords == null || username.isEmpty() || passwords.isEmpty()) {
            response.put("success", false);
            response.put("message", "請提供用戶名和密碼");
            return ResponseEntity.badRequest().body(response);
        }

        boolean result = dbService.deleteUser(username, passwords);
        if (result) {
            response.put("success", true);
            response.put("message", "用戶刪除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用戶刪除失敗");
            return ResponseEntity.status(500).body(response);
        }
    }
}



