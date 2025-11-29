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
            
            Map<String, Object> result = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            boolean success = (Boolean) result.get("success");
            
            if (success) {
                Map<String, Object> userMap = (Map<String, Object>) result.get("user");
                Users user = gson.fromJson(gson.toJson(userMap), Users.class);
                this.currentUsername = user.username;
                return new LoginResponse(true, user, (String) result.get("message"));
            } else {
                return new LoginResponse(false, null, (String) result.get("message"));
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

