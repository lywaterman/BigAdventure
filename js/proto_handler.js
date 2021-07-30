var JSONfn = require('./jsonfn.js');

msg_open_grid = {
    id : 111,
    posX : 11,
    posY : 12
}

msg_open_grid = {

}

//开格子
function onOpenGrid(msg, player) {
    var gameReel = player.getCurGameReel()
    gameReel.curFrag = gameReel.curFrag + 1

    gameReelService.updateGameReel(gameReel)
    jsb.sendMessage(player, "协议处理完毕")
}

function onEnterRoom(msg, player) {
    var roomId = msg.room_id

    if (roomId == 0) {
        var lobbyRoom = gameRoomService.getLobbyRoom()
        lobbyRoom.onEnter(player)
        return
    }

    //进入房间挖宝的卷轴id
    //这里还要判断地图和这个id是不是符合的
    var reel_id = msg.reel_id
    if (reel_id == 0) {
        jsb.sendMessage(player, JSONfn.stringify({result:13, desc:"请选择卷轴进入挖宝地"}))
        return
    }

    var success = player.setCurGameReel(reel_id)
    if (!success) {
        jsb.sendMessage(player, JSONfn.stringify({result:14, desc:"没有找到对应卷轴"}))
        return
    }

    var gameRoom = gameRoomService.getGameRoomById(roomId)

    if (gameRoom) {
        gameRoom.onEnter(player)
    } else {
        jsb.sendMessage(player, JSONfn.stringify({result:11, desc:"没有这个房间"}))
    }
}

function onTestGetReel(msg, player) {
    var temp_id = msg.temp_id
    gameReelService.newGameReel(player.getId(), temp_id)
    jsb.sendMessage(player, JSONfn.stringify({result:0, desc: "创建成功"}))
}

var protocol_handlers = {

}

protocol_handlers[1111] = onOpenGrid
protocol_handlers[1112] = onEnterRoom
protocol_handlers[1113] = onTestGetReel

function getResultProto(id, desc) {
    var result_proto = {
        id : id,
        desc : desc
    }

    return JSONfn.stringify(result_proto)
}


function onProtocol(msg, player) {
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