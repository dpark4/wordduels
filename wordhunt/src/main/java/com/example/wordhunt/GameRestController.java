package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
@RestController
@RequestMapping("/api")
public class GameRestController {

    private final LobbyManager lobbyManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Set<String> dictionary;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameRestController(LobbyManager lobbyManager, SimpMessagingTemplate messagingTemplate) {
        this.lobbyManager = lobbyManager;
        this.messagingTemplate = messagingTemplate;

        Set<String> tempDictionary = Set.of();
        try {
            tempDictionary = DictionaryLoader.loadDictionary("src/main/resources/static/dictionary2.txt");
            System.out.println("Dictionary loaded with " + tempDictionary.size() + " words.");
            WordFinder.setDictionary(tempDictionary);
        } catch (Exception e) {
            System.out.println("Error loading dictionary: " + e.getMessage());
            e.printStackTrace();
        }
        this.dictionary = tempDictionary;
    }

    @GetMapping("/lobbies")
    public ResponseEntity<Map<Integer, Map<String, Object>>> getLobbies() {
        Map<Integer, Map<String, Object>> lobbyDetails = new HashMap<>();
        for (Lobby lobby : lobbyManager.getLobbies()) {
            Map<String, Object> lobbyInfo = new HashMap<>();
            lobbyInfo.put("isFull", lobby.isFull());
            lobbyInfo.put("playerCount", lobby.getPlayers().size());
            lobbyDetails.put(lobby.getId(), lobbyInfo);
        }
        return ResponseEntity.ok(lobbyDetails);
    }

    @GetMapping("/lobbies/{lobbyId}/results")
    public ResponseEntity<Map<String, Object>> getGameResults(@PathVariable int lobbyId) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);

        if (lobby == null || lobby.getGameState() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Lobby does not exist or game has not started"));
        }

        GameState gameState = lobby.getGameState();
        Map<String, Object> results = new HashMap<>();

        // Extract player results based on player IDs
        Player[] players = gameState.getPlayers().values().toArray(new Player[0]);
        if (players.length < 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Insufficient players in the game."));
        }

        // Format the results for each player
        Map<String, Object> playerOneResults = new HashMap<>();
        Map<String, Object> playerTwoResults = new HashMap<>();

        // Assuming player one is the first in the list and player two is the second
        Player playerOne = players[0];
        Player playerTwo = players[1];

        playerOneResults.put("score", playerOne.getScore());
        playerOneResults.put("submittedWords", gameState.getSubmittedWordsByPlayer(playerOne.getId()));

        playerTwoResults.put("score", playerTwo.getScore());
        playerTwoResults.put("submittedWords", gameState.getSubmittedWordsByPlayer(playerTwo.getId()));

        // Prepare response data
        results.put("playerOneName", playerOne.getName());
        results.put("playerOneResults", playerOneResults);

        results.put("playerTwoName", playerTwo.getName());
        results.put("playerTwoResults", playerTwoResults);

        return ResponseEntity.ok(results);
    }

    @PostMapping("/lobbies/{lobbyId}/join")
    public ResponseEntity<Map<String, Object>> joinLobby(
            @PathVariable int lobbyId,
            @RequestBody Map<String, String> requestBody) {

        String playerId = requestBody.get("playerId");
        String playerName = requestBody.get("playerName");
        Player player = new Player(playerId, playerName);

        Optional<Lobby> lobbyOpt = lobbyManager.joinLobby(lobbyId, player);
        if (lobbyOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Lobby is full or does not exist"));
        }

        Lobby lobby = lobbyOpt.get();
        lobby.getGameState().addPlayer(playerId, player);

        // Check if both players have joined
        if (lobby.getPlayers().size() == 2) {
            System.out.println("Both players have joined. Preparing to send 'game-ready' message.");

            // Delay broadcast to ensure all clients have subscribed
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                messagingTemplate.convertAndSend("/topic/lobbies/" + lobbyId, "game-ready");
                System.out.println("Broadcasted 'game-ready' to lobby " + lobbyId);
            }, 500, TimeUnit.MILLISECONDS); // Adjust delay if necessary

            scheduler.shutdown();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("playerId", playerId);
        response.put("playerName", playerName);
        response.put("grid", lobby.getGameState().getGrid());
        response.put("lobbyId", lobbyId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/lobbies/{lobbyId}/leave")
    public ResponseEntity<String> leaveLobby(@PathVariable int lobbyId, @RequestBody Map<String, String> requestBody) {
        String playerId = requestBody.get("playerId");
        System.out.println("Leave request: Lobby ID = " + lobbyId + ", Player ID = " + playerId);

        // Notify lobby manager to handle player leaving and reset if needed
        lobbyManager.leaveLobby(lobbyId, playerId);

        return ResponseEntity.ok("{\"message\": \"Player removed from lobby and remaining players notified\"}");
    }

    @PostMapping("/lobbies/{lobbyId}/submitWord")
    public ResponseEntity<String> submitWord(@PathVariable int lobbyId, @RequestBody Map<String, Object> submissionData) {
        String playerId = (String) submissionData.get("playerId");
        String submittedWord = (String) submissionData.get("word");
        Map<String, String> positions = (Map<String, String>) submissionData.get("positions");

        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby == null || lobby.getGameState() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Lobby does not exist or has no active game.\"}");
        }

        GameState gameState = lobby.getGameState();
        Player player = gameState.getPlayers().get(playerId);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Player not found\"}");
        }

        // Check if the word has already been submitted by this player
        if (gameState.isWordAlreadySubmitted(playerId, submittedWord)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Word already submitted by this player\"}");
        }

        // Validate the word and positions
        if (!isValidSubmission(gameState, playerId, submittedWord, positions)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Invalid word or mismatch with positions\"}");
        }

        // Add word submission to GameState's tracking and update player score
        gameState.addSubmittedWord(playerId, submittedWord);
        int points = submittedWord.length();
        player.addScore(points);

        Map<String, Object> response = new HashMap<>();
        response.put("playerName", player.getName());
        response.put("points", points);
        response.put("totalScore", player.getScore());

        try {
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Unable to submit word\"}");
        }
    }

    // Helper to validate submission word, letter positions, and prior submissions
    private boolean isValidSubmission(GameState gameState, String playerId, String submittedWord, Map<String, String> positions) {
        // Check if the word was already submitted by this player
        if (gameState.isWordAlreadySubmitted(playerId, submittedWord)) {
            return false;
        }

        StringBuilder serverWord = new StringBuilder();
        for (String positionKey : positions.keySet()) {
            serverWord.append(positions.get(positionKey));
        }
        return serverWord.toString().equals(submittedWord) && gameState.getValidWords().contains(submittedWord);
    }
}
