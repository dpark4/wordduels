// App.js
import React, { useState, useRef } from 'react';
import './App.css';
import WordGrid from './components/WordGrid';
import ScoreBoard from './components/ScoreBoard';
import WebSocketClient from './components/WebSocketClient';

function App() {
    const [score, setScore] = useState(0);

    const handleWordFormed = (word) => {
        console.log(`Word formed: ${word}`);
        // Send the word to the WebSocketClient
        webSocketClientRef.current.sendMessage(word);
    };

    // Set the score directly to the total score sent by the server
    const handleScoreUpdate = (totalScore) => {
        console.log(`Setting score to total score from server: ${totalScore}`);
        setScore(totalScore);
    };

    // Create a reference to the WebSocketClient
    const webSocketClientRef = useRef();

    return (
        <div className="App">
            <header className="App-header">
                <h1>Word Hunt Game</h1>
            </header>
            {/* Pass the current score to ScoreBoard */}
            <ScoreBoard score={score} />
            <WordGrid onWordFormed={handleWordFormed} />
            <WebSocketClient ref={webSocketClientRef} onScoreUpdate={handleScoreUpdate} />
        </div>
    );
}

export default App;
