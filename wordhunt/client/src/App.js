import React, { useState, useRef, useCallback } from 'react';
import './App.css';
import WordGrid from './components/WordGrid';
import ScoreBoard from './components/ScoreBoard';
import WebSocketClient from './components/WebSocketClient';

function App() {
    const [playerId, setPlayerId] = useState(null);
    const [playerName, setPlayerName] = useState("Player 1");
    const [score, setScore] = useState(0);
    const webSocketClientRef = useRef();

    const handlePlayerInit = useCallback((id, name) => {
        setPlayerId(id);
        console.log(`Initialized player: ${name} with ID: ${id}`);
    }, []);

    const handleWordFormed = (word) => {
        console.log(`Word formed: ${word}`);
        if (webSocketClientRef.current && playerId) {
            webSocketClientRef.current.submitWord(word, playerId);
        } else {
            console.warn("Player not initialized, can't submit word");
        }
    };

    const handleScoreUpdate = useCallback((newScore) => setScore(newScore), []);

    return (
        <div className="App">
            <header className="App-header">
                <h1>Word Hunt Game</h1>
            </header>
            <ScoreBoard score={score} />
            <WordGrid onWordFormed={handleWordFormed} />
            <WebSocketClient
                ref={webSocketClientRef}
                playerName={playerName}
                onPlayerInit={handlePlayerInit}
                onScoreUpdate={handleScoreUpdate}
            />
        </div>
    );
}

export default App;
