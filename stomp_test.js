Object.assign(global, { WebSocket: require('websocket').w3cwebsocket });
StompJs = require('@stomp/stompjs');
const client = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/chat',
    connectHeaders: {
        token: 'eyJhbGciOiJIUzI1NiJ9.eyJ3eF9uYW1lIjoibHkxMTEyMjIxMSIsInd4X25pY2tfbmFtZSI6Iua0i-a0izIyMjIiLCJpc3MiOiJiYWQiLCJleHAiOjE2MjQ4Nzc5MzAsImlhdCI6MTYyNDg1NjMzMH0.TC2rgbT9aJwjb-6FjHs7_eOtUFnQofUKPMPzhaE3QF4'
    },
    debug: function (str) {
        console.log(str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
});

public_callback = function (message) {
    console.log("收到public消息")
    console.log(message.body)
}

private_callback = function (message) {
    console.log("收到private消息")
    console.log(message.body)
}

client.onConnect = function (frame) {
    client.subscribe('/topic/public', public_callback)
    client.subscribe('/user/topic/msg', private_callback)
    // Do something, all subscribes must be done is this callback
    // This is needed because this will be executed after a (re)connect

    // var chatMessage = {
    //     content: 'hello，大家好',
    //     type: 'CHAT',
    //     to: 'all',
    //     toUser:false
    // }
    //
    // client.publish({
    //     destination: "/app/chat.sendMsg",
    //     body: JSON.stringify(chatMessage)
    // })

    var pchatMessage = {
        content: 'hello,帅锅好',
        type: 'CHAT',
        to: 'ly11122211',
        toUser:true
    }

    setInterval(function () {
        client.publish({
            destination: "/app/chat.sendMsg",
            body: JSON.stringify(pchatMessage)
        })
        },
        1000
    )


    //client.publish("/app/chat.sendMsg", {priority: 9}, JSON.stringify(pchatMessage));
};

client.onStompError = function (frame) {
    // Will be invoked in case of error encountered at Broker
    // Bad login/passcode typically will cause an error
    // Complaint brokers will set `message` header with a brief message. Body may contain details.
    // Compliant brokers will terminate the connection after any error
    console.log('Broker reported error: ' + frame.headers['message']);
    console.log('Additional details: ' + frame.body);
};

client.activate();