import React, { useState } from 'react';
import './WordGrid.css';

const WordGrid = ({ grid = [], onWordFormed, playerId, disabled }) => {
    const formattedGrid = grid.map(row => row.split(''));

    const [selectedPositions, setSelectedPositions] = useState({});
    const [isDragging, setIsDragging] = useState(false);
    const [lastPosition, setLastPosition] = useState(null);

    const isAdjacent = (row, col) => {
        if (!lastPosition) return true;
        const [lastRow, lastCol] = lastPosition;
        return (
            Math.abs(lastRow - row) <= 1 &&
            Math.abs(lastCol - col) <= 1
        );
    };

    const handleLetterMouseDown = (row, col) => {
        if (disabled) return; // Prevent selection if disabled
        setSelectedPositions({ [`${row},${col}`]: formattedGrid[row][col] });
        setLastPosition([row, col]);
        setIsDragging(true);
    };

    const handleLetterMouseOver = (row, col) => {
        if (disabled || !isDragging || !isAdjacent(row, col)) return;
        setSelectedPositions((prev) => ({
            ...prev,
            [`${row},${col}`]: formattedGrid[row][col],
        }));
        setLastPosition([row, col]);
    };

    const handleMouseUp = () => {
        if (Object.keys(selectedPositions).length > 0 && !disabled) {
            const submissionData = {
                playerId,
                word: Object.values(selectedPositions).join(''),
                positions: selectedPositions,
            };
            onWordFormed(submissionData);
        }

        setSelectedPositions({});
        setIsDragging(false);
        setLastPosition(null);
    };

    return (
        <div
            className={`word-grid ${disabled ? 'disabled' : ''}`}
            style={{ userSelect: 'none' }}
            onMouseUp={handleMouseUp}
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
