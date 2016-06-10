/**
 * Created by GFalcon-UA on 28.04.2016.
 */
'use strict';

angular.module('dashboardJsApp').factory('deployService', function deployService($http, $q) {

  var getBP = function (url, sID, callback) {
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    var request = {
      method: 'GET',
      url: url,
      params: {
        sID: sID
      }
    };

    $http(request)
      .success(function(response){
        var data = angular.fromJson(response);
        deferred.resolve(data);
        return cb();
      }).
      error(function (err) {
        deferred.reject(err);
        return cb(err);
      }.bind(this));

    return deferred.promise;
  };

  var getListBP = function (url, oFilter, callback) {
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    if(!oFilter){
      oFilter = {};
    }

    var request = {
      method: 'GET',
      url: url,
      data: {},
      params: {
        sID_BP: oFilter.sID_BP,
        sFieldType: oFilter.sFieldType,
        sID_Field: oFilter.sID_Field
      }
    };

    $http(request)
      .success(function(response){
        var data = angular.fromJson(response);
        deferred.resolve(data);
        return cb();
      }).
      error(function (err) {
        deferred.reject(err);
        return cb(err);
      }.bind(this));

    return deferred.promise;
  };

  var removeListBP = function(url, oFilter, callback){
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    if(!oFilter){
      oFilter = {};
    }

    var request = {
      method: 'DELETE',
      url: url,
      params: {
        sID_BP: oFilter.sID_BP,
        sFieldType: oFilter.sFieldType,
        sID_Field: oFilter.sID_Field,
        sVersion: oFilter.sVersion
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

    getBP: function (sID, callback) {
      return getListBP('/api/deploy/getBP', sID, callback);
    },

    getListBP: function (oFilter, callback) {
      return getListBP('/api/deploy/getListBP', oFilter, callback);
    },

    removeListBP: function (oFilter, callback) {
      return removeListBP('/api/deploy/removeListBP', oFilter, callback);
    }
  }
});
