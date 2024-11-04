// src/pages/LobbyPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const baseUrl = process.env.REACT_APP_API_BASE_URL;

function LobbyPage() {
    const [lobbies, setLobbies] = useState([]);
    const navigate = useNavigate();
    useEffect(() => {
        fetch(`${baseUrl}/api/lobbies`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json(); // Expect JSON only if response is successful
            })
            .then(data => {
                const lobbyArray = Object.keys(data).map(id => ({
                    id: parseInt(id),
                    isFull: data[id].isFull,
                    playerCount: data[id].playerCount
                }));
                setLobbies(lobbyArray);
            })
            .catch(error => console.error("Error fetching lobbies:", error));
    }, [baseUrl]);
    
       

    const handleJoinLobby = async (lobbyId) => {
        try {
            const response = await fetch(`${baseUrl}/api/lobbies/${lobbyId}/join`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ playerId: 'player123', playerName: 'Player 1' })
            });

            const data = await response.json();
            console.log(data);
            if (response.ok) {
                // Navigate to the Game Page, passing player data along with the lobby ID
                navigate(`/game/${lobbyId}`, { state: { playerData: data } });
            } else {
                console.error("Failed to join lobby:", data.error);
            }
        } catch (error) {
            console.error("Error joining lobby:", error);
        }
    };

    return (
        <div>
            <h2>Select a Lobby</h2>
            {lobbies.map((lobby) => (
                <button 
                    key={lobby.id} 
                    onClick={() => handleJoinLobby(lobby.id)} 
                    disabled={lobby.isFull}>
                    Lobby {lobby.id} {lobby.isFull ? "(Full)" : ""}
                </button>
            ))}
        </div>
    );
}

export default LobbyPage;
