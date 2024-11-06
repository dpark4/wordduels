package com.example.wordhunt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameState {

    private char[][] grid;
    private Set<String> validWords;
    private final Map<String, Player> players = new HashMap<>();
    private final Map<String, Set<String>> playerSubmissions = new HashMap<>(); // Track submitted words per player

    public char[][] getGrid() {
        return grid;
    }

    public void setGrid(char[][] grid) {
        this.grid = grid;
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
        playerSubmissions.put(playerId, new HashSet<>());
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
        playerSubmissions.remove(playerId);
    }

    // Method to check if a specific player has already submitted a specific word
    public boolean isWordAlreadySubmitted(String playerId, String word) {
        return playerSubmissions.getOrDefault(playerId, new HashSet<>()).contains(word);
    }

    // Method to retrieve all words submitted by a specific player
    public Set<String> getSubmittedWordsByPlayer(String playerId) {
        return playerSubmissions.getOrDefault(playerId, new HashSet<>());
    }

    public void addSubmittedWord(String playerId, String word) {
        playerSubmissions.computeIfAbsent(playerId, k -> new HashSet<>()).add(word);
    }

    public boolean isGridGenerated() {
        return grid != null;
    }
}
