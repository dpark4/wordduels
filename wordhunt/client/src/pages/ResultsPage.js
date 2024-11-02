// src/pages/ResultsPage.js
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './ResultsPage.css';

function ResultsPage() {
    const { lobbyId } = useParams();
    const [playerOneResults, setPlayerOneResults] = useState({ correctWords: [], incorrectWords: [] });
    const [playerTwoResults, setPlayerTwoResults] = useState({ correctWords: [], incorrectWords: [] });
    const [players, setPlayers] = useState({ playerOne: '', playerTwo: '' });

    useEffect(() => {
        // Fetch results from the server based on lobbyId
        fetch(`/api/results/${lobbyId}`)
            .then(response => response.json())
            .then(data => {
                setPlayerOneResults(data.playerOneResults);
                setPlayerTwoResults(data.playerTwoResults);
                setPlayers({ playerOne: data.playerOneName, playerTwo: data.playerTwoName });
            })
            .catch(error => console.error("Error fetching results:", error));
    }, [lobbyId]);

    return (
        <div className="results-container">
            <div className="results-column">
                <h2>{players.playerOne}</h2>
                <h3>Correct Words</h3>
                {playerOneResults.correctWords.map(word => (
                    <p key={word} className="correct-word">{word}</p>
                ))}
                <h3>Incorrect Words</h3>
                {playerOneResults.incorrectWords.map(word => (
                    <p key={word} className="incorrect-word">{word}</p>
                ))}
            </div>

            <div className="results-column">
                <h2>{players.playerTwo}</h2>
                <h3>Correct Words</h3>
                {playerTwoResults.correctWords.map(word => (
                    <p key={word} className="correct-word">{word}</p>
                ))}
                <h3>Incorrect Words</h3>
                {playerTwoResults.incorrectWords.map(word => (
                    <p key={word} className="incorrect-word">{word}</p>
                ))}
            </div>
        </div>
    );
}

export default ResultsPage;
