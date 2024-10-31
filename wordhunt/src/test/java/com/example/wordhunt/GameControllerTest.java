package com.example.wordhunt;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class GameControllerTest {

    private GameController gameController;

    @BeforeEach
    void setUp() {
        gameController = new GameController();
    }

    @Test
    void testSingleGridGeneration() throws Exception {
        // Initialize first player
        String response1 = gameController.initializePlayer("Player1");
        String response2 = gameController.initializePlayer("Player2");

        // Parse the responses to compare only the grid and valid words
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap1 = mapper.readValue(response1, Map.class);
        Map<String, Object> responseMap2 = mapper.readValue(response2, Map.class);

        assertEquals(responseMap1.get("grid"), responseMap2.get("grid"), "Both players should receive the same grid");
        assertEquals(responseMap1.get("validWords"), responseMap2.get("validWords"), "Both players should receive the same set of valid words");

        // Verify that different player IDs are assigned to each player
        assertNotEquals(responseMap1.get("playerId"), responseMap2.get("playerId"), "Each player should have a unique playerId");
    }
}
