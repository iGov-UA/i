angular.module('app').service('FeedbackService', function ($http, $q) {

  this.getFeedback = function (id, token) {
    var deferred = $q.defer();
    $http.get('./api/messages/feedback?sID_Order=' + id + '&sToken=' + token + '').success(function (data, status) {
      if (data.code === 'SYSTEM_ERR' || data.code === 'BUSINESS_ERR') {
        deferred.reject(data);
      } else {
        deferred.resolve(data);
      }
    });

    return deferred.promise;
  };

  this.postFeedback = function (id, token, body) {
    var data = {
      "sID_Order": id,
      "sToken": token,
      "sBody": body
    };

    $http.post('./api/messages/feedback', data).then(function (response) {
    });
  };

  this.getFeedbackForService = function (serviceId, id, token) {
    var deferred = $q.defer();
    $http.get('./api/service/' + serviceId + '/feedback?sID_Order=' + id + '&sID_Token=' + token + '').then(function (data) {
      if (data.code === 'SYSTEM_ERR' || data.code === 'BUSINESS_ERR') {
        deferred.reject(data);
      } else {
        deferred.resolve(data);
      }
    });

    return deferred.promise;
  };

  this.getFeedbackListForService = function (serviceId, nRowsMax, nID__LessThen_Filter) {
    var deferred = $q.defer();
    $http.get('./api/service/' + serviceId + '/feedback', {
      params: {
        nID__LessThen_Filter: nID__LessThen_Filter,
        nRowsMax: nRowsMax
      }
    }).then(function (data) {
      if (data.code === 'SYSTEM_ERR' || data.code === 'BUSINESS_ERR') {
        deferred.reject(data);
      } else {
        deferred.resolve(data);
      }
    });

    return deferred.promise;
  };

  this.postFeedbackForService = function (feedbackParams) {
    var deferred = $q.defer();

    $http.post('./api/service/' + feedbackParams.nID_Service + '/feedback', feedbackParams).then(function (response) {
      if (response.code === 'SYSTEM_ERR' || response.code === 'BUSINESS_ERR') {
        deferred.reject(response);
      } else {
        deferred.resolve(response);
      }
    });

    return deferred.promise;
  };

  this.postFeedbackAnswerForService = function (feedbackParams) {
    var deferred = $q.defer();
    var data = {
      'sID_Token': feedbackParams.sID_Token,
      'sBody': feedbackParams.sBody,
      'nID_SubjectMessageFeedback': feedbackParams.nID_SubjectMessageFeedback,
      'nID_Subject': feedbackParams.nID_Subject,
      'sAuthorFIO': feedbackParams.sAuthorFIO
    };

    $http.post('./api/service/' + feedbackParams.nID_Service + '/feedbackAnswer', data).then(function (response) {
      if (response.code === 'SYSTEM_ERR' || response.code === 'BUSINESS_ERR') {
        deferred.reject(response);
      } else {
        deferred.resolve(response);
      }
    });

    return deferred.promise;
  };

});
