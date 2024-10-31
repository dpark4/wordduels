package com.example.wordhunt;

import java.util.HashSet;
import java.util.Set;

public class WordFinder {

    private static final int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0};
    private static final int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};

    public static Set<String> dictionary;

    // Alg for finding all words in the grid
    public static Set<String> findAllWords(char[][] grid) {
        Set<String> foundWords = new HashSet<>();
        int n = grid.length;
        boolean[][] visited = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dfs(grid, visited, i, j, "", foundWords);
            }
        }
        return foundWords;
    }

    private static void dfs(char[][] grid, boolean[][] visited, int x, int y, String currentWord, Set<String> foundWords) {
        if (x < 0 || x >= grid.length || y < 0 || y >= grid.length || visited[x][y]) {
            return;
        }

        currentWord += grid[x][y];
        if (dictionary.contains(currentWord)) {
            foundWords.add(currentWord);
        }

        visited[x][y] = true;
        for (int d = 0; d < 8; d++) {
            int nx = x + dx[d], ny = y + dy[d];
            dfs(grid, visited, nx, ny, currentWord, foundWords);
        }
        visited[x][y] = false;
    }
}
