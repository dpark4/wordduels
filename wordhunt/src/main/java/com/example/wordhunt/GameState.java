package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameState {
    private Map<String, Player> players = new HashMap<>();
    private String[][] wordGrid;
    private Set<String> validWords;

    // Initialize game state, word grid, etc.

    public String[][] getWordGrid() {
        return wordGrid;
    }

    public void setGrid(String[][] wordGrid) {
        this.wordGrid = wordGrid;
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
