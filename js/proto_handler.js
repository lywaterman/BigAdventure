msg_open_grid = {
    id : 111,
    posX : 11,
    posY : 12
}

function onOpenGrid(msg, player) {
    jsb.sendMessage(player, "协议处理完毕")
}

var protocol_handlers = {

}

protocol_handlers[111] = onOpenGrid

function onProtocol(msg, player, room) {
    var handler = protocol_handlers[msg.id]

    if (handler) {
        jsb.sendMessage(player, "有这个协议, 你现在在房间" + room.getId())
        handler(msg, player)
    } else {
        jsb.sendMessage(player, "没有这个协议, 你现在在房间" + room.getId())
    }
}

exports.onProtocol = onProtocol