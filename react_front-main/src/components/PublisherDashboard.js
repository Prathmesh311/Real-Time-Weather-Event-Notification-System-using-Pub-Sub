import './PublisherDashboardStyles.css';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import { useState, useEffect } from 'react';
import MenuItem from '@mui/material/MenuItem';
import InputLabel from '@mui/material/InputLabel';
import TextField from '@mui/material/TextField';
import axios from 'axios';
import { useNavigate } from "react-router-dom";
import * as URLS from '../endpoints.js';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

function PublisherDashboard() {
    const [topics, setTopics] = useState([]);
    const [selectedTopic, setSelectedTopic] = useState('');
    const [publishMessage, setPublishMessage] = useState('');

    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate("/");
    }

    useEffect(() => {
        axios.get(URLS.PUBLISHER_FETCH_TOPICS)
            .then((response) => {
                setTopics(response.data);
            })
            .catch((error) => {
                console.error('Error fetching topics:', error);
            });
    }, []);

    const handlePublish = async (e) => {
        e.preventDefault();
        try {
            await axios.post(URLS.PUBLISHER_SEND_DATA, {
                publishMasterId: selectedTopic,
                publishSector: topics.find(item => item.id === selectedTopic).publishSector,
                publishMessage: publishMessage
            });
            toast.success('Message successfully published!', {
                position: 'top-left',
                autoClose: 3000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
            });
        } catch (err) {
            toast.error('Failed to publish message!', {
                position: 'top-left',
                autoClose: 3000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
            });
        }
    }

    return (
        <div className='dashboardContainer'>
            <Button className="logoutButton" variant="danger" onClick={handleLogout}>Logout</Button>
            <div className="contentCentered">
                <Card className="text-center">
                    <Card.Header>PUBLISHER</Card.Header>
                    <Card.Body>
                        <Card.Title>Share your message here!</Card.Title>
                        <FormControl fullWidth>
                            <InputLabel style={{ margin: '10px' }} id="topic-select-label">Topic</InputLabel>
                            <Select
                                style={{ margin: '10px' }}
                                labelId="topic-select-label"
                                id="topic-select"
                                value={selectedTopic}
                                label="Topic"
                                onChange={(e) => setSelectedTopic(e.target.value)}
                            >
                                {topics.map((topic) => (
                                    <MenuItem key={topic.id} value={topic.id}>{topic.publishSector}</MenuItem>
                                ))}
                            </Select>
                            <TextField
                                style={{ margin: '10px' }}
                                id="message-field"
                                label="Message"
                                variant="outlined"
                                value={publishMessage}
                                onChange={(e) => setPublishMessage(e.target.value)}
                            />
                        </FormControl>
                        <Button variant="primary" onClick={handlePublish}>Publish</Button>
                        <ToastContainer />
                    </Card.Body>
                    <Card.Footer className="text-muted">COEN 327</Card.Footer>
                </Card>
            </div>
        </div>
    );
}

export default PublisherDashboard;
