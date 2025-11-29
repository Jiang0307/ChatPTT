package com.chatptt.service;

import com.chatptt.model.Articles;
import com.chatptt.model.Comments;
import com.chatptt.model.Users;
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
            String sql = String.format("SELECT COUNT(*) FROM likes where article_ID='%d';", articleId);
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
            String sql = String.format("DELETE FROM articles WHERE (article_ID = %d);", articleId);
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
            String sql = String.format("SELECT * FROM likes where username = '%s' and article_ID = '%d';", username, articleId);
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
                String sql = String.format("DELETE FROM likes WHERE username = '%s' AND article_ID = '%d';", username, articleId);
                System.out.println(sql);
                stmt.executeUpdate(sql);
                return 0;
            } else {
                // 按讚
                String sql = String.format("INSERT INTO likes VALUES ('%s', '%d');", username, articleId);
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
            String sql = String.format("SELECT MAX(comment_ID) FROM comments;");
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            int id = 0;
            if (rs.next()) {
                id = rs.getInt("MAX(comment_ID)") + 1;
            }
            // 開始上傳留言
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            sql = String.format("INSERT INTO comments VALUES ('%d' , '%s', '%d', '%s', '%s');",
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
            String sql = String.format("SELECT COUNT(*) FROM comments where article_ID='%d';", articleId);
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
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ArrayList<Comments> results = new ArrayList<>();
            String sql = String.format("SELECT * FROM comments where article_ID='%d'", articleId);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Comments tmp = new Comments();
                tmp.comment_ID = rs.getInt("comment_ID");
                tmp.username = rs.getString("username");
                tmp.article_ID = rs.getInt("article_ID");
                tmp.content = rs.getString("content");
                tmp.times = rs.getTimestamp("times");
                
                // 找出對應的暱稱
                String sql2 = String.format("SELECT nickname FROM users where username='%s'", tmp.username);
                Statement stmt2 = conn.createStatement();
                ResultSet rs2 = stmt2.executeQuery(sql2);
                if (rs2.next()) {
                    tmp.nickname = rs2.getString("nickname");
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
    }

    public boolean checkArticleAlive(int articleId) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("SELECT * FROM articles where article_ID = %d", articleId);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public ArrayList<Articles> getArticles(String articleClass) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ArrayList<Articles> results = new ArrayList<>();
            String sql;
            if (articleClass.equals("遊戲"))
                sql = String.format("SELECT * FROM articles where class='遊戲' ORDER BY times DESC");
            else if (articleClass.equals("生活"))
                sql = String.format("SELECT * FROM articles where class='生活' ORDER BY times DESC");
            else if (articleClass.equals("新聞"))
                sql = String.format("SELECT * FROM articles where class='新聞' ORDER BY times DESC;");
            else
                sql = String.format("SELECT * FROM articles ORDER BY times DESC");

            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Articles tmp = new Articles();
                tmp.article_ID = rs.getInt("article_ID");
                tmp.username = rs.getString("username");
                tmp.title = rs.getString("title");
                tmp.articleClass = rs.getString("class");
                tmp.content = rs.getString("content");
                tmp.times = rs.getTimestamp("times");
                
                String sql2 = String.format("SELECT nickname FROM users where username='%s'", tmp.username);
                Statement stmt2 = conn.createStatement();
                ResultSet rs2 = stmt2.executeQuery(sql2);
                if (rs2.next()) {
                    tmp.nickname = rs2.getString("nickname");
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
    }

    public boolean deleteUser(String username, String passwords) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("DELETE FROM Users WHERE Username = '%s' AND Passwords = '%s';", username, passwords);
            System.out.println(sql);
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(String username, String newPasswords, String newNickname) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            if (newPasswords.length() != 0 && newNickname.length() == 0) {
                // 只更新密碼
                String sql = String.format("UPDATE users set passwords='%s' where username='%s'", newPasswords, username);
                System.out.println(sql);
                stmt.executeUpdate(sql);
            } else if (newPasswords.length() == 0 && newNickname.length() != 0) {
                // 只更新暱稱
                String sql = String.format("UPDATE users set nickname='%s' where username='%s'", newNickname, username);
                System.out.println(sql);
                stmt.executeUpdate(sql);
            } else {
                // 更新兩者
                String sql = String.format("UPDATE users set passwords='%s', nickname='%s' where username='%s'", newPasswords, newNickname, username);
                System.out.println(sql);
                stmt.executeUpdate(sql);
            }
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public Users verifyLogin(String username, String passwords) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format("SELECT * FROM users where username='%s' and passwords='%s'", username, passwords);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Users userInfo = new Users();
                userInfo.username = rs.getString("username");
                userInfo.passwords = rs.getString("passwords");
                userInfo.nickname = rs.getString("nickname");
                System.out.println(String.format("成功取得使用者資訊: %s, %s, %s", userInfo.username, userInfo.passwords, userInfo.nickname));
                return userInfo;
            } else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    public boolean uploadArticle(String username, String title, String content, String uploadClass) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // 找出目前最後的article
            String sql = String.format("SELECT MAX(article_ID) FROM articles;");
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            int id = 0;
            if (rs.next()) {
                id = rs.getInt("MAX(article_ID)") + 1;
            }
            // 開始上傳文章
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            sql = String.format("INSERT INTO articles VALUES ('%d' , '%s', '%s', '%s', '%s', '%s');",
                    id, username, title, uploadClass, content, dt.format(date));
            System.out.println(sql);
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public boolean signUp(String username, String passwords, String nickname) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql0 = String.format("SELECT * FROM Users WHERE Username = '%s';", username);
            System.out.println(sql0);
            ResultSet rs = stmt.executeQuery(sql0);
            if (rs.next()) {
                return false; // 用戶已存在
            } else {
                String sql = String.format("INSERT INTO Users VALUES ('%s' , '%s' , '%s')", username, passwords, nickname);
                System.out.println(sql);
                stmt.executeUpdate(sql);
                return true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }
}


