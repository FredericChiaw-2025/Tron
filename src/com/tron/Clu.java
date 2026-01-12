package com.tron;
import java.util.ArrayList;
import java.awt.Color;
public class Clu extends Enemy{
    public Clu(Player player, ArrayList<Enemy> enemyList, ArrayList<Disc> discList, int gridSize, boolean[][] trail){
        super("Clu", player, enemyList, discList, gridSize, trail);
    }//Clu,CluGOLD,Impossible,1000,80,Aggressive,Brilliant,75,2,12
}