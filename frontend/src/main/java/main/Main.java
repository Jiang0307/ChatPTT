package main;

import java.awt.*;

public class Main {


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    frame frame = new frame();
                    frame.setVisible(true);

                    System.out.println("結束");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
