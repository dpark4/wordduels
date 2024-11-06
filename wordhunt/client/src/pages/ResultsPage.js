import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './ResultsPage.css';

function ResultsPage() {
    const { lobbyId } = useParams();
    const [playerOneResults, setPlayerOneResults] = useState({ submittedWords: [], score: 0 });
    const [playerTwoResults, setPlayerTwoResults] = useState({ submittedWords: [], score: 0 });
    const [players, setPlayers] = useState({ playerOne: '', playerTwo: '' });

    useEffect(() => {
        fetch(`/api/lobbies/${lobbyId}/results`)
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
                <p>Score: {playerOneResults.score}</p>
                <h3>Submitted Words:</h3>
                <ul>
                    {playerOneResults.submittedWords.map((word, index) => (
                        <li key={index}>{word}</li>
                    ))}
                </ul>
            </div>

            <div className="results-column">
                <h2>{players.playerTwo}</h2>
                <p>Score: {playerTwoResults.score}</p>
                <h3>Submitted Words:</h3>
                <ul>
                    {playerTwoResults.submittedWords.map((word, index) => (
                        <li key={index}>{word}</li>
                    ))}
                </ul>
            </div>
        </div>
    );
}

export default ResultsPage;
