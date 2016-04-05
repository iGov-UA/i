angular.module('app').service('FeedbackService', function ($http, $q) {

    this.getFeedback = function (id, token){
      var deferred = $q.defer();
      $http.get('./api/messages/feedback?sID_Order='+id+'&sToken='+token+'').success(function (data, status) {
        if (data.code === 'SYSTEM_ERR' || data.code==='BUSINESS_ERR'){
          deferred.reject(data);
        } else {
          deferred.resolve(data);
        }
      });

      return deferred.promise;
    };

    this.postFeedback =function(id,token, body){
      var data = {
        "sID_Order": id,
        "sToken": token,
        "sBody": body
      };

      $http.post('./api/messages/feedback', data).then(function(response) {});
    };

});
