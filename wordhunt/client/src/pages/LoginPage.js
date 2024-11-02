// src/pages/LoginPage.js
import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
    const navigate = useNavigate();

    const handleGoogleLogin = () => {
        // Use Google Sign-In and Redis here
        console.log("Google Sign-In handled here");
        navigate("/lobbies");
    };

    return (
        <div>
            <h2>Welcome to Word Hunt</h2>
            <button onClick={handleGoogleLogin}>Login with Google</button>
        </div>
    );
}

export default LoginPage;
