/**
 * Created by GFalcon-UA on 28.04.2016.
 */
'use strict';

angular.module('dashboardJsApp').factory('deployService', function deployService($http, $q) {

  var getListBP = function (url, filter, callback) {
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    var request = {
      method: 'GET',
      url: url,
      data: {},
      params: {
        sID_BP: filter.sID_BP,
        sFieldType: filter.sFieldType,
        sID_Field: filter.sID_Field
      }
    };

    $http(request).
      success(function (data) {
        var slots = angular.fromJson(data);
        slots.forEach(clearAndConvert);
        deferred.resolve(slots);
        return cb();
      }).
      error(function (err) {
        deferred.reject(err);
        return cb(err);
      }.bind(this));

    return deferred.promise;
  };

  var removeListBP = function(url, filter, callback){
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    var request = {
      method: 'DELETE',
      url: url,
      params: {
        sID_BP: filter.sID_BP,
        sFieldType: filter.sFieldType,
        sID_Field: filter.sID_Field
      }
    };

    $http(request)
      .success(function(response){
        var data = angular.fromJson(response);
        deferred.resolve(data);
        return cb();
      })
      .error(function(err){
        deferred.reject(err);
        return cb(err);
      }.bind(this));

    return deferred.promise;
  };

  return {
    setBP: function () {
      //todo добавление файла БП
    },

    getBP: function () {
      //todo загрузка файла БП
    },

    getListBP: function (filter, callback) {
      return getListBP('/api/deploy/getListBP', filter, callback);
    },

    removeListBP: function (filter, callback) {
      return removeListBP('/api/deploy/removeListBP', filter, callback);
    }
  }
});
