package ru.sticks.common;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public int size;
    public boolean[][] hor;
    public boolean[][] ver;
    public int[][] owner;
    public String[] names = new String[3];
    public int[] score = new int[3];
    public int currentPlayer = 1;
    public boolean started = false;
    public boolean gameOver = false;
    public int version = 0;
    public String message = "Ожидание игроков...";

    public GameState(int size) {
        this.size = size;
        hor = new boolean[size + 1][size];
        ver = new boolean[size][size + 1];
        owner = new int[size][size];
    }

    public GameState copy() {
        GameState c = new GameState(size);
        for (int i = 0; i < hor.length; i++) c.hor[i] = hor[i].clone();
        for (int i = 0; i < ver.length; i++) c.ver[i] = ver[i].clone();
        for (int i = 0; i < owner.length; i++) c.owner[i] = owner[i].clone();
        c.names = names.clone();
        c.score = score.clone();
        c.currentPlayer = currentPlayer;
        c.started = started;
        c.gameOver = gameOver;
        c.version = version;
        c.message = message;
        return c;
    }

    public void print() {
        System.out.println();
        System.out.println("    ");
        for (int c = 0; c <= size; c++) {
            System.out.printf("%4d", c);
        }
        System.out.println();

        for (int r = 0; r <= size; r++) {
            System.out.printf("%3d ", r);
            for (int c = 0; c <= size; c++) {
                System.out.print("+");
                if (c < size) {
                    System.out.print(hor[r][c] ? "---" : "   ");
                }
            }
            System.out.println();
            if (r < size) {
                System.out.print("    ");
                for (int c = 0; c <= size; c++) {
                    System.out.print(ver[r][c] ? "|" : " ");
                    if (c < size) {
                        System.out.print(owner[r][c] == 0 ? "   " : " " + owner[r][c] + " ");
                    }
                }
                System.out.println();
            }
        }
        System.out.println();
        for (int p = 1; p <= 2; p++) {
            String name = names[p] == null ? "(нет)" : names[p];
            System.out.println("Игрок " + p + " (" + name + "): " + score[p]);
        }
        System.out.println(message);
    }
}
