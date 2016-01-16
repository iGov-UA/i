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

  this.getServiceMessages = function (id){
    var deferred = $q.defer();
    $http.get('./api/messages/service?sID_Order='+id).success(function (data, status) {
      deferred.resolve(data);
    });

    return deferred.promise;
  };


  this.postServiceMessage = function(id,body) {
    var data = {
      "sID_Order": id,
      "sBody": body
    };

    return $http.post('./api/messages/service', data).then(function(response) {
      return response.data;
    });
  };
});
