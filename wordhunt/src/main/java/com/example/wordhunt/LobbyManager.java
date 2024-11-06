package com.example.wordhunt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class LobbyManager {

    private final Map<Integer, Lobby> lobbies;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyManager(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
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

    public void leaveLobby(int lobbyId, String playerId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            lobby.removePlayer(playerId);

            // Notify remaining players that the game is interrupted
            messagingTemplate.convertAndSend("/topic/lobbies/" + lobbyId, "game-interrupted");

            // Check if the lobby is now empty after notifying, then reset the game state and scores
            if (lobby.getPlayers().isEmpty()) {
                lobby.setGameState(null);  // Clear the game state
                // lobby.getGameState().getPlayers().forEach((id, player) -> player.setScore(0)); // Reset scores
                System.out.println("Game state and scores reset for lobby " + lobbyId);
            }
        }
    }

    public Lobby getLobby(int lobbyId) {
        return lobbies.get(lobbyId);
    }

    public List<Lobby> getLobbies() {
        return lobbies.values().stream().collect(Collectors.toList());
    }
}
