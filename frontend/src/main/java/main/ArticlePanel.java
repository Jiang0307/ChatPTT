package main;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ArticlePanel extends JPanel{
	public ArticlePanel(Articles article, int height) {
		//設置Panel大小、位置、顏色
		super();
		setBounds(30, height, 400, 90);
//		setBackground(new Color(60, 60, 60));
		setBackground(new Color(32, 32, 32));
		setLayout(null);

		//設置標題
		JLabel title = new JLabel();
		title.setForeground(MyGlobal.colorWhite);
		title.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		title.setBounds(60, 0, 340, 40);
		title.setText(article.title);
		title.setVisible(true);
		this.add(title);
		//設置文本內容
		JLabel content = new JLabel();
		content.setForeground(MyGlobal.colorWhite);
		content.setFont(new Font(MyGlobal.fontname, Font.BOLD, 14));
		content.setBounds(60, 22, 340, 40);
		content.setText(article.content);
		this.add(content);

		//設置使用者暱稱
		JLabel nickname = new JLabel();
		nickname.setForeground(MyGlobal.colorGray);
		nickname.setFont(new Font(MyGlobal.fontname, Font.BOLD, 14));
		nickname.setBounds(60, 59, 160, 25);
		nickname.setText(article.nickname);
		this.add(nickname);

		//設置發文時間
		JLabel uploadTime = new JLabel();
		uploadTime.setForeground(MyGlobal.colorGray);
		uploadTime.setFont(new Font(MyGlobal.fontname, Font.BOLD, 13));
		uploadTime.setBounds(175, 60, 160, 25);
		
		
		
		//計算發文時間與當前時間的差值
		Date nowDate = new Date();
		long elapsedms = nowDate.getTime() - article.times.getTime();
		//轉換成小時制
		long minutes = TimeUnit.MINUTES.convert(elapsedms, TimeUnit.MILLISECONDS);
		long hour = TimeUnit.HOURS.convert(elapsedms, TimeUnit.MILLISECONDS);
		long day =  TimeUnit.DAYS.convert(elapsedms, TimeUnit.MILLISECONDS);
		
		if (minutes==0) // 設定成當前
			uploadTime.setText("剛剛");
		else  if (minutes <= 59)	// 設定成幾分鐘前
			uploadTime.setText(String.format("%d分鐘前",minutes));

		else if(hour < 24)	// 設定成幾小時前
			uploadTime.setText(String.format("%d小時前",hour));
		else if(day > 0)	// 設定成幾天前
			uploadTime.setText(String.format("%d天前",day));
			
		this.add(uploadTime);
		
		
		//設置Like圖片
		int iconSize = 16;
		ImageIcon imageIcon = new ImageIcon(frame.class.getResource("/img/like.png")); // load the image to a imageIcon
		Image image = imageIcon.getImage(); // transform it 
		image = image.getScaledInstance(iconSize, iconSize,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		imageIcon = new ImageIcon(image);  // transform it back
		
		JLabel like = new JLabel();
		like.setIcon(null);
		like.setBounds(243, 64, iconSize, iconSize);
		like.setIcon(imageIcon);
		this.add(like);

		//設置like人數
		JLabel likeNumber = new JLabel();
		likeNumber.setForeground(MyGlobal.colorWhite);
		likeNumber.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 14));
		likeNumber.setBounds(263, 61, 50, 20);
		likeNumber.setText(String.valueOf(article.likeNumber));
		this.add(likeNumber);
		
		//設置Comment圖片
		int iconSize2 = 20;
		imageIcon = new ImageIcon(frame.class.getResource("/img/comment.png")); // load the image to a imageIcon
		image = imageIcon.getImage(); // transform it 
		image = image.getScaledInstance(iconSize2, iconSize2,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		imageIcon = new ImageIcon(image);  // transform it back
		
		JLabel comment = new JLabel();
		comment.setIcon(null);
		comment.setBounds(295, 62, iconSize2, iconSize2);
		comment.setIcon(imageIcon);
		this.add(comment);

		//設置Comment人數
		JLabel commentNumber = new JLabel();
		commentNumber.setForeground(MyGlobal.colorWhite);
		commentNumber.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 13));
		commentNumber.setBounds(317, 61, 50, 20);
		commentNumber.setText(String.valueOf(String.valueOf(article.commentNumber)));
		this.add(commentNumber);
		
		//設置類別標籤
		JButton Class = new JButton();
		Class.setBorder(MyGlobal.BorderClassRound);
		Class.setBackground(MyGlobal.colorBlue);
		Class.setText(article.articleClass);
		Class.setFont(new Font(MyGlobal.fontname, Font.BOLD, 14));
		Class.setForeground(MyGlobal.colorWhite);
		
		switch(article.articleClass) {
			case "遊戲":
				Class.setBackground(MyGlobal.colorClassGame);
				break;
			case "新聞":
				Class.setBackground(MyGlobal.colorClassNews);
				break;
			case "生活":
				Class.setBackground(MyGlobal.colorClassLife);
				break;
		}
		Class.setBounds(345,53,58,34);
		this.add(Class);
	}
}
