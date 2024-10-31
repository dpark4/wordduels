package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameState {

    private final Map<String, Player> players = new HashMap<>();
    private char[][] grid;
    private Set<String> validWords;
    private boolean gridGenerated = false;

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
}
