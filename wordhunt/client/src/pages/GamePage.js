// src/pages/GamePage.js
import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import WordGrid from '../components/WordGrid';
import ScoreBoard from '../components/ScoreBoard';

function GamePage() {
    const location = useLocation();
    const { playerData } = location.state || {}; // Contains playerId, playerName, grid

    // Set up initial game state
    const [grid, setGrid] = useState(playerData.grid || []);
    const [score, setScore] = useState(0);
    const [playerName, setPlayerName] = useState(playerData.playerName || 'Player');

    const handleWordFormed = (submissionData) => {
        console.log(`Word formed: ${submissionData.word}`, submissionData.positions);

        // Submit word to the server
        fetch(`/api/lobbies/${playerData.lobbyId}/submitWord`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                playerId: playerData.playerId,
                word: submissionData.word,
                positions: submissionData.positions,
            }),
        })
            .then(response => response.json())
            .then(data => {
                if (data.points) {
                    // Update score based on response from server
                    setScore(score + data.points);
                }
            })
            .catch(error => console.error("Error submitting word:", error));
    };

    return (
        <div>
            <ScoreBoard score={score} playerName={playerName} />
            <WordGrid grid={grid} onWordFormed={handleWordFormed} />
        </div>
    );
}

export default GamePage;
