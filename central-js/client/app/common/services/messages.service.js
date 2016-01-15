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
});
