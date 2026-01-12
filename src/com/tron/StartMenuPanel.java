package com.tron;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
public class StartMenuPanel extends JPanel {
    
    public StartMenuPanel(MainFrame frame) {
        setPreferredSize(new Dimension(630, 630));
        setBackground(new Color(0, 120, 255).darker().darker().darker());
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        
        //Title
        JLabel title = new JLabel("TRON");
        title.setFont(new Font("Times New Roman", Font.BOLD, 48));
        title.setForeground(new Color(0, 120, 255));
        add(title, gbc);
        //NEWGAME
        JButton newButton = new JButton("NEW GAME");
        GamePanel.mouseButton(newButton);
        newButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ frame.startGame(false); }
        });
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 0, 0);
        add(newButton, gbc);
        //LOADGAME
        try(Scanner is = new Scanner(new FileInputStream("PlayerLoad.txt"))){
            if(is.hasNextLine()){
                String[] c = is.nextLine().split(",");
                if(c.length == 16){
                    JButton loadButton = new JButton("LOAD GAME");
                    GamePanel.mouseButton(loadButton);
                    loadButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
                    loadButton.addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e){ frame.startGame(true); }
                    });
                    gbc.gridy++;
                    gbc.insets = new Insets(15, 0, 0, 0);
                    add(loadButton, gbc);
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("PlayerLoad.txt: File was not found");
        }catch(IOException e){
            System.out.println("PlayerLoad.txt: Error reading from file");
        }catch(Exception e){
            System.out.println("PlayerLoad.txt: Error");
        }
        JButton exitButton = new JButton("EXIT GAME");
        GamePanel.mouseButton(exitButton);
        exitButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        exitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ System.exit(0); }
        });
        gbc.gridy++;
        gbc.insets = new Insets(30, 0, 0, 0);
        add(exitButton, gbc);
    }
}