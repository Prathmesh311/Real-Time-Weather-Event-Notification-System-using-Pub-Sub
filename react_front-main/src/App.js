import './App.css'; 
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import WeatherLandingPage from './components/WeatherLandingPage'; 
import PublisherAuth from './components/PublisherAuth'; 
import SubscriberAuth from './components/SubscriberAuth'; 
import SubscriberMessages from './components/MessageFeed';

function App() { 
  return (
    <Router>
      <Routes>
        <Route path="/" element={<WeatherLandingPage />}></Route> 
        <Route path="/publishers" element={<PublisherAuth />}></Route> 
        <Route path="/subscribers" element={<SubscriberAuth />}></Route> 
        <Route path="/subscriber/messages" element={<SubscriberMessages />}></Route>
      </Routes>
    </Router>
  );
}

export default App; 