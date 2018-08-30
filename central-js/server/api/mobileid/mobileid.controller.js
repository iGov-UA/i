var request = require('request')
  , config = require('../../config/environment')

module.exports.mobileid= function(req, res) {

var transactionID;
var pingCount = 1;

    var callback = function(error, response, body) {
        console.log(body);  
        if (body == null) {
            res.send(body);   
            res.end();   
        }
        else if (body.statusCode == "504" && pingCount < 100) {
            transactionID = body.transactionID;
            resultRestapi (transactionID, callback);
        } else {
            res.send(body);   
            res.end();       
        }        

    };
  
    var apInstant = new Date();
    var apTransactionID ="trxID_" + Date.now();

    var resultServices = request.post({

        'url': config.mobileid.IP + "/MSSP/restapi/services/service_ds/formats/PKCS7/signTextTransaction",
        //'url': "https://httpstat.us/200",
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



  function resultRestapi () {

    // var pingCount = 1;
    // var maxPings = 3;

    // console.log ("ид транзакции", transactionID); 
    // var pingMobileId = setInterval(pingResultRestapi, 3000);

  
    // function pingResultRestapi (){

        // if (pingCount > maxPings) {
        //     clearInterval(pingMobileId);
        //     console.log ("Пинг закончен");
            
            //return req;  
        // } else {
            pingCount++;
            request.get({
                'url': config.mobileid.IP + "/MSSP/restapi/status/" + transactionID,
                //'url': "https://httpstat.us/202",
                'headers': {
                    'Content-Type': 'application/json; charset=utf-8'
                }, 
                'auth': {
                    'username': config.mobileid.login,
                    'password': config.mobileid.password
                },
                'json': true

        }, callback) 
    }; 
// }
        
    
       
//    };
    
}; 