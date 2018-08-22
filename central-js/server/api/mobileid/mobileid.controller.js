var request = require('request')
  , config = require('../../config/environment')

module.exports.mobileid= function(req, res) {


    var callback = function(error, response, body) {
        console.log(resultServices);
        res.send(body);
        res.end();
    };

    var apInstant = new Date();

    var resultServices = request.post({

        'url': config.mobileid.IP + "/MSSP/restapi/services/service_ds/formats/PKCS7/signTextTransaction",
        'auth': {
            'username': config.mobileid.login,
            'password': config.mobileid.password
        },
        'qs': {
            //"msisdn": req.body.msisdn,
            "msisdn": "+380672340261",
            "dtbs":"Kari test",
            "apTransactionID": "_222228888888888888888",
            "apInstant": apInstant
        }

    }, callback);
    // var resultRestapi = request.get({

    //     'url': "https://81.23.16.246:8080//MSSP/restapi/status/222228888888888888888",
    //     'auth': {
    //         'username': "kyivstar",
    //         'password': "kyivstar"
    //       }

    // }, callback);

    return resultServices;  
    
    
    
};