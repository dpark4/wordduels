import React, { useEffect, useState, forwardRef, useImperativeHandle, useRef } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const WebSocketClient = forwardRef(({ playerName, onPlayerInit, onScoreUpdate }, ref) => {
    const [stompClient, setStompClient] = useState(null);
    const initializedRef = useRef(false);
    const connectedRef = useRef(false);
    const playerNameRef = useRef(playerName);

    useEffect(() => {
        if (connectedRef.current) return;

        const socket = new SockJS('http://localhost:8080/wordhunt');
        const stompClientInstance = Stomp.over(socket);

        stompClientInstance.connect({}, (frame) => {
            console.log('Connected:', frame);
            setStompClient(stompClientInstance);
            connectedRef.current = true;

            if (!initializedRef.current) {
                stompClientInstance.send('/app/initializePlayer', {}, playerNameRef.current);
                console.log(`Sent initializePlayer message with name: ${playerNameRef.current}`);
                initializedRef.current = true;
            }

            stompClientInstance.subscribe('/topic/playerInit', (response) => {
                const message = JSON.parse(response.body);
                onPlayerInit(message.playerId, message.playerName);
            });

            stompClientInstance.subscribe('/topic/leaderboard', (response) => {
                const message = JSON.parse(response.body);
                onScoreUpdate(message.totalScore);
            });
        });

        return () => {
            if (stompClientInstance.connected) {
                stompClientInstance.disconnect();
                console.log('Disconnected from WebSocket server');
                connectedRef.current = false;
                initializedRef.current = false;
            }
        };
    }, [onPlayerInit, onScoreUpdate]);

    useImperativeHandle(ref, () => ({
        submitWord: (word, playerId, positions) => {
            if (stompClient && stompClient.connected) {
                const message = JSON.stringify({ playerId, word, positions });
                stompClient.send('/app/submitWord', {}, message);
                console.log(`Sent submitWord message with word: ${word} and positions:`, positions);
            } else {
                console.warn('WebSocket connection is not established');
            }
        }
    }), [stompClient]);

    return <div />;
});

export default WebSocketClient;
