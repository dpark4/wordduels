package com.example.wordhunt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private GameController gameController;

    @BeforeEach
    void setUp() {
        gameController = new GameController();
    }

    @Test
    void testSingleGridGeneration() {
        String player1Grid = gameController.initializePlayer("Player1");
        String player2Grid = gameController.initializePlayer("Player2");

        // Test that both players receive the same grid
        assertEquals(player1Grid, player2Grid, "Both players should receive the same grid");
    }
}
