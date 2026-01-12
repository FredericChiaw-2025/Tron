package com.tron;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Color;
abstract class Enemy {
    private String name;
    private Color color;
    private Difficulty difficulty;
    private int xp;
    private int speed;
    private Handling handling;
    private Intelligence intelligence;
    private double lives;
    private int x, y;
    private int dx, dy;
    private int[][] trail;
    private int discOwned;
    private int discRange;
    private int discCool;
    private int invincible;
    private int impossible;
    
    Random rd = new Random();
    
    public Enemy (String id, Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail){
        try(Scanner is = new Scanner(new FileInputStream("Enemy.txt"))){
            while(is.hasNextLine()){
                String[] c = is.nextLine().split(",");
                if(c[0].equals(id)){
                    this.name = c[0];
                    switch(c[1]) {
                        case "CluGOLD": this.color = new Color(255, 215, 0); break;
                        case "RED": this.color = Color.RED; break;
                        case "YELLOW": this.color = Color.YELLOW; break;
                        case "GREEN": this.color = Color.GREEN; break;
                        default: this.color = Color.YELLOW; break;
                    }
                    switch(c[2]) {
                        case "Impossible": this.difficulty = Difficulty.Impossible; break;
                        case "Hard": this.difficulty = Difficulty.Hard; break;
                        case "Medium": this.difficulty = Difficulty.Medium; break;
                        case "Easy": this.difficulty = Difficulty.Easy; break;
                        default: this.difficulty = Difficulty.Medium; break;
                    }
                    this.xp = Integer.parseInt(c[3]);
                    this.speed = Integer.parseInt(c[4]);
                    switch(c[5]){
                        case "Aggressive": this.handling = Handling.Aggressive; break;
                        case "Sharp": this.handling = Handling.Sharp; break;
                        case "Predictable": this.handling = Handling.Predictable; break;
                        case "Erratic": this.handling = Handling.Erratic; break;
                        default: this.handling = Handling.Predictable; break;
                    }
                    switch(c[6]){
                        case "Brilliant": this.intelligence = Intelligence.Brilliant; break;
                        case "Clever": this.intelligence = Intelligence.Clever; break;
                        case "Moderate": this.intelligence = Intelligence.Moderate; break;
                        case "Low": this.intelligence = Intelligence.Low; break;
                        default: this.intelligence = Intelligence.Moderate; break;
                    }
                    this.lives = Double.parseDouble(c[7]);
                    boolean e;
                    do{
                        e = false;
                        this.x = rd.nextInt(gridSize-2)+1;
                        this.y = rd.nextInt(gridSize-2)+1;
                        for(Enemy f : enemyList){
                            if(f != this && f.getX() == this.x && f.getY() == this.y){ e = true; }
                        }
                        for(Disc f : discList){
                            if(f.getX() == this.x && f.getY() == this.y){ e = true; }
                        }
                        if(this.x < player.getX() + 5 && this.x > player.getX() - 5 && this.y < player.getY() + 5 && this.y > player.getY() - 5) e = true;
                        if(trail[this.x][this.y]) e = true;
                    }while(e);
                    if(rd.nextBoolean()) {
                        this.dx = 0;
                        this.dy = rd.nextBoolean() ? 1 : -1;
                    } else {
                        this.dy = 0;
                        this.dx = rd.nextBoolean() ? 1 : -1;
                    }
                    this.trail = new int[gridSize][gridSize];
                    this.discOwned = Integer.parseInt(c[8]);
                    this.discRange = Integer.parseInt(c[9]);
                    this.invincible = 0;
                    this.impossible = 0;
                    if(this.xp < 0 || this.speed < 0 || this.lives < 0 || this.discOwned < 0 ||
                            this.discRange < 0 || this.discOwned < 0 || this.discRange < 0) 
                        throw new Exception();
                    break;
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("Enemy.txt: File was not found");
            EnemySark(id, player, enemyList, discList, gridSize, trail);
        }catch(IOException e){
            System.out.println("Enemy.txt: Error reading from file");
            EnemySark(id, player, enemyList, discList, gridSize, trail);
        }catch(Exception e){
            System.out.println("Enemy.txt: Error");
            EnemySark(id, player, enemyList, discList, gridSize, trail);
        }
    }
    private void EnemySark(String id, Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail){
        this.name = "Sark";
        this.color = Color.YELLOW;
        this.difficulty = Difficulty.Medium;
        this.xp = 100;
        this.speed = 20;
        this.handling = Handling.Predictable;
        this.intelligence = Intelligence.Moderate;
        this.lives = 35;
        boolean e;
        do{
            e = false;
            this.x = rd.nextInt(gridSize-2)+1;
            this.y = rd.nextInt(gridSize-2)+1;
            for(Enemy f : enemyList){
                if(f != this && f.getX() == this.x && f.getY() == this.y){ e = true; }
            }
            for(Disc f : discList){
                if(f.getX() == this.x && f.getY() == this.y){ e = true; }
            }
            if(this.x < player.getX() + 5 && this.x > player.getX() - 5 && this.y < player.getY() + 5 && this.y > player.getY() - 5) e = true;
            if(trail[this.x][this.y]) e = true;
        }while(e);
        if(rd.nextBoolean()) {
            this.dx = 0;
            this.dy = rd.nextBoolean() ? 1 : -1;
        } else {
            this.dy = 0;
            this.dx = rd.nextBoolean() ? 1 : -1;
        }
        this.trail = new int[gridSize][gridSize];
        this.discOwned = 1;
        this.discRange = 0;
        this.invincible = 0;
        this.impossible = 0; 
    }
    
    //Getter
    public Color getColor() { return color; }
    public int getXp() { return xp; }
    public double getLives() { return lives; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getTrail(int x, int y) { return trail[x][y]; }
    public int getDiscOwned() { return discOwned; }
    public int getDiscCool() { return discCool; }
    private boolean getInvincible(){ return invincible > 0; }
    
    //Setter
    private void setTrail(int Deep) { trail[x][y] = Deep; }
    private void setInvincible(){ invincible=5; }
    
    //Method
    public void shoot(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail) {
        //EnemyDisc
        if(discOwned > 0 && discCool <= 0){
            Disc disc = new Disc(color, speed, x, y, dx, dy, discRange, player, enemyList, discList, gridSize, trail);
            discList.add(disc);
            discOwned--;
            discCool = 50;
        }
    }
    public void move(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail, int deep) {
        //EnemySpeed
        if(speed > player.getSpeed()){
            do{
                moving(player, enemyList, discList, gridSize, trail, deep); }while(rd.nextDouble()*Math.max(20, 100.0 / (player.getSpeed() + 20) * 20) > Math.max(20, 100.0 / (speed + 20) * 20));
        }else{
            if(rd.nextDouble()*Math.max(20.0, 100.0 / (speed + 20) * 20) < Math.max(20, 100.0 / (player.getSpeed() + 20) * 20)) moving(player, enemyList, discList, gridSize, trail, deep);
        }
    }
    private void moving(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail, int deep) {
        //EnemyTurn
        if(impossible > 0){
            impossible--;
        }
        int e, f, Dx = dx, Dy = dy, DX = dx, DY = dy, d = 0;
        boolean D = false;
        switch(handling){
            case Aggressive: e = 100; break;
            case Sharp: e = 90; break;
            case Predictable: e = 80; break;
            case Erratic: e = 50; break;
            default: e = 0;
        }switch(intelligence){
            case Brilliant: f = 10; break;
            case Clever: f = 8; break;
            case Moderate: f = 5; break;
            case Low: f = 3; break;
            default: f = 0;
        }
        if(rd.nextInt(1000) >= (100 - e)){
            switch(difficulty){
                case Impossible:
                    D = false;
                    if(rd.nextInt(40) < f){
                        if(dx == 0){
                            if(player.getX() < x){
                                DX = -1; DY = 0; D = true;
                            }else if(player.getX() > x){
                                DX = 1; DY = 0; D = true;
                            }
                        }else{
                            if(player.getY() < y){
                                DX = 0;DY = -1; D = true;
                            }else if(player.getY() > y){
                                DX = 0;DY = 1; D = true;
                            }
                        }
                        if( D && x + DX >= 1 && x + DX <= gridSize - 2 && y + DY >= 1 && y + DY <= gridSize - 2 && !trail[x + DX][y + DY] ) {
                            Dx = DX; Dy = DY; d++; impossible = 10;
                        }
                    }
                case Hard:
                    D = false;
                    if(rd.nextInt(20) < f){
                        for(int i = 0; i < 4; i++){
                            switch(i){
                                case 0: DX = 0; DY = 1; break;
                                case 1: DX = 0; DY = -1; break;
                                case 2: DX = 1; DY = 0; break;
                                case 3: DX = -1; DY = 0; break;
                            }
                            if(x + DX == player.getX() + player.getDx() && y + DY == player.getY() + player.getDy()) { D = true; break; }
                        }
                        if( D && x + DX >= 1 && x + DX <= gridSize - 2 && y + DY >= 1 && y + DY <= gridSize - 2 && !trail[x + DX][y + DY] ) {
                            Dx = DX; Dy = DY; d++;
                        }
                    }
                case Medium:
                    D = false;
                    if(rd.nextInt(10) < f){
                        if(x + Dx < 1 || x + Dx > gridSize - 2 || y + Dy < 1 || y + Dy > gridSize - 2 || trail[x + Dx][y + Dy]){
                            int g = rd.nextBoolean()? -1 : 1;
                            for(int i = 0; i < 4; i++){
                                switch((g*i+4)%4){
                                    case 0: DX = 0; DY = 1; break;
                                    case 1: DX = 0; DY = -1; break;
                                    case 2: DX = 1; DY = 0; break;
                                    case 3: DX = -1; DY = 0; break;
                                }
                                if(x + DX >= 1 && x + DX <= gridSize - 2 && y + DY >= 1 && y + DY <= gridSize - 2 && !trail[x + DX][y + DY]) { D = true; break; }
                            }
                        }
                        if( D && x + DX >= 1 && x + DX <= gridSize - 2 && y + DY >= 1 && y + DY <= gridSize - 2 && !trail[x + DX][y + DY] ) {
                            Dx = DX; Dy = DY; d++;
                        }
                    }
                case Easy:
                    D = false;
                    if(rd.nextInt(5) < f){
                        if(x + Dx < 1 || x + Dx > gridSize - 2 || y + Dy < 1 || y + Dy > gridSize - 2){
                            int g = rd.nextBoolean()? -1 : 1;
                            for(int i = 0; i < 4; i++){
                                switch((g*i+4)%4){
                                    case 0: DX = 0; DY = 1; break;
                                    case 1: DX = 0; DY = -1; break;
                                    case 2: DX = 1; DY = 0; break;
                                    case 3: DX = -1; DY = 0; break;
                                }
                                if((x + DX >= 1 && x + DX <= gridSize - 2 && y + DY >= 1 && y + DY <= gridSize - 2) && (!trail[x + DX][y + DY])) { D = true; break; }
                            }
                        }
                        if(D && x + DX >= 1 && x + DX <= gridSize - 2 && y + DY >= 1 && y + DY <= gridSize - 2 && !trail[x + DX][y + DY]) {
                            Dx = DX; Dy = DY; d++;
                        }
                    }
                default:
                    if(d <= 0 && rd.nextInt(f) == 0 && impossible <= 0){
                        if(dx == 0){
                            DY = 0; DX = rd.nextBoolean()? -1 : 1;
                        }else{
                            DX = 0; DY = rd.nextBoolean()? -1 : 1;
                        }
                        Dx = DX; Dy = DY;
                    }
            }
        }
        dx = Dx; dy = Dy;
        movingMove(player, enemyList, discList, gridSize, trail, deep);
    }
    private void movingMove(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail, int deep) {
        //EnemyMove
        setTrail(deep);
        if(rd.nextInt(50) == 0) shoot(player, enemyList, discList, gridSize, trail);
        if(lives > 0){
            if(x + dx >= 1 && x + dx <= gridSize - 2 && y + dy >= 1 && y + dy <= gridSize - 2) {
                if(!trail[x + dx][y + dy]){
                    x += dx; y += dy;
                    for(Disc e : discList){
                        int f = 0;
                        for(Enemy g : enemyList){ if(Color.BLACK.equals(e.getColor()) || e.getColor().equals(g.getColor())) f++; }
                        if(e.getAlive() && e.getLives() > 0 && lives > 0 && f <= 0 && e.getX() == x && e.getY() == y){ hurt(player, enemyList, discList, gridSize, trail, 1); e.setLives(0); }
                        if(e.getAlive() && lives > 0 && f > 0 && e.getX() == x && e.getY() == y){ addDisc(1); e.setAlive(false); }
                    }
                    for(Enemy e : enemyList){
                        if(e != this && e.getLives() > 0 && lives > 0 && e.getX() == x && e.getY() == y){ hurt(player, enemyList, discList, gridSize, trail, 1); e.hurt(player, enemyList, discList, gridSize, trail, 1); }
                    }
                    if(player.getLives() > 0 && lives > 0 && player.getX() == x && player.getY() == y){ hurt(player, enemyList, discList, gridSize, trail, 1); player.hurt(1); }
                }else{ hurt(player, enemyList, discList, gridSize, trail, 0.5); }
            }else{ hurt(player, enemyList, discList, gridSize, trail, 1); }
        }
    }
    public void hurt(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail, double hurt){
        if(!getInvincible()){
            lives -= hurt;
            setInvincible();
        }
        reborn(player, enemyList, discList, gridSize, trail);
    }
    private void reborn(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail){
        int e;
        do{
            e = 0;
            x = rd.nextInt(gridSize-2)+1;
            y = rd.nextInt(gridSize-2)+1;
            for(Enemy f : enemyList){
                if(f != this && f.getX() == x && f.getY() == y){ e++; }
            }
            for(Disc f : discList){
                if(f.getX() == x && f.getY() == y){ e++; }
            }
        }while((x < player.getX() + 5 && x > player.getX() - 5 && y < player.getY() + 5 && y > player.getY() - 5) || e > 0 || trail[x][y]);
    }
    public void addDisc(int discOwned) { this.discOwned += discOwned; }
    
    //EnemyCool
    public void coolTrail() { for(int i = 0; i < trail.length; i++) { for(int j = 0; j < trail[i].length; j++) { if(trail[i][j] > 0) trail[i][j]--; }}}
    public void coolDisc() { if(discCool > 0) discCool--; }
    public void coolInvincible() { if(invincible > 0) invincible--; }
}