'use strict';
var md5 = require('md5'),
    config = require('../../config/environment'),
    NodeCache = require("node-cache");


var cache = new NodeCache();
var cacheTtl = 1800; // 30min

var buildKey = function (params) {
  var key = 'MP';
  if (params) {
    for (var k in params) {
      key += '&' + k + '=' + params[k];
    }
  }
  return key;
};

module.exports.getUserAuth = function () {
  var date = new Date();
  var secret = config.masterpass.token;

  var year = date.getFullYear(),
    month = date.getMonth() + 1 >= 10 ? date.getMonth() + 1 : '0' + (date.getMonth() + 1),
    day = date.getDate() >= 10 ? date.getDate() : '0' + date.getDate(),
    hours = date.getHours() >= 10 ? date.getHours() : '0' + date.getHours(),
    minutes = date.getMinutes() >= 10 ? date.getMinutes() : '0' + date.getMinutes(),
    seconds = date.getSeconds() >= 10 ? date.getSeconds() : '0' + date.getSeconds();

  var formattedDate = year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds;

  var sign = md5(formattedDate + secret);

  return {
    "login" : config.masterpass.login,
    "time" : formattedDate,
    "sign" : sign
  };
};

module.exports.createGuid = function () {
  var result;

  function guid() {
    return s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4();
  }

  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }

  cache.get(buildKey('guid'), function (error, value) {
    if (value) {
      result = value;
    } else {
      var guidkey = guid();
      cache.set(buildKey('guid'), guidkey, cacheTtl);
      result = guidkey;
    }
  });

  return result;
};

module.exports.createAndCheckOTP = function (data) {
  var isTestServer = config.bTest, response;

  if( data.phone && !data.value && data.value !== "") {
    var code = isTestServer ? '0000' : Math.floor(1000 + Math.random() * 9000);
    var string = '0000' ? '0000' : code.toString();

    cache.set(buildKey(data.phone), string, 600);
    response = {phone: data.phone, code: string};

  } else if( data.phone && data.value ) {
    cache.get(buildKey(data.phone), function (error, value) {
      response = !!(value && value === data.value);
    });
  }

  return response;
};
