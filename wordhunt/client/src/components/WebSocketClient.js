import React, { useEffect, useState, forwardRef, useImperativeHandle } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const WebSocketClient = forwardRef(({ playerName, onPlayerInit, onScoreUpdate }, ref) => {
    const [stompClient, setStompClient] = useState(null);
    const [connected, setConnected] = useState(false);
    const [initialized, setInitialized] = useState(false); // Boolean to prevent reinitialization

    useEffect(() => {
        if (connected) return; // Prevent reconnection if already connected

        const socket = new SockJS('http://localhost:8080/wordhunt');
        const stompClientInstance = Stomp.over(socket);

        stompClientInstance.connect({}, (frame) => {
            console.log('Connected:', frame);
            setStompClient(stompClientInstance);
            setConnected(true);

            // Initialize the player only once when connected
            if (!initialized) {
                stompClientInstance.send('/app/initializePlayer', {}, playerName);
                console.log(`Sent initializePlayer message with name: ${playerName}`);
                setInitialized(true); // Prevent further initialization
            }

            // Subscribe to player initialization responses
            stompClientInstance.subscribe('/topic/playerInit', (response) => {
                const message = JSON.parse(response.body);
                onPlayerInit(message.playerId, message.playerName);
            });

            // Subscribe to score updates
            stompClientInstance.subscribe('/topic/leaderboard', (response) => {
                const message = JSON.parse(response.body);
                onScoreUpdate(message.totalScore);
            });
        });

        // Cleanup function to disconnect WebSocket on unmount
        return () => {
            if (stompClientInstance.connected) {
                stompClientInstance.disconnect();
                console.log('Disconnected from WebSocket server');
                setConnected(false);
                setInitialized(false); // Reset initialization state on disconnect
            }
        };
    }, [connected, initialized, playerName, onPlayerInit, onScoreUpdate]);

    // Expose submitWord to the parent component using useImperativeHandle
    useImperativeHandle(ref, () => ({
        submitWord: (word, playerId) => {
            if (stompClient && stompClient.connected) {
                const message = JSON.stringify({ playerId, word });
                stompClient.send('/app/submitWord', {}, message);
                console.log(`Sent submitWord message with word: ${word}`);
            } else {
                console.warn('WebSocket connection is not established');
            }
        }
    }), [stompClient]);

    return <div />;
});

export default WebSocketClient;
