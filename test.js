var JSONfn = require('./jsonfn.js');
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

function getGeziStr() {
    var cloneGezi = { ...gezi01 }
    var s = JSONfn.stringify(cloneGezi)
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
