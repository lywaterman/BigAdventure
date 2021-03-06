var JSONfn = require('./jsonfn.js');
var test = require('./test.js')
var gameMap = require('./map.js')
var proto_handler = require('./proto_handler.js')


//function getA(a) {return a};
var getA = function (a) {return a};
var getB = function() {return 1.0};

function getC() {
    return 33.0;
}

var gezi = {
    type: 1,
    check: function(result) {

    }
}

var gezi01 = {
    type: 2,
    news: "今天天气不错",
    check: function(m) {
        return m.type;
    }
}

//对象创造模版
function getGezi01() {
    var gezi01 = {
        type: 2,
        news: "今天天气不错",
        check: function(m) {
            return m.type;
        }
    }
    return gezi01;
}


function getGeziStr() {
    //var cloneGezi = { ...gezi01 }
    var s = JSONfn.stringify(gezi01)
    //var s = JSON.stringify(gezi01, function(key, val) {
    //    if (typeof val === 'function') {
    //        return val + '';
    //    }
    //    return val;
    //})

    return s;
}

function getGeziByStr(s) {
    return JSONfn.parse(s)
}

function getGezi() {
    return gezi01;
}

function checkResult(result) {

}

var chat_another_login = JSON.stringify({
    content: '在其他地方登陆了',
    type: 'LOGIN'
})

//玩家登陆成功事件， 多线程， 链接成功就是登陆
function onLogin(player) {
    jsb.sendMessage(player, "你好, "+player.getWx_name())
}



function onEnter(player, room) {
    //处理房间信息
    jsb.sendMessage(player, "欢迎来到"+room.getId()+"房间")
    room.broadcastMessageExcept(player.getWx_name()+"进入了房间", player)
}

function onLeave(player, room, desc) {
    room.broadcastMessageExcept(player.getWx_name()+"离开了房间, 因为"+desc, player)
}

//玩家消息，进入消息逻辑队列
//game socket 收到消息, 玩家必然在一个房间里面
function onMessage(message, player) {
    //jsb.sendMessage(session, "sb")
    //jsb.sendMessage(session, JSONfn.stringify(gezi01));

    //jsb.sendMessage(session, "你说啥我就说啥:"+message)

    var msg = null
    try {
        msg = JSON.parse(message);
    } catch (err) {

    }

    //协议处理完成，交给对应的协议处理模块
    if (msg != null && msg.id != null) {
        proto_handler.onProtocol(msg, player)
    } else {
        jsb.sendMessage(player, JSONfn.stringify({id:2, desc:"协议格式不对"}))
    }
}


function testReload(log) {
    test.test(log)
}

function getGameMapConfig(id) {
    return gameMap.gamemaps[id]
}

//初始化两张地图
function initGameRoomService() {
    //tempId
    var rooms = [1, 2, 3, 4, 5, 6, 7];
    rooms.forEach(function (item,index) {
        gameRoomService.newPublicGameRoom(item, item, 1)
    })
}