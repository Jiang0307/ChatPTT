package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DBConnection {

    private Users userInfo = null;
    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  

    
    static final String DB_URL = "jdbc:mysql://localhost:3306/ChatPTT?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Taipei";

    //    static final String DB_URL = "jdbc:mysql://114.35.187.165:3306/project?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8";
    
    private Connection conn = null;	// 資料庫的連線端口
    private Statement stmt = null;	// 資料庫的實例端口，用於取資料。

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "User";
    static final String PASS = "20010307";
 
    // 用途：確認目前是否連上資料庫。
    public boolean isConnect() {
    	if(conn == null)
    		return false;
    	else
    		return true;
    }
    
    // 用途：重新與資料庫連線，若conn值為null則代表資料庫連線異常。
    public void reConnect() {
    	if(conn == null)
    		conn = getConn();
    }
    
    //建構子：初始化conn成員，若conn值為null則代表資料庫連線異常。
    public DBConnection() {
    	this.conn = getConn();
    }
        
    public int getLikeNumber(Articles article) {
    	try {
	    	String sql = String.format("SELECT COUNT(*) FROM likes where article_ID='%d';" , article.article_ID);
	        stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        System.out.println(sql);
	        ResultSet rs = stmt.executeQuery(sql);
	        if(rs.next())
	        	return rs.getInt("COUNT(*)");
	        else
	        	return 0;
    	}catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    	return 0;
    }
    
    public boolean DeleteArticle (Articles article) {
    	try {
    		
	    	String sql = String.format("DELETE FROM articles WHERE (article_ID = %d);" , article.article_ID);
	        stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        System.out.println(sql);
	        stmt.executeUpdate(sql);
	        return true;	
    	}catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    	
    	return false;
    }
    
    public boolean check_liked(Articles article)
    {
    	try
    	{
	    	String sql = String.format("SELECT * FROM likes where username = '%s' and article_ID = '%d';" , userInfo.username, article.article_ID);
	        stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        System.out.println(sql);
	        ResultSet rs = stmt.executeQuery(sql);
	        if(rs.next())
	        	return true;
	        else
	        	return false;
    	}
    	catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
    	catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    	return false;
    }
    
    public int uploadLike (Articles article)
    {
    	try {
    		boolean liked = check_liked(article);
	        //若有按過讚，則失敗
	        if(liked)
	        {
	        	String sql1 = String.format("DELETE FROM likes WHERE username = '%s' AND article_ID = '%d';" , userInfo.username, article.article_ID);
	        	stmt = conn.prepareStatement(sql1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        	System.out.println("");
	        	System.out.println(sql1);
		        stmt.executeUpdate(sql1);
	        	return 0;
	        }
	        //若沒按過讚，則插入新的讚
	        else
	        {
	        	String sql1 = String.format("INSERT INTO likes VALUES ('%s', '%d');" , userInfo.username, article.article_ID);
		        stmt = conn.prepareStatement(sql1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		        System.out.println("");
		        System.out.println(sql1);
		        stmt.executeUpdate(sql1);
		        return 1;
	        }
	        	
    	}
    	catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
    	catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    	
    	return 2;
    }
    public boolean uploadComment (Articles article, String content) {
    	try {
//			首先找出目前最後的comments_ID
			String sql = String.format("SELECT MAX(comment_ID) FROM comments;");
            stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            int id=0;
            if(rs.next()) {
            	id = rs.getInt("MAX(comment_ID)")+1;
            }
            //  開始上傳留言
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date  = new Date();
            sql = String.format(String.format("INSERT INTO comments VALUES ('%d' , '%s', '%d', '%s', '%s');",
            								   id,
            								   userInfo.username,
            								   article.article_ID,
            								   content,
            								   dt.format(date)));
            stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println(sql);
            stmt.executeUpdate(sql);
            
            return true;
    	}catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    	
    	return false;
    }
    
    public int getCommentNumber(Articles article) {
    	try {
    		String sql = String.format("SELECT COUNT(*) FROM comments where article_ID='%d';" , article.article_ID);
	        stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        ResultSet rs = stmt.executeQuery(sql);
	        if(rs.next())
	        	return rs.getInt("COUNT(*)");
	        else
	        	return 0;
    	}catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    	return 0;
    }
    public ArrayList<Comments> getComments(int Article_ID){

    	if(isConnect()) {
    		try {
    	        ArrayList<Comments> results = new ArrayList<Comments>();
    	        String sql;
	    		sql = String.format("SELECT * FROM comments where article_ID='%d'", Article_ID);
    			
    			System.out.println(sql);
                stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                	Comments tmp = new Comments();
                	tmp.comment_ID = rs.getInt("comment_ID");
                	tmp.username = rs.getString("username");
                	tmp.article_ID = rs.getInt("article_ID");
                	tmp.content = rs.getString("content");
                	tmp.times = rs.getTimestamp("times");
                	
                	//找出對應的暱稱
        			String sql2 = String.format("SELECT nickname FROM users where username='%s'", tmp.username);
                    stmt = conn.prepareStatement(sql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs2 = stmt.executeQuery(sql2);
                    rs2.next();
                    tmp.nickname = rs2.getString("nickname");
                    results.add(tmp);
                }
                return results;
                
    		}catch(SQLException se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
                // 处理 Class.forName 错误
                e.printStackTrace();
            }
    	}
    	return null;
    }
    public boolean checkArticleAlive(Articles article) {
		try {
	        String sql = String.format("SELECT * FROM articles where article_ID = %d",  article.article_ID);
			System.out.println(sql);
            stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next())
            	return true;
            else
            	return false;
            
		}catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    	return false;
    }
    
    public ArrayList<Articles> getArticles(String articleClass) {
    	if(isConnect()) {
    		try {
    	        ArrayList<Articles> results = new ArrayList<Articles>();
    	        String sql;
    			if(articleClass.equals("遊戲"))
	    			sql = String.format("SELECT * FROM articles where class='遊戲' ORDER BY times DESC");
    			
    			else if(articleClass.equals("生活"))
	    			sql = String.format("SELECT * FROM articles where class='生活' ORDER BY times DESC");
    			
    			else if(articleClass.equals("新聞"))
	    			sql = String.format("SELECT * FROM articles where class='新聞' ORDER BY times DESC;");
    			
    			else
	    			sql = String.format("SELECT * FROM articles ORDER BY times DESC");
    			
    			
    			System.out.println(sql);
                stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                	Articles tmp = new Articles();
                	tmp.article_ID = rs.getInt("article_ID");
                	tmp.username = rs.getString("username");
                	tmp.title = rs.getString("title");
                	tmp.articleClass = rs.getString("class");
                	tmp.content = rs.getString("content");
                	tmp.times = rs.getTimestamp("times");
        			String sql2 = String.format("SELECT nickname FROM users where username='%s'", tmp.username);
                    stmt = conn.prepareStatement(sql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs2 = stmt.executeQuery(sql2);
                    rs2.next();
                    tmp.nickname = rs2.getString("nickname");
                    tmp.likeNumber = getLikeNumber(tmp);
                    tmp.commentNumber = getCommentNumber(tmp);
//                	System.out.println(String.format("%s, %s, %s ,%s, %s, %s",
//                			tmp.nickname,
//                			tmp.username,
//                			tmp.title,
//                			tmp.articleClass,
//                			tmp.content,
//                			tmp.times.toString()));
                    results.add(tmp);
                }
                return results;
                
    		}catch(SQLException se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
                // 处理 Class.forName 错误
                e.printStackTrace();
            }
    		return null;
    	}
    	return null;
    }
    
    // 用途：回傳獲得的userInfo資訊
    public Users getUser() {
    	return  userInfo;
    }
    // 用途：更新使用者資訊
    public boolean deleteUser()
    {
    	if(isConnect())
    	{
    		try
    		{
    			String sql = String.format("DELETE FROM Users WHERE Username = '%s' AND Passwords = '%s';", userInfo.username, userInfo.passwords);
    			System.out.println(sql);
                stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                stmt.executeUpdate(sql);
                return true;
    		}
    		catch(SQLException se)
    		{
                // 处理 JDBC 错误
                se.printStackTrace();
            }
    		catch(Exception e)
    		{
                // 处理 Class.forName 错误
                e.printStackTrace();
            }
    		return false;
    	}
    	return false;
    }
    
    public boolean updateUser(String new_passwords, String new_nickname) {
    	if(isConnect()) {
    		try
    		{
    			if(new_passwords.length() != 0 && new_nickname.length() == 0) //只更新密碼
    			{
        			String sql = String.format("UPDATE users set passwords='%s' where username='%s'", new_passwords, userInfo.username);
        			System.out.println(sql);
                    stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stmt.executeUpdate(sql);
                    userInfo.passwords = new_passwords;
    			}
    			else if(new_passwords.length() == 0 && new_nickname.length() != 0) //只更新暱稱
    			{
        			String sql = String.format("UPDATE users set nickname='%s' where username='%s'", new_nickname, userInfo.username);
        			System.out.println(sql);
                    stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stmt.executeUpdate(sql);
                    userInfo.nickname = new_nickname;			
    			}
    			else 
    			{
        			String sql = String.format("UPDATE users set passwords='%s', nickname='%s' where username='%s'", new_passwords, new_nickname, userInfo.username);
        			System.out.println(sql);
                    stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stmt.executeUpdate(sql);
                    userInfo.nickname = new_nickname;
                    userInfo.passwords = new_passwords;	
    			}
                return true;
    		}catch(SQLException se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
                // 处理 Class.forName 错误
                e.printStackTrace();
            }
    		return false;
    	}
    	return false;
    }
    
    // 用途 : 驗證登入資料是否存在資料庫
    public boolean verifyLogin(String username, String passwords) {
    	if(isConnect()) {
    		try {
    			String sql = String.format("SELECT * FROM users where username='%s' and passwords='%s'", username, passwords);
                stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                System.out.println(sql);
                ResultSet rs = stmt.executeQuery(sql);
                int rowcount = 0;
                
                if(rs == null) {
                	System.out.println("null");
                	return false;
                }
                else {
                	rs.next();
                	rowcount = rs.getRow();
                	if(rowcount == 0)
                		return false;
                	else {
                		userInfo = new Users();
                    	userInfo.username = rs.getString("username");
                    	userInfo.passwords = rs.getString("passwords");
                    	userInfo.nickname = rs.getString("nickname");
                    	System.out.println(String.format("成功取得使用者資訊: %s, %s, %s",userInfo.username, userInfo.passwords,  userInfo.nickname));
                		return true;
                	}
                }
    		}catch(SQLException se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
                // 处理 Class.forName 错误
                e.printStackTrace();
            }
    	}
    	return false;
    }
    // 用途：上傳文章 
    public boolean UploadArticle(String title, String content, String uploadClass) {
    	if(isConnect()) {
    		try {
//    			首先找出目前最後的article
    			String sql = String.format("SELECT MAX(article_ID) FROM articles;");
                stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                System.out.println(sql);
                ResultSet rs = stmt.executeQuery(sql);
                int id=0;
                if(rs.next()) {
                	id = rs.getInt("MAX(article_ID)")+1;
                }
                //  開始上傳文章
//                INSERT INTO `javaproject`.`articles` (`article_ID`, `username`, `title`, `class`, `content`, `times`) VALUES ('1', 'wx200010', 'asd', '新聞', 'asd', '2023-05-21 23:30:00');
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date  = new Date();
                sql = String.format(String.format("INSERT INTO articles VALUES ('%d' , '%s', '%s', '%s', '%s', '%s');",
                								   id,
                								   userInfo.username,
                								   title,
                								   uploadClass,
                								   content,
                								   dt.format(date)));
                stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                System.out.println(sql);
                stmt.executeUpdate(sql);
                
                return true;
                
    		}catch(SQLException se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
                // 处理 Class.forName 错误
                e.printStackTrace();
            }
    	}
    	return false;
    }
    
    // 用途：註冊用戶
    public boolean SignUp(String username, String passwords, String nickname) {
    	if(isConnect()) {
    		try {
	    			String sql0 = String.format("SELECT * FROM Users WHERE Username = '%s';", username);
	    			stmt = conn.prepareStatement(sql0, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    			System.out.println(sql0);
	    	        ResultSet rs = stmt.executeQuery(sql0);
	    	        if(rs.next())
	    	        {
	    	        	return false;
	    	        }
	    	        else
	    	        {
	        			String sql = String.format("INSERT INTO Users VALUES ('%s' , '%s' , '%s')", username, passwords, nickname);
	                    stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	                    System.out.println(sql);
	                    stmt.executeUpdate(sql);
	                    return true;
	    	        }
    			                
    		}catch(SQLException se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
                // 处理 Class.forName 错误
                e.printStackTrace();
            }
    	}
    	return false;
    }

    // 用途：與資料庫建立連線，並回傳conn，若回傳null則代表資料庫連線異常。
	private Connection getConn() {
		Connection conn = null;
	
	    try{
	        Class.forName(JDBC_DRIVER);
	        // 打开链接
	        System.out.println("正在連接資料庫...");
	        DriverManager.setLoginTimeout(4);
	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
	    }catch(SQLException se){
	        // 处理 JDBC 错误
	        se.printStackTrace();
	    }catch(Exception e){
	        // 处理 Class.forName 错误
	        e.printStackTrace();
	    }
		return conn;
	}
}
