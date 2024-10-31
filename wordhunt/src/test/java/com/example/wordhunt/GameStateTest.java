package com.example.wordhunt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void testSetGridOnce() {
        GameState gameState = new GameState();

        char[][] testGrid = {
            {'A', 'B', 'C'},
            {'D', 'E', 'F'},
            {'G', 'H', 'I'}
        };

        gameState.setGrid(testGrid);
        assertTrue(gameState.isGridGenerated(), "Grid should be marked as generated");
        assertArrayEquals(testGrid, gameState.getGrid(), "Grid should match the set grid");
    }
}
