package main;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ArticleReadUnitPanel extends JPanel{
	public ArticleReadUnitPanel(Articles article, ArrayList<Comments> comments, int width) {
		//設置Panel大小、位置、顏色
			super();
			int textError = 5;
			int LikeError = 20;
			int totalHeight = 0;
			setLocation(0,0);
//				setBackground(new Color(60, 60, 60));
			setBackground(new Color(32, 32, 32));
			setLayout(null);
			

			//設置標題
			JTextArea title = new JTextArea(article.title);
			title.setFont(new Font(MyGlobal.fontname, Font.BOLD, 22));
			title.setForeground(MyGlobal.colorWhite);
			title.setBackground(MyGlobal.colorBlack);
			title.setEditable(false);
			title.setLineWrap(true);
			title.setBounds(0, 0, width, 35);
			Dimension size = title.getPreferredSize();
			title.setSize(new Dimension(width, size.height));
			totalHeight += size.height-5;
			this.add(title);
			
			//設置使用者暱稱
			JLabel nickname = new JLabel();
			nickname.setForeground(MyGlobal.colorGray);
			nickname.setFont(new Font(MyGlobal.fontname, Font.BOLD, 14));
			nickname.setBounds(0, totalHeight+textError, 160, 25);
			nickname.setText(article.nickname);
			this.add(nickname);
			
			//計算發文時間與當前時間的差值
			Date nowDate = new Date();
			long elapsedms = nowDate.getTime() - article.times.getTime();
			//轉換成小時制
			long minutes = TimeUnit.MINUTES.convert(elapsedms, TimeUnit.MILLISECONDS);
			long hour = TimeUnit.HOURS.convert(elapsedms, TimeUnit.MILLISECONDS);
			long day =  TimeUnit.DAYS.convert(elapsedms, TimeUnit.MILLISECONDS);
			String timeDiff = "";
			if (minutes==0) // 設定成當前
				timeDiff = "剛剛";
			else  if (minutes <= 59)	// 設定成幾分鐘前
				timeDiff = String.format("%d分鐘前",minutes);

			else if(hour < 24)	// 設定成幾小時前
				timeDiff = String.format("%d小時前",hour);
			else if(day > 0)	// 設定成幾天前
				timeDiff = String.format("%d天前",day);
				
			
			
			//設置發文時間
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
			int xShift = nickname.getPreferredSize().width + 15;
			JLabel uploadTime = new JLabel();
			uploadTime.setForeground(MyGlobal.colorGray);
			uploadTime.setFont(new Font(MyGlobal.fontname, Font.BOLD, 13));
			uploadTime.setBounds(xShift, totalHeight+textError+1, 300, 25);
			uploadTime.setText(timeDiff + " (" + sdf.format(article.times) + ")");
			this.add(uploadTime);
			totalHeight += textError+28;
			
			
			
			//設置文本內容
			JTextArea content = new JTextArea(article.content);
			content.setFont(new Font(MyGlobal.fontname, Font.BOLD, 16));
			content.setForeground(MyGlobal.colorWhite);
			content.setBackground(MyGlobal.colorBlack);
			content.setEditable(false);
			content.setLineWrap(true);
			content.setBounds(0, totalHeight+textError+10, width, 400);
			size =content.getPreferredSize();
			content.setSize(new Dimension(width, size.height));
			this.add(content);
			
			totalHeight += textError +10+ size.height;
			
			//設置讚與留言數
			//設置Like圖片
			int iconSize = 16;
			ImageIcon imageIcon = new ImageIcon(frame.class.getResource("/img/like.png")); // load the image to a imageIcon
			Image image = imageIcon.getImage(); // transform it 
			image = image.getScaledInstance(iconSize, iconSize,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
			imageIcon = new ImageIcon(image);  // transform it back
			
			JLabel like = new JLabel();
			like.setIcon(null);
			like.setBounds(0, totalHeight+LikeError, iconSize, iconSize);
			like.setIcon(imageIcon);
			this.add(like);
			
			//設置like人數
			JLabel likeNumber = new JLabel();
			likeNumber.setForeground(MyGlobal.colorWhite);
			likeNumber.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 14));
			likeNumber.setBounds(22, totalHeight+LikeError, 50, 20);
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
			comment.setBounds(60, totalHeight+LikeError, iconSize2, iconSize2);
			comment.setIcon(imageIcon);
			this.add(comment);

			//設置Comment人數
			JLabel commentNumber = new JLabel();
			commentNumber.setForeground(MyGlobal.colorWhite);
			commentNumber.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 13));
			commentNumber.setBounds(82, totalHeight+LikeError, 50, 20);
			commentNumber.setText(String.valueOf(String.valueOf(article.commentNumber)));
			this.add(commentNumber);
			
			totalHeight += LikeError + 20;
			
			//設置評論區標題
			JLabel commentTitle = new JLabel("評論區");
			commentTitle.setForeground(MyGlobal.colorCommentTitle);
			commentTitle.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
			commentTitle.setBounds(0, totalHeight+30, width, 20);
			this.add(commentTitle);
			totalHeight += 30 + 20;
			
			//若沒有任何留言
			if(comments.size() == 0) {
				//設置此文章目前無任何留言！
				comment = new JLabel("此文章目前無任何留言！");
				comment.setForeground(MyGlobal.colorGray);
				comment.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 16));
				comment.setBounds(0, totalHeight+textError, width, 20);
				this.add(comment);
				totalHeight += textError + 20;
			}
			else {
				//設置每個留言
				for (Comments tmpComment: comments) {
					//顯示留言間隔線
					JLabel commentLine = new JLabel();
					commentLine.setBounds(0, totalHeight+5, width, 5);
					commentLine.setIcon(new ImageIcon(frame.class.getResource("/img/commentLine.png")));
					this.add(commentLine);
					totalHeight += 5;
					
					//顯示使用者名稱
					nickname = new JLabel(String.format("%s(%s)",tmpComment.nickname,tmpComment.username));
					nickname.setForeground(MyGlobal.colorWhite);
					nickname.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 13));
					nickname.setBounds(0, totalHeight+textError, width, 25);
					this.add(nickname);
					
					
					//計算發文時間與當前時間的差值
					nowDate = new Date();
					elapsedms = nowDate.getTime() - tmpComment.times.getTime();
					//轉換成小時制
					minutes = TimeUnit.MINUTES.convert(elapsedms, TimeUnit.MILLISECONDS);
					hour = TimeUnit.HOURS.convert(elapsedms, TimeUnit.MILLISECONDS);
					day =  TimeUnit.DAYS.convert(elapsedms, TimeUnit.MILLISECONDS);
					timeDiff = "";
					if (minutes==0) // 設定成當前
						timeDiff = "剛剛";
					else  if (minutes <= 59)	// 設定成幾分鐘前
						timeDiff = String.format("%d分鐘前",minutes);

					else if(hour < 24)	// 設定成幾小時前
						timeDiff = String.format("%d小時前",hour);
					else if(day > 0)	// 設定成幾天前
						timeDiff = String.format("%d天前",day);
						
					
					
					//設置發文時間
					xShift = nickname.getPreferredSize().width + 12;
					uploadTime = new JLabel();
					uploadTime.setForeground(MyGlobal.colorGray);
					uploadTime.setFont(new Font(MyGlobal.fontname, Font.BOLD, 13));
					uploadTime.setBounds(xShift, totalHeight+textError, 300, 25);
					uploadTime.setText(timeDiff);
					this.add(uploadTime);
					totalHeight += textError+15;
					
					//顯示留言內容
					JTextArea tmp = new JTextArea(tmpComment.content);
					tmp.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 15));
					tmp.setForeground(MyGlobal.colorWhite);
					tmp.setBackground(MyGlobal.colorBlack);
					tmp.setEditable(false);
					tmp.setLineWrap(true);
					tmp.setBounds(20, totalHeight+textError, width-20, 40);
					size = tmp.getPreferredSize();
					tmp.setSize(new Dimension(width-20, size.height));
					totalHeight += textError+size.height;
					this.add(tmp);
					System.out.println(tmpComment.content);
				}
			}
			

			


			setPreferredSize(new Dimension(width,totalHeight));
			
	}

}
