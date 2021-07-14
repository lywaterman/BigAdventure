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

//玩家登陆成功事件
function onLogin(session, player) {
    jsb.sendMessage(session, "你好, "+player.getWx_name())
}

function onLoginout(session, player) {
    jsb.sendMessage(session, "goodbye, "+player.getWx_name())
}

//game socket 收到消息
function onMessage(message, session, player) {
    //jsb.sendMessage(session, "sb")
    //jsb.sendMessage(session, JSONfn.stringify(gezi01));

    //jsb.sendMessage(session, "你说啥我就说啥:"+message)

    var msg = null
    try {
        msg = JSON.parse(message);
    } catch (err) {

    }

    if (msg != null && msg.id != null) {
        proto_handler.onProtocol(msg, session, player)
    } else {
        jsb.sendMessage(session, "你说啥我就说啥:"+message)
    }
}


function testReload(log) {
    test.test(log)
}

function getGameMapConfig(id) {
    return gameMap.gamemaps[id]
}

//初始化两张地图
function initGameMapManager(mapManager) {
    var maps = [1, 2];
    maps.forEach(function (item,index) {
        mapManager.getGameMap(item)
    })
}