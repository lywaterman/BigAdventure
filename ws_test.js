const WebSocket = require('ws');

const wss = new WebSocket('ws://localhost:8080/game');

wss.on
('connection', function connection(ws) {
    //console.log('connection')
})

wss.on('error', function (err, xxx) {
    console.log("sdfsdf")
    console.log(err)
    console.log(wss)

})

wss.on('response', function response(res) {
    console.log(res)
})