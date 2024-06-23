import React from 'react';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';
import './WeatherLandingPageStyles.css';

function WeatherLandingPage() {
    const redirectTo = useNavigate();
    return (
        <div className="weather-wrapper">
            <div className="content-box">
                <header className="header-title">
                    <h1>Live Weather Alerts</h1>
                </header>
                <div className="button-wrapper">
                    <Button 
                        className="action-button" 
                        variant="contained" 
                        color="primary" 
                        onClick={() => redirectTo("/publishers")}
                    >
                        Publisher
                    </Button>
                    <Button 
                        className="action-button" 
                        variant="contained" 
                        color="secondary" 
                        onClick={() => redirectTo("/subscribers")}
                    >
                        Subscriber
                    </Button>
                </div>
            </div>
        </div>
    );
}

export default WeatherLandingPage;
