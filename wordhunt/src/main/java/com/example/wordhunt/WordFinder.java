package com.example.wordhunt;

import java.util.HashSet;
import java.util.Set;

public class WordFinder {

    private static final int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0};
    private static final int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};
    private static final int MAX_WORD_LENGTH = 15; // Limit word length to control recursion

    private static Trie dictionaryTrie;

    public static void setDictionary(Set<String> dictionary) {
        dictionaryTrie = new Trie();
        for (String word : dictionary) {
            dictionaryTrie.insert(word);
        }
    }

    public static Set<String> findAllWords(char[][] grid) {
        Set<String> foundWords = new HashSet<>();
        int n = grid.length;
        boolean[][] visited = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dfs(grid, visited, i, j, new StringBuilder(), foundWords);
            }
        }
        return foundWords;
    }

    private static void dfs(char[][] grid, boolean[][] visited, int x, int y, StringBuilder currentWord, Set<String> foundWords) {
        if (x < 0 || x >= grid.length || y < 0 || y >= grid.length || visited[x][y]) {
            return;
        }

        currentWord.append(grid[x][y]);

        // Exit early if currentWord exceeds max length
        if (currentWord.length() > MAX_WORD_LENGTH) {
            currentWord.setLength(currentWord.length() - 1); // Backtrack
            return;
        }

        // Exit early if currentWord is not a valid prefix
        if (!dictionaryTrie.isPrefix(currentWord.toString())) {
            currentWord.setLength(currentWord.length() - 1); // Backtrack
            return;
        }

        // Check if currentWord is a complete word
        if (dictionaryTrie.isWord(currentWord.toString())) {
            foundWords.add(currentWord.toString());
            System.out.println("Found word: " + currentWord);
        }

        // Continue DFS with current path
        visited[x][y] = true;
        for (int d = 0; d < 8; d++) {
            int nx = x + dx[d], ny = y + dy[d];
            dfs(grid, visited, nx, ny, currentWord, foundWords);
        }
        visited[x][y] = false; // Unmark cell after backtracking

        // Backtrack by removing the last character added
        currentWord.setLength(currentWord.length() - 1);
    }
}
