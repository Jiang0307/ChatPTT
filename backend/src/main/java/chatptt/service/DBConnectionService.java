package chatptt.service;

import chatptt.model.Articles;
import chatptt.model.Comments;
import chatptt.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class DBConnectionService {

    @Autowired
    private DataSource dataSource;

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public int getLikeNumber(int articleId) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("SELECT COUNT(*) FROM Likes WHERE Article_ID='%d';", articleId);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                return rs.getInt("COUNT(*)");
            else
                return 0;
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    public boolean deleteArticle(int articleId) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("DELETE FROM Articles WHERE (Article_ID = %d);", articleId);
            System.out.println(sql);
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public boolean checkLiked(String username, int articleId) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("SELECT * FROM Likes WHERE Username = '%s' AND Article_ID = '%d';", username, articleId);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public int uploadLike(String username, int articleId) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            boolean liked = checkLiked(username, articleId);
            if (liked) {
                // 取消讚
                String sql = String.format("DELETE FROM Likes WHERE Username = '%s' AND Article_ID = '%d';", username, articleId);
                System.out.println(sql);
                stmt.executeUpdate(sql);
                return 0;
            } else {
                // 按讚
                String sql = String.format("INSERT INTO Likes VALUES ('%s', '%d');", username, articleId);
                System.out.println(sql);
                stmt.executeUpdate(sql);
                return 1;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return 2;
        }
    }

    public boolean uploadComment(String username, int articleId, String content) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // 找出目前最後的comments_ID
            String sql = String.format("SELECT MAX(Comment_ID) FROM Comments;");
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            int id = 0;
            if (rs.next()) {
                id = rs.getInt("MAX(comment_ID)") + 1;
            }
            // 開始上傳留言
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            sql = String.format("INSERT INTO Comments VALUES ('%d' , '%s', '%d', '%s', '%s');",
                    id, username, articleId, content, dt.format(date));
            System.out.println(sql);
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public int getCommentNumber(int articleId) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("SELECT COUNT(*) FROM Comments WHERE Article_ID='%d';", articleId);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                return rs.getInt("COUNT(*)");
            else
                return 0;
        } catch (SQLException se) {
            se.printStackTrace();
            return 0;
        }
    }

    public ArrayList<Comments> getComments(int articleId) {
        // ============================================
        // 優化版本：使用 JOIN 一次查詢取得所有資料
        // ============================================
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT c.Comment_ID, c.Username, c.Article_ID, c.Content, c.Times, u.Nickname " +
                "FROM Comments c " +
                "LEFT JOIN Users u ON c.Username = u.Username " +
                "WHERE c.Article_ID = ? " +
                "ORDER BY c.Times ASC")) {
            
            pstmt.setInt(1, articleId);
            System.out.println("📝 [JOIN 優化] 查詢留言: Article_ID=" + articleId);
            
            ArrayList<Comments> results = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Comments tmp = new Comments();
                    tmp.comment_ID = rs.getInt("Comment_ID");
                    tmp.username = rs.getString("Username");
                    tmp.article_ID = rs.getInt("Article_ID");
                    tmp.content = rs.getString("Content");
                    tmp.times = rs.getTimestamp("Times");
                    tmp.nickname = rs.getString("Nickname"); // 直接從 JOIN 取得
                    results.add(tmp);
                }
            }
            System.out.println("✅ [JOIN 優化] 取得 " + results.size() + " 筆留言");
            return results;
        } catch (SQLException se) {
            System.err.println("❌ [JOIN 優化] 查詢留言失敗: " + se.getMessage());
            se.printStackTrace();
            return null;
        }
        
        // ============================================
        // 原本的版本（已註解保留，供參考）
        // ============================================
        /*
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ArrayList<Comments> results = new ArrayList<>();
            String sql = String.format("SELECT * FROM Comments WHERE Article_ID='%d'", articleId);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Comments tmp = new Comments();
                tmp.comment_ID = rs.getInt("Comment_ID");
                tmp.username = rs.getString("Username");
                tmp.article_ID = rs.getInt("Article_ID");
                tmp.content = rs.getString("Content");
                tmp.times = rs.getTimestamp("Times");
                
                // 找出對應的暱稱
                String sql2 = String.format("SELECT Nickname FROM Users WHERE Username='%s'", tmp.username);
                Statement stmt2 = conn.createStatement();
                ResultSet rs2 = stmt2.executeQuery(sql2);
                if (rs2.next()) {
                    tmp.nickname = rs2.getString("Nickname");
                }
                rs2.close();
                stmt2.close();
                results.add(tmp);
            }
            return results;
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
        */
    }

    public boolean checkArticleAlive(int articleId) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("SELECT * FROM Articles WHERE Article_ID = %d", articleId);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public ArrayList<Articles> getArticles(String articleClass) {
        // ============================================
        // 優化版本：使用 JOIN 一次查詢取得所有資料（包含統計）
        // ============================================
        try (Connection conn = getConnection()) {
            // 建立 SQL 查詢（使用 JOIN 和 GROUP BY 統計）
            StringBuilder sqlBuilder = new StringBuilder(
                "SELECT " +
                "    a.Article_ID, " +
                "    a.Username, " +
                "    a.Title, " +
                "    a.Class, " +
                "    a.Content, " +
                "    a.Times, " +
                "    u.Nickname, " +
                "    COUNT(DISTINCT l.Username) as likeNumber, " +
                "    COUNT(DISTINCT c.Comment_ID) as commentNumber " +
                "FROM Articles a " +
                "LEFT JOIN Users u ON a.Username = u.Username " +
                "LEFT JOIN Likes l ON a.Article_ID = l.Article_ID " +
                "LEFT JOIN Comments c ON a.Article_ID = c.Article_ID "
            );
            
            // 根據分類添加 WHERE 條件
            PreparedStatement pstmt;
            if (articleClass != null && !articleClass.equals("全部")) {
                sqlBuilder.append("WHERE a.Class = ? ");
                sqlBuilder.append("GROUP BY a.Article_ID, a.Username, a.Title, a.Class, a.Content, a.Times, u.Nickname ");
                sqlBuilder.append("ORDER BY a.Times DESC");
                
                pstmt = conn.prepareStatement(sqlBuilder.toString());
                pstmt.setString(1, articleClass);
                System.out.println("📰 [JOIN 優化] 查詢分類文章: Class=" + articleClass);
            } else {
                sqlBuilder.append("GROUP BY a.Article_ID, a.Username, a.Title, a.Class, a.Content, a.Times, u.Nickname ");
                sqlBuilder.append("ORDER BY a.Times DESC");
                
                pstmt = conn.prepareStatement(sqlBuilder.toString());
                System.out.println("📰 [JOIN 優化] 查詢全部文章");
            }
            
            ArrayList<Articles> results = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Articles tmp = new Articles();
                    tmp.article_ID = rs.getInt("Article_ID");
                    tmp.username = rs.getString("Username");
                    tmp.title = rs.getString("Title");
                    tmp.articleClass = rs.getString("Class");
                    tmp.content = rs.getString("Content");
                    tmp.times = rs.getTimestamp("Times");
                    tmp.nickname = rs.getString("Nickname"); // 直接從 JOIN 取得
                    tmp.likeNumber = rs.getInt("likeNumber"); // 直接從 COUNT 取得
                    tmp.commentNumber = rs.getInt("commentNumber"); // 直接從 COUNT 取得
                    results.add(tmp);
                }
            }
            System.out.println("✅ [JOIN 優化] 取得 " + results.size() + " 筆文章");
            return results;
        } catch (SQLException se) {
            System.err.println("❌ [JOIN 優化] 查詢文章失敗: " + se.getMessage());
            se.printStackTrace();
            return null;
        }
        
        // ============================================
        // 原本的版本（已註解保留，供參考）
        // ============================================
        /*
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ArrayList<Articles> results = new ArrayList<>();
            String sql;
            if (articleClass.equals("遊戲"))
                sql = String.format("SELECT * FROM Articles WHERE Class='遊戲' ORDER BY Times DESC");
            else if (articleClass.equals("生活"))
                sql = String.format("SELECT * FROM Articles WHERE Class='生活' ORDER BY Times DESC");
            else if (articleClass.equals("新聞"))
                sql = String.format("SELECT * FROM Articles WHERE Class='新聞' ORDER BY Times DESC;");
            else
                sql = String.format("SELECT * FROM Articles ORDER BY Times DESC");

            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Articles tmp = new Articles();
                tmp.article_ID = rs.getInt("Article_ID");
                tmp.username = rs.getString("Username");
                tmp.title = rs.getString("Title");
                tmp.articleClass = rs.getString("Class");
                tmp.content = rs.getString("Content");
                tmp.times = rs.getTimestamp("Times");
                
                String sql2 = String.format("SELECT Nickname FROM Users WHERE Username='%s'", tmp.username);
                Statement stmt2 = conn.createStatement();
                ResultSet rs2 = stmt2.executeQuery(sql2);
                if (rs2.next()) {
                    tmp.nickname = rs2.getString("Nickname");
                }
                rs2.close();
                stmt2.close();
                
                tmp.likeNumber = getLikeNumber(tmp.article_ID);
                tmp.commentNumber = getCommentNumber(tmp.article_ID);
                results.add(tmp);
            }
            return results;
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
        */
    }

    public boolean deleteUser(String username, String passwords) {
        // 去除前後空白字符
        if (username != null) {
            username = username.trim();
        }
        if (passwords != null) {
            passwords = passwords.trim();
        }
        
        System.out.println(String.format("[刪除用戶] 開始刪除用戶: username='%s'", username));
        
        String sql = "DELETE FROM Users WHERE Username = ? AND Passwords = ?";
        System.out.println(String.format("[刪除用戶] SQL 查詢: %s", sql));
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, passwords);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(String.format("[刪除用戶] 刪除成功: username='%s', 影響行數=%d", username, rowsAffected));
                return true;
            } else {
                System.out.println(String.format("[刪除用戶] 刪除失敗: 找不到匹配的用戶 (username='%s')", username));
                return false;
            }
        } catch (SQLException se) {
            System.err.println(String.format("[刪除用戶] SQL 異常: %s", se.getMessage()));
            se.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println(String.format("[刪除用戶] 未知異常: %s", e.getMessage()));
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(String username, String newPasswords, String newNickname) {
        // 去除前後空白字符
        if (username != null) {
            username = username.trim();
        }
        if (newPasswords != null) {
            newPasswords = newPasswords.trim();
        }
        if (newNickname != null) {
            newNickname = newNickname.trim();
        }
        
        System.out.println(String.format("[更新用戶] 開始更新用戶: username='%s'", username));
        
        try (Connection conn = getConnection()) {
            String sql;
            PreparedStatement pstmt;
            
            if (newPasswords != null && !newPasswords.isEmpty() && (newNickname == null || newNickname.isEmpty())) {
                // 只更新密碼
                sql = "UPDATE Users SET Passwords = ? WHERE Username = ?";
                System.out.println(String.format("[更新用戶] 只更新密碼: SQL=%s", sql));
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newPasswords);
                pstmt.setString(2, username);
            } else if ((newPasswords == null || newPasswords.isEmpty()) && newNickname != null && !newNickname.isEmpty()) {
                // 只更新暱稱
                sql = "UPDATE Users SET Nickname = ? WHERE Username = ?";
                System.out.println(String.format("[更新用戶] 只更新暱稱: SQL=%s", sql));
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newNickname);
                pstmt.setString(2, username);
            } else if (newPasswords != null && !newPasswords.isEmpty() && newNickname != null && !newNickname.isEmpty()) {
                // 更新兩者
                sql = "UPDATE Users SET Passwords = ?, Nickname = ? WHERE Username = ?";
                System.out.println(String.format("[更新用戶] 更新密碼和暱稱: SQL=%s", sql));
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newPasswords);
                pstmt.setString(2, newNickname);
                pstmt.setString(3, username);
            } else {
                System.out.println(String.format("[更新用戶] 更新失敗: 沒有要更新的欄位 (username='%s')", username));
                return false;
            }
            
            try (pstmt) {
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println(String.format("[更新用戶] 更新成功: username='%s', 影響行數=%d", username, rowsAffected));
                    return true;
                } else {
                    System.out.println(String.format("[更新用戶] 更新失敗: 找不到用戶 (username='%s')", username));
                    return false;
                }
            }
        } catch (SQLException se) {
            System.err.println(String.format("[更新用戶] SQL 異常: %s", se.getMessage()));
            se.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println(String.format("[更新用戶] 未知異常: %s", e.getMessage()));
            e.printStackTrace();
            return false;
        }
    }

    public Users login(String username, String passwords) {
        // 去除前後空白字符
        if (username != null) {
            username = username.trim();
        }
        if (passwords != null) {
            passwords = passwords.trim();
        }
        
        System.out.println(String.format("[登入驗證] 開始驗證用戶: username='%s', password='%s'", username, passwords != null ? "***" : "null"));
        
        String sql = "SELECT * FROM Users WHERE Username = ? AND Passwords = ?";
        System.out.println(String.format("[登入驗證] SQL 查詢: %s", sql));
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 設置參數
            pstmt.setString(1, username);
            pstmt.setString(2, passwords);
            
            System.out.println(String.format("[登入驗證] 執行查詢，參數: username='%s'", username));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Users userInfo = new Users();
                    userInfo.username = rs.getString("Username");
                    userInfo.passwords = rs.getString("Passwords");
                    userInfo.nickname = rs.getString("Nickname");
                    System.out.println(String.format("[登入驗證] 成功取得使用者資訊: username=%s, nickname=%s", 
                            userInfo.username, userInfo.nickname));
                    return userInfo;
                } else {
                    System.out.println(String.format("[登入驗證] 查詢失敗: 找不到匹配的用戶 (username='%s')", username));
                    return null;
                }
            }
        } catch (SQLException se) {
            System.err.println(String.format("[登入驗證] SQL 異常: %s", se.getMessage()));
            se.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println(String.format("[登入驗證] 未知異常: %s", e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }

    public boolean uploadArticle(String username, String title, String content, String uploadClass) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // 找出目前最後的article
            String sql = String.format("SELECT MAX(Article_ID) FROM Articles;");
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            int id = 0;
            if (rs.next()) {
                id = rs.getInt("MAX(article_ID)") + 1;
            }
            // 開始上傳文章
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            sql = String.format("INSERT INTO Articles VALUES ('%d' , '%s', '%s', '%s', '%s', '%s');",
                    id, username, title, uploadClass, content, dt.format(date));
            System.out.println(sql);
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public boolean signup(String username, String passwords, String nickname) {
        // 去除前後空白字符
        if (username != null) {
            username = username.trim();
        }
        if (passwords != null) {
            passwords = passwords.trim();
        }
        if (nickname != null) {
            nickname = nickname.trim();
        }
        
        System.out.println(String.format("[註冊] 開始註冊用戶: username='%s', nickname='%s'", username, nickname));
        
        try (Connection conn = getConnection()) {
            // 檢查用戶是否已存在
            String checkSql = "SELECT * FROM Users WHERE Username = ?";
            System.out.println(String.format("[註冊] 檢查用戶是否存在: SQL=%s", checkSql));
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println(String.format("[註冊] 註冊失敗: 用戶名 '%s' 已存在", username));
                        return false; // 用戶已存在
                    }
                }
            }
            
            // 插入新用戶
            String insertSql = "INSERT INTO Users (Username, Passwords, Nickname) VALUES (?, ?, ?)";
            System.out.println(String.format("[註冊] 插入新用戶: SQL=%s", insertSql));
            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, passwords);
                insertStmt.setString(3, nickname);
                
                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println(String.format("[註冊] 註冊成功: username='%s', nickname='%s'", username, nickname));
                    return true;
                } else {
                    System.out.println(String.format("[註冊] 註冊失敗: 沒有行被插入 (username='%s')", username));
                    return false;
                }
            }
        } catch (SQLException se) {
            System.err.println(String.format("[註冊] SQL 異常: %s", se.getMessage()));
            se.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println(String.format("[註冊] 未知異常: %s", e.getMessage()));
            e.printStackTrace();
            return false;
        }
    }
}

