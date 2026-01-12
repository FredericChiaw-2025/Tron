package com.tron;
import javax.swing.*;
import java.awt.*;
public class MainFrame extends JFrame {
    
    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);
    
    StartMenuPanel startMenuPanel;
    GamePanel gamePanel;
    
    public MainFrame() {
        setLayout(new BorderLayout());
        setTitle("TRON");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.BLACK);
        
        //Top
        JLabel title = new JLabel("***********************TRON***********************", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(title, BorderLayout.NORTH);
        
        startMenuPanel = new StartMenuPanel(this);
        gamePanel = new GamePanel(this);
        
        mainPanel.add(startMenuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        
        add(mainPanel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void showMenu() {
        mainPanel.remove(startMenuPanel);
        startMenuPanel = new StartMenuPanel(this);
        mainPanel.add(startMenuPanel, "MENU");
        cardLayout.show(mainPanel, "MENU");
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    public void startGame(boolean loadLevel) {
        gamePanel.restartGame(loadLevel);
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
    }
}