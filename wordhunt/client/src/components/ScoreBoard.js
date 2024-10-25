// ScoreBoard.js
import React from 'react';

const ScoreBoard = ({ score }) => {
    return (
        <div className="scoreboard">
            <h3>Score: {score}</h3>
        </div>
    );
};

export default ScoreBoard;
