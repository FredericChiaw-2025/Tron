package com.tron;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Color;
abstract class Player {
    private String name;
    private Color color;
    private int xp;
    private int speed;
    private Handling handling;
    private double lives;
    private int x, y;
    private int dx, dy;
    private int[][] trail;
    private int discOwned;
    private int discRange;
    private int discCool;
    private int level;
    private int stability, precision;
    private int xpUp, speedUp, discUp, stabilityUp, precisionUp;
    private int invincible;
    
    Random rd = new Random();
    
    public Player (String id, int gridSize){
        try(Scanner is = new Scanner(new FileInputStream("Character.txt"))){
            while(is.hasNextLine()){
                String[] c = is.nextLine().split(",");
                if(c[0].equals(id)){
                    this.name = c[0];
                    switch(c[1]) {
                        case "TronBLUE": this.color = new Color(0, 120, 255); break;
                        case "WHITE": this.color = Color.WHITE; break;
                        default: this.color = new Color(0, 120, 255); break;
                    }
                    this.xp = Integer.parseInt(c[2]);
                    this.speed = Integer.parseInt(c[3]);
                    switch(c[4]){
                        case "Balanced": this.handling = Handling.Balanced; break;
                        case "Smooth": this.handling = Handling.Smooth; break;
                        default: this.handling = Handling.Balanced; break;
                    }
                    this.lives = Double.parseDouble(c[5]);
                    this.x = 20; this.y = 31; this.dx = 0; this.dy = -1;
                    this.trail = new int[gridSize][gridSize];
                    this.discOwned = Integer.parseInt(c[6]);
                    this.discRange = Integer.parseInt(c[7]);
                    this.level = Integer.parseInt(c[8]);
                    this.stability = Integer.parseInt(c[9]);
                    this.precision = Integer.parseInt(c[10]);
                    this.xpUp = Integer.parseInt(c[11]);
                    this.speedUp = Integer.parseInt(c[12]);
                    this.discUp = Integer.parseInt(c[13]);
                    this.stabilityUp = Integer.parseInt(c[14]);
                    this.precisionUp = Integer.parseInt(c[15]);
                    this.invincible = 1;
                    if(this.xp < 0 || this.speed < 0 || this.lives < 0 || this.discOwned < 0 || this.discRange < 0 ||
                            this.level < 0 || this.stability < 0 || this.precision < 0 || this.xpUp < 0 ||
                            this.speedUp < 0 || this.discUp < 0 || this.stabilityUp < 0 || this.precisionUp < 0)
                        throw new Exception();
                    break;
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("PlayerLoad.txt: File was not found");
            playerTron(gridSize);
        }catch(IOException e){
            System.out.println("PlayerLoad.txt: Error reading from file");
            playerTron(gridSize);
        }catch(Exception e){
            System.out.println("PlayerLoad.txt: Error");
            playerTron(gridSize);
        }
    }
    public Player (int gridSize){
        try(Scanner is = new Scanner(new FileInputStream("PlayerLoad.txt"))){
            while(is.hasNextLine()){
                String[] c = is.nextLine().split(",");
                this.name = c[0];
                switch(c[1]) {
                    case "TronBlue": this.color = new Color(0, 120, 255); break;
                    case "WHITE": this.color = Color.WHITE; break;
                    default: this.color = new Color(0, 120, 255); break;
                }
                this.xp = Integer.parseInt(c[2]);
                this.speed = Integer.parseInt(c[3]);
                switch(c[4]){
                    case "Balanced": handling = Handling.Balanced; break;
                    case "Smooth": handling = Handling.Smooth; break;
                    default: handling = Handling.Balanced; break;
                }
                this.lives = Double.parseDouble(c[5]);
                this.x = 20; this.y = 31; this.dx = 0; this.dy = -1;
                this.trail = new int[gridSize][gridSize];
                this.discOwned = Integer.parseInt(c[6]);
                this.discRange = Integer.parseInt(c[7]);
                this.level = Integer.parseInt(c[8]);
                this.stability = Integer.parseInt(c[9]);
                this.precision = Integer.parseInt(c[10]);
                this.xpUp = Integer.parseInt(c[11]);
                this.speedUp = Integer.parseInt(c[12]);
                this.discUp = Integer.parseInt(c[13]);
                this.stabilityUp = Integer.parseInt(c[14]);
                this.precisionUp = Integer.parseInt(c[15]);
                this.invincible = 1;
                if(this.xp < 0 || this.speed < 0 || this.lives < 0 || this.discOwned < 0 || this.discRange < 0 ||
                        this.level < 0 || this.stability < 0 || this.precision < 0 || this.xpUp < 0 ||
                        this.speedUp < 0 || this.discUp < 0 || this.stabilityUp < 0 || this.precisionUp < 0)
                    throw new Exception();
                break;
            }
        }catch(FileNotFoundException e){
            System.out.println("PlayerLoad.txt: File was not found");
            playerTron(gridSize);
        }catch(IOException e){
            System.out.println("PlayerLoad.txt: Error reading from file");
            playerTron(gridSize);
        }catch(Exception e){
            System.out.println("PlayerLoad.txt: Error");
            playerTron(gridSize);
        }
    }
    public void playerSave(){
        try(PrintWriter os = new PrintWriter(new FileOutputStream("PlayerLoad.txt"))){
            String[] c = new String[16];
            c[0] = name;
            if(color.equals(new Color(0, 120, 255))){
                c[1] = "TronBLUE";
            }else if(color.equals(Color.WHITE)){
                c[1] = "WHITE";
            }else{ c[1] = "TronBLUE"; }
            c[2] = Integer.toString(xp);
            c[3] = Integer.toString(speed);
            switch(handling){
                case Balanced: c[4] = "Balanced"; break;
                case Smooth: c[4] = "Smooth"; break;
                default: c[4] = "Balanced"; break;
            }
            c[5] = Double.toString(lives);
            c[6] = Integer.toString(discOwned);
            c[7] = Integer.toString(discRange);
            c[8] = Integer.toString(level);
            c[9] = Integer.toString(stability);
            c[10] = Integer.toString(precision);
            c[11] = Integer.toString(xpUp);
            c[12] = Integer.toString(speedUp);
            c[13] = Integer.toString(discUp);
            c[14] = Integer.toString(stabilityUp);
            c[15] = Integer.toString(precisionUp);
            os.print(c[0] +","+ c[1] +","+ c[2] +","+ c[3] +","+ c[4] +","+ c[5] +","+ c[6] +","+ c[7] +","+
                    c[8] +","+ c[9] +","+ c[10] +","+ c[11] +","+ c[12] +","+ c[13] +","+ c[14] +","+ c[15]);
        }catch(FileNotFoundException e){
            System.out.println("PlayerLoad.txt: File was not found");
        }catch(IOException e){
            System.out.println("PlayerLoad.txt: Error writing to file");
        }catch(Exception e){
            System.out.println("PlayerLoad.txt: Error");
        }
    }
    private void playerTron(int gridSize){
        this.name = "Tron";
        this.color = new Color(0, 120, 255);
        this.xp = 0;
        this.speed = 0;
        this.handling = Handling.Balanced;
        this.lives = 5;
        this.x = 20; y = 31; dx = 0; dy = -1;
        this.trail = new int[gridSize][gridSize];
        this.discOwned = 1;
        this.discRange = 0;
        this.level = 0;
        this.stability = 0;
        this.precision = 0;
        this.xpUp = 200;
        this.speedUp = 2;
        this.discUp = 0;
        this.stabilityUp = 2;
        this.precisionUp = 1;
        this.invincible = 1;
    }
    
    //Getter
    public Color getColor() { return color; }
    public int getXp() { return xp; }
    public int getSpeed() { return speed; }
    public Handling getHandling() { return handling; }
    public double getLives() { return lives; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public int getTrail(int x, int y) { return trail[x][y]; }
    public int getDiscOwned() { return discOwned; }
    public int getDiscCool() { return discCool; }
    public int getLevel() { return level; }
    public int getPrecision(){ return precision; }
    public int getXpUp(){ return xpUp; }
    public boolean getInvincible(){ return invincible > 0; }
    
    //Setter
    public void setDx(int dx) { this.dx = dx; }
    public void setDy(int dy) { this.dy = dy; }
    private void setTrail(int Deep) { trail[x][y] = Deep; }
    private void setInvincible() { invincible=10; }
    
    //Method
    public void shoot(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail) {
        //PlayerDisc
        if(discOwned > 0 && discCool <= 0){
            Disc disc = new Disc(color, speed, x, y, dx, dy, discRange, player, enemyList, discList, gridSize, trail);
            discList.add(disc);
            discOwned--;
            discCool = 50;
        }
    }
    public void move(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail, int deep) {
        //PlayerMove
        setTrail(deep);
        if(!player.getInvincible()) { player.addXp(1); }
        if(lives > 0){
            if(x + dx >= 1 && x + dx <= gridSize - 2 && y + dy >= 1 && y + dy <= gridSize - 2) {
                if(!trail[x + dx][y + dy]){
                    x += dx; y += dy;
                    for(Disc e : discList){
                        if(e.getAlive() && e.getLives() > 0 && !(Color.BLACK.equals(e.getColor()) || e.getColor().equals(color)) && lives > 0 && e.getX() == x && e.getY() == y){ hurt(1); e.setLives(0); }
                        if(e.getAlive() && lives > 0 && (Color.BLACK.equals(e.getColor()) || e.getColor().equals(color)) && e.getX() == x && e.getY() == y){ addDisc(1); e.setAlive(false); }
                    }
                    for(Enemy e : enemyList){
                        if(e.getLives() > 0 && lives > 0 && e.getX() == x && e.getY() == y){ hurt(1); e.hurt(player, enemyList, discList, gridSize, trail, 1); }
                    }
                }else{ hurt(0.5); }
            }else{ lives = -999; }
        }
        stability();
    }
    private void stability(){
        //PlayerStability
        int e;
        switch(handling){
            case Balanced: e = 6; break;
            case Smooth: e = 10;break;
            default: e = 0;
        }
        if(rd.nextInt(5000) < (10 - Math.min(e, stability))){
            if(dx == 0){
                dy = 0; dx = rd.nextBoolean()? 1 : -1;
            }else{
                dx = 0; dy = rd.nextBoolean()? 1 : -1;
            }
        }
    }
    public void hurt(double hurt){
        if(!getInvincible()){
            lives -= hurt;
            setInvincible();
        }
    }
    public void addLives(int lives) { this.lives += lives; }
    public void addDisc(int discOwned) { this.discOwned += discOwned; }
    public void addXp(int xp) { this.xp += xp; }
    public void addLevel(int level) {
        for(int i = 1; i <= level; i++){
            this.level++;
            if(level % 10 == 0) lives += 1;
            speed += speedUp;
            if(level % 15 == 0) { discOwned += discUp; discRange += 3; }
            stability += stabilityUp;
            precision += precisionUp;
        }
    }
    public void levelUp() {
        while(xp >= xpUp){
            xp -= xpUp;
            if(level >= 10) xpUp = 1000;
            if(level < 99) addLevel(1);
        }
    }
    
    //PlayerCool
    public void coolTrail() { for(int i = 0; i < trail.length; i++) { for(int j = 0; j < trail[i].length; j++) { if(trail[i][j] > 0) trail[i][j]--; }}}
    public void coolDisc() { if(discCool > 0) discCool--; }
    public void coolInvincible() { if(invincible > 0) invincible--; }
}