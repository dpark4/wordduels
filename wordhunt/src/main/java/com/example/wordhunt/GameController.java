package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class GameController {

    private GameState gameState = new GameState();
    private ObjectMapper objectMapper = new ObjectMapper(); // For JSON conversion
    private Set<String> dictionary;

    // Server-side grid generation
    public GameController() {
        try {
            this.dictionary = DictionaryLoader.loadDictionary("src/main/resources/static/dictionary.txt");
            System.out.println("Dictionary loaded with " + dictionary.size() + " words.");
        } catch (Exception e) {
            System.out.println("Error loading dictionary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/initializePlayer")
    @SendTo("/topic/playerInit")
    public String initializePlayer(String playerName) {
        String playerId = UUID.randomUUID().toString();
        Player newPlayer = new Player(playerId, playerName);

        gameState.addPlayer(playerId, newPlayer);
        System.out.println("Added player with ID: " + playerId);

        if (!gameState.isGenerated()) {
            int gridSize = 5; // Example grid size, can be dynamic
            char[][] grid = BestGridFinder.findBestGrid(gridSize, 10);
            gameState.setGrid(grid); // Set the generated grid in GameState
            System.out.println("Generated new grid for the game.");
        } else {
            System.out.println("Reusing existing grid.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("playerId", playerId);
        response.put("playerName", playerName);
        response.put("grid", gameState.getGrid());

        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Unable to initialize player\"}";
        }
    }

    // Server-side in GameController
    @MessageMapping("/submitWord")
    @SendTo("/topic/leaderboard")
    public String processWordSubmission(Map<String, Object> submissionData) {
        String playerId = (String) submissionData.get("playerId");
        String submittedWord = (String) submissionData.get("word");
        Map<String, String> positions = (Map<String, String>) submissionData.get("positions");

        // Validate the player
        Player player = gameState.getPlayers().get(playerId);
        if (player == null) {
            return "{\"error\": \"Player not found\"}";
        }

        // Combine letters from positions to verify the word
        StringBuilder serverWord = new StringBuilder();
        for (String positionKey : positions.keySet()) {
            serverWord.append(positions.get(positionKey));
        }

        // Validate word consistency
        if (!serverWord.toString().equals(submittedWord)) {
            return "{\"error\": \"Submitted word does not match letter positions\"}";
        }

        // Calculate points and update the player's score
        int points = submittedWord.length();
        player.addScore(points);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("playerName", player.getName());
        response.put("points", points);
        response.put("totalScore", player.getScore());
        response.put("highlightedLetters", positions);

        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Unable to create response\"}";
        }
    }

}
