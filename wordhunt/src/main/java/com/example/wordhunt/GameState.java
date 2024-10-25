package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    private Map<String, Player> players = new HashMap<>();
    private String[][] wordGrid;

    // Initialize game state, word grid, etc.
    public GameState() {
        // Placeholder word grid setup
        this.wordGrid = new String[][]{
            {"A", "B", "C", "D"},
            {"E", "F", "G", "H"},
            {"I", "J", "K", "L"},
            {"M", "N", "O", "P"}
        };
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void addPlayer(String playerId, Player player) {
        players.put(playerId, player);
    }

    public String[][] getWordGrid() {
        return wordGrid;
    }

    // Additional game state management methods
}
