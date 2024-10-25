import React, { useState } from 'react';
import './WordGrid.css';

const WordGrid = ({ onWordFormed }) => {
    const letters = [
        ['A', 'B', 'C', 'D'],
        ['E', 'F', 'G', 'H'],
        ['I', 'J', 'K', 'L'],
        ['M', 'N', 'O', 'P']
    ];

    const [selectedLetters, setSelectedLetters] = useState([]);

    const handleLetterClick = (letter) => {
        console.log('Letter clicked:', letter);
        setSelectedLetters((prevSelectedLetters) => {
            const newSelection = [...prevSelectedLetters, letter];
            console.log('Updated selected letters:', newSelection);
            return newSelection;
        });
    };
    
    

    const handleSubmitWord = () => {
        // Form the word from the selected letters and send it to the parent component
        const formedWord = selectedLetters.join('');
        onWordFormed(formedWord);
        console.log('Selected letters:', selectedLetters);
        // Clear the selection after submitting
        setSelectedLetters([]);
        console.log('Selected letters:', selectedLetters);
    };

    return (
        <div className="word-grid">
            {letters.map((row, rowIndex) => (
                <div key={rowIndex} className="word-grid-row">
                    {row.map((letter, colIndex) => (
                        <div
                            key={colIndex}
                            className={`word-grid-cell ${selectedLetters.includes(letter) ? 'selected' : ''}`}
                            onClick={() => handleLetterClick(letter)}
                        >
                            {letter}
                        </div>
                    ))}
                </div>
            ))}
            {/* <button onClick={handleSubmitWord} disabled={selectedLetters.length === 0}> */}
            <button onClick={() => { console.log('Button clicked'); handleSubmitWord(); }} disabled={selectedLetters.length === 0}>
                Submit Word
            </button>
        </div>
    );
};

export default WordGrid;
