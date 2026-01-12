package com.tron;
import java.util.ArrayList;
import java.awt.Color;
public class Koura extends Enemy{
    public Koura(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail){
        super("Koura", player, enemyList, discList, gridSize, trail);
    }//Koura,GREEN,Easy,10,5,Erratic,Low,20,1,0
}