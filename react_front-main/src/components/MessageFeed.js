import {MDBCard,MDBCardBody,MDBCardTitle,MDBCardText} from 'mdb-react-ui-kit';
import './MessageFeedStyles.css'; 
import { useNavigate } from "react-router-dom";
import { useState, useEffect } from 'react';
import Button from 'react-bootstrap/Button';
import { useLocation } from "react-router-dom";
import * as API from '../endpoints.js';
import axios from 'axios';

const MessageFeed = () => {
    const location = useLocation();
    const topicIds = location.state;
    console.log(topicIds);
    const navigate = useNavigate();

    const [feed, setFeed] = useState([]);

    useEffect(() => {
        const fetchFeed = async () => {
            try {
                const response = await axios.post(API.PUBLISHER_GET_SUBSCRIBER_MESSAGES, {
                    subscriberUsername: localStorage.getItem('user'),
                    publishSectorIds: topicIds
                });

                const feedList = [];
                if (response.data.length) {
                    console.log('POST response:', response.data);
                }
                response.data.forEach(topic => {
                    topic.messages.forEach(msg => {
                        feedList.push({
                            sector: topic.publishSector,
                            content: msg
                        });
                    });
                });
                setFeed(feedList);
            } catch (error) {
                console.error('Error fetching feed:', error);
            }
        };

        const intervalTime = 5000;
        const intervalId = setInterval(fetchFeed, intervalTime);
        return () => clearInterval(intervalId);
    }, [topicIds]);

    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate("/");
    }

    return (
        <div className='feedContainer'>
            <Button className="logoutButton" variant="danger" onClick={handleLogout}>Logout</Button>
            <div className='feedWrapper'>
                {feed.map((item, index) => (
                    <MDBCard key={index} className='feedCard'>
                        <MDBCardBody>
                            <MDBCardTitle>{item.sector}</MDBCardTitle>
                            <MDBCardText>{item.content}</MDBCardText>
                        </MDBCardBody>
                    </MDBCard>
                ))}
            </div>
        </div>
    );
};

export default MessageFeed;
