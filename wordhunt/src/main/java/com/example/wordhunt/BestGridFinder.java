package com.example.wordhunt;

import java.util.Set;

public class BestGridFinder {

    public static char[][] findBestGrid(int size, int gridCount) {
        char[][] bestGrid = null;
        int maxScore = 0;

        for (int i = 0; i < gridCount; i++) {
            char[][] grid = GridGenerator.generateRandomGrid(size);
            Set<String> words = WordFinder.findAllWords(grid);
            int score = calculateScore(words); // Updated scoring based on distinct words and length

            if (score > maxScore) {
                maxScore = score;
                bestGrid = grid;
            }
        }
        return bestGrid;
    }

    private static int calculateScore(Set<String> words) {
        int score = 0;
        for (String word : words) {
            score += word.length(); // Each word contributes points equal to its length
        }
        return score;
    }
    // potential alternate scoring method
    // private static int calculateScore(Set<String> words) {
    //     int score = 0;
    //     for (String word : words) {
    //         score += 1 + word.length() * word.length(); // Adds 1 point per word + squared length
    //     }
    //     return score;
    // }

}
