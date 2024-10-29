package com.example.wordhunt;

import java.util.List;

public class Player {
    private String id;
    private String name;
    private int score;
    private List<String> highlightedLetters;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public List<String> getHighlightedLetters() {
        return highlightedLetters;
    }

    public void setHighlightedLetters(List<String> highlightedLetters) {
        this.highlightedLetters = highlightedLetters;
    }

    public static class LetterPosition {
        private String letter;
        private int row;
        private int column;

        public LetterPosition(String letter, int row, int column) {
            this.letter = letter;
            this.row = row;
            this.column = column;
        }

        public String getLetter() {
            return letter;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }
    }
}
