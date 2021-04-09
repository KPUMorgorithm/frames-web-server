// This is for able to see chart. We are using Apex Chart. U can check the documentation of Apex Charts too..
var stompClient = null;
var username = null;

'use strict';
var connectingElement = document.querySelector('.connecting');

function connect() {


  console.log("connect!!!!*****");
  //sockjs not defined 문제는 해당 <script>를 포함 안 시켜서 그런다
  var socket = new SockJS('/ws');

  stompClient = Stomp.over(socket);

  stompClient.connect({}, onConnected, onError);

}


function onConnected() {
  // Subscribe to the Public Topic
  stompClient.subscribe('/topic/public', onMessageReceived);

  // Tell your username to the server


  sendMessage();
}



function onError(error) {
  console.log("error");
  connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
  connectingElement.style.color = 'red';
}


function sendMessage() {

  var chatMessage = {
    sender: "username",
    content: "messageInput.value",
    type: 'CHAT'
  };
  stompClient.send("/app/status.sendMessage", {}, JSON.stringify(chatMessage));

  console.log("message send 메시지 전송!!");
  setTimeout(sendMessage, 3000);

}


function onMessageReceived(payload) {

}

connect();