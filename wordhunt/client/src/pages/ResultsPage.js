import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './ResultsPage.css';

function ResultsPage() {
    const { lobbyId } = useParams();
    const navigate = useNavigate();
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

    const handleReturnHome = () => {
        navigate('/');
    };

    return (
        <div className="results-page-container">
            <h2 className="results-title">Game Results</h2>
            <div className="results-box">
                <div className="results-column">
                    <h3 className="player-name">{players.playerOne}</h3>
                    <p className="score">Score: {playerOneResults.score}</p>
                    <h4 className="submitted-words-title">Submitted Words:</h4>
                    <ul className="submitted-words-list">
                        {playerOneResults.submittedWords.map((word, index) => (
                            <li key={index} className="submitted-word-item">{word}</li>
                        ))}
                    </ul>
                </div>

                <div className="divider" />

                <div className="results-column">
                    <h3 className="player-name">{players.playerTwo}</h3>
                    <p className="score">Score: {playerTwoResults.score}</p>
                    <h4 className="submitted-words-title">Submitted Words:</h4>
                    <ul className="submitted-words-list">
                        {playerTwoResults.submittedWords.map((word, index) => (
                            <li key={index} className="submitted-word-item">{word}</li>
                        ))}
                    </ul>
                </div>
            </div>

            <button className="return-home-button" onClick={handleReturnHome}>
                Return to Home
            </button>
        </div>
    );
}

export default ResultsPage;
