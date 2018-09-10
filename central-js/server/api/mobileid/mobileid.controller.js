var request = require('request')
  , config = require('../../config/environment')

module.exports.mobileid= function(req, res) {

var transactionID;
var pingCount = 0;

    var callback = function(error, response, body) {
        console.log('mobileid', body);  
        console.log('mobileid', req);  
        if (body == null) {
            res.send(body);   
            res.end();   
        }
        else if (body.statusCode == 504 && pingCount < 30) {
            
            transactionID = body.transactionID;
            setTimeout(resultRestapi, 10000);
        } else {
            res.send(body);   
            res.end(); 
    
        }        

    };
  
    var apInstant = new Date();
    var apTransactionID ="trxID_" + Date.now();

    var resultServices = request.post({

        'url': config.mobileid.IP + "/MSSP/restapi/services/service_ds/formats/PKCS1/signTextTransaction",
        'headers': {
            'Content-Type': 'application/json; charset=utf-8'
        }, 
        'auth': {
            'username': config.mobileid.login,
            'password': config.mobileid.password
        },
        'json': true,
        'body': {
            "msisdn": req.body.msisdn,
            "dtbd": "Test message",
            "dtbs": apTransactionID,
            "apTransactionID": apTransactionID,
            "apInstant": apInstant
        }
    }, callback);



  function resultRestapi () {

            pingCount++;
            request.get({
                'url': config.mobileid.IP + "/MSSP/restapi/status/" + transactionID,
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