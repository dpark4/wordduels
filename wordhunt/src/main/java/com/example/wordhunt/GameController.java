package com.example.wordhunt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class GameController {
    private GameState gameState = new GameState();
    private ObjectMapper objectMapper = new ObjectMapper(); // For JSON conversion
    
    @MessageMapping("/initializePlayer")
    @SendTo("/topic/playerInit")
    public String initializePlayer(String playerName) {
        String playerId = UUID.randomUUID().toString();
        Player newPlayer = new Player(playerId, playerName);

        gameState.addPlayer(playerId, newPlayer);
        System.out.println("Added player with ID: " + playerId); 
        Map<String, Object> response = new HashMap<>();
        response.put("playerId", playerId);
        response.put("playerName", playerName);
        
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
        List<Map<String, Object>> positions = (List<Map<String, Object>>) submissionData.get("positions");

        // Validate the player
        Player player = gameState.getPlayers().get(playerId);
        if (player == null) {
            return "{\"error\": \"Player not found\"}";
        }

        // Combine letters from positions to verify the word
        StringBuilder serverWord = new StringBuilder();
        for (Map<String, Object> position : positions) {
            serverWord.append((String) position.get("letter"));
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
