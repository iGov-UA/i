var request = require('request');
var _ = require('lodash');

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

module.exports.postServiceMessage = function(req, res){
  var oData = req.body;
  var sToken = oData.sToken;
  if (!!req.session.subject.nID || sToken!==null){
    var nID_Subject = (req.session && req.session!==null && req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) ? req.session.subject.nID : null;
  //if (!!req.session.subject.nID){
  //  var nID_Subject = req.session.subject.nID;
    var options = getOptions(req);
    var sURL = options.protocol + '://' + options.hostname + options.path + '/subject/message/setServiceMessage';

    var callback = function(error, response, body) {
      res.send(body);
      res.end();
    };


    var oDateNew = {
        'sID_Order': oData.sID_Order,
        'sBody': oData.sBody,
        'nID_SubjectMessageType' : 8
        , 'bAuth': true
    };
    if(sToken!==null){
        oDateNew = $.extend(oDateNew,{sToken: sToken});
    }
    if(nID_Subject!==null){
        oDateNew = $.extend(oDateNew,{nID_Subject: nID_Subject});
    }

    return request.post({
      'url': sURL,
      'auth': {
        'username': options.username,
        'password': options.password
      },
      'qs': oDateNew/* {
        'sID_Order': oData.sID_Order,
        'sBody': oData.sBody,
        'nID_SubjectMessageType' : 8,
        'nID_Subject' : nID_Subject
      }*/
    }, callback);

  } else {
    res.end();
  }

};

module.exports.findServiceMessages = function(req, res){
  var sToken = req.param('sToken');
  if (!!req.session.subject.nID || sToken!==null){
    var nID_Subject = (req.session && req.session!==null && req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) ? req.session.subject.nID : null;
    //var nID_Subject = req.session.subject.nID;

    var options = getOptions(req);
    var url = options.protocol + '://'
      + options.hostname
      + options.path
      + '/subject/message/getServiceMessages?sID_Order='
      + 'sID_Order=' + req.param('sID_Order')
      //+ '&nID_Subject=' + nID_Subject
      + (nID_Subject!==null?'&nID_Subject=' + nID_Subject:"") 
      + (sToken!==null?'&sToken=' + sToken:"") 
      + '&bAuth=true'
      ;

    var callback = function(error, response, body) {
      var resout = {
        messages : JSON.parse(body)
        //,nID_Subject: req.session.subject.nID
      };
      res.send(resout);
      res.end();
    };

    return request.get({
      'url': url,
      'auth': {
        'username': options.username,
        'password': options.password
      }
    }, callback);
  } else {
    res.end();
  }

};
