package com.example.wordhunt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class LobbyManager {

    private final Map<Integer, Lobby> lobbies;

    public LobbyManager() {
        lobbies = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            lobbies.put(i, new Lobby(i));  // Initialize lobbies with IDs 1 through 5
        }
    }

    public Optional<Lobby> joinLobby(int lobbyId, Player player) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null && !lobby.isFull()) {
            lobby.addPlayer(player);

            // Initialize GameState if this is the first player joining
            if (lobby.getPlayers().size() == 1 && lobby.getGameState() == null) {
                int gridSize = 5; // Example grid size
                char[][] grid = BestGridFinder.findBestGrid(gridSize, 10);
                GameState gameState = new GameState();
                gameState.setGrid(grid);
                Set<String> validWords = WordFinder.findAllWords(grid);
                gameState.setValidWords(validWords);
                lobby.setGameState(gameState);
                System.out.println("GameState initialized for lobby: " + lobbyId);
            }
            System.out.println("Player " + player.getId() + " joined lobby " + lobbyId);
            return Optional.of(lobby);
        }
        System.out.println("Failed to join lobby: " + lobbyId);
        return Optional.empty();
    }

    public void leaveLobby(int lobbyId, String playerId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            lobby.removePlayer(playerId);
            System.out.println("Player " + playerId + " left lobby " + lobbyId);

            // Remove GameState if the lobby is empty
            if (lobby.getPlayers().isEmpty()) {
                lobby.setGameState(null);
                System.out.println("GameState cleared for lobby " + lobbyId);
            }
        }
    }

    public Lobby getLobby(int lobbyId) {
        return lobbies.get(lobbyId);
    }

    // New method to return a list of all lobbies
    public List<Lobby> getLobbies() {
        return lobbies.values().stream().collect(Collectors.toList());
    }
}
