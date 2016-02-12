angular.module('app').service('MessagesService', function($http, $q) {

  this.setMessage = function(message, userMessage) {
    var data = {
      "sMail": message.sMail,
      "sHead": message.sHead,
      "sBody": message.sBody
    };
    return $http.post('./api/messages', data).then(function(response) {
      return response.data;
    });
  };

  this.getServiceMessages = function (sID_Order, sToken){
    var deferred = $q.defer();
    $http.get('./api/messages/service?sID_Order='+sID_Order+(sToken?'&sToken='+sToken:"")).success(function (data, status) {
      angular.forEach(data.messages, function (message) {
        if (message.hasOwnProperty('sData') && message.sData.length > 1) {
          message.osData = JSON.parse(message.sData);
        }
      });
      deferred.resolve(data);
    });
    return deferred.promise;
  };

  this.postServiceMessage = function(sID_Order,sComment,sToken,file) {
    var oData = {
      "sID_Order": sID_Order,
      "sBody": sComment
    };
    if(sToken){
        oData = $.extend(oData,{sToken:sToken});
    }
    if (file && file.value) {
      oData.sID_File = file.value.id;
      oData.sFileName = file.fileName;
    }
    return $http.post('./api/messages/service', oData).then(function(response) {
      file.value = null;
      return response.data;
    });
  };

});
