// package com.example.wordhunt;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.Optional;
// import java.util.Set;
// import java.util.UUID;

// import org.springframework.messaging.handler.annotation.DestinationVariable;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.Payload;
// import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.stereotype.Controller;

// import com.fasterxml.jackson.databind.ObjectMapper;

// @Controller
// public class GameController {

//     private final LobbyManager lobbyManager; // Now uses LobbyManager
//     private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON conversion
//     private final Set<String> dictionary;

//     // Constructor: Loads dictionary and initializes lobby manager
//     public GameController() {
//         this.lobbyManager = new LobbyManager();
//         Set<String> tempDictionary = Set.of();
//         try {
//             tempDictionary = DictionaryLoader.loadDictionary("src/main/resources/static/dictionary2.txt");
//             System.out.println("Dictionary loaded with " + tempDictionary.size() + " words.");
//             WordFinder.setDictionary(tempDictionary);
//         } catch (Exception e) {
//             System.out.println("Error loading dictionary: " + e.getMessage());
//             e.printStackTrace();
//         }
//         this.dictionary = tempDictionary;
//     }

//     // Initialize player and lobby's game state if first player to join
//     @MessageMapping("/initializePlayer/{lobbyId}")
//     @SendTo("/topic/playerInit/{lobbyId}")
//     public String initializePlayer(@DestinationVariable int lobbyId, @Payload Map<String, String> payload) {
//         String playerName = payload.get("playerName");
//         String playerId = UUID.randomUUID().toString();
//         Player newPlayer = new Player(playerId, playerName);

//         // Attempt to join lobby and set up the game state
//         Optional<Lobby> lobbyOpt = lobbyManager.joinLobby(lobbyId, newPlayer);
//         if (lobbyOpt.isEmpty()) {
//             return "{\"error\": \"Lobby is full or does not exist.\"}";
//         }

//         // Generate response with player info and game grid
//         Lobby lobby = lobbyOpt.get();
//         GameState gameState = lobby.getGameState();
//         Map<String, Object> response = new HashMap<>();
//         response.put("playerId", playerId);
//         response.put("playerName", playerName);
//         response.put("grid", gameState.getGrid());

//         try {
//             return objectMapper.writeValueAsString(response);
//         } catch (Exception e) {
//             e.printStackTrace();
//             return "{\"error\": \"Unable to initialize player\"}";
//         }
//     }

//     // Process word submission by player
//     @MessageMapping("/submitWord/{lobbyId}")
//     @SendTo("/topic/leaderboard/{lobbyId}")
//     public String processWordSubmission(@DestinationVariable int lobbyId, @Payload Map<String, Object> submissionData) {
//         String playerId = (String) submissionData.get("playerId");
//         String submittedWord = (String) submissionData.get("word");
//         Map<String, String> positions = (Map<String, String>) submissionData.get("positions");

//         Lobby lobby = lobbyManager.getLobby(lobbyId);
//         if (lobby == null || lobby.getGameState() == null) {
//             return "{\"error\": \"Lobby does not exist or has no active game.\"}";
//         }

//         GameState gameState = lobby.getGameState();
//         Player player = gameState.getPlayers().get(playerId);
//         if (player == null) {
//             return "{\"error\": \"Player not found\"}";
//         }

//         // Verify and validate word submission
//         if (!isValidSubmission(gameState, submittedWord, positions)) {
//             return "{\"error\": \"Invalid word or mismatch with positions\"}";
//         }

//         player.getSubmittedWords().add(submittedWord);
//         int points = submittedWord.length();
//         player.addScore(points);

//         // Create leaderboard response
//         Map<String, Object> response = new HashMap<>();
//         response.put("playerName", player.getName());
//         response.put("points", points);
//         response.put("totalScore", player.getScore());

//         try {
//             return objectMapper.writeValueAsString(response);
//         } catch (Exception e) {
//             e.printStackTrace();
//             return "{\"error\": \"Unable to create response\"}";
//         }
//     }

//     // Helper to validate submission word and letter positions
//     private boolean isValidSubmission(GameState gameState, String submittedWord, Map<String, String> positions) {
//         // Combine positions letters
//         StringBuilder serverWord = new StringBuilder();
//         for (String positionKey : positions.keySet()) {
//             serverWord.append(positions.get(positionKey));
//         }
//         // Check if word matches and exists in valid words set
//         return serverWord.toString().equals(submittedWord) && gameState.getValidWords().contains(submittedWord);
//     }
// }
