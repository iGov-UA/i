var options;
var request = require('request');
var _ = require('lodash');

var aServerCache = new NodeCache();


module.exports.getConfigOptions = function () {

	if (options)
		return options;

	var config = require('../../config/environment');
	var activiti = config.activiti;

	options = {
		protocol: activiti.protocol,
		hostname: activiti.hostname,
		port: activiti.port,
		path: activiti.path,
		username: activiti.username,
		password: activiti.password
	};

	return options;
};

module.exports.getRequestUrl = function (apiURL, sHost) {
	var options = this.getConfigOptions();
	return (sHost!==null && sHost !== undefined ? sHost : options.protocol + '://' + options.hostname + options.path) + apiURL;
};

module.exports.buildRequest = function (req, apiURL, params, sHost) {
  var sURL = this.getRequestUrl(apiURL, sHost);
	return {
		'url': sURL,
		'auth': this.getAuth(),
		'qs': _.extend(params, {nID_Subject: req.session.subject ? req.session.subject.nID : null})
	};
};

module.exports.getAuth = function () {
	var options = this.getConfigOptions();
	return {
		'username': options.username,
		'password': options.password
	};
};

module.exports.getDefaultCallback = function (res){
  return function (error, response, body) {
    if (error) {
      res.statusCode = 500;
      res.send(error);
    } else {
      res.statusCode = response.statusCode;
      res.send(body);
    }
    res.end();
  }
};

module.exports.sendGetRequest = function (req, res, apiURL, params, callback, sHost) {
	var _callback = callback ? callback : this.getDefaultCallback(res);
	var url = this.buildRequest(req, apiURL, params, sHost);
	return request(url, _callback);
};

module.exports.sendPostRequest = function (req, res, apiURL, params, callback, sHost) {
	var apiReq = this.buildRequest(req, apiURL, params, sHost);
  return this.executePostRequest(apiReq, res, callback);
};

module.exports.executePostRequest = function(apiReq, res, callback) {
  var _callback = callback ? callback : this.getDefaultCallback(res);
  return request.post(apiReq, _callback);
};

module.exports.sendDeleteRequest = function (req, res, apiURL, params, callback, sHost) {
  var _callback = callback ? callback : this.getDefaultCallback(res);
  var url = this.buildRequest(req, apiURL, params, sHost);
  return request.del(url, _callback);
};

/*
module.exports.buildRequestFromServer = function (sPagePath, oParams, sHost) {
  var sURL = this.getRequestUrl(sPagePath, sHost);
	return {
		'url': sURL,
		'auth': this.getAuth(),
		'qs': oParams
	};
};


module.exports.getRegionURL = function (res, nID) {
	//var _callback = callback ? callback : this.getDefaultCallback(res);
	//var url = this.buildRequest(req, apiURL, params, sHost);
	//return request(url, _callback);
	var sURL = this.buildRequestFromServer('/subject/getServer', {nID: nID});
	var oServer = request(sURL, this.getDefaultCallback(res));
        //var oServer = this.sendGetRequest(req, res, '/subject/getServer', _.extend(req.query, {nID: nID}));
        return oServer !== null ? oServer.sURL : null;
};
*/

module.exports.getServerRegionHost = function (nID_Server) {
            var oServer = this.getServerRegion(nID_Server);
            console.log("oServer="+oServer);
            var sHost=null;
            if(oServer && oServer!==null){
                sHost = oServer.sURL;
            }
            console.log("sHost="+sHost);
};

module.exports.getServerRegion = function (nID_Server) {
    var options = this.getConfigOptions();
    var sURL = options.protocol+'://'+options.hostname+options.path+'/subject/getServer?nID='+nID_Server;
    console.log("sURL="+sURL);
    var oServerCache = aServerCache.get(sURL) || null;
    //var structureValue = getStructureServer(nID_Server);
    if(oServerCache) {
        console.log("oServerCache="+oServerCache);
        return oServerCache;
    }
    process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
    return request.get({
            'url': sURL,
            'auth': {
                    'username': options.username,
                    'password': options.password
            }
    }, function(error, response, body) {
        console.log("body="+body);
        aServerCache.set(sURL, JSON.parse(body), 86400); //'api/places/server?nID='+nID_Server
        //console.log("body="+body);
        return JSON.parse(body);
    });
};
