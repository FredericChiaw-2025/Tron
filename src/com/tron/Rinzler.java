package com.tron;
import java.util.ArrayList;
import java.awt.Color;
public class Rinzler extends Enemy{
    public Rinzler(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail){
        super("Rinzler", player, enemyList, discList, gridSize, trail);
    }//Rinzler,RED,Hard,500,40,Sharp,Clever,50,2,6
}