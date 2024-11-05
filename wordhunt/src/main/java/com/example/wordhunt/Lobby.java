package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;

public class Lobby {

    private int id;
    private Map<String, Player> players = new HashMap<>();
    private GameState gameState;

    public Lobby(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isFull() {
        return players.size() >= 2;
    }

    public void addPlayer(Player player) {
        if (!isFull()) {
            players.put(player.getId(), player);
        }
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
