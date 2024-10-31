package com.example.wordhunt;

import java.util.Random;

public class GridGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random random = new Random();

    public static char[][] generateRandomGrid(int size) {
        char[][] grid = new char[size][size];
        for (int i = 0; i <= size - 1; i++) {
            for (int j = 0; j <= size - 1; j++) {
                grid[i][j] = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
            }
        }
        return grid;
    }
}
