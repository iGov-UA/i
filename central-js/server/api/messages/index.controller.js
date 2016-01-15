var request = require('request');


function getOptions(req) {
    var config = require('../../config/environment');
    var activiti = config.activiti;

    return {
        protocol: activiti.protocol,
        hostname: activiti.hostname,
        port: activiti.port,
        path: activiti.path,
        username: activiti.username,
        password: activiti.password
    };
}

module.exports.post = function(req, res) {
    //options, callback
    var options = getOptions(req);
    var url = options.protocol + '://' + options.hostname + options.path + '/subject/message/setMessage';

    var data = req.body;

    var callback = function(error, response, body) {
        res.send(body);
        res.end();
    };

    return request.post({
        'url': url,
        'auth': {
            'username': options.username,
            'password': options.password
        },
        'qs': {
            'sMail': data.sMail,
            'sHead': data.sHead,
            'sBody': data.sBody
        }
    }, callback);
};

module.exports.get = function(req, res) {
    //options, callback
    var options = getOptions(req);
    var url = options.protocol + '://' + options.hostname + options.path + '/subject/message/getMessages';

    var callback = function(error, response, body) {
        res.send(body);
        res.end();
    };

    return request.get({
        'url': url,
        'auth': {
            'username': options.username,
            'password': options.password
        }
    }, callback);
};

module.exports.findFeedback = function(req, res){

  var options = getOptions(req);
  var url = options.protocol + '://'
    + options.hostname
    + options.path
    + '/subject/message/getMessageFeedbackExtended?sID_Order='
    + req.param('sID_Order')
    + '&sToken='+req.param('sToken');

  var callback = function(error, response, body) {
    res.send(body);
    res.end();
  };

  return request.get({
    'url': url,
    'auth': {
      'username': options.username,
      'password': options.password
    }
  }, callback);
};

module.exports.postFeedback = function(req, res){
  var options = getOptions(req);
  var url = options.protocol + '://' + options.hostname + options.path + '/subject/message/setMessageFeedbackExtended';

  var data = req.body;

  var callback = function(error, response, body) {
    res.send(body);
    res.end();
  };

  return request.post({
    'url': url,
    'auth': {
      'username': options.username,
      'password': options.password
    },
    'qs': {
      'sID_Order': data.sID_Order,
      'sToken': data.sToken,
      'sBody': data.sBody
    }
  }, callback);
};


module.exports.findServiceMessages = function(req, res){

  var options = getOptions(req);
  var url = options.protocol + '://'
    + options.hostname
    + options.path
    + '/subject/message/getServiceMessages?sID_Order='
    + req.param('sID_Order');

  var callback = function(error, response, body) {
    var stubResult = [
      {
        "sHead" : "testhead",
        "sBody" : "testbody",
        "sDate" : "10.09.1989",
        "nID_Subject" : 123456789,
        "oMail" : {},
        "sContacts": "contacts",
        "sData" : "10.09.1989",
        "nID_SubjectMessageType": {},
        "sBody_Indirectly" : "sBody_Indirectly",
        "nID_HistoryEvent_Service": 123
      },
      {
        "sHead" : "testhead1",
        "sBody" : "testbody1",
        "sDate" : "11.09.1989",
        "nID_Subject" : 987654321,
        "oMail" : {},
        "sContacts": "contacts1",
        "sData" : "11.09.1989",
        "nID_SubjectMessageType": {},
        "sBody_Indirectly" : "sBody_Indirectly1",
        "nID_HistoryEvent_Service": 321
      }
    ];
    res.send(stubResult);
    //res.send(body);
    res.end();
  };

  return request.get({
    'url': url,
    'auth': {
      'username': options.username,
      'password': options.password
    }
  }, callback);
};
