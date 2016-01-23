var request = require('request');
var config = require('../../config/environment');
var _ = require('lodash');
var activiti = require('../../components/activiti');

var oUtil = require('../../components/activiti');

function getOptions() {
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

module.exports.searchOrderBySID = function (req, res) {
    
    var nID_Subject = (oUtil.bExist(req.session) && req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) ? req.session.subject.nID : null;

    //TODO: Temporary (back compatibility)
    var sID_Order = req.params.sID_Order;
    /*if(sID_Order.indexOf("-")<0){
        sID_Order="0-"+sID_Order;
    }*/
    
    var oDateNew = {
            'sID_Order': sID_Order
            //, 'bAuth': true
    };
    var sToken = req.query.sToken;
    /*if(oUtil.bExist(sToken)){
        oDateNew = $.extend(oDateNew,{sToken: sToken});
    }
    if(oUtil.bExist(nID_Subject)){
        oDateNew = $.extend(oDateNew,{nID_Subject: nID_Subject});
    }*/
    
    var options = getOptions();
    var sURL = getUrl('/action/event/getHistoryEvent_Service');
    var callback = function(error, response, body) {
        var oData = JSON.parse(body);
        
        var nID_Subject_Auth = null;
        if(oUtil.bExist(sToken)){
            if(sToken===oData.sToken){
                nID_Subject_Auth = oData.nID_Subject;
            }
        }
        if(nID_Subject_Auth !== oData.nID_Subject && oUtil.bExist(nID_Subject)){
            nID_Subject_Auth = nID_Subject;
        }
        if(oUtil.bExist(nID_Subject_Auth)){
            oData = _.extend(oData, {nID_Subject_Auth: nID_Subject_Auth})
        }
        if(nID_Subject_Auth !== oData.nID_Subject){
            oData.sToken=null;
            oData.soData="[]";
            oData.sBody=null;
        }
        
        res.send(oData);
        res.end();
    };
    
    return request.get({
        'url': sURL,
        'auth': {
            'username': options.username,
            'password': options.password
        },
        'qs': oDateNew /*{
            'sID_Order': sID_Order,
            'sToken': req.query.sToken
        }*/
    }, callback);
};

module.exports.setTaskAnswer = function(req, res) {
  //if (req.session && req.session!==null && req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) {
    var options = getOptions();
    var url = getUrl('/action/task/setTaskAnswer_Central');///rest
    var callback = function(error, response, body) {
      res.send(body);
      res.end();
    };

    var oDataNew = req.body;
    if (oUtil.bExist(req.session) && req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) {
        oDataNew = _.extend(oDataNew, {nID_Subject: req.session.subject.nID});
    }
    oDataNew = _.extend(oDataNew, {bAuth: true});

    return request.get({
      'url': url,
      'auth': {
        'username': options.username,
        'password': options.password
      },
      'qs': oDataNew
    }, callback);
  //}
};

function getUrl(apiURL) {
    var options = getOptions();
    return options.protocol + '://' + options.hostname + options.path + apiURL;
}

module.exports.getCountOrders = function (req, res) {
  //if (req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) {
  if (req.session && req.session!==null && req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) {
    var params = req.params;
    params = _.extend(params, {nID_Subject: req.session.subject.nID});
    activiti.sendGetRequest(req, res, '/action/event/getCountOrders', _.extend(req.query, params));
  }
};

module.exports.getStartFormByTask = function(req, res) {
  if (req.session && req.session!==null && req.session.hasOwnProperty('subject') && req.session.subject.hasOwnProperty('nID')) {
    var params = req.params;
    params = _.extend(params, {nID_Subject: req.session.subject.nID});
    activiti.sendGetRequest(req, res, '/action/task/getStartFormByTask_Central', _.extend(req.query, params));
  }
};
