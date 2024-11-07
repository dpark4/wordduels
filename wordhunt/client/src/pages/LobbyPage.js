import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid'; // Import UUID for generating unique IDs
import './LobbyPage.css';

const baseUrl = process.env.REACT_APP_API_BASE_URL;

function LobbyPage() {
    const [lobbies, setLobbies] = useState([]);
    const navigate = useNavigate();

    // Retrieve or generate a unique player ID
    const getPlayerId = () => {
        let playerId = localStorage.getItem('playerId');
        if (!playerId) {
            playerId = uuidv4(); // Generate a new UUID
            localStorage.setItem('playerId', playerId);
        }
        return playerId;
    };

    const playerId = getPlayerId();
    const playerName = "Player"; // Placeholder name; update as needed

    useEffect(() => {
        fetch(`${baseUrl}/api/lobbies`)
            .then(response => response.json())
            .then(data => {
                const lobbyArray = Object.keys(data).map(id => ({
                    id: parseInt(id),
                    isFull: data[id].isFull,
                    playerCount: data[id].playerCount,
                }));
                setLobbies(lobbyArray);
            })
            .catch(error => console.error("Error fetching lobbies:", error));
    }, []);

    const handleJoinLobby = async (lobbyId) => {
        try {
            const response = await fetch(`${baseUrl}/api/lobbies/${lobbyId}/join`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ playerId, playerName })
            });

            const data = await response.json();
            if (response.ok) {
                navigate(`/game/${lobbyId}`, { state: { playerData: data } });
            } else {
                console.error("Failed to join lobby:", data.error);
            }
        } catch (error) {
            console.error("Error joining lobby:", error);
        }
    };

    return (
        <div className="lobby-container">
            <h2 className="lobby-title">Select a Lobby</h2>
            <div className="lobby-grid">
                {lobbies.map((lobby, index) => (
                    <div key={index} className="lobby-grid-row">
                        <button 
                            onClick={() => handleJoinLobby(lobby.id)} 
                            className="lobby-button"
                            disabled={lobby.isFull}
                        >
                            {lobby.isFull ? "Full" : `Lobby ${lobby.id}`}
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default LobbyPage;
