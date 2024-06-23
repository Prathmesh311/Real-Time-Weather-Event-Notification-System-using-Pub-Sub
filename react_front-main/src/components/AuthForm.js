import { MDBContainer, MDBCol, MDBRow, MDBBtn, MDBInput } from 'mdb-react-ui-kit';
import { useState } from 'react';
import axios from 'axios';
import 'mdb-react-ui-kit/dist/css/mdb.min.css';
import "@fortawesome/fontawesome-free/css/all.min.css";
import 'react-toastify/dist/ReactToastify.css';
import * as URLS from '../endpoints.js';
import PublisherDashboard from './PublisherDashboard.js';
import SubscriberDashboard from './SubscriberDashboard.js';
import './AuthFormStyles.css';

function AuthForm({ type }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const url = type === 'publisher' ? URLS.PUBLISHER_AUTH_URL : URLS.SUBSCRIBER_AUTH_URL;
        try {
            await axios.post(url, {
                username: username,
                password: password
            });
            setUsername(username);
            setPassword(password);
            localStorage.setItem("username", username);
            setIsAuthenticated(true);
        } catch (err) {
            setIsAuthenticated(false);
            setError(err.response?.data?.message || 'Login failed');
        }
    }

    return (
        <div className="auth-wrapper" >
            {isAuthenticated ? (
                <>
                    {type === 'publisher' ? <PublisherDashboard /> : <SubscriberDashboard />}
                </>
            ) : (
                <MDBContainer className="auth-container">
                    <MDBRow className="auth-row">
                        {error && (
                            <div className="error-box">
                                {error}
                            </div>
                        )}
                        <MDBCol md="6" className="auth-form-col">
                            <div className="form-box">
                                <form onSubmit={handleSubmit}>
                                    <MDBInput
                                        wrapperClass="input-wrapper"
                                        label="Email Address"
                                        id="usernameField"
                                        type="email"
                                        size="lg"
                                        onChange={(e) => setUsername(e.target.value)}
                                        value={username}
                                        required
                                    />
                                    <MDBInput
                                        wrapperClass="input-wrapper"
                                        label="User Password"
                                        id="passwordField"
                                        type="password"
                                        size="lg"
                                        onChange={(e) => setPassword(e.target.value)}
                                        value={password}
                                        required
                                    />
                                    <div className="submit-button">
                                        <MDBBtn type="submit">Login</MDBBtn>
                                    </div>
                                </form>
                            </div>
                        </MDBCol>
                    </MDBRow>
                </MDBContainer>
            )}
        </div>
    );
}

export default AuthForm;
