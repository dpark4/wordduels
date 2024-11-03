package com.example.wordhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(GameRestController.class)
public class GameRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyManager lobbyManager;

    @InjectMocks
    private GameRestController gameRestController;

    private ObjectMapper objectMapper;
    private Lobby lobby;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        lobby = new Lobby(1);
        gameState = new GameState();

        // Set up a grid and valid words in the game state
        char[][] grid = {{'A', 'B'}, {'C', 'D'}};
        gameState.setGrid(grid);
        gameState.setValidWords(Set.of("AB", "CD", "ABCD"));
        lobby.setGameState(gameState);

        // Mock the LobbyManager to return this specific lobby on retrieval and joining
        when(lobbyManager.getLobby(1)).thenReturn(lobby);
        when(lobbyManager.joinLobby(eq(1), any(Player.class))).thenReturn(Optional.of(lobby));
    }

    // Helper method to simulate adding a player to the lobby
    private void addPlayerToLobby(String playerId, String playerName) throws Exception {
        Map<String, String> requestPayload = Map.of(
                "playerId", playerId,
                "playerName", playerName
        );

        mockMvc.perform(post("/api/lobbies/1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)))
                .andExpect(status().isOk());
    }

    @Test
    public void testInitializePlayerInLobby() throws Exception {
        addPlayerToLobby("testPlayer1", "Player1");

        ResultActions result = mockMvc.perform(post("/api/lobbies/1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("playerId", "testPlayer1", "playerName", "Player1"))));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value("testPlayer1"))
                .andExpect(jsonPath("$.playerName").value("Player1"))
                .andExpect(jsonPath("$.grid").isArray());
    }

    @Test
    public void testSubmitWord() throws Exception {
        addPlayerToLobby("testPlayer1", "Player1");

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("playerId", "testPlayer1");
        requestPayload.put("word", "AB");
        requestPayload.put("positions", Map.of("0,0", "A", "0,1", "B"));

        ResultActions result = mockMvc.perform(post("/api/lobbies/1/submitWord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName").value("Player1"))
                .andExpect(jsonPath("$.points").value(2))
                .andExpect(jsonPath("$.totalScore").value(2));
    }

    @Test
    public void testSubmitInvalidWord() throws Exception {
        addPlayerToLobby("testPlayer1", "Player1");

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("playerId", "testPlayer1");
        requestPayload.put("word", "XYZ");
        requestPayload.put("positions", Map.of("0,0", "X", "0,1", "Y", "1,1", "Z"));

        ResultActions result = mockMvc.perform(post("/api/lobbies/1/submitWord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid word, mismatch with positions, or word already submitted"));
    }
}
