package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameState {

    private Map<String, Player> players = new HashMap<>();
    private char[][] grid;
    private Set<String> validWords;
    private Boolean gridGenerated = false;

    // Initialize game state, word grid, etc.
    public char[][] getGrid() {
        return grid;
    }

    public void setGrid(char[][] grid) {
        this.grid = grid;
        this.gridGenerated = true;
    }

    public boolean isGridGenerated() {
        return gridGenerated;
    }

    public void resetGrid(char[][] grid) {
        this.grid = null;
        // maybe change this later
        this.gridGenerated = false;
    }

    public Set<String> getValidWords() {
        return validWords;
    }

    public void setValidWords(Set<String> validWords) {
        this.validWords = validWords;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void addPlayer(String playerId, Player player) {
        players.put(playerId, player);
    }

    // Additional game state management methods
}
