import React, { useState } from 'react';
import './WordGrid.css';

const generateGrid = () => {
    // Define the grid with letters at specific positions
    return [
        ['A', 'B', 'C', 'D'],
        ['E', 'F', 'G', 'H'],
        ['I', 'J', 'K', 'L'],
        ['M', 'N', 'O', 'P']
    ];
};

const WordGrid = ({ onWordFormed }) => {
    const [grid] = useState(generateGrid()); // Generate the grid only once
    const [selectedPositions, setSelectedPositions] = useState({});

    const handleLetterClick = (row, col) => {
        const positionKey = `${row},${col}`;
    
        setSelectedPositions((prev) => {
            if (positionKey in prev) { // Check if the positionKey exists in prev
                // Deselect if already selected
                const newSelection = { ...prev };
                delete newSelection[positionKey];
                return newSelection;
            } else {
                // Select if not already selected
                return {
                    ...prev,
                    [positionKey]: grid[row][col],
                };
            }
        });
    };
    

    const handleSubmitWord = () => {
        // Create the word from selected positions
        const submissionData = {
            playerId: 'player1', // Placeholder; replace with actual player ID
            word: Object.values(selectedPositions).join(''), // Concatenate letters for word
            positions: selectedPositions, // Send positions to server
        };
        onWordFormed(submissionData);

        // Clear the selection after submitting
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
