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

    // Update the score by adding the points received from the server
    const handleScoreUpdate = (points) => {
        // Use a functional update to ensure we're adding to the previous score
        setScore((prevScore) => prevScore + points);
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
