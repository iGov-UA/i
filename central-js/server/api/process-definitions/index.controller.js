var request = require('request');

//var activiti = require('../../components/activiti');

module.exports.index = function(options, callback) {
        var sHost = options.params.url;
        var nID_Server = options.params.nID_Server;
        //module.exports.getRegionURL
        //activiti.exports.getRegionURL()
        console.log("nID_Server="+nID_Server);
        if(nID_Server!==null){
            //router.get('/server', function(req, res, next, nID_Server) {
            //var oPlacesController = require('./index.controller');
            var oPlacesController = require('../places/index.controller');
            var oServer = oPlacesController.getServer(options, null, nID_Server);//req.query.nID_Server || null
            //res.send(oServer);
            //res.end();
            if(oServer!==null){
                sHost = oServer.sURL;
                console.log("sHost="+sHost);
            }
            //var oPlaces = require('../places');
            //oPlaces.exports.getRegionURL()
        }
	var sURL = sHost+'service/repository/process-definitions';
	console.log(sURL);
        

	return request.get({
		'url': sURL,
		'auth': {
			'username': options.username,
			'password': options.password
		},
		'qs': {
			'latest': options.params.latest
			,'size': 1000
		}
	}, callback);
};

/*module.exports.getCountryList = function (req, res) {
  activiti.sendGetRequest(req, res, '/object/place/getCountries', _.extend(req.query, req.params));
};*/


/*




*/