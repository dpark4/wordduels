import React, { useEffect, useState, forwardRef, useImperativeHandle } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const WebSocketClient = forwardRef(({ onScoreUpdate }, ref) => {
    const [connected, setConnected] = useState(false);
    const [stompClient, setStompClient] = useState(null);

    useEffect(() => {
        // Set up the WebSocket connection
        const socket = new SockJS('http://localhost:8080/wordhunt');
        const stompClientInstance = Stomp.over(socket);

        // Connect with error handling
        stompClientInstance.connect(
            {},
            (frame) => {
                console.log('Connected: ' + frame);
                setConnected(true);
                setStompClient(stompClientInstance);

                // Subscribe to the /topic/leaderboard topic
                stompClientInstance.subscribe('/topic/leaderboard', (response) => {
                    try {
                        console.log('Received message from server:', response.body);
                        const message = JSON.parse(response.body); // Parse the JSON message
                        onScoreUpdate(message.points);
                        console.log('Updated score:', message.totalScore);
                    } catch (error) {
                        console.error('Error parsing JSON:', error);
                        console.log('Original response:', response.body);
                    }
                });
                
                
            },
            (error) => {
                console.error('WebSocket connection error:', error);
            }
        );

        // Cleanup
        return () => {
            // if (stompClientInstance.connected) {
            //     stompClientInstance.disconnect();
            //     console.log('Disconnected from WebSocket server');
            // }
        };
    }, [onScoreUpdate]);

    useImperativeHandle(ref, () => ({
        sendMessage: (word) => {
            if (connected && stompClient) {
                console.log('Sending word to server: ' + word);
                stompClient.send('/app/submitWord', {}, word);
            } else {
                console.warn('WebSocket connection is not established');
            }
        },
    }));

    return <div />;
});

export default WebSocketClient;
