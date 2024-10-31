package com.example.wordhunt;

import java.util.Set;

public class BestGridFinder {

    public static char[][] findBestGrid(int size, int gridCount) {
        char[][] bestGrid = null;
        int maxScore = 0;

        System.out.println("Entering findBestGrid with gridSize: " + size + " and gridCount: " + gridCount);

        for (int i = 0; i < gridCount; i++) {
            System.out.println("Generating grid " + (i + 1) + " of " + gridCount);
            char[][] grid = GridGenerator.generateRandomGrid(size);

            System.out.println("Finding all words in the grid...");
            Set<String> words = WordFinder.findAllWords(grid);
            System.out.println("Found words: " + words);

            int score = calculateScore(words);
            System.out.println("Score for this grid: " + score);

            if (score > maxScore) {
                maxScore = score;
                bestGrid = grid;
                System.out.println("New best grid found with score: " + maxScore);
            }
        }

        System.out.println("Exiting findBestGrid with best score: " + maxScore);
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
