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
          try{
            message.osData = JSON.parse(message.sData);
          } catch (e){
            message.osData = {};
          }
        }
      });
      deferred.resolve(data);
    });
    return deferred.promise;
  };

  this.getSubjectMessageData = function (nID) {
    return $http.get('./api/messages/getSubjectMessageData', {
      params : {
        nID : nID
      }
    })
  };

  this.getMessageFile = function (nID) {
    var deferred = $q.defer();
    var oReq = new XMLHttpRequest();

    oReq.open("GET", "/api/messages/getMessageFile?nID="+nID, true);
    oReq.responseType = "arraybuffer";

    oReq.onload = function (oEvent) {
      var arrayBuffer = oReq.response;

      if (arrayBuffer) {
        var byteArray = new Uint8Array(arrayBuffer);

        deferred.resolve(byteArray);

      }else {
        deferred.reject({err: 'error: XMLHttpRequest something wrong'})
      }
    };

    oReq.send(null);

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
      if (file) {
        file.value = null;
      }
      return response.data;
    });
  };

});
