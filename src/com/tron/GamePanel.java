package com.tron;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    
    private static final int gridSize = 42;
    private static final int cellSize = 23;
    private static final int playerDeep = 45;
    private static final int EnemyDeep = 140;
    
    private boolean invincible;
    private String levelLoad;
    private String winTitle;
    private String loseTitle;
    private String[] winStory;
    private String[] winButton;
    private String[] loseStory;
    private String[] loseButton;
    
    private MainFrame frame;
    private GameState gameState = GameState.Running;
    private Random rd = new Random();
    private Timer timer;
    
    private Player player;
    private ArrayList<Disc> discList;
    private ArrayList<Enemy> enemyList;
    private boolean[][] trail = new boolean[gridSize][gridSize];
    private boolean[][] map = new boolean[gridSize][gridSize];
    
    public GamePanel(MainFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
        setBackground(Color.BLACK);
        setFocusable(true);
        setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.WHITE, cellSize));
        
        //Initial Player
        player = null;
        //Initial Enemy
        enemyList = new ArrayList<>();
        //Initial Disc
        discList = new ArrayList<>();
        //Initial Trail
        for(int i = 0; i < trail.length; i++){
            for(int j = 0; j < trail[i].length; j++){
                trail[i][j] = false;
            }
        }
        
        //KeyboardListener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameState != GameState.Running) return;
                if (player == null) return;
                int f;
                switch (player.getHandling()){
                    case Balanced: f = 6; break;
                    case Smooth: f = 10;break;
                    default: f = 0;
                }
                if(rd.nextInt(500) >= (10 - Math.min(f, player.getPrecision()))){
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W: player.setDx(0); player.setDy(-1); break;
                        case KeyEvent.VK_S: player.setDx(0); player.setDy(1); break;
                        case KeyEvent.VK_A: player.setDx(-1); player.setDy(0); break;
                        case KeyEvent.VK_D: player.setDx(1); player.setDy(0); break;
                        case KeyEvent.VK_SPACE: player.shoot(player, enemyList, discList, gridSize, trail); break;
                    }
                }
            }
        });
        
        //Timer100ms
        timer = new Timer(100, e -> gameLoop());
        timer.stop();
    }
    
    private void gameLoop() {
        
        if(player == null)return;
        
        //DiscMove
        for(Disc e : discList) { e.move(player, enemyList, discList, gridSize, trail); }
        //PlayerMove
        player.move(player, enemyList, discList, gridSize, trail, playerDeep); TrailRefresh();
        //EnemyMove
        for(Enemy e : enemyList) { e.move(player, enemyList, discList, gridSize, trail, Math.min(80, EnemyDeep / Math.max(1, enemyList.size()) + 1)); TrailRefresh(); }
        
        repaint();
        
        //PlayerRemove
        if (player.getLives() <= 0 && gameState == GameState.Running) {
            timer.stop();
            gameState = GameState.GameOver;
            showGameOverOverlay(loseTitle, loseStory, loseButton, false);
        }
        //EnemyRemove
        ArrayList<Enemy> enemyRemove = new ArrayList<>();
        for(Enemy e : enemyList) {
            if(e.getLives() <= 0) {
                player.addXp(e.getXp());
                enemyRemove.add(e);
            }
        }
        enemyList.removeAll(enemyRemove);
        if (enemyList.size() <= 0) {
            for(Disc e : discList){ player.addDisc(1); }
            player.addLevel(1);
            timer.stop();
            gameState = GameState.GameOver;
            player.playerSave();
            showGameOverOverlay(winTitle, winStory, winButton, true); }
        //DiscRemove
        ArrayList<Disc> discRemove = new ArrayList<>();
        for(Disc e : discList) {
            if(!e.getAlive()) discRemove.add(e);
        }
        discList.removeAll(discRemove);
        //DiscPublic
        for(Disc e : discList) {
            boolean discPublic = true;
            for(Enemy f : enemyList) { if(e.getColor().equals(f.getColor())) discPublic = false; }
            if(e.getColor().equals(player.getColor())) discPublic = false;
            if(discPublic) e.discPublic();
        }
        
        
        if(rd.nextDouble()*100 < Math.max(20, 100.0 / (player.getSpeed() + 20) * 20)){
            //DiscCool
            player.coolDisc();
            for(Enemy e : enemyList) { e.coolDisc(); }
            //TrailCool
            player.coolTrail();
            for(Enemy e : enemyList) { e.coolTrail(); }
            //InvincibleCool;
            player.coolInvincible();
            for(Enemy e : enemyList) { e.coolInvincible(); }
        }
        
        //PlayerXp
        if(player.getXp() >= player.getXpUp()){
            player.levelUp();
            timer.setDelay(Math.max(20, 100 / (player.getSpeed() + 20) * 20));
        }
    }
    
    public void TrailRefresh(){
        //TrailRefresh
        for(int i = 0; i < trail.length; i++){
            for(int j = 0; j < trail[i].length; j++){
                trail[i][j] = map[i][j];
            }
        }
        for(int i = 0; i < trail.length; i++){
            for(int j = 0; j < trail[i].length; j++){
                if(player.getTrail(i, j) > 0) trail[i][j] = true;
            }
        }
        for(Enemy e : enemyList){
            for(int i = 0; i < trail.length; i++){
                for(int j = 0; j < trail[i].length; j++){
                    if(e.getTrail(i, j) > 0) trail[i][j] = true;
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        if (gameState == GameState.GameOver) return;
        
        //DrawExcel
        g.setColor(Color.DARK_GRAY);
        for (int i = 2; i <= gridSize-2; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize, gridSize * cellSize);
            g.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize);
        }
        
        //DrawTrail
        for(Enemy e : enemyList) {
            for(int i = 0; i < trail.length; i++) {
                for(int j = 0; j < trail[i].length; j++){
                    if(e.getTrail(i, j) > 0){
                        g.setColor(e.getColor().darker().darker());
                        g.fillRect(i*cellSize, j*cellSize, cellSize, cellSize);
                    }
                }
            }
        }
        for(Enemy e : enemyList) {
            for(int i = 0; i < trail.length; i++) {
                for(int j = 0; j < trail[i].length; j++){
                    if(e.getTrail(i, j) > 10){
                        g.setColor(e.getColor().darker());
                        g.fillRect(i*cellSize, j*cellSize, cellSize, cellSize);
                    }
                }
            }
        }
        for(int i = 0; i < trail.length; i++) {
            for(int j = 0; j < trail[i].length; j++){
                if(player.getTrail(i, j) > 0){
                    g.setColor(player.getColor().darker().darker());
                    g.fillRect(i*cellSize, j*cellSize, cellSize, cellSize);
                }
            }
        }
        for(int i = 0; i < trail.length; i++) {
            for(int j = 0; j < trail[i].length; j++){
                if(player.getTrail(i, j) > 10){
                    g.setColor(player.getColor().darker());
                    g.fillRect(i*cellSize, j*cellSize, cellSize, cellSize);
                }
            }
        }
        for(int i = 0; i < trail.length; i++) {
            for(int j = 0; j < trail[i].length; j++){
                if(map[i][j]){
                    g.setColor(Color.WHITE);
                    g.fillRect(i*cellSize, j*cellSize, cellSize, cellSize);
                }
            }
        }
        
        //Player
        if(invincible){ invincible = false; }else{invincible = true;}
        if(!player.getInvincible() || invincible) {
            g.setColor(player.getColor());
            g.fillRect(player.getX()*cellSize, player.getY()*cellSize, cellSize, cellSize);
            if(player.getDiscOwned() > 0){
                if(player.getDiscCool() <= 0){
                    g.setColor(Color.WHITE);
                    g.drawOval(player.getX()*cellSize + 3, player.getY()*cellSize + 3, cellSize - 6, cellSize -6);
                    g.setColor(player.getColor().darker().darker());
                    g.drawOval(player.getX()*cellSize + 4, player.getY()*cellSize + 4, cellSize - 8, cellSize -8);
                    g.drawOval(player.getX()*cellSize + 5, player.getY()*cellSize + 5, cellSize - 10, cellSize -10);
                    g.drawOval(player.getX()*cellSize + 6, player.getY()*cellSize + 6, cellSize - 12, cellSize -12);
                    g.setColor(player.getColor().brighter());
                    g.drawOval(player.getX()*cellSize + 7, player.getY()*cellSize + 7, cellSize - 14, cellSize -14);
                    g.setColor(player.getColor().darker());
                    g.drawOval(player.getX()*cellSize + 8, player.getY()*cellSize + 8, cellSize - 16, cellSize -16);
                    g.drawOval(player.getX()*cellSize + 9, player.getY()*cellSize + 9, cellSize - 18, cellSize -18);
                }else{
                    g.setColor(player.getColor().darker().darker());
                    g.drawOval(player.getX()*cellSize + 3, player.getY()*cellSize + 3, cellSize - 6, cellSize -6);
                    g.drawOval(player.getX()*cellSize + 4, player.getY()*cellSize + 4, cellSize - 8, cellSize -8);
                    g.drawOval(player.getX()*cellSize + 5, player.getY()*cellSize + 5, cellSize - 10, cellSize -10);
                    g.setColor(player.getColor().brighter());
                    g.drawOval(player.getX()*cellSize + 6, player.getY()*cellSize + 6, cellSize - 12, cellSize -12);
                    g.setColor(player.getColor().darker());
                    g.drawOval(player.getX()*cellSize + 7, player.getY()*cellSize + 7, cellSize - 14, cellSize -14);
                }
            }
        }
        
        //Enemy
        for(Enemy e : enemyList) {
            g.setColor(e.getColor());
            g.fillRect(e.getX()*cellSize, e.getY()*cellSize, cellSize, cellSize);
            if(e.getDiscOwned() > 0){
                if(e.getDiscCool() <= 0){
                    g.setColor(Color.WHITE);
                    g.drawOval(e.getX()*cellSize + 3, e.getY()*cellSize + 3, cellSize - 6, cellSize -6);
                    g.setColor(e.getColor().darker().darker());
                    g.drawOval(e.getX()*cellSize + 4, e.getY()*cellSize + 4, cellSize - 8, cellSize -8);
                    g.drawOval(e.getX()*cellSize + 5, e.getY()*cellSize + 5, cellSize - 10, cellSize -10);
                    g.drawOval(e.getX()*cellSize + 6, e.getY()*cellSize + 6, cellSize - 12, cellSize -12);
                    g.setColor(e.getColor().brighter());
                    g.drawOval(e.getX()*cellSize + 7, e.getY()*cellSize + 7, cellSize - 14, cellSize -14);
                    g.setColor(e.getColor().darker());
                    g.drawOval(e.getX()*cellSize + 8, e.getY()*cellSize + 8, cellSize - 16, cellSize -16);
                    g.drawOval(e.getX()*cellSize + 9, e.getY()*cellSize + 9, cellSize - 18, cellSize -18);
                }else{
                    g.setColor(e.getColor().darker().darker());
                    g.drawOval(e.getX()*cellSize + 3, e.getY()*cellSize + 3, cellSize - 6, cellSize -6);
                    g.drawOval(e.getX()*cellSize + 4, e.getY()*cellSize + 4, cellSize - 8, cellSize -8);
                    g.drawOval(e.getX()*cellSize + 5, e.getY()*cellSize + 5, cellSize - 10, cellSize -10);
                    g.setColor(e.getColor().brighter());
                    g.drawOval(e.getX()*cellSize + 6, e.getY()*cellSize + 6, cellSize - 12, cellSize -12);
                    g.setColor(e.getColor().darker());
                    g.drawOval(e.getX()*cellSize + 7, e.getY()*cellSize + 7, cellSize - 14, cellSize -14);
                }
            }
        }
        
        //Disc
        for(Disc e : discList) {
            if(e.getAlive()){
                if(e.getLives() > 0){
                    g.setColor(Color.WHITE);
                    g.drawOval(e.getX()*cellSize + 1, e.getY()*cellSize + 1, cellSize - 2, cellSize -2);
                    g.drawOval(e.getX()*cellSize + 2, e.getY()*cellSize + 2, cellSize - 4, cellSize -4);
                    g.setColor(e.getColor().darker().darker());
                    g.drawOval(e.getX()*cellSize + 3, e.getY()*cellSize + 3, cellSize - 6, cellSize -6);
                    g.drawOval(e.getX()*cellSize + 4, e.getY()*cellSize + 4, cellSize - 8, cellSize -8);
                    g.drawOval(e.getX()*cellSize + 5, e.getY()*cellSize + 5, cellSize - 10, cellSize -10);
                    g.setColor(e.getColor().brighter());
                    g.drawOval(e.getX()*cellSize + 6, e.getY()*cellSize + 6, cellSize - 12, cellSize -12);
                    g.drawOval(e.getX()*cellSize + 7, e.getY()*cellSize + 7, cellSize - 14, cellSize -14);
                }else{
                    g.setColor(e.getColor().darker().darker());
                    g.drawOval(e.getX()*cellSize + 2, e.getY()*cellSize + 2, cellSize - 4, cellSize -4);
                    g.drawOval(e.getX()*cellSize + 3, e.getY()*cellSize + 3, cellSize - 6, cellSize -6);
                    g.drawOval(e.getX()*cellSize + 4, e.getY()*cellSize + 4, cellSize - 8, cellSize -8);
                    g.drawOval(e.getX()*cellSize + 7, e.getY()*cellSize + 7, cellSize - 14, cellSize -14);
                    g.drawOval(e.getX()*cellSize + 8, e.getY()*cellSize + 8, cellSize - 16, cellSize -16);
                    if(Color.BLACK.equals(e.getColor())){
                        g.setColor(Color.WHITE);
                    }else{ g.setColor(e.getColor().brighter()); }
                    g.drawOval(e.getX()*cellSize + 5, e.getY()*cellSize + 5, cellSize - 10, cellSize -10);
                    g.drawOval(e.getX()*cellSize + 6, e.getY()*cellSize + 6, cellSize - 12, cellSize -12);
                }
            }
        }

        // HUD
        g.setColor(player.getColor().darker());
        g.setFont(new Font("Arial", Font.BOLD, cellSize));
        g.drawString(
            "Lives: " + Math.max(0, player.getLives()) + 
            "  XP: " + player.getXp() + 
            "  Level: " + player.getLevel()
            , cellSize, 2 * cellSize);
        g.drawString(
            "Speed: " + player.getSpeed() +
            "  Discs: " + player.getDiscOwned()
            , cellSize, 3 * cellSize);
    }
    
    private void showGameOverOverlay(String title, String[] story, String[] button, boolean loadLevel) {
        
        setLayout(null);
        
        //box
        JPanel box = new JPanel();
        box.setBackground(new Color(0, 0, 0, 0));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        
        //TitleLabel
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 2 * cellSize));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(titleLabel);
        
        //NextButton
        JButton[] nextButton = new JButton[button.length];
        for(int i = 0; i < button.length; i++){
            nextButton[i] = new JButton(button[i]);
            final int tree = i;
            nextButton[i].addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){ levelSave(story[tree]); restartGame(loadLevel); }
            });
            mouseButton(nextButton[i]);
            nextButton[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(Box.createVerticalStrut(cellSize));
            box.add(nextButton[i]);
        }
        
        //MenuButton
        JButton menuButton = new JButton("Back To Menu");
        menuButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ levelSave(story[0]); backToMenu(); }
        });
        mouseButton(menuButton);
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(Box.createVerticalStrut(2 * cellSize));
        box.add(menuButton);
        
        //overlay
        JPanel overlay = new JPanel();
        overlay.setBounds(0, 0, getPreferredSize().width, getPreferredSize().height);
        overlay.setBackground(new Color(0, 120, 255, 180));
        overlay.setLayout(new GridBagLayout());
        overlay.add(box);
        add(overlay);
        
        revalidate();
        repaint();
    }
    
    public void restartGame(boolean loadLevel) {
        removeAll();
        
        invincible = false;
        
        boolean error = false;
        
        //LevelLoad
        error = false;
        levelLoad = "A";
        if(loadLevel){
            try(Scanner is = new Scanner(new FileInputStream("LevelLoad.txt"))){
                if(is.hasNextLine()){
                    levelLoad = is.nextLine();
                }
            }catch(FileNotFoundException e){
                System.out.println("LevelLoad.txt: File was not found");
                error = true;
            }catch(IOException e){
                System.out.println("LevelLoad.txt: Error reading from file");
                error = true;
            }catch(Exception e){
                System.out.println("LevelLoad.txt: Error");
                error = true;
            }
        }
        if(error){
            levelLoad = "A";
        }
        
        //Level
        error = false;
        boolean loadPlayer = false;
        boolean Tron = true;
        int Map = 1;
        String[] enemy = {"Koura","Koura","Koura","Koura","Koura","Koura","Koura"};
        try(Scanner is = new Scanner(new FileInputStream("Level.txt"))){
            while(is.hasNextLine()){
                String[] c = is.nextLine().split("_");
                if(c[0].equals(levelLoad)){
                    switch(c[1]){
                        case"load": loadPlayer = true; break;
                        case"new": loadPlayer = false; break;
                        default: loadPlayer = false; break;
                    }
                    switch(c[2]){
                        case"Tron": Tron = true; break;
                        case"Kevin": Tron = false; break;
                        default: Tron = true; break;
                    }
                    switch(c[3]){
                        case"Map1": Map = 1; break;
                        case"Map2": Map = 2; break;
                        case"Map3": Map = 3; break;
                        case"MapRandom": Map = 4; break;
                        default: Map = 1; break;
                    }
                    for(int i = 0; i < 7; i++){
                        enemy[i] = c[i+4];
                    }
                    break;
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("Level.txt: File was not found");
            error = true;
        }catch(IOException e){
            System.out.println("Level.txt: Error reading from file");
            error = true;
        }catch(Exception e){
            System.out.println("Level.txt: Error");
            error = true;
        }
        if(error){
            loadPlayer = false;
            Tron = true;
            Map = 1;
            for(int i = 0; i < 7; i++){
                enemy[i] = "Koura";
            }
        }
        
        //StoryTree
        error = false;
        winTitle = "Win!";
        loseTitle = "Game Over!";
        winStory = new String[1];
        winButton = new String[1];
        winStory[0] = "A";
        winButton[0] = "Proceed";
        loseStory = new String[1];
        loseButton = new String[1];
        loseStory[0] = "A";
        loseButton[0] = "Again";
        try(Scanner is = new Scanner(new FileInputStream("StoryTree.txt"))){
            while(is.hasNextLine()){
                String[] c = is.nextLine().split("_");
                if(c[0].equals(levelLoad)){
                    winTitle = c[1];
                    loseTitle = c[2];
                    winStory = new String[(c.length - 5) / 2];
                    winButton = new String[(c.length - 5) / 2];
                    for(int i = 0; i < (c.length - 5) / 2; i++){
                        winStory[i] = c[2 * i + 3];
                        winButton[i] = c[2 * i + 4];
                    }
                    loseStory = new String[1];
                    loseButton = new String[1];
                    loseStory[0] = c[c.length - 2];
                    loseButton[0] = c[c.length - 1];
                    break;
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("Level.txt: File was not found");
            error = true;
        }catch(IOException e){
            System.out.println("Level.txt: Error reading from file");
            error = true;
        }catch(Exception e){
            System.out.println("Level.txt: Error");
            error = true;
        }
        if(error){
            winTitle = "Win!";
            loseTitle = "Game Over!";
            winStory = new String[1];
            winButton = new String[1];
            winStory[0] = "A";
            winButton[0] = "Proceed";
            loseStory = new String[1];
            loseButton = new String[1];
            loseStory[0] = "A";
            loseButton[0] = "Again";
        }
        
        //Initial Player
        if(loadPlayer){
            player = new PlayerLoad(gridSize);
        }else if(Tron){
            player = new Tron(gridSize);
        }else{
            player = new Kevin(gridSize);
        }
        player.addLives(player.getSpeed());
        playerClear();
        levelClear();
        
        //Initial Map
        switch(Map){
            case 1: Map("Map1"); break;
            case 2: Map("Map2"); break;
            case 3: Map("Map3"); break;
            case 4: Map("MapRandom"); break;
            default: Map("Map1"); break;
        }
        TrailRefresh();
        
        //Initial Enemy
        enemyList.clear();
        for(int i = 0; i < 7; i++){
            switch(enemy[i]){
                case "Clu": enemyList.add(new Clu(player, enemyList, discList, gridSize, trail)); break;
                case "Rinzler": enemyList.add(new Rinzler(player, enemyList, discList, gridSize, trail)); break;
                case "Sark": enemyList.add(new Sark(player, enemyList, discList, gridSize, trail)); break;
                case "Koura": enemyList.add(new Koura(player, enemyList, discList, gridSize, trail)); break;
                default: enemyList.add(new Koura(player, enemyList, discList, gridSize, trail)); break;
            }
        }
        
        //Initial Disc
        discList.clear();
        
        gameState = GameState.Running;
        timer.start();
        requestFocusInWindow();
        revalidate();
        repaint();
        
        timer.setDelay(Math.max(20, 100 / (player.getSpeed() + 20) * 20));
    }
    
    private void backToMenu() {
        frame.showMenu();
    }
    
    public void levelSave(String story){
        try(PrintWriter os = new PrintWriter(new FileOutputStream("LevelLoad.txt"))){
            os.print(story);
        }catch(FileNotFoundException e){
            System.out.println("PlayerLoad.txt: File was not found");
        }catch(IOException e){
            System.out.println("PlayerLoad.txt: Error writing to file");
        }
    }
    
    public void playerClear(){
        try(PrintWriter os = new PrintWriter(new FileOutputStream("PlayerLoad.txt"))){
            os.print("");
        }catch(FileNotFoundException e){
            System.out.println("PlayerLoad.txt: File was not found");
        }catch(IOException e){
            System.out.println("PlayerLoad.txt: Error writing to file");
        }
    }
    
    public void levelClear(){
        try(PrintWriter os = new PrintWriter(new FileOutputStream("LevelLoad.txt"))){
            os.print("");
        }catch(FileNotFoundException e){
            System.out.println("PlayerLoad.txt: File was not found");
        }catch(IOException e){
            System.out.println("PlayerLoad.txt: Error writing to file");
        }
    }
    
    public void Map (String id){
        boolean error = false;
        try(Scanner is = new Scanner(new FileInputStream("Map.txt"))){
            while(is.hasNextLine()){
                String b = is.nextLine();
                if(b.equals(id)){
                    for(int i = 0; i < gridSize; i++){
                        String[] c = is.nextLine().split(",");
                        for(int j = 0; j < gridSize; j++){
                            map[i][j] = false;
                            if(c[j].equals("1")) map[i][j] = true;
                        }
                    }
                }
                break;
            }
        }catch(FileNotFoundException e){
            System.out.println("Map.txt: File was not found");
            error = true;
        }catch(IOException e){
            System.out.println("Map.txt: Error reading from file");
            error = true;
        }catch(Exception e){
            System.out.println("Map.txt: Error");
            error = true;
        }
        if(error){
            for(int i = 0; i < gridSize; i++){
                for(int j = 0; j < gridSize; j++){
                    map[i][j] = false;
                }
            }
        }
    }
    
    public static void mouseButton(JButton button){
        button.setFont(new Font("Times New Roman", Font.BOLD, 2 * cellSize));
        button.setPreferredSize(new Dimension(500, 70));
        button.setMaximumSize(new Dimension(500, 70));
        button.setMinimumSize(new Dimension(500, 70));
        button.setBackground(new Color(0, 120, 255).brighter());
        button.setForeground(Color.BLACK);
        
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 120, 255).darker());
                button.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 120, 255).brighter());
                button.setForeground(Color.BLACK);
            }
        });
    }
}