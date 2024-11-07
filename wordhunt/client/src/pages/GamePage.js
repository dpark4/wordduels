import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import WordGrid from '../components/WordGrid';
import ScoreBoard from '../components/ScoreBoard';
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import './GamePage.css';

const baseUrl = process.env.REACT_APP_API_BASE_URL;

function GamePage() {
    const location = useLocation();
    const navigate = useNavigate();
    const { playerData } = location.state || {}; // Contains playerId, playerName, grid

    const [grid, setGrid] = useState(playerData.grid || []);
    const [score, setScore] = useState(0);
    const [playerName, setPlayerName] = useState(playerData.playerName || 'Player');
    const [timer, setTimer] = useState(null); // Countdown and game timer
    const [gamePhase, setGamePhase] = useState("waiting"); // 'waiting', 'countdown', 'active', 'finished'
    const [hasJoined, setHasJoined] = useState(false); // Track if player has joined fully

    const playerId = playerData.playerId;

    // Function to notify server that player is leaving the lobby
    const leaveLobby = () => {
        if (hasJoined) {
            console.log("Attempting to leave lobby...");
            fetch(`${baseUrl}/api/lobbies/${playerData.lobbyId}/leave`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ playerId })
            }).catch(error => console.error("Error notifying server of player leaving:", error));
        }
    };

    useEffect(() => {
        // Initialize WebSocket connection
        const stompClient = Stomp.over(() => new SockJS(`${baseUrl}/wordhunt`));

        stompClient.connect({}, () => {
            console.log("Connected to WebSocket server");

            // Subscribe to the specific lobby topic
            stompClient.subscribe(`/topic/lobbies/${playerData.lobbyId}`, (message) => {
                console.log("Received message:", message.body);

                if (message.body === "game-ready") {
                    setGamePhase("countdown"); // Start 3-second countdown
                    setTimer(3);
                } else if (message.body === "game-interrupted") {
                    // Check if there’s less than 20 seconds remaining on the timer
                    if (timer >= 20) {
                        alert("Game has been interrupted. Returning to homepage.");
                        leaveLobby(); // Leave lobby and navigate to homepage
                        navigate("/"); // Redirect to the host homepage
                    } else {
                        console.log("Game interruption ignored due to less than 20 seconds remaining.");
                    }
                }
            });

            // Indicate player has fully joined the lobby and WebSocket connection is established
            setHasJoined(true);
        });

        // Notify server when user leaves the page
        const handleBeforeUnload = () => {
            leaveLobby();
        };

        window.addEventListener("beforeunload", handleBeforeUnload);

        // Clean up on component unmount
        return () => {
            window.removeEventListener("beforeunload", handleBeforeUnload);
            leaveLobby(); // Ensure player leaves lobby if navigating away
            stompClient.disconnect(); // Disconnect WebSocket
        };
    }, [hasJoined]); // Trigger once after joining is confirmed

    useEffect(() => {
        // Countdown for the 3-second timer
        if (gamePhase === "countdown" && timer > 0) {
            const countdownInterval = setInterval(() => {
                setTimer((prev) => {
                    if (prev === 1) {
                        clearInterval(countdownInterval);
                        setGamePhase("active"); // Start the 60-second game phase
                        setTimer(10);
                    }
                    return prev - 1;
                });
            }, 1000);
            return () => clearInterval(countdownInterval);
        }

        // Countdown for the 60-second game timer
        if (gamePhase === "active" && timer > 0) {
            const gameInterval = setInterval(() => {
                setTimer((prev) => {
                    if (prev === 1) {
                        clearInterval(gameInterval);
                        setGamePhase("finished"); // End the game phase
                        showResults(); // Fetch and navigate to results page
                    }
                    return prev - 1;
                });
            }, 1000);
            return () => clearInterval(gameInterval);
        }
    }, [gamePhase, timer]);

    const handleWordFormed = (submissionData) => {
        if (gamePhase !== "active") return; // Prevent word submission if not in the active phase

        // Submit word to the server
        fetch(`${baseUrl}/api/lobbies/${playerData.lobbyId}/submitWord`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                playerId,
                word: submissionData.word,
                positions: submissionData.positions,
            }),
        })
        .then(response => response.json())
        .then(data => {
            if (data.points) {
                setScore(score + data.points); // Update score based on server response
            }
        })
        .catch(error => console.error("Error submitting word:", error));
    };

    const showResults = () => {
        fetch(`${baseUrl}/api/results/${playerData.lobbyId}`)
            .then(response => response.json())
            .then(data => navigate(`/results/${playerData.lobbyId}`, { state: { results: data } }))
            .catch(error => console.error("Error fetching results:", error));
    };

    return (
        <div className="game-page-container">
            <h2 className="game-page-title">{playerName}'s Score: {score}</h2>
            <h3 className="game-page-timer">
                {gamePhase === "waiting" ? "Waiting for players..." : `Time left: ${timer}`}
            </h3>
            <div className="word-grid-container">
                <WordGrid grid={grid} onWordFormed={handleWordFormed} disabled={gamePhase !== "active"} />
            </div>
        </div>
    );
}

export default GamePage;
