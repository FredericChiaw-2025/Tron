package com.tron;
import java.util.ArrayList;
import java.awt.Color;
public class Sark extends Enemy{
    public Sark(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail){
        super("Sark", player, enemyList, discList, gridSize, trail);
    }//Sark,YELLOW,Medium,100,20,Predictable,Moderate,35,1,0
}