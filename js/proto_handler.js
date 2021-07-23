var JSONfn = require('./jsonfn.js');

msg_open_grid = {
    id : 111,
    posX : 11,
    posY : 12
}

msg_open_grid = {

}

function onOpenGrid(msg, player) {
    jsb.sendMessage(player, "协议处理完毕")
}

function onEnterRoom(msg, player) {

}

var protocol_handlers = {

}

protocol_handlers[1111] = onOpenGrid
protocol_handlers[1112] = onEnterRoom

function getResultProto(id, desc) {
    var result_proto = {
        id : id,
        desc : desc
    }

    return JSONfn.stringify(result_proto)
}


function onProtocol(msg, player, room) {
    var handler = protocol_handlers[msg.id]

    if (handler) {
        //jsb.sendMessage(player, "有这个协议, 你现在在房间" + room.getId())
        handler(msg, player)
    } else {
        //jsb.sendMessage(player, "没有这个协议, 你现在在房间" + room.getId())
        jsb.sendMessage(player, getResultProto(1, "没有这个协议"))
    }
}

exports.onProtocol = onProtocol