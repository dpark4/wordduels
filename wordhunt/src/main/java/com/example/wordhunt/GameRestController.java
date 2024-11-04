package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class GameRestController {

    private final LobbyManager lobbyManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Set<String> dictionary;

    @Autowired
    public GameRestController(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
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

    @PostMapping("/lobbies/{lobbyId}/join")
    public ResponseEntity<Map<String, Object>> joinLobby(
            @PathVariable int lobbyId,
            @RequestBody Map<String, String> requestBody) {

        String playerId = requestBody.get("playerId");  // Use playerId provided by the client
        String playerName = requestBody.get("playerName");
        Player player = new Player(playerId, playerName);

        Optional<Lobby> lobbyOpt = lobbyManager.joinLobby(lobbyId, player);
        if (lobbyOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Lobby is full or does not exist"));
        }
        Lobby lobby = lobbyOpt.get();
        lobby.getGameState().addPlayer(playerId, player);

        // Include playerId in the response so the client can store it
        Map<String, Object> response = new HashMap<>();
        response.put("playerId", playerId);
        response.put("playerName", playerName);
        response.put("grid", lobbyOpt.get().getGameState().getGrid());
        response.put("lobbyId", lobbyId);

        return ResponseEntity.ok(response);
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

        // Validate the word and positions, and check for previous submissions
        if (!isValidSubmission(gameState, submittedWord, positions)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Invalid word, mismatch with positions, or word already submitted\"}");
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
    private boolean isValidSubmission(GameState gameState, String submittedWord, Map<String, String> positions) {
        if (gameState.isWordAlreadySubmitted(submittedWord)) {
            return false;
        }
        StringBuilder serverWord = new StringBuilder();
        for (String positionKey : positions.keySet()) {
            serverWord.append(positions.get(positionKey));
        }
        return serverWord.toString().equals(submittedWord) && gameState.getValidWords().contains(submittedWord);
    }
}
