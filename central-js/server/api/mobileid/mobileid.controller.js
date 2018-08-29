var request = require('request')
  , config = require('../../config/environment')

module.exports.mobileid= function(req, res) {


    var callback = function(error, response, body) {
        //console.log('run callback', error, response, body); 

        res.send(body);
        res.end();
    };
  
    var apInstant = new Date();
    var apTransactionID ="trxID_" + Date.now();

    var resultServices = request.post({

        'url': config.mobileid.IP + "/MSSP/restapi/services/service_ds/formats/PKCS7/signTextTransaction",
        'headers': {
            'Content-Type': 'application/json; charset=utf-8'
        }, 
        'auth': {
            'username': config.mobileid.login,
            'password': config.mobileid.password
        },
        'json': true,
        'body': {
            "msisdn":"+380672340261",
            "dtbd":"Test message",
            "dtbs":apTransactionID,
            "apTransactionID": apTransactionID,
            "apInstant": apInstant
        }
    }, callback);

    var pingCount = 0;
    var maxPings = 5;
  
    var pingMobileId = setInterval(resultRestapi, 30000);
  
    function resultRestapi (){

        if (
            resultServices.statusCode = 504 ||
            pingCount > maxPings) {
            clearInterval(pingMobileId);
        } else {
            pingCount++;
            request.get({
                'url': config.mobileid.IP + "/MSSP/restapi/status/" + resultServices.transactionID,
                'auth': {
                    'username': config.mobileid.login,
                    'password': config.mobileid.password
                }     
        }, callback)
    };
}
    return resultServices;      
    
};