// src/pages/GamePage.js
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import WordGrid from '../components/WordGrid';
import ScoreBoard from '../components/ScoreBoard';
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

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
    
    useEffect(() => {
        const stompClient = Stomp.over(() => new SockJS(`${baseUrl}/wordhunt`)); // Updated initialization

        stompClient.connect({}, () => {
            stompClient.subscribe(`/topic/lobbies/${playerData.lobbyId}`, (message) => {
                console.log(message.body())
                if (message.body === "game-ready") {
                    setGamePhase("countdown"); // Start 3-second countdown
                    setTimer(3);
                }
            });
        });

        return () => stompClient.disconnect();
    }, [playerData.lobbyId]);

    useEffect(() => {
        // Countdown for the 3-second timer
        if (gamePhase === "countdown" && timer > 0) {
            const countdownInterval = setInterval(() => {
                setTimer((prev) => {
                    if (prev === 1) {
                        clearInterval(countdownInterval);
                        setGamePhase("active"); // Start the 60-second game phase
                        setTimer(60);
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
                playerId: playerData.playerId,
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
        <div>
            <ScoreBoard score={score} playerName={playerName} />
            <h2>{gamePhase === "waiting" ? "Waiting for players..." : `Time left: ${timer}`}</h2>
            <WordGrid grid={grid} onWordFormed={handleWordFormed} disabled={gamePhase !== "active"} />
        </div>
    );
}

export default GamePage;
