var request = require('request');

module.exports.mobileid= function(req, res) {

    var callback = function(error, response, body) {
        console.log(result)
        res.send(body);
        res.end();
    };

    var result = request.post({

        'url': "https://81.23.16.246/MSSP/restapi/services/Service_NR/formats/PKCS7/signTextTransaction",
        'qs': {
            //"msisdn": req.body.msisdn,
            "msisdn": "+380672340261",
            "dtbs":"Kari test",
            "apTransactionID": "_22222",
            "apInstant":"2017-04-01T09:12:01.000Z"
        }

    }, callback);
    
    return result;
    
};








