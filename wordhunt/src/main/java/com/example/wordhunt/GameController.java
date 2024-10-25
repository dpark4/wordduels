package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class GameController {
    private GameState gameState = new GameState();
    private ObjectMapper objectMapper = new ObjectMapper(); // For JSON conversion

    @MessageMapping("/submitWord")
    @SendTo("/topic/leaderboard")
    public String processWordSubmission(String word) {
        int points = word.length(); // Points based on word length
        String playerId = "player1"; // Placeholder player ID

        // Update the player's score
        Player player = gameState.getPlayers().getOrDefault(playerId, new Player(playerId, "Player 1"));
        player.addScore(points);
        gameState.addPlayer(playerId, player);

        // Create a response object
        Map<String, Object> response = new HashMap<>();
        response.put("playerName", player.getName());
        response.put("points", points);
        response.put("totalScore", player.getScore());

        // Convert response to JSON string
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Unable to create response\"}";
        }
    }
}
