# 🌤️ Real-Time Weather Event Notification System using Pub/Sub

This project implements a highly scalable and fault-tolerant real-time weather notification system using a distributed Pub/Sub architecture on AWS EC2.

## 🚀 Features

- **Scalable Architecture**: Utilizes AWS EC2 instances to handle high volumes of data and user requests.
- **Optimized Message Brokering**: Ensures low latency and high throughput for real-time notifications.
- **Leader Election**: Implements leader election algorithms for effective node coordination and fault tolerance.
- **Heartbeat Protocols**: Monitors node health to maintain system reliability and availability.
- **Personalized Alerts**: Delivers weather notifications based on geographic and user preferences.

## 🛠️ Technologies Used

- **AWS EC2**: For scalable infrastructure.
- **Java**: Backend development.
- **Node.js**: Message broker application.
- **React.js**: Frontend application.
- **MongoDB**: Database for storing user and message data.
- **MySQL**: Database for message broker application.
- **Distributed Systems Principles**: Leader election, heartbeat protocols, message brokering.

## 🔧 Message-Broker App

This is a broker application written in Node.js that manages node balancing, leader election, message publishing, and queue management in a distributed system.

### Prerequisites

- Node.js and npm installed on your machine.
- MySQL database setup.
- Environment variables configured.

### Environment Variables

Update a `.env` file in the root directory of the project and add the following variables:

```
DATABASE_HOST = your-database-host
DATABASE_USER = your-database-user
DATABASE_PASSWORD = your-database-password
DATABASE_NAME = your-database-name
```

### Installation

1. Install the dependencies:
   ```bash
   npm install
   ```

2. Set up the MySQL database:
   - Create the database and necessary tables.
   - Ensure the database configuration matches the environment variables.

### Running the Project

1. Start the server:
   ```bash
   npm start
   ```
2. The server will start on the port specified in the `.env` file (default is 3000).

## 🌐 Frontend-App

This frontend application is part of a Real-Time Weather notification system. It allows users to sign in as either a Publisher or a Subscriber. Publishers can post real-time weather notifications, while Subscribers can view these notifications. The application uses React.js and Material-UI for a responsive and user-friendly interface.

### Prerequisites

- Node.js (v12 or later)
- npm or yarn
- MongoDB

### Install Dependencies

- Install frontend dependencies:
  ```bash
  npm install
  ```

### Configure Environment Variables

Update the `endpoints.js` file with the following content:
```
API_BASE_URL = your-endpoint
```

### Run the Frontend Application

- Start the frontend server:
  ```bash
  npm run start
  ```

## 🔙 Backend-App

This project implements a Publish-Subscribe (Pub/Sub) notification system using Spring Boot and MongoDB. The system allows publishers to send messages to a broker, which then forwards the messages to subscribers.

### Prerequisites

- Java 11 or higher
- Maven
- MongoDB

### Installation

- Install the dependencies:
  ```bash
  mvn install
  ```

### Configure MongoDB

- Ensure MongoDB is running on your machine. The default configuration is done correctly.
- Update `application.properties` file with correct endpoints:
  ```
  spring.data.mongodb.uri = mongodb-conn-uri
  spring.data.mongodb.database = database-name
  ```

### Run the Application

- Start the backend server:
  ```bash
  mvn spring-boot:run
  ```
