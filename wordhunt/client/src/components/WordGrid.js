import React, { useState } from 'react';
import './WordGrid.css';

// Helper function to generate a random letter
const getRandomLetter = () => {
    const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    return letters[Math.floor(Math.random() * letters.length)];
};

// Function to generate a grid with random letters
const generateGrid = () => {
    return Array.from({ length: 4 }, () => Array.from({ length: 4 }, () => getRandomLetter()));
};

const WordGrid = ({ onWordFormed, playerId }) => {
    const [grid] = useState(generateGrid()); // Generate the grid only once per render
    const [selectedPositions, setSelectedPositions] = useState({});

    const handleLetterClick = (row, col) => {
        const positionKey = `${row},${col}`;

        setSelectedPositions((prev) => {
            if (positionKey in prev) {
                const newSelection = { ...prev };
                delete newSelection[positionKey];
                return newSelection;
            } else {
                return {
                    ...prev,
                    [positionKey]: grid[row][col],
                };
            }
        });
    };

    const handleSubmitWord = () => {
        const submissionData = {
            playerId, // Use playerId from props
            word: Object.values(selectedPositions).join(''), // Concatenate letters for word
            positions: selectedPositions, // Send positions to server
        };
        onWordFormed(submissionData);
        setSelectedPositions({});
    };

    return (
        <div className="word-grid">
            {grid.map((row, rowIndex) => (
                <div key={rowIndex} className="word-grid-row">
                    {row.map((letter, colIndex) => {
                        const positionKey = `${rowIndex},${colIndex}`;
                        const isSelected = positionKey in selectedPositions;
                        return (
                            <div
                                key={colIndex}
                                className={`word-grid-cell ${isSelected ? 'selected' : ''}`}
                                onClick={() => handleLetterClick(rowIndex, colIndex)}
                            >
                                {letter}
                            </div>
                        );
                    })}
                </div>
            ))}
            <button onClick={handleSubmitWord} disabled={Object.keys(selectedPositions).length === 0}>
                Submit Word
            </button>
        </div>
    );
};

export default WordGrid;
