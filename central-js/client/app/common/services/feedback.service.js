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

    this.getFeedbackForService = function (serviceId, id, token) {
      var deferred = $q.defer();
      $http.get('./api/service/' + serviceId + '/feedback?sID_Order='+id+'&sID_Token='+token+'').then(function(data){
        if(data.code === 'SYSTEM_ERR' || data.code==='BUSINESS_ERR'){
          deferred.reject(data);
        }else{
          deferred.resolve(data);
        }
      });

      return deferred.promise;
    };

    this.getFeedbackListForService = function (serviceId) {
      var deferred = $q.defer();
      $http.get('./api/service/' + serviceId + '/feedback').then(function(data){
        if(data.code === 'SYSTEM_ERR' || data.code === 'BUSINESS_ERR'){
          deferred.reject(data);
        }else{
          deferred.resolve(data);
        }
      });

      return deferred.promise;
    };

    this.postFeedbackForService =function(nId,
                                          serviceId,
                                          token,
                                          body,
                                          sAuthorFIO,
                                          sMail,
                                          sHead,
                                          nID_Rate,
                                          sAnswer){
      var deferred = $q.defer();
      var data = {
        'sToken': token,
        'sBody': body,
        'sID_Source': nId,
        'sAuthorFIO': sAuthorFIO,
        'sMail': sMail,
        'sHead': sHead,
        'nID_Rate': nID_Rate,
        'nID_Service': serviceId,
        'sAnswer': sAnswer
      };

      $http.post('./api/service/'+serviceId+'/feedback', data).then(function(response) {
        if(response.code === 'SYSTEM_ERR' || response.code === 'BUSINESS_ERR'){
          deferred.reject(response);
        }else{
          deferred.resolve(response);
        }
      });

      return deferred.promise;
    };

});
