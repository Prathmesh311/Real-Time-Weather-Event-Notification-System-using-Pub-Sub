var http = require('http');
var mysql2 = require('mysql2/promise');
var express = require('express');
const bodyParser = require('body-parser');
require('dotenv').config()

var publicIpAddress = "";
var leaderNode = "";
var serverQueue = {};

var app = express();
app.use(bodyParser.json());
app.set('port', process.env.PORT || 3000);

const fetchExternalIpAddress = () => {
    return new Promise((fulfill, decline) => {
      const requestConfig = {
        hostname: 'httpbin.org',
        port: 80,
        path: '/ip',
        method: 'GET',
      };
  
      const request = http.request(requestConfig, (response) => {
        let responseBody = '';
  
        response.on('data', (fragment) => {
          responseBody += fragment;
        });
  
        response.on('end', () => {
          const parsedResponse = JSON.parse(responseBody);
          const externalIp = parsedResponse.origin + ":" + app.get('port');
          console.log(`Server is operational at ${externalIp}`);
          fulfill(externalIp);
        });
      });
  
      request.on('error', (err) => {
        decline(err);
      });
  
      request.end();
    });
  };
  

const refreshExternalIpInDb = async () => {
    try {
      const externalIp = await fetchExternalIpAddress();
      const dbServer = process.env.DATABASE_HOST;
      const dbUser = process.env.DATABASE_USER;
      const dbPass = process.env.DATABASE_PASSWORD;
      const dbName = process.env.DATABASE_NAME;
  
      const dbConfig = {
        host: dbServer,
        user: dbUser,
        password: dbPass,
        database: dbName,
      };
  
      const connection = await mysql2.createConnection(dbConfig);
      // Retrieve leader node
      let [results, metadata] = await connection.execute('SELECT name FROM servers WHERE isLeader = 1');
      // console.log(results[0][metadata[0]['name']]);
      let leaderFlag = 1;
      let leaderNode;
  
      if (results.length !== 0) {
        leaderFlag = 0;
        leaderNode = results[0][metadata[0]['name']];
      } else {
        leaderNode = externalIp;
      }
  
      [results, metadata] = await connection.execute('INSERT INTO servers (name, isLeader) VALUES (?, ?)', [externalIp, leaderFlag]);
      console.log(`Node ${externalIp} is now online!`);
      console.log(`Current Leader Node: ${leaderNode}`);
  
      initiateBalanceRequest();
    } catch (err) {
      console.error(`Failed to obtain external IP address: ${err.message}`);
    }
};
  
  
function initiateBalanceRequest() {
    try {
      const leaderServer = leaderNode.trim().split(":");
      const requestOptions = {
        hostname: leaderServer[0],
        port: leaderServer[1],
        path: '/balanceNodes',
        method: 'GET',
      };
  
      const balanceRequest = http.request(requestOptions, (response) => {
        response.on('end', () => {
          if (response.statusCode !== 200) {
            console.error('Request failed:', response.statusCode);
          }
        });
      });
  
      balanceRequest.on('error', (err) => {
        console.error(err);
      });
  
      balanceRequest.end();
    } catch (err) {
      console.error(err);
    }
  }
  

function gossipProtocol() {
    rumorTimer = setInterval(() => {
      for (let i = 0; i < peerNodes.length; ++i) {
        const node = peerNodes[i].trim().split(":");
        const requestConfig = {
          hostname: node[0],
          port: node[1],
          path: '/healthcheck',
          method: 'GET',
        };

        const healthCheckReq = http.request(requestConfig);
        healthCheckReq.on('error', (err) => {
          console.error(err);
          clearInterval(rumorTimer);
          console.log('Peer node down: ', node[0] + ":" + node[1]);
          handleNodeFailure(node[0] + ":" + node[1]);
        });
        healthCheckReq.end();
      }
    }, 2000);
  }
  
async function handleNodeFailure(failedNode) {
  try {
    const [result] = await connection.execute('SELECT name FROM servers WHERE name = ?', [failedNode]);
    if (result.length === 0) return;

    await connection.execute('DELETE FROM servers WHERE name = ?', [failedNode]);

    let [leaderResult] = await connection.execute('SELECT name FROM servers WHERE isLeader = 1');
    if (leaderResult.length === 0) {
      [leaderResult] = await connection.execute('SELECT name FROM servers ORDER BY id DESC LIMIT 1');
      await connection.execute('UPDATE servers SET isLeader = 1 WHERE name = ?', [leaderResult[0].name]);
      leaderNode = leaderResult[0].name;
    }

    leaderNode = leaderResult[0].name;
    console.log("Leader node updated. New leader: ", leaderNode);
    const leaderServer = leaderNode.split(":");
    const requestConfig = {
      hostname: leaderServer[0],
      port: leaderServer[1],
      path: '/balanceNodes',
      method: 'GET',
    };

    const balanceReq = http.request(requestConfig);
    balanceReq.on('error', (err) => {
      console.error(err);
    });
    balanceReq.end();
  } catch (err) {
    console.error('Error during transaction:', err);
  }
}

//Ratelimiting
const maxAllowedRequests = 5; 
const intervalDuration = 2 * 1000; 

let requestCounter = 0;
let lastIntervalReset = Date.now();

const rateControlMiddleware = (req, res, next) => {
  const currentTimestamp = Date.now();

  if (currentTimestamp - lastIntervalReset > intervalDuration) {
    requestCounter = 0;
    lastIntervalReset = currentTimestamp;
  }

  if (requestCounter < maxAllowedRequests) {
    requestCounter++;
    next();
  } else {
    res.status(429).json({ error: 'Rate limit exceeded. Please try again later.' });
  }
};



function getMessages(options, postData) {
    return new Promise((resolve, reject) => {
        const jsonData = JSON.stringify(postData);
        const req = http.request(options, (res) => {
            let data = '';
            res.on('data', (chunk) => {
            data += chunk;
            }); 
            res.on('end', () => {
            resolve(JSON.parse(data));
            });
        });
        req.write(jsonData);
        req.on('error', (error) => {
            reject(error);
        });
        req.end();
    });
  }

app.get('/', (req, res) => {
    res.send('Broker is Up!');
});

app.get('/healthcheck', async (req, res) => {
    res.status(200).send("OK");
});

app.get('/balanceNodes', rateControlMiddleware, async (request, response) => {
    console.log("Balancing nodes");
    let [serverList] = await connection.execute('SELECT name FROM servers');
    let [queueMasterList] = await connection.execute('SELECT name FROM queue_master');
  
    const serverQueueMap = {};
    let index = 0;
    for (let i = 0; i < queueMasterList.length; i++) {
      if (index === serverList.length) index = 0;
      serverQueue[queueMasterList[i]['name']] = serverList[index]['name'];
      if (serverQueueMap[serverList[index]['name']]) {
        serverQueueMap[serverList[index]['name']] += "," + queueMasterList[i]['name'];
      } else {
        serverQueueMap[serverList[index]['name']] = queueMasterList[i]['name'];
      }
      index++;
    }
  
    neighborMap = {};
    console.log("Servers that are currently up and running:");
    console.log(serverList);
    if (serverList.length === 1) {
      neighborMap[serverList[0]['name']] = null;
    } else if (serverList.length === 2) {
      neighborMap[serverList[0]['name']] = serverList[1]['name'];
      neighborMap[serverList[1]['name']] = serverList[0]['name'];
    } else {
      serverList.push(serverList[0], serverList[1]);
      for (let i = 0; i < serverList.length - 2; i++) {
        neighborMap[serverList[i]['name']] = serverList[i + 1]['name'] + "," + serverList[i + 2]['name'];
      }
      serverList.splice(-2);
    }
  
    const insertData = [];
    for (let i = 0; i < serverList.length; i++) {
      insertData.push([serverList[i].name, "", ""]);
      if (neighborMap[serverList[i].name]) {
        insertData[i][2] = neighborMap[serverList[i].name];
      }
      if (serverQueueMap[serverList[i].name]) {
        insertData[i][1] = serverQueueMap[serverList[i].name];
      }
    }
  
    console.log("Array of server_name, queues, gossip_neighbors:");
    console.log(insertData);
  
    const flattenedValues = insertData.flat(Infinity);
    try {
      await connection.execute('DELETE FROM server_to_queue_map');
      await connection.execute(
        'INSERT INTO server_to_queue_map (server_name, queues, neighbors) VALUES ' +
        serverList.map((_, index) => '(?, ?, ?)').join(', '),
        flattenedValues
      );
    } catch (err) {
      console.log(err);
    }
  
    console.log("Sending the leader node to the server hosting the website");
    const targetServer = "54.153.52.213:8080".trim().split(":");
    const leaderRequestConfig = {
      hostname: targetServer[0],
      // hostname: 'localhost',
      port: targetServer[1],
      path: '/api/v1/publisher/fetchBrokerIp',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    };
    const leaderReq = http.request(leaderRequestConfig);
    leaderReq.write(JSON.stringify({ "ipAddress": publicIpAddress }));
    leaderReq.on('error', (err) => {
      console.error('Error:', err.message);
    });
    leaderReq.end();
  
    console.log("Nodes balanced according to queues. Broadcasting request to nodes to get their queues");
    response.status(200).send(insertData);
  
    for (let i = 0; i < serverList.length; i++) {
      const targetNode = serverList[i].name.split(":");
      const nodeRequestConfig = {
        hostname: targetNode[0],
        // hostname: 'localhost',
        port: targetNode[1],
        path: '/balanceQueues',
        method: 'GET',
      };
      const nodeReq = http.request(nodeRequestConfig);
      nodeReq.on('error', (err) => {
        console.error(err);
      });
      nodeReq.end();
    }
  });
  
  app.get('/balanceQueues', async (request, response) => {
    clearInterval(rumorTimer);
    const [queueData] = await connection.execute(`SELECT queues, neighbors FROM server_to_queue_map WHERE server_name = '${externalIp}'`);
    console.log(`Queues to manage and neighbors to gossip about heartbeat`);
    console.log(queueData);
  
    const queueList = queueData[0].queues.trim().split(",");
    neighborList = queueData[0].neighbors ? queueData[0].neighbors.trim().split(",") : [];
  
    const [messagesData] = await connection.execute(`SELECT queue_name, message FROM queue WHERE queue_name IN (` + queueList.map(() => '?').join(', ') + `)`, queueList);
    queueMap = {};
    for (let i = 0; i < messagesData.length; i++) {
      if (queueMap[messagesData[i].queue_name]) {
        queueMap[messagesData[i].queue_name].push(messagesData[i].message);
      } else {
        queueMap[messagesData[i].queue_name] = [messagesData[i].message];
      }
    }
    for (let i = 0; i < queueList.length; i++) {
      if (!queueMap[queueList[i]]) {
        queueMap[queueList[i]] = [];
      }
    }
  
    response.status(200).send(queueMap);
    gossipProtocol();
});
  
app.post('/publishData', (request, response) => {
    try {
      const payload = request.body;
      console.log(payload);
      console.log(payload.publishSector);
      console.log(serverQueue[payload.publishSector]);
      const targetServer = serverQueue[payload.publishSector].trim().split(":");
      const publishOptions = {
        hostname: targetServer[0],
        port: targetServer[1],
        path: '/publish-message',
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      };
      const publishReq = http.request(publishOptions, (publishRes) => {
        response.status(200).send("OK");
      });
      publishReq.on('error', (err) => {
        console.error(err);
      });
      publishReq.write(JSON.stringify(payload));
      publishReq.end();
    } catch (err) {
      console.error(err);
    }
  });
  
  app.post('/publish-message', async (request, response) => {
    const payload = request.body;
    const message = payload.publishMessage;
    const queueName = payload.publishSector;
    
    if (queueMap[queueName]) {
      queueMap[queueName].push(message);
    } else {
      queueMap[queueName] = [message];
    }
  
    await connection.execute('INSERT INTO queue (message, queue_name) VALUES (?, ?)', [message, queueName]);
    // console.log(`Message: ${message} added to the queue: ${queueName}`);
    response.status(200).send("OK");
  });
  
  app.post('/fetchPublishedData', (request, response) => {
    const payload = request.body;
    const requests = [];
    try {
      for (let i = 0; i < payload.length; i++) {
        const targetServer = serverQueue[payload[i].publishSector].trim().split(":");
        const fetchOptions = {
          hostname: targetServer[0],
          // hostname: 'localhost',
          port: targetServer[1],
          path: '/get-message',
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
        };
        // console.log("Request forwarded to: ", targetServer[0]);
        requests.push(fetchMessages(fetchOptions, payload[i]));
      }
      
      Promise.all(requests)
        .then((responses) => {
          // console.log("Final response for the request", responses);
          response.status(200).send(responses);
        })
        .catch((err) => {
          console.error('Error:', err.message);
        });
    } catch (err) {
      console.error(err);
    }
  });
  
  app.post('/retrieve-message', (request, response) => {
    const payload = request.body;
    const result = {
      publishSector: payload.publishSector,
      messages: queueMap[payload.publishSector].slice(payload.offset ? payload.offset : 0).reverse()
    };
    response.status(200).send(result);
  });
  
  app.get("/recursiveRequest", (request, response) => {
    const recursiveOptions = {
      hostname: 'localhost',
      port: 3000,
      path: '/',
      method: 'GET',
    };
  
    const recursiveReq = http.request(recursiveOptions, (recursiveRes) => {
      let responseData = '';
      recursiveRes.on('data', (chunk) => {
        responseData += chunk;
      });
  
      recursiveRes.on('end', () => {
        response.send(responseData);
      });
    });
  
    recursiveReq.on('error', (err) => {
      console.error(err);
    });
  
    recursiveReq.end();
  });
  
  app.get('/fetchLeader', (request, response) => {
    console.log("fetchLeader - starts -");
    response.send('Welcome, this is a basic Node.js API!');
  });
  
refreshExternalIpInDb();
  
app.listen(app.get('port'), function() {
    console.log('Message Broker server listening on port %d', app.get('port'));
});