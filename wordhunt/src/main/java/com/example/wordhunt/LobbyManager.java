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
            }
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

    // public Optional<Lobby> joinLobby(int lobbyId, Player player) {
    //     Lobby lobby = lobbies.get(lobbyId);
    //     if (lobby != null && !lobby.isFull()) {
    //         // maybe just change this to run after the if statement to avoid async issues
    //         lobby.addPlayer(player);
    //         // Initialize GameState if this is the first player
    //         if (lobby.getPlayers().size() == 1) {
    //             int gridSize = 5; // Example grid size
    //             char[][] grid = BestGridFinder.findBestGrid(gridSize, 10);
    //             GameState gameState = new GameState();
    //             gameState.setGrid(grid);
    //             Set<String> validWords = WordFinder.findAllWords(grid);
    //             gameState.setValidWords(validWords);
    //             lobby.setGameState(gameState);
    //             // need to add locking mechanism so that player doesn't reach for grid before the grid get's generated
    //         }
    //         return Optional.of(lobby);
    //     }
    //     return Optional.empty();
    // }
    public void leaveLobby(int lobbyId, String playerId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            lobby.removePlayer(playerId);
            // Remove GameState if the lobby is empty
            if (lobby.getPlayers().isEmpty()) {
                lobby.setGameState(null);
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
