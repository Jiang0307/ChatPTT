package main;

import java.util.ArrayList;

import javax.swing.border.EmptyBorder;


import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class frame extends JFrame
{
	private Client client; // WebSocket 客戶端
	
    private CardLayout cardLayout;
    private ApiClient apiClient;	// API 客戶端，用於與後端通信
    private Users currentUser;	// 當前登入的用戶信息
    
    private int idxPages = 1;
    private int totalPages = 10;
    private int idxNowArticle = 0;
    private String articleClassNames[] = {"全部", "遊戲", "生活", "新聞"};

	private ArrayList<ArticlePanel> P2_articlePanels = new ArrayList<ArticlePanel>();
	private ArrayList<JButton> P2_articleDelButtons = new ArrayList<JButton>();
	private ArrayList<JButton> P5_btnClasses = new ArrayList<JButton>();
    private int selectClass = 0; // 0:全部, 1:遊戲, 2:生活, 3:新聞。
    private String uploadClass; // 0:全部, 1:遊戲, 2:生活, 3:新聞。
    private ArrayList<Articles> articles;
	
    private ArrayList<JLabel> P2_lbs;
    
	private JPanel contentPanel;
	private JTextField P1_textUsername;
	private JTextField P1_textPassword;
	private JTextField P3_textUsername;
	private JTextField P3_textPassword;
	private JTextField P3_textNickname;
	private JTextField P4_textPassword;
	private JTextField P4_textNickname;
	private JPanel LoginPanel;
	private JPanel BrowserPanel;
	private JPanel SignUpPanel;
	private JPanel SettingPanel;
	private JLabel P1_lbSystemHint;
	private JLabel P3_lbSystemHint;
	private JLabel P4_lbSystemHint;
	private JLabel P4_lbUsername;
	private JLabel P2_lbPages;
	private JTextField P5_textTitle;
	private JLabel P5_lbUsername;
	private JTextArea P5_textContent;
	private JLabel P5_lbSystemHint;
	private JTextField P6_textComment;
	private JScrollPane P6_ArticleScrollPane;
	private JPanel ArticleReadPanel;
	private JLabel P6_lbSystemHint;
	private JButton P6_btnClass;
	private JButton P6_btnLike;
	private JButton P6_btnDel;
	private JLabel P2_lbSystemHint;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public int getIdxNowArticle()
	{
	    return idxNowArticle;
	}
	
	public frame()
	{
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setBounds(100, 100, 450, 300);
		contentPanel = new JPanel();
		contentPanel.setPreferredSize(new Dimension(500,800));
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		setContentPane(contentPanel);
		

//		setSize(500, 800);
		cardLayout = new CardLayout(0,0);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 設定黑色背景 
		contentPanel.setBackground(MyGlobal.colorBlack);
		contentPanel.setLayout(cardLayout);
		
		LoginPanel = new JPanel();
		LoginPanel.setBackground(MyGlobal.colorBlack);
		contentPanel.add(LoginPanel, "LoginPanel");
		LoginPanel.setLayout(null);
		
		ImageIcon imageIcon = new ImageIcon(frame.class.getResource("/img/title.png")); // load the image to a imageIcon
		Image image = imageIcon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way 
		imageIcon = new ImageIcon(newimg);  // transform it back
		
		JLabel P1_TitleIcon = new JLabel("");
		P1_TitleIcon.setIcon(new ImageIcon(frame.class.getResource("/img/title.png")));
		P1_TitleIcon.setBounds(0, 0, 400, 58);
		LoginPanel.add(P1_TitleIcon);
		
		JLabel P1_lbTitle = new JLabel("歡迎，客人");
		P1_lbTitle.setForeground(MyGlobal.colorWhite);
		P1_lbTitle.setFont(new Font(MyGlobal.fontname, Font.BOLD, 50));
		P1_lbTitle.setBounds(133, 208, 300, 50);
		LoginPanel.add(P1_lbTitle);
		
		JLabel P1_lb2 = new JLabel("帳號：");
		P1_lb2.setForeground(MyGlobal.colorWhite);
		P1_lb2.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P1_lb2.setBounds(100, 349, 200, 50);
		LoginPanel.add(P1_lb2);
		
		JLabel P1_lb3 = new JLabel("密碼：");
		P1_lb3.setForeground(MyGlobal.colorWhite);
		P1_lb3.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P1_lb3.setBounds(100, 418, 200, 50);
		LoginPanel.add(P1_lb3);
		
		JLabel P1_lbHint = new JLabel("<系統提示訊息>");
		P1_lbHint.setHorizontalAlignment(SwingConstants.CENTER);
		P1_lbHint.setForeground(MyGlobal.colorWhite);
		P1_lbHint.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P1_lbHint.setBounds(0, 594, 500, 50);
		LoginPanel.add(P1_lbHint);
		
		P1_lbSystemHint = new JLabel("歡迎使用，請選擇登入或註冊~");
		P1_lbSystemHint.setHorizontalAlignment(SwingConstants.CENTER);
		P1_lbSystemHint.setForeground(MyGlobal.colorWhite);
		P1_lbSystemHint.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P1_lbSystemHint.setBounds(0, 654, 500, 50);
		LoginPanel.add(P1_lbSystemHint);
		
		P1_textUsername = new JTextField(1);
		P1_textUsername.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P1_textUsername.setBounds(192, 360, 200, 30);
		P1_textUsername.setBorder(MyGlobal.RoundText);
		LoginPanel.add(P1_textUsername);
		
		P1_textPassword = new JTextField(1);
		P1_textPassword.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P1_textPassword.setBounds(192, 428, 200, 30);
		P1_textPassword.setBorder(MyGlobal.RoundText);
		LoginPanel.add(P1_textPassword);
		
		JButton P1_btnLogin = new JButton("登入");
		P1_btnLogin.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P1_btnLogin.setBackground(MyGlobal.colorWhite);
		P1_btnLogin.setBounds(136, 509, 80, 41);
		P1_btnLogin.setBorder(MyGlobal.BorderRound);
		LoginPanel.add(P1_btnLogin);
		
		JButton P1_btnSign = new JButton("註冊頁面");
		P1_btnSign.setHideActionText(true);
		P1_btnSign.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P1_btnSign.setBackground(MyGlobal.colorWhite);
		P1_btnSign.setBounds(263, 509, 114, 41);
		P1_btnSign.setBorder(MyGlobal.BorderRound);
		LoginPanel.add(P1_btnSign);
		
		JPanel UploadArticlePanel = new JPanel();
		UploadArticlePanel.setBackground(MyGlobal.colorBlack);
		contentPanel.add(UploadArticlePanel, "UploadArticlePanel");
		UploadArticlePanel.setLayout(null);
		
		JButton P5_btnBack = new JButton("返回");
		P5_btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 返回browserPanel
				reset_BrowserPanel();
				cardLayout.show(contentPanel, "BrowserPanel");
			}
		});
		P5_btnBack.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P5_btnBack.setBorder(MyGlobal.BorderRound);
		P5_btnBack.setBackground(MyGlobal.colorWhite);
		P5_btnBack.setBounds(277, 734, 76, 41);
		UploadArticlePanel.add(P5_btnBack);
		
		JLabel P5_lb2 = new JLabel("標題：");
		P5_lb2.setForeground(MyGlobal.colorWhite);
		P5_lb2.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P5_lb2.setBounds(72, 205, 200, 50);
		UploadArticlePanel.add(P5_lb2);
		
		P5_lbUsername = new JLabel("123");
		P5_lbUsername.setHorizontalAlignment(SwingConstants.LEFT);
		P5_lbUsername.setForeground(MyGlobal.colorWhite);
		P5_lbUsername.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P5_lbUsername.setBounds(153, 147, 200, 50);
		UploadArticlePanel.add(P5_lbUsername);
		
		JButton P5_btnClass1 = new JButton("遊戲");
		P5_btnClass1.setForeground(MyGlobal.colorWhite);
		P5_btnClass1.setBackground(MyGlobal.colorBlack);
		P5_btnClass1.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P5_btnClass1.setBounds(145, 270, 80, 40);
		P5_btnClass1.setBorder(MyGlobal.BorderRound);
		UploadArticlePanel.add(P5_btnClass1);
		P5_btnClasses.add(P5_btnClass1);
		
		JButton P5_btnClass2 = new JButton("生活");
		P5_btnClass2.setForeground(MyGlobal.colorWhite);
		P5_btnClass2.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P5_btnClass2.setBackground(MyGlobal.colorBlack);
		P5_btnClass2.setBounds(250, 270, 80, 40);
		P5_btnClass2.setBorder(MyGlobal.BorderRound);
		UploadArticlePanel.add(P5_btnClass2);
		P5_btnClasses.add(P5_btnClass2);
		
		JButton P5_btnClass3 = new JButton("新聞");
		P5_btnClass3.setForeground(MyGlobal.colorWhite);
		P5_btnClass3.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P5_btnClass3.setBackground(MyGlobal.colorBlack);
		P5_btnClass3.setBounds(360, 270, 80, 40);
		P5_btnClass3.setBorder(MyGlobal.BorderRound);
		UploadArticlePanel.add(P5_btnClass3);
		P5_btnClasses.add(P5_btnClass3);
		
		JLabel P5_lb3 = new JLabel("類別：");
		P5_lb3.setForeground(MyGlobal.colorWhite);
		P5_lb3.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P5_lb3.setBounds(71, 265, 80, 50);
		UploadArticlePanel.add(P5_lb3);
		
		JLabel P5_lb4 = new JLabel("內容：");
		P5_lb4.setForeground(MyGlobal.colorWhite);
		P5_lb4.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P5_lb4.setBounds(71, 325, 80, 50);
		UploadArticlePanel.add(P5_lb4);
		
		JLabel P5_lb1 = new JLabel("暱稱：");
		P5_lb1.setForeground(MyGlobal.colorWhite);
		P5_lb1.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P5_lb1.setBounds(71, 147, 80, 50);
		UploadArticlePanel.add(P5_lb1);
		
		P5_textContent = new JTextArea();
		P5_textContent.setLineWrap(true);
		P5_textContent.setWrapStyleWord(true);
		P5_textContent.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 16));
		P5_textContent.setBounds(71, 379, 364, 285);
		JScrollPane P5_textScrollBar =  new JScrollPane(P5_textContent);
		P5_textScrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		P5_textScrollBar.setSize(363, 300);
		P5_textScrollBar.setLocation(72, 375);
		UploadArticlePanel.add(P5_textScrollBar);
		
		JLabel P5_TitleIcon = new JLabel("");
		P5_TitleIcon.setIcon(new ImageIcon(frame.class.getResource("/img/title.png")));
		P5_TitleIcon.setBounds(0, 0, 510, 58);
		UploadArticlePanel.add(P5_TitleIcon);
		
		P5_lbSystemHint = new JLabel("此頁面可以上傳文章~");
		P5_lbSystemHint.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P5_lbSystemHint.setForeground(new Color(255, 255, 255));
		P5_lbSystemHint.setHorizontalAlignment(SwingConstants.CENTER);
		P5_lbSystemHint.setBounds(0, 680, 500, 40);
		UploadArticlePanel.add(P5_lbSystemHint);
		
		JButton P5_btnUpdate = new JButton("發表");
		P5_btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String title = P5_textTitle.getText();
				String content = P5_textContent.getText();
				// String uploadClass = uploadClass;
				if(title.length() == 0 || content.length() == 0){
					P5_lbSystemHint.setText("標題或文章內容不可為空!!");
					return;
				}
				else if(uploadClass.length()==0) {
					P5_lbSystemHint.setText("請選擇類別！");
					return;
				}
				if(apiClient.uploadArticle(title, content, uploadClass)) {
					reset_BrowserPanel();
					cardLayout.show(contentPanel, "BrowserPanel");
					P2_lbSystemHint.setText("文章上傳成功 <(=^･^=)>");
					// 不需要手動發送消息，後端會自動廣播
				}
				else
					P5_lbSystemHint.setText("文章上傳失敗！資料庫處理異常。");
				
			}
		});
		P5_btnUpdate.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P5_btnUpdate.setBorder(MyGlobal.BorderRound);
		P5_btnUpdate.setBackground(MyGlobal.colorWhite);
		P5_btnUpdate.setBounds(145, 734, 76, 41);
		UploadArticlePanel.add(P5_btnUpdate);
		
		P5_textTitle = new JTextField(1);
		P5_textTitle.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 16));
		P5_textTitle.setBorder(MyGlobal.RoundText);
		P5_textTitle.setBounds(153, 217, 282, 30);
		UploadArticlePanel.add(P5_textTitle);
		
		
		BrowserPanel = new JPanel();
		BrowserPanel.setBackground(MyGlobal.colorBlack);
		contentPanel.add(BrowserPanel, "BrowserPanel");
		BrowserPanel.setLayout(null);
		
		JLabel P2_TitleIcon = new JLabel("");
		P2_TitleIcon.setIcon(new ImageIcon(frame.class.getResource("/img/title.png")));
		P2_TitleIcon.setBounds(0, 0, 400, 58);
		BrowserPanel.add(P2_TitleIcon);
		
		JButton P2_btnSetting = new JButton("設定");
		P2_btnSetting.setBorder(MyGlobal.BorderRound);
		
		
		
		
		P2_lbs = new ArrayList<JLabel>(5);
		
		JLabel P2_lb1 = new JLabel("全部");
		P2_lb1.setHorizontalAlignment(SwingConstants.CENTER);
		P2_lb1.setForeground(MyGlobal.colorBlue);
		P2_lb1.setFont(new Font(MyGlobal.fontname, Font.BOLD, 28));
		P2_lb1.setBounds(75, 146, 72, 58);
		P2_lbs.add(P2_lb1);
		BrowserPanel.add(P2_lb1);
		
		JLabel P2_lb2 = new JLabel("遊戲");
		P2_lb2.setHorizontalAlignment(SwingConstants.CENTER);
		P2_lb2.setForeground(MyGlobal.colorWhite);
		P2_lb2.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 24));
		P2_lb2.setBounds(165, 147, 72, 58);
		P2_lbs.add(P2_lb2);
		BrowserPanel.add(P2_lb2);
		
		
		P2_btnSetting.setFont(new Font(MyGlobal.fontname, Font.BOLD, 14));
		P2_btnSetting.setBackground(MyGlobal.colorWhite);
		P2_btnSetting.setBounds(437, 72, 57, 34);
		BrowserPanel.add(P2_btnSetting);
		
		JLabel P2_lb3 = new JLabel("生活");
		P2_lb3.setHorizontalAlignment(SwingConstants.CENTER);
		P2_lb3.setForeground(MyGlobal.colorWhite);
		P2_lb3.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 24));
		P2_lb3.setBounds(255, 147, 72, 58);
		P2_lbs.add(P2_lb3);
		BrowserPanel.add(P2_lb3);
		
		JLabel P2_lb4 = new JLabel("新聞");
		P2_lb4.setHorizontalAlignment(SwingConstants.CENTER);
		P2_lb4.setForeground(MyGlobal.colorWhite);
		P2_lb4.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 24));
		P2_lb4.setBounds(350, 147, 72, 58);
		P2_lbs.add(P2_lb4);
		BrowserPanel.add(P2_lb4);
		
		
		JButton P2_btnLogout = new JButton("登出");
		P2_btnLogout.setBorder(MyGlobal.BorderRound);
		P2_btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 切換回LoginPanel，做相關初始化
				
				if (client != null)
				{
				    client.disconnect();
				    client = null;
				}
				
				reset_LoginPanel();
				cardLayout.show(contentPanel, "LoginPanel");
			}
		});
		P2_btnLogout.setFont(new Font(MyGlobal.fontname, Font.BOLD, 14));
		P2_btnLogout.setBackground(MyGlobal.colorWhite);
		P2_btnLogout.setBounds(437, 108, 57, 34);
		BrowserPanel.add(P2_btnLogout);
		
		P2_lbPages = new JLabel("第x頁 / 共x頁");
		P2_lbPages.setFont(new Font(MyGlobal.fontname, Font.BOLD, 22));
		P2_lbPages.setHorizontalAlignment(SwingConstants.CENTER);
		P2_lbPages.setForeground(MyGlobal.colorWhite);
		P2_lbPages.setBounds(140, 739, 220, 40);
		BrowserPanel.add(P2_lbPages);
		
		JButton P2_btnPrevPages_1 = new JButton("上頁");
		P2_btnPrevPages_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(idxPages > 1) {
					idxPages--;
					P2_lbPages.setText(String.format("第%d頁 / 共%d頁" , idxPages, totalPages));
					show_articles();
				}
				else if(idxPages == 1) {
					idxPages = totalPages;
					P2_lbPages.setText(String.format("第%d頁 / 共%d頁" , idxPages, totalPages));
					show_articles();
				}
			}
		});
		P2_btnPrevPages_1.setFont(new Font(MyGlobal.fontname, Font.BOLD, 16));
		P2_btnPrevPages_1.setBorder(MyGlobal.BorderRound);
		P2_btnPrevPages_1.setBackground(MyGlobal.colorWhite);
		P2_btnPrevPages_1.setBounds(86, 741, 62, 38);
		BrowserPanel.add(P2_btnPrevPages_1);
		
		JButton P2_btnNextPages = new JButton("下頁");
		P2_btnNextPages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(idxPages <  totalPages) {
					idxPages++;
					P2_lbPages.setText(String.format("第%d頁 / 共%d頁" , idxPages, totalPages));
					show_articles();
				}
				else if (idxPages ==  totalPages) {
					idxPages=1;
					P2_lbPages.setText(String.format("第%d頁 / 共%d頁" , idxPages, totalPages));
					show_articles();
					
				}
			}
		});

		P2_btnNextPages.setBorder(MyGlobal.BorderRound);
		P2_btnNextPages.setFont(new Font(MyGlobal.fontname, Font.BOLD, 16));
		P2_btnNextPages.setBounds(358, 741, 62, 38);
		P2_btnNextPages.setBackground(MyGlobal.colorWhite);
		BrowserPanel.add(P2_btnNextPages);
		
		JButton P2_btnUploadArticle = new JButton("發表文章");
		P2_btnUploadArticle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset_UploadArticalPanel();
				cardLayout.show(contentPanel, "UploadArticlePanel");
			}
		});
		P2_btnUploadArticle.setFont(new Font(MyGlobal.fontname, Font.BOLD, 18));
		P2_btnUploadArticle.setBounds(200, 685, 100, 47);
		P2_btnUploadArticle.setBackground(MyGlobal.colorWhite);
		P2_btnUploadArticle.setBorder(MyGlobal.BorderRound);
		BrowserPanel.add(P2_btnUploadArticle);
		
		JButton P2_btnRefresh = new JButton("重整");
		P2_btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset_BrowserPanel();
			}
		});
		P2_btnRefresh.setFont(new Font(MyGlobal.fontname, Font.BOLD, 16));
		P2_btnRefresh.setBorder(MyGlobal.BorderRound);
		P2_btnRefresh.setBackground(MyGlobal.colorWhite);
		P2_btnRefresh.setBounds(430, 742, 62, 38);
		BrowserPanel.add(P2_btnRefresh);
		
		
		JLabel P2_lbslice1 = new JLabel("New label");
		P2_lbslice1.setIcon(new ImageIcon(frame.class.getResource("/img/slice.png")));
		P2_lbslice1.setBounds(140, 155, 31, 43);
		BrowserPanel.add(P2_lbslice1);
		
		JLabel P2_lbslice2 = new JLabel("New label");
		P2_lbslice2.setIcon(new ImageIcon(frame.class.getResource("/img/slice.png")));
		P2_lbslice2.setBounds(230, 155, 31, 43);
		BrowserPanel.add(P2_lbslice2);
		
		
		JLabel P2_lbslice3 = new JLabel("New label");
		P2_lbslice3.setIcon(new ImageIcon(frame.class.getResource("/img/slice.png")));
		P2_lbslice3.setBounds(323, 155, 31, 43);
		BrowserPanel.add(P2_lbslice3);
		
		P2_lbSystemHint = new JLabel("<(=^･^=)>");
		P2_lbSystemHint.setHorizontalAlignment(SwingConstants.CENTER);
		P2_lbSystemHint.setForeground(MyGlobal.colorWhite);
		P2_lbSystemHint.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P2_lbSystemHint.setBounds(0, 100, 500, 40);
		BrowserPanel.add(P2_lbSystemHint);
		
		SignUpPanel = new JPanel();
		SignUpPanel.setBackground(MyGlobal.colorBlack);
		contentPanel.add(SignUpPanel, "SignUpPanel");
		SignUpPanel.setLayout(null);
		
		JLabel P3_TitleIcon = new JLabel("");
		P3_TitleIcon.setIcon(new ImageIcon(frame.class.getResource("/img/title.png")));
		P3_TitleIcon.setBounds(0, 0, 250, 58);
		SignUpPanel.add(P3_TitleIcon);
		
		JLabel P3_lb = new JLabel("歡迎，客人");
		P3_lb.setForeground(MyGlobal.colorWhite);
		P3_lb.setFont(new Font(MyGlobal.fontname, Font.BOLD, 50));
		P3_lb.setBounds(133, 208, 300, 50);
		SignUpPanel.add(P3_lb);
		
		P3_lbSystemHint = new JLabel("按下按鈕即可註冊。");
		P3_lbSystemHint.setHorizontalAlignment(SwingConstants.CENTER);
		P3_lbSystemHint.setForeground(MyGlobal.colorWhite);
		P3_lbSystemHint.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P3_lbSystemHint.setBounds(0, 654, 500, 50);
		SignUpPanel.add(P3_lbSystemHint);
		
		JLabel P3_lb2 = new JLabel("帳號：");
		P3_lb2.setForeground(MyGlobal.colorWhite);
		P3_lb2.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P3_lb2.setBounds(100, 349, 200, 50);
		SignUpPanel.add(P3_lb2);
		
		P3_textUsername = new JTextField(1);
		P3_textUsername.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P3_textUsername.setBounds(192, 360, 200, 30);
		P3_textUsername.setBorder(MyGlobal.RoundText);
		SignUpPanel.add(P3_textUsername);
		
		P3_textPassword = new JTextField(1);
		P3_textPassword.setBorder(MyGlobal.RoundText);
		P3_textPassword.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P3_textPassword.setBounds(192, 428, 200, 30);
		SignUpPanel.add(P3_textPassword);
		
		JLabel P3_lb3 = new JLabel("密碼：");
		P3_lb3.setForeground(MyGlobal.colorWhite);
		P3_lb3.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P3_lb3.setBounds(100, 418, 200, 50);
		SignUpPanel.add(P3_lb3);
		
		JButton P3_btnSign = new JButton("註冊");
		P3_btnSign.setBorder(MyGlobal.BorderRound);
		P3_btnSign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//若conn沒有成功連線則會先重連一次
				// API 客戶端不需要手動連接，直接使用即可
				String username = P3_textUsername.getText();
				String password = P3_textPassword.getText();
				String nickname = P3_textNickname.getText();
				// 若這三者有任何一個為空值，就不給註冊
				if(username.length() ==0 || password.length()==0 || nickname.length()==0) {
					P3_lbSystemHint.setText("請輸入正確的帳號、密碼、暱稱！");
					return;
				}
				
				
				boolean result = apiClient.signUp(username, password, nickname);
				if (result == true) {
					P3_lbSystemHint.setText("註冊成功！");
				}
				else {
    	        	System.out.println("User exists");
					P3_lbSystemHint.setText("註冊失敗，用戶已存在");
				}
				
			}
		});
		P3_btnSign.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P3_btnSign.setBackground(MyGlobal.colorWhite);
		P3_btnSign.setBounds(136, 573, 76, 42);
		SignUpPanel.add(P3_btnSign);
		
		JButton P3_btnLogin = new JButton("返回登入");
		P3_btnLogin.setBorder(MyGlobal.BorderRound);
		P3_btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 切換回LoginPanel，做相關初始化
				reset_LoginPanel();
				cardLayout.show(contentPanel, "LoginPanel");
			}
		});
		P3_btnLogin.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P3_btnLogin.setBackground(MyGlobal.colorWhite);
		P3_btnLogin.setBounds(262, 573, 110, 41);
		SignUpPanel.add(P3_btnLogin);
		
		JLabel P3_lb3_1 = new JLabel("暱稱：");
		P3_lb3_1.setForeground(MyGlobal.colorWhite);
		P3_lb3_1.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P3_lb3_1.setBounds(100, 484, 200, 50);
		SignUpPanel.add(P3_lb3_1);
		
		P3_textNickname = new JTextField(1);
		P3_textNickname.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P3_textNickname.setBounds(192, 494, 200, 30);
		P3_textNickname.setBorder(MyGlobal.RoundText);
		SignUpPanel.add(P3_textNickname);
		
		SettingPanel = new JPanel();
		SettingPanel.setBackground(MyGlobal.colorBlack);
		contentPanel.add(SettingPanel, "SettingPanel");
		SettingPanel.setLayout(null);
		
		JButton P4_btnBack = new JButton("返回前頁");
		P4_btnBack.setBorder(MyGlobal.BorderRound);
		P4_btnBack.setBackground(MyGlobal.colorWhite);
		P4_btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(contentPanel, "BrowserPanel");
				reset_BrowserPanel();
				repaint();
			}
		});
		P4_btnBack.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P4_btnBack.setBounds(75, 565, 110, 45);
		SettingPanel.add(P4_btnBack);
			
		JButton P4_btnUpdate = new JButton("更新");
		P4_btnUpdate.setBorder(MyGlobal.BorderRound);
		P4_btnUpdate.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P4_btnUpdate.setBackground(MyGlobal.colorWhite);
		P4_btnUpdate.setBounds(200, 565, 110, 45);
		SettingPanel.add(P4_btnUpdate);
		
		JButton P4_btnDelete = new JButton("刪除用戶");
		P4_btnDelete.setBorder(MyGlobal.BorderRound);
		P4_btnDelete.setBackground(MyGlobal.colorWhite);
		P4_btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				String password = P4_textPassword.getText();
//				String nickname = P4_textNickname.getText();
				boolean result = apiClient.deleteUser(P4_textPassword.getText());
				if(result == true)
				{
					reset_LoginPanel();
					cardLayout.show(contentPanel, "LoginPanel");
				}
			}
		});
		P4_btnDelete.setFont(new Font(MyGlobal.fontname, Font.BOLD, 20));
		P4_btnDelete.setBounds(325, 565, 110, 45);
		SettingPanel.add(P4_btnDelete);
		
		
		P4_lbSystemHint = new JLabel("此頁面可以更新用戶資訊~");
		P4_lbSystemHint.setHorizontalAlignment(SwingConstants.CENTER);
		P4_lbSystemHint.setForeground(MyGlobal.colorWhite);
		P4_lbSystemHint.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P4_lbSystemHint.setBounds(0, 635, 500, 50);
		SettingPanel.add(P4_lbSystemHint);
		
		JLabel P4_TitleIcon = new JLabel("");
		P4_TitleIcon.setIcon(new ImageIcon(frame.class.getResource("/img/title.png")));
		P4_TitleIcon.setBounds(0, 0, 400, 58);
		SettingPanel.add(P4_TitleIcon);
		
		JLabel P4_lb2 = new JLabel("帳號：");
		P4_lb2.setForeground(MyGlobal.colorWhite);
		P4_lb2.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P4_lb2.setBounds(100, 349, 200, 50);
		SettingPanel.add(P4_lb2);
		
		P4_lbUsername = new JLabel("123");
		P4_lbUsername.setHorizontalAlignment(SwingConstants.LEFT);
		P4_lbUsername.setForeground(MyGlobal.colorWhite);
		P4_lbUsername.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P4_lbUsername.setBounds(192, 349, 200, 50);
		SettingPanel.add(P4_lbUsername);
		
		P4_textPassword = new JTextField(1);
		P4_textPassword.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P4_textPassword.setBounds(192, 428, 200, 30);
		P4_textPassword.setBorder(MyGlobal.RoundText);
		SettingPanel.add(P4_textPassword);
		
		P4_textNickname = new JTextField(1);
		P4_textNickname.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 18));
		P4_textNickname.setBounds(192, 494, 200, 30);
		P4_textNickname.setBorder(MyGlobal.RoundText);
		SettingPanel.add(P4_textNickname);
		
		JLabel P4_lb3 = new JLabel("密碼：");
		P4_lb3.setForeground(MyGlobal.colorWhite);
		P4_lb3.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P4_lb3.setBounds(100, 418, 200, 50);
		SettingPanel.add(P4_lb3);
		
		JLabel P4_lb4 = new JLabel("暱稱：");
		P4_lb4.setForeground(MyGlobal.colorWhite);
		P4_lb4.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 22));
		P4_lb4.setBounds(100, 484, 200, 50);
		SettingPanel.add(P4_lb4);
		
		ArticleReadPanel = new JPanel();
		ArticleReadPanel.setBackground(MyGlobal.colorBlack);
		contentPanel.add(ArticleReadPanel, "ArticleReadPanel");
		ArticleReadPanel.setLayout(null);
		
		P6_lbSystemHint = new JLabel("<對其發送留言吧！>");
		P6_lbSystemHint.setHorizontalAlignment(SwingConstants.CENTER);
		P6_lbSystemHint.setFont(new Font(MyGlobal.fontname, Font.BOLD, 18));
		P6_lbSystemHint.setForeground(MyGlobal.colorWhite);
		
		P6_lbSystemHint.setBounds(0, 676, 500, 30);
		ArticleReadPanel.add(P6_lbSystemHint);
		
		P6_ArticleScrollPane = new JScrollPane();
		P6_ArticleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		P6_ArticleScrollPane.setBounds(60, 135, 380, 531);
		
		ArticleReadPanel.add(P6_ArticleScrollPane);
		
		P6_textComment = new JTextField();
		P6_textComment.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 14));
		P6_textComment.setBounds(60, 709, 380, 30);
		P6_textComment.setBorder(MyGlobal.RoundText);
		ArticleReadPanel.add(P6_textComment);
		P6_textComment.setColumns(10);
		
		JLayeredPane P6_layerLike = new JLayeredPane();
		P6_layerLike.setBounds(100, 750, 75, 40);
		ArticleReadPanel.add(P6_layerLike);
		
		JLabel P6_lbLikeIcon = new JLabel("");
		P6_layerLike.setLayer(P6_lbLikeIcon, 3);
		P6_lbLikeIcon.setIcon(new ImageIcon(frame.class.getResource("/img/like_20.png")));
		P6_lbLikeIcon.setBounds(10, 0, 20, 38);
		P6_layerLike.add(P6_lbLikeIcon);
		
		JLabel P6_lbLike = new JLabel("點讚");
		P6_layerLike.setLayer(P6_lbLike, 2);
		P6_lbLike.setFont(new Font(MyGlobal.fontname, Font.BOLD, 17));
		P6_lbLike.setBounds(31, 0, 46, 38);
		P6_layerLike.add(P6_lbLike);
		
		P6_btnLike = new JButton("");
		P6_btnLike.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//發送讚之前先檢查文章是否被刪除
				if(!apiClient.checkArticleAlive(articles.get(idxNowArticle).article_ID))
					P6_lbSystemHint.setText("按讚失敗，文章已被刪除。");
				//嘗試發送讚，並判斷是否成功。
				else 
				{
					int result = apiClient.uploadLike(articles.get(idxNowArticle).article_ID);
					if(result == 1)
					{
						reset_ArticleReadPanel(idxNowArticle);
						P6_lbSystemHint.setText("已成功按讚！");
					    client.sendMessage("update_likes");
					}
					else if(result == 0)
					{
						reset_ArticleReadPanel(idxNowArticle);
						P6_lbSystemHint.setText("已收回按讚！");
					    client.sendMessage("update_likes");
					}
				}

			}
		});
		P6_layerLike.setLayer(P6_btnLike, 1);
		P6_btnLike.setFont(new Font(MyGlobal.fontname, Font.BOLD, 18));
		P6_btnLike.setBorder(MyGlobal.BorderRound);
		P6_btnLike.setBackground(Color.WHITE);
		P6_btnLike.setBounds(0, 0, 77, 38);
		P6_layerLike.add(P6_btnLike);
		
		JLayeredPane P6_layerComment = new JLayeredPane();
		P6_layerComment.setBounds(195, 750, 110, 40);
		ArticleReadPanel.add(P6_layerComment);
		
		JLabel P6_lbCommentIcon = new JLabel("");
		P6_lbCommentIcon.setIcon(new ImageIcon(frame.class.getResource("/img/comment_22.png")));
		P6_layerComment.setLayer(P6_lbCommentIcon, 3);
		P6_lbCommentIcon.setBounds(9, 0, 46, 40);
		P6_layerComment.add(P6_lbCommentIcon);
		
		JLabel P6_lbComment = new JLabel("發送留言");
		P6_layerComment.setLayer(P6_lbComment, 2);
		P6_lbComment.setFont(new Font(MyGlobal.fontname, Font.BOLD, 17));
		P6_lbComment.setBounds(30, 0, 90, 38);
		P6_layerComment.add(P6_lbComment);
		
		JButton P6_btnComment = new JButton("");
		P6_btnComment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//若留言文字是空的，則拒絕請求
				if(P6_textComment.getText().length() == 0)
				{
					P6_lbSystemHint.setText("留言文字不可為空！");
					return ;
				}
				//確認文章是否還在
				else if(!apiClient.checkArticleAlive(articles.get(idxNowArticle).article_ID))
					P6_lbSystemHint.setText("留言發送失敗，文章已被刪除。");
				//發送留言，並確認是否發送成功
				else if(apiClient.uploadComment(articles.get(idxNowArticle).article_ID, P6_textComment.getText()))
				{
					int newCommentCount = apiClient.getCommentNumber(articles.get(idxNowArticle).article_ID);
					articles.get(idxNowArticle).commentNumber = newCommentCount;
					    
					reset_ArticleReadPanel(idxNowArticle);
					P6_lbSystemHint.setText("留言發送成功！");
					P6_textComment.setText("");
					
				    // 不需要手動發送消息，後端會自動廣播
				}
				else
					P6_lbSystemHint.setText("留言發送失敗，資料庫執行異常。");
				
			}
		});
		P6_layerComment.setLayer(P6_btnComment, 1);
		P6_btnComment.setFont(new Font(MyGlobal.fontname, Font.BOLD, 18));
		P6_btnComment.setBorder(MyGlobal.BorderRound);
		P6_btnComment.setBackground(Color.WHITE);
		P6_btnComment.setBounds(0, 0, 110, 38);
		P6_layerComment.add(P6_btnComment);
		
		JLayeredPane P6_layerBack = new JLayeredPane();
		P6_layerBack.setBounds(330, 750, 75, 40);
		ArticleReadPanel.add(P6_layerBack);
		
		JLabel P6_lbBackIcon = new JLabel("");
		P6_lbBackIcon.setFont(new Font("PMingLiU", Font.PLAIN, 12));
		P6_layerBack.setLayer(P6_lbBackIcon, 3);
		P6_lbBackIcon.setIcon(new ImageIcon(frame.class.getResource("/img/back_20.png")));
		P6_lbBackIcon.setBounds(10, 0, 25, 37);
		P6_layerBack.add(P6_lbBackIcon);
		
		JLabel P6_lbBack = new JLabel("返回");
		P6_layerBack.setLayer(P6_lbBack, 2);
		P6_lbBack.setFont(new Font(MyGlobal.fontname, Font.BOLD, 17));
		P6_lbBack.setBounds(30, 0, 46, 38);
		P6_layerBack.add(P6_lbBack);
		
		JButton P6_btnBack = new JButton("");
		P6_btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset_BrowserPanel();
				cardLayout.show(contentPanel, "BrowserPanel");
			}
		});
		P6_btnBack.setFont(new Font(MyGlobal.fontname, Font.BOLD, 18));
		P6_btnBack.setBorder(MyGlobal.BorderRound);
		P6_btnBack.setBackground(Color.WHITE);
		P6_btnBack.setBounds(0, 0, 77, 38);
		P6_layerBack.add(P6_btnBack, new Integer(1));
		
		JLabel P6_lbTitleIcon = new JLabel("New label");
		P6_lbTitleIcon.setIcon(new ImageIcon(frame.class.getResource("/img/title.png")));
		P6_lbTitleIcon.setBounds(0, 0, 170, 58);
		ArticleReadPanel.add(P6_lbTitleIcon);
		
		P6_btnClass = new JButton("類別");
		P6_btnClass.setFont(new Font(MyGlobal.fontname, Font.BOLD, 15));
		P6_btnClass.setBounds(56, 96, 63, 34);
		P6_btnClass.setBorder(MyGlobal.BorderClassRound);
		ArticleReadPanel.add(P6_btnClass);
		
		P6_btnDel = new JButton("刪除文章");
		P6_btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apiClient.deleteArticle(articles.get(idxNowArticle).article_ID);
				reset_BrowserPanel();
				P2_lbSystemHint.setText("文章刪除成功 <(=^･^=)>");
				cardLayout.show(contentPanel, "BrowserPanel");
				// 不需要手動發送消息，後端會自動廣播
			}
		});
		P6_btnDel.setFont(new Font(MyGlobal.fontname, Font.BOLD, 15));
		P6_btnDel.setBorder(MyGlobal.BorderClassRound);
		P6_btnDel.setBackground(MyGlobal.colorWhite);
		P6_btnDel.setBounds(134, 96, 87, 34);
		ArticleReadPanel.add(P6_btnDel);
		
		
		pack();
		
		// 初始化 API 客戶端（從配置文件讀取後端地址）
		String serverUrl = ConfigManager.getServerUrl();
		apiClient = new ApiClient(serverUrl);
		System.out.println("後端服務器地址: " + serverUrl);
		
		P1_btnLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//若conn沒有成功連線則會先重連一次
				String  username = P1_textUsername.getText();
				String  password = P1_textPassword.getText();
				if(username.length() == 0 || password.length()==0) {
					P1_lbSystemHint.setText("請正確輸入帳號與密碼");
					return;
				}
				
				ApiClient.LoginResponse response = apiClient.login(username, password);
				if(response.isSuccess()) {
					// 成功登入，切換成瀏覽頁面
					currentUser = response.getUser();
					apiClient.setCurrentUsername(currentUser.username);
					
					client = new Client(frame.this);
					client.connect();
					
					Users userInfo = currentUser;
					System.out.println(String.format("登入成功，歡迎，%s。" , userInfo.nickname));
					P1_lbSystemHint.setText(String.format("登入成功，歡迎，%s。" , userInfo.nickname));
					cardLayout.show(contentPanel , "BrowserPanel");
					reset_BrowserPanel();
					
				}
				else
					P1_lbSystemHint.setText("登入失敗：使用者帳號或密碼錯誤。");
//				cardLayout.next(contentPanel);
			}
		});

		// 對P2_lbs裡的每個label 新增滑鼠點擊事件
		int lb_i = 0;
		for(JLabel lb : P2_lbs) {
			final Integer innerMi = lb_i;
			lb.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int lb_j=0;
					// 設定字體變化的部分
					for(JLabel lb_tmp : P2_lbs) {
						if(lb_j == innerMi) {
							lb_tmp.setFont(new Font(MyGlobal.fontname, Font.BOLD, 28));
							lb_tmp.setForeground(MyGlobal.colorBlue);
						}
						else {
							lb_tmp.setFont(new Font(MyGlobal.fontname, Font.PLAIN, 24));
							lb_tmp.setForeground(MyGlobal.colorWhite);
						}
						lb_j++;
					}
					// 從資料庫抓取特定的文章
					selectClass = innerMi;
					articles = apiClient.getArticles(articleClassNames[selectClass]);
					// 重新設定頁數
					idxPages = 1;
					totalPages = 1 + (articles.size()-1)/4;
					P2_lbPages.setText(String.format("第%d頁 / 共%d頁" , idxPages, totalPages));
					show_articles();
				}

			});
			lb_i++;
		}


		P2_btnSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset_SettingPanel();
				cardLayout.show(contentPanel, "SettingPanel");
			}
		});
		
		P1_btnSign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset_SignUpPanel();
				cardLayout.show(contentPanel , "SignUpPanel");
			}
		});
		

		// 用於更新使用者資訊的按鈕
		P4_btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = P4_textPassword.getText();
				String nickname = P4_textNickname.getText();
								
				if(password.length() == 0 && nickname.length() == 0)
				{
					P4_lbSystemHint.setText("請至少更新一項！");
				}
				//若輸入的暱稱太長，也不給過
				else if (nickname.length() > 7)
					P4_lbSystemHint.setText("暱稱不可以超過7個字！");
				else {
					boolean result = apiClient.updateUser(password, nickname);
					if(result == true)
						P4_lbSystemHint.setText("更新成功。");
					else
						P4_lbSystemHint.setText("更新失敗，可能是伺服器連線異常。");
						
				}
			}
		});
				

		// 對P5_btnClasses裡的每個按鈕 新增滑鼠點擊事件
		lb_i = 0;
		for(JButton btn : P5_btnClasses) {
			final Integer innerMi = lb_i;
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int lb_j=0;
					// 設定點擊後變化的部分
					for(JButton btn_tmp : P5_btnClasses) {
						if(lb_j == innerMi) {
							btn_tmp.setBackground(MyGlobal.colorWhite);
							btn_tmp.setForeground(MyGlobal.colorBlack);
						}
						else {
							btn_tmp.setBackground(MyGlobal.colorBlack);
							btn_tmp.setForeground(MyGlobal.colorWhite);
						}
						lb_j++;
					}
					// 設定要上傳的Class名稱
					uploadClass = articleClassNames[innerMi+1];
					System.out.println(uploadClass);
				}
			});
			lb_i++;
		}
		
		setVisible(true);
		
		// === 初始化 WebSocket 客戶端，用於留言 + 按讚同步 ===
//		client = new Client(this);
		client = null;
	}

	
	// 用於控制BrowserPanel中，顯示文章的部分	
	public void  show_articles() {

		int height =  220;
		//先將原本的articlePanels與刪除按鈕給移除
		for (int i=0; i<P2_articlePanels.size(); ++i) {
			P2_articlePanels.get(i).setVisible(false);
			BrowserPanel.remove(P2_articlePanels.get(i));
		}
		for(int i=0; i<P2_articleDelButtons.size();  ++i) {
			P2_articleDelButtons.get(i).setVisible(false);
			BrowserPanel.remove(P2_articleDelButtons.get(i));
		}
		P2_articleDelButtons.clear();
		P2_articlePanels.clear();

		
		//新增新的articlePanels進去BrowserPanel
		// 找出剩餘的文章數是否超過4個
		int size = articles.size() - ((idxPages-1)*4);
		if(size > 4) size = 4;
		for (int i=0;i<size; ++i) {
			// 取得現在要新增的articles
			int idx_article = (idxPages-1)*4 + i;
			Articles tmpArticle = articles.get(idx_article);
			// 創建新的ArticlePanel
			ArticlePanel tmpPanel = new ArticlePanel(tmpArticle, height + 120 * i);
			tmpPanel.setVisible(true);
			P2_articlePanels.add(tmpPanel);
			BrowserPanel.add(tmpPanel);

			tmpPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					final int idx = idx_article;
					idxNowArticle = idx_article;
					// 首先檢查文章是否還在
					if(!apiClient.checkArticleAlive(articles.get(idxNowArticle).article_ID)) {
						//若不在了，就不做下面的事。
						reset_BrowserPanel();
						P2_lbSystemHint.setText("您欲檢索的文章已被刪除 (ｘ.ｘ)");
						repaint();
						return;
					}
					reset_ArticleReadPanel(idx);
					//若欲閱讀的文章是自己發的，則顯示按鈕，否則不顯示。
					if(articles.get(idx).username.equals(currentUser.username)) {
						P6_btnDel.setVisible(true);
						//點擊按鈕後會刪除文章
						
					}
					else
						P6_btnDel.setVisible(false);
					
					//  創建類別標籤
					

					//設置類別標籤
					P6_btnClass.setBorder(MyGlobal.BorderClassRound);
					P6_btnClass.setBackground(MyGlobal.colorBlue);
					P6_btnClass.setText(articles.get(idx).articleClass);
					P6_btnClass.setForeground(MyGlobal.colorWhite);
					
					switch(articles.get(idx).articleClass) {
						case "遊戲":
							P6_btnClass.setBackground(MyGlobal.colorClassGame);
							break;
						case "新聞":
							P6_btnClass.setBackground(MyGlobal.colorClassNews);
							break;
						case "生活":
							P6_btnClass.setBackground(MyGlobal.colorClassLife);
							break;
					}
					
					cardLayout.show(contentPanel, "ArticleReadPanel");
					
				}
			});
			
		}

		repaint();
	}

	public void reset_LoginPanel() {
		P1_lbSystemHint.setText("歡迎使用，請選擇登入或註冊~");
		P1_textUsername.setText("");
		P1_textPassword.setText("");
		repaint();
		invalidate();
		validate();
	}
	public void reset_BrowserPanel() {

		P2_lbSystemHint.setText("<(=^･^=)>");
		articles = apiClient.getArticles(articleClassNames[selectClass]);
		idxPages = 1;
		totalPages = 1 + (articles.size()-1)/4;
		P2_lbPages.setText(String.format("第%d頁 / 共%d頁" , idxPages, totalPages));
		show_articles();
		repaint();
		invalidate();
		validate();
	}
	
	
	public void reset_SignUpPanel() {
		P3_lbSystemHint.setText("按下按鈕即可註冊。");
		P3_textUsername.setText("");
		P3_textPassword.setText("");
		P3_textNickname.setText("");
		repaint();
		invalidate();
		validate();
	}
	public void reset_SettingPanel() {
		P4_lbSystemHint.setText("此頁面可以更新用戶資訊~");
		P4_lbUsername.setText(currentUser.username);
		P4_textNickname.setText(currentUser.nickname);
		P4_textPassword.setText(currentUser.passwords);
		repaint();
		invalidate();
		validate();
	}
	public void reset_UploadArticalPanel() {
		P5_lbUsername.setText(currentUser.nickname);
		P5_textTitle.setText("");
		P5_textContent.setText("");
		P5_lbSystemHint.setText("此頁面可以上傳文章~");
		// 重置所有class按鈕的外觀
		for(JButton btn : P5_btnClasses) {
			btn.setBackground(MyGlobal.colorBlack);
			btn.setForeground(MyGlobal.colorWhite);
		}
		uploadClass = "";
		repaint();
		invalidate();
		validate();
	}
	
	public void reset_ArticleReadPanel(int idx) {
		
		// 使用批量 API 獲取所有數據（單一 HTTP 請求）
		ApiClient.ArticleDetails details = apiClient.getArticleDetails(articles.get(idx).article_ID);
		if (details == null) {
			// 如果獲取失敗，使用舊的方式作為後備
			if(apiClient.checkLiked(articles.get(idx).article_ID))
				P6_btnLike.setBackground(MyGlobal.colorSky);
			else
				P6_btnLike.setBackground(MyGlobal.colorWhite);
			
			articles.get(idx).likeNumber = apiClient.getLikeNumber(articles.get(idx).article_ID);
			articles.get(idx).commentNumber = apiClient.getCommentNumber(articles.get(idx).article_ID);
			ArrayList<Comments> comments = apiClient.getComments(articles.get(idx).article_ID);
			
			ArticleReadPanel.remove(P6_ArticleScrollPane);
			P6_ArticleScrollPane.removeAll();
			P6_ArticleScrollPane = new JScrollPane();
			P6_ArticleScrollPane.setBorder(MyGlobal.RoundText);
			P6_ArticleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			P6_ArticleScrollPane.setBounds(60, 135, 380, 531);
			P6_ArticleScrollPane.getVerticalScrollBar().setValue(P6_ArticleScrollPane.getVerticalScrollBar().getMinimum());
			P6_lbSystemHint.setText("<對其發送留言吧！>");
			P6_textComment.setText("");
			
			ArticleReadUnitPanel tmpPanel = new ArticleReadUnitPanel(articles.get(idx), comments, 361);
			P6_ArticleScrollPane.setViewportView(tmpPanel);
			ArticleReadPanel.add(P6_ArticleScrollPane);
			return;
		}
		
		// 使用批量 API 返回的數據更新 UI
		if(details.liked)
			P6_btnLike.setBackground(MyGlobal.colorSky);
		else
			P6_btnLike.setBackground(MyGlobal.colorWhite);

		ArticleReadPanel.remove(P6_ArticleScrollPane);
		P6_ArticleScrollPane.removeAll();
		P6_ArticleScrollPane = new JScrollPane();
		P6_ArticleScrollPane.setBorder(MyGlobal.RoundText);
		P6_ArticleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		P6_ArticleScrollPane.setBounds(60, 135, 380, 531);
		P6_ArticleScrollPane.getVerticalScrollBar().setValue(P6_ArticleScrollPane.getVerticalScrollBar().getMinimum());
		P6_lbSystemHint.setText("<對其發送留言吧！>");
		P6_textComment.setText("");

		articles.get(idx).likeNumber = details.likeNumber;
	    articles.get(idx).commentNumber = details.commentNumber;
			    
		ArticleReadUnitPanel tmpPanel = new ArticleReadUnitPanel(articles.get(idx), details.comments, 361);
		P6_ArticleScrollPane.setViewportView(tmpPanel);
		ArticleReadPanel.add(P6_ArticleScrollPane);
	}
}
