package main;
import java.awt.Color;
public class MyGlobal {
	final static String fontname = "微軟正黑體"; 
	
	final static Color colorBlack = new Color(0x20, 0x20, 0x20);
	final static Color colorWhite = new Color(0xFF, 0xFF, 0xFF);
	final static Color colorGray = new Color(175, 175, 175);
	final static Color colorCommentTitle = new Color(210, 210, 210);
	final static Color colorBlue = new Color(189, 223, 255);
	final static Color colorSky = new Color(0xBD, 0xDF, 0xFF);
	final static Color colorZero  = new Color(0,0,0,0);
	final static Color colorClassGame  = new Color(0xB1,0x21,0x21);
	final static Color colorClassLife  = new Color(0x29,0x86,0x08);
	final static Color colorClassNews  = new Color(0x10,0x5C,0xA3);
	final static RoundedBorder BorderRound = new RoundedBorder(colorBlack, 5 ,25);
	final static RoundedBorder BorderClassRound = new RoundedBorder(colorBlack, 4 ,20);
	final static RoundedBorder RoundText = new RoundedBorder(colorBlack, 1 ,2);
}
