import React, { useState } from 'react';
import './WordGrid.css';

const WordGrid = ({ grid = [], onWordFormed, playerId }) => {
    // Convert the grid rows from strings to arrays of characters
    const formattedGrid = grid.map(row => row.split(''));
    
    const [selectedPositions, setSelectedPositions] = useState({});
    const [isDragging, setIsDragging] = useState(false);
    const [lastPosition, setLastPosition] = useState(null);

    // Check if a cell is adjacent to the last selected cell
    const isAdjacent = (row, col) => {
        if (!lastPosition) return true; // No previous position, so it's the first cell
        const [lastRow, lastCol] = lastPosition;
        return (
            Math.abs(lastRow - row) <= 1 &&
            Math.abs(lastCol - col) <= 1
        );
    };

    const handleLetterMouseDown = (row, col) => {
        setSelectedPositions({ [`${row},${col}`]: formattedGrid[row][col] });
        setLastPosition([row, col]);
        setIsDragging(true);
    };

    const handleLetterMouseOver = (row, col) => {
        if (isDragging && isAdjacent(row, col)) {
            setSelectedPositions((prev) => ({
                ...prev,
                [`${row},${col}`]: formattedGrid[row][col],
            }));
            setLastPosition([row, col]);
        }
    };

    const handleMouseUp = () => {
        if (Object.keys(selectedPositions).length > 0) {
            // Formulate submission data
            const submissionData = {
                playerId,
                word: Object.values(selectedPositions).join(''), // Concatenate letters for the word
                positions: selectedPositions, // Send positions to the server
            };
            onWordFormed(submissionData);
        }

        // Reset selections and states
        setSelectedPositions({});
        setIsDragging(false);
        setLastPosition(null);
    };

    return (
        <div
            className="word-grid"
            style={{ userSelect: 'none'}}
            onMouseUp={handleMouseUp} // Listen for mouse up on the grid container
        >
            {formattedGrid.map((row, rowIndex) => (
                <div key={rowIndex} className="word-grid-row">
                    {row.map((letter, colIndex) => {
                        const positionKey = `${rowIndex},${colIndex}`;
                        const isSelected = positionKey in selectedPositions;
                        return (
                            <div
                                key={colIndex}
                                className={`word-grid-cell ${isSelected ? 'selected' : ''}`}
                                onMouseDown={() => handleLetterMouseDown(rowIndex, colIndex)}
                                onMouseOver={() => handleLetterMouseOver(rowIndex, colIndex)}
                            >
                                {letter}
                            </div>
                        );
                    })}
                </div>
            ))}
        </div>
    );
};

export default WordGrid;
