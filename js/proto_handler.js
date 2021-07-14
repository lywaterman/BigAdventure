msg_open_grid = {
    id : 111,
    posX : 11,
    posY : 12
}

function onOpenGrid(session, player) {

}

var protocol_handlers = {

}

protocol_handlers[111] = onOpenGrid

function onProtocol(msg, session, player) {
    var handler = protocol_handlers[msg.id]

    if (handler) {
        handler(msg, session, player)
    } else
    {
        jsb.sendMessage(session, "没有这个协议")
    }
}

exports.onProtocol = onProtocol