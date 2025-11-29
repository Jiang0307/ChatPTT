package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final Gson gson;
    private String currentUsername; // 保存當前登入的用戶名

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    // 登入
    public LoginResponse login(String username, String password) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);

            String jsonBody = gson.toJson(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // 檢查 HTTP 狀態碼
            int statusCode = response.statusCode();
            
            // 檢查響應體是否為空
            String responseBody = response.body();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return new LoginResponse(false, null, "伺服器無回應 (HTTP " + statusCode + ")");
            }
            
            // 處理 Railway 的錯誤響應格式（502 等錯誤）
            if (statusCode >= 500) {
                try {
                    Map<String, Object> errorResult = gson.fromJson(responseBody, new TypeToken<Map<String, Object>>(){}.getType());
                    String errorMsg = errorResult.get("message") != null ? errorResult.get("message").toString() : "伺服器錯誤";
                    return new LoginResponse(false, null, errorMsg + " (HTTP " + statusCode + ")");
                } catch (Exception e) {
                    return new LoginResponse(false, null, "伺服器錯誤 (HTTP " + statusCode + "): " + responseBody);
                }
            }
            
            if (statusCode < 200 || statusCode >= 300) {
                return new LoginResponse(false, null, "請求失敗 (HTTP " + statusCode + ")");
            }
            
            Map<String, Object> result = gson.fromJson(responseBody, new TypeToken<Map<String, Object>>(){}.getType());
            
            // 安全地獲取 success 字段
            Object successObj = result.get("success");
            if (successObj == null) {
                System.err.println("後端響應格式錯誤，缺少 'success' 字段。響應內容: " + responseBody);
                return new LoginResponse(false, null, "後端響應格式錯誤");
            }
            
            boolean success = successObj instanceof Boolean ? (Boolean) successObj : Boolean.parseBoolean(successObj.toString());
            
            if (success) {
                Map<String, Object> userMap = (Map<String, Object>) result.get("user");
                if (userMap == null) {
                    return new LoginResponse(false, null, "後端響應缺少用戶信息");
                }
                Users user = gson.fromJson(gson.toJson(userMap), Users.class);
                this.currentUsername = user.username;
                String message = result.get("message") != null ? result.get("message").toString() : "登入成功";
                return new LoginResponse(true, user, message);
            } else {
                String message = result.get("message") != null ? result.get("message").toString() : "登入失敗";
                return new LoginResponse(false, null, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse(false, null, "連線失敗：" + e.getMessage());
        }
    }

    // 註冊
    public boolean signUp(String username, String password, String nickname) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("nickname", nickname);

            String jsonBody = gson.toJson(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/auth/signup"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return (Boolean) result.get("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 獲取文章列表
    public ArrayList<Articles> getArticles(String articleClass) {
        try {
            String url = baseUrl + "/api/articles?class=" + java.net.URLEncoder.encode(articleClass, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Articles[] articlesArray = gson.fromJson(response.body(), Articles[].class);
            ArrayList<Articles> articles = new ArrayList<>();
            for (Articles article : articlesArray) {
                articles.add(article);
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 上傳文章
    public boolean uploadArticle(String title, String content, String uploadClass) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", currentUsername);
            requestBody.put("title", title);
            requestBody.put("content", content);
            requestBody.put("uploadClass", uploadClass);

            String jsonBody = gson.toJson(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/articles"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return (Boolean) result.get("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 刪除文章
    public boolean deleteArticle(int articleId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/articles/" + articleId))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return (Boolean) result.get("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 獲取留言
    public ArrayList<Comments> getComments(int articleId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/articles/" + articleId + "/comments"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Comments[] commentsArray = gson.fromJson(response.body(), Comments[].class);
            ArrayList<Comments> comments = new ArrayList<>();
            for (Comments comment : commentsArray) {
                comments.add(comment);
            }
            return comments;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 上傳留言
    public boolean uploadComment(int articleId, String content) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", currentUsername);
            requestBody.put("content", content);

            String jsonBody = gson.toJson(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/articles/" + articleId + "/comments"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return (Boolean) result.get("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 獲取留言數
    public int getCommentNumber(int articleId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/articles/" + articleId + "/comments/count"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return ((Double) result.get("count")).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 按讚/取消讚
    public int uploadLike(int articleId) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", currentUsername);

            String jsonBody = gson.toJson(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/articles/" + articleId + "/likes"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return ((Double) result.get("result")).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    // 獲取讚數
    public int getLikeNumber(int articleId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/articles/" + articleId + "/likes/count"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return ((Double) result.get("count")).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 檢查是否已按讚
    public boolean checkLiked(int articleId) {
        try {
            String url = baseUrl + "/api/articles/" + articleId + "/likes/check?username=" + 
                        java.net.URLEncoder.encode(currentUsername, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return (Boolean) result.get("liked");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 更新用戶信息
    public boolean updateUser(String newPasswords, String newNickname) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", currentUsername);
            requestBody.put("newPasswords", newPasswords);
            requestBody.put("newNickname", newNickname);

            String jsonBody = gson.toJson(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/users/profile"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return (Boolean) result.get("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 刪除用戶
    public boolean deleteUser(String passwords) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", currentUsername);
            requestBody.put("passwords", passwords);

            String jsonBody = gson.toJson(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/users"))
                    .header("Content-Type", "application/json")
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return (Boolean) result.get("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 檢查文章是否存在
    public boolean checkArticleAlive(int articleId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/articles/" + articleId + "/alive"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            return (Boolean) result.get("alive");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 登入響應類
    public static class LoginResponse {
        private boolean success;
        private Users user;
        private String message;

        public LoginResponse(boolean success, Users user, String message) {
            this.success = success;
            this.user = user;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public Users getUser() {
            return user;
        }

        public String getMessage() {
            return message;
        }
    }
}

