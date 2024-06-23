//export const API_BASE_URL = 'http://54.153.52.213:8080/api/v1/';
export const API_BASE_URL = 'http://localhost:8989/api/v1/';


export const PUBLISHER_ENDPOINT = 'publishers/';

//Publisher validation
export const PUBLISHER_AUTH_URL =  API_BASE_URL + PUBLISHER_ENDPOINT + 'validate';
//Get managers
export const PUBLISHER_FETCH_TOPICS = API_BASE_URL + PUBLISHER_ENDPOINT + 'managers'; 
//add messages
export const PUBLISHER_SEND_DATA = API_BASE_URL + PUBLISHER_ENDPOINT + 'publish';  
//updated subscriber mappes messges
export const PUBLISHER_GET_SUBSCRIBER_MESSAGES = API_BASE_URL + PUBLISHER_ENDPOINT + 'updateSubscriberData' // GET MESSAGES BY TOPIC FOR SUBSCRIBER



export const SUBSCRIBER_ENDPOINT = 'subscribers/';
//validate subscriber
export const SUBSCRIBER_AUTH_URL = API_BASE_URL + SUBSCRIBER_ENDPOINT + 'validate';
