// src/pages/LobbyPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function LobbyPage() {
    const [lobbies, setLobbies] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        // Fetch lobby data from server, populate `lobbies` state
    }, []);

    const handleJoinLobby = (lobbyId) => {
        // Join the selected lobby (using WebSocket, if needed)
        navigate(`/game/${lobbyId}`);
    };

    return (
        <div>
            <h2>Select a Lobby</h2>
            {lobbies.map((lobby) => (
                <button key={lobby.id} onClick={() => handleJoinLobby(lobby.id)} disabled={lobby.isFull}>
                    Lobby {lobby.id} {lobby.isFull ? "(Full)" : ""}
                </button>
            ))}
        </div>
    );
}

export default LobbyPage;
