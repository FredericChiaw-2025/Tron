package com.tron;
import java.util.Random;
import java.util.ArrayList;
import java.awt.Color;
public class Disc {
    private Color color;
    private int speed;
    private int lives;
    private boolean alive;
    private int x, y;
    private int dx, dy;
    
    Random rd = new Random();
    
    public Disc(Color color, int speed, int x, int y, int dx, int dy, int discRange, Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail) {
        this.color = color;
        this.speed = speed;
        this.lives = 3 + discRange;
        this.alive = true;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        move(player, enemyList, discList, gridSize, trail);
    }
    
    //Getter
    public Color getColor() { return color; }
    public int getLives() { return lives; }
    public boolean getAlive() { return alive; }
    public int getX() { return x; }
    public int getY() { return y; }
    
    //Setter
    public void setAlive(boolean alive) { this.alive = alive; }
    public void setLives(int lives) { this.lives = lives; }
    
    //Method
    public void move(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail) {
        if(speed > player.getSpeed()){
            do{
                moving(player, enemyList, discList, gridSize, trail);
            }while(rd.nextDouble()*Math.max(20, 100.0 / (player.getSpeed() + 20) * 20) > Math.max(20, 100.0 / (speed + 20) * 20));
        }else{
            if(rd.nextDouble()*Math.max(20, 100.0 / (speed + 20) * 20) < Math.max(20, 100.0 / (player.getSpeed() + 20) * 20)) moving(player, enemyList, discList, gridSize, trail);
        }
    }
    private void moving(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail) {
        if(alive && lives > 0){
            if(this.x + dx >= 1 && this.x + dx <= gridSize - 2 && this.y + dy >= 1 && this.y + dy <= gridSize - 2 && !trail[x + dx][y + dy]) {
                this.x += dx; this.y += dy;
                for(Disc e : discList){
                    if(e != this && e.getAlive() && e.getLives() > 0 && alive && lives > 0 && e.getX() == x && e.getY() == y){ lives = 0; e.lives = 0; }
                }
                int e = 0;
                for(Enemy f : enemyList){ if(Color.BLACK.equals(color) || f.getColor().equals(color)) e++; }
                for(Enemy f : enemyList){
                    if(f.getLives() > 0 && alive && lives > 0 && e <= 0 && f.getX() == x && f.getY() == y){ lives = 0; f.hurt(player, enemyList, discList, gridSize, trail, 1); }
                }
                if(player.getLives() > 0  && alive && lives > 0 && !(Color.BLACK.equals(color) || player.getColor().equals(color)) && player.getX() == x && player.getY() == y){ lives = 0; player.hurt(1); }
                if(player.getLives() > 0  && alive && (Color.BLACK.equals(color) || player.getColor().equals(color)) && player.getX() == x && player.getY() == y){ alive = false; player.addDisc(1); }
                for(Enemy f : enemyList){
                    if(f.getLives() > 0 && alive && e > 0 && f.getX() == x && f.getY() == y){ alive = false; f.addDisc(1); }
                }
                lives--; speed--;
            }else{ lives = 0; }
        }
    }
    public void discPublic(){
        color = Color.BLACK;
    }
}