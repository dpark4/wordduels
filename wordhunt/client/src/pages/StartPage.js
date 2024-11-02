// src/pages/StartPage.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../App.css';

function StartPage() {
    const navigate = useNavigate();

    const handleStartClick = () => {
        navigate('/lobbies');
    };

    return (
        <header className="App-header">
            <h1>Word Hunt Game</h1>
            <button className="start-button" onClick={handleStartClick}>
                Join Game
            </button>
        </header>
    );
}

export default StartPage;
