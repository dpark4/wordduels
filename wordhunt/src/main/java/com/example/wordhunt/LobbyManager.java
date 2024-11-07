package com.example.wordhunt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

// Other imports remain the same
@Service
public class LobbyManager {

    private final Map<Integer, Lobby> lobbies;
    private final SimpMessagingTemplate messagingTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Scheduler for delayed cleanup

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

            // Schedule to reset game state and scores after a grace period
            if (lobby.getPlayers().isEmpty()) {
                scheduler.schedule(() -> {
                    // Only clear if the lobby is still empty (no one rejoined during the grace period)
                    if (lobby.getPlayers().isEmpty()) {
                        lobby.setGameState(null);  // Clear the game state after the grace period
                        System.out.println("Game state and scores reset for lobby " + lobbyId);
                    }
                }, 5, TimeUnit.SECONDS); // Grace period of 10 seconds
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
