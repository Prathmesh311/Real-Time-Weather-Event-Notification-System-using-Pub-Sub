import './SubscriberDashboardStyles.css';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import { useState, useEffect } from 'react';
import { useTheme } from '@mui/material/styles';
import MenuItem from '@mui/material/MenuItem';
import InputLabel from '@mui/material/InputLabel';
import Chip from '@mui/material/Chip';
import { useNavigate } from "react-router-dom";
import Box from '@mui/material/Box';
import OutlinedInput from '@mui/material/OutlinedInput';
import axios from 'axios';
import * as API from '../endpoints.js';

const DROPDOWN_ITEM_HEIGHT = 48;
const DROPDOWN_ITEM_PADDING_TOP = 8;
const CustomMenuProps = {
  PaperProps: {
    style: {
      maxHeight: DROPDOWN_ITEM_HEIGHT * 4.5 + DROPDOWN_ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

const computeStyles = (name, selectedItems, theme) => ({
    fontWeight: selectedItems.includes(name) ? theme.typography.fontWeightMedium : theme.typography.fontWeightRegular,
});

function SubscriberDashboard() {
    const theme = useTheme();
    const [topicOptions, setTopicOptions] = useState([]);
    const [chosenTopics, setChosenTopics] = useState([]);

    const navigate = useNavigate();

    const logOutUser = () => {
        localStorage.removeItem('user');
        navigate("/");
    };

    useEffect(() => {
        axios.get(API.PUBLISHER_FETCH_TOPICS)
            .then((response) => {
                setTopicOptions(response.data);
            })
            .catch((error) => {
                console.error('Error fetching topics:', error);
            });
    }, []);

    const handleTopicSelection = (event) => {
        const { value } = event.target;
        setChosenTopics(typeof value === 'string' ? value.split(',') : value);
    };

    const mapTopicsToIds = (topics, selected) => {
        return selected.map(name => {
            const topic = topics.find(item => item.publishSector === name);
            return topic ? topic.id : null;
        }).filter(id => id !== null);
    };

    const handleSubscribe = () => {
        const topicIds = mapTopicsToIds(topicOptions, chosenTopics);
        navigate('/subscriber/messages', { state: topicIds });
    };

    return (
        <div className='dashboardContainer'>
            <Button className="signOutButton" variant="danger" onClick={logOutUser}>Logout</Button>
            <div className="contentWrapper">
                <Card className="text-center">
                    <Card.Header>SUBSCRIBER</Card.Header>
                    <Card.Body>
                        <Card.Title>Select Topics to Subscribe</Card.Title>
                        <FormControl sx={{ m: 1, width: 300 }}>
                            <InputLabel id="topics-select-label">Topics</InputLabel>
                            <Select
                                labelId="topics-select-label"
                                id="topics-select"
                                multiple
                                value={chosenTopics}
                                onChange={handleTopicSelection}
                                input={<OutlinedInput id="select-multiple-chip" label="Topics" />}
                                renderValue={(selected) => (
                                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                                        {selected.map((value) => (
                                            <Chip key={value} label={value} />
                                        ))}
                                    </Box>
                                )}
                                MenuProps={CustomMenuProps}
                            >
                                {topicOptions.map((topic) => (
                                    <MenuItem
                                        key={topic.id}
                                        value={topic.publishSector}
                                        style={computeStyles(topic.publishSector, chosenTopics, theme)}
                                    >
                                        {topic.publishSector}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <div>
                            <Button variant="primary" onClick={handleSubscribe}>Subscribe</Button>
                        </div>
                    </Card.Body>
                    <Card.Footer className="text-muted">COEN 327</Card.Footer>
                </Card>
            </div>
        </div>
    );
}

export default SubscriberDashboard;
