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
        title.setFont(new Font("Times New Roman", Font.BOLD, 92));
        title.setForeground(new Color(0, 120, 255));
        add(title, gbc);
        
        //NEWGAME
        JButton newButton = new JButton("NEW GAME");
        newButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ frame.startGame(false); }
        });
        GamePanel.mouseButton(newButton);
        gbc.gridy++;
        gbc.insets = new Insets(23, 0, 0, 0);
        add(newButton, gbc);
        
        //LOADGAME
        try(Scanner is = new Scanner(new FileInputStream("PlayerLoad.txt"))){
            if(is.hasNextLine()){
                String[] c = is.nextLine().split(",");
                if(c.length == 16){
                    JButton loadButton = new JButton("LOAD GAME");
                    loadButton.addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e){ frame.startGame(true); }
                    });
                    GamePanel.mouseButton(loadButton);
                    gbc.gridy++;
                    gbc.insets = new Insets(23, 0, 0, 0);
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
        
        //EXITGAME
        JButton exitButton = new JButton("EXIT GAME");
        exitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ System.exit(0); }
        });
        GamePanel.mouseButton(exitButton);
        gbc.gridy++;
        gbc.insets = new Insets(46, 0, 0, 0);
        add(exitButton, gbc);
    }
}