'use strict';

var express = require('express');

var router = express.Router();

var request = require('request');

var config = require('../../config/environment');
var activiti = config.activiti;

var sHost = activiti.protocol + '://' + activiti.hostname + activiti.path;

var buildUrl = function(path){
    return sHost + path;
};

router.get('/verifyContactEmail', function (req, res) {
    var options = {
        protocol: activiti.protocol,
        hostname: activiti.hostname,
        port: activiti.port,
        path: activiti.path,
        username: activiti.username,
        password: activiti.password,
        params: {
            sQuestion: req.query.sQuestion
        }
    };

    var url = buildUrl('/access/verifyContactEmail');

    var callback = function (error, response, body) {
        res.send(body);
        res.end();
    };

    return request.get({
        'url': url,
        'auth': {
            'username': options.username,
            'password': options.password
        },
        'qs': {
            'sQuestion': options.params.sQuestion
        }
    }, callback);
});

router.get('/verifyContactEmailAndCode', function (req, res) {
    var options = {
        protocol: activiti.protocol,
        hostname: activiti.hostname,
        port: activiti.port,
        path: activiti.path,
        username: activiti.username,
        password: activiti.password,
        params: {
            sQuestion: req.query.sQuestion,
            sAnswer: req.query.sAnswer
        }
    };

    var url = buildUrl('/access/verifyContactEmail');

    var callback = function (error, response, body) {
        // TODO /syncSubject
        res.send(body);
        res.end();
    };

    return request.get({
        'url': url,
        'auth': {
            'username': options.username,
            'password': options.password
        },
        'qs': {
            'sQuestion': options.params.sQuestion,
            'sAnswer': options.params.sAnswer
        }
    }, callback);
});

module.exports = router;
