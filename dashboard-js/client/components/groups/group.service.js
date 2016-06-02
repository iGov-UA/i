/**
 * Created by ijmac on 18.05.16.
 */
'use strict';

angular.module('dashboardJsApp')
  .factory('group', function services($http, $q){
    var getGroups = function(url, sLogin, callback){
      var cb = callback || angular.noop;
      var deferred = $q.defer();

      var request = {
        method: 'GET',
        url: url,
        data: {},
        params: {
          sLogin: sLogin
        }
      };

      $http(request)
        .success(function(data){
          var groups = angular.fromJson(data);
          deferred.resolve(groups);
          return cb();
      })
        .error(function(err){
          deferred.reject(err);
          return cb(err);
      }.bind(this));

      return deferred.promise;
    };

    var getGroup = function(url, sID, callback){

    };

    var setGroup = function(url, sID, sName, callback){
      var cb = callback || angular.noop;
      var deferred = $q.defer();

      var request = {
        method: 'POST',
        url: url,
        params: {
          sID: sID,
          sName: sName
        }
      };

      $http(request)
        .success(function(response){
          var group = angular.fromJson(response);
          deferred.resolve(group);
          return cb();
        })
        .error(function(err){
          deferred.reject(err);
          return cb(err);
        }.bind(this));

      return deferred.promise;
    };

    var deleteGroup = function(url, sID, callback){
      var cb = callback || angular.noop;
      var deferred = $q.defer();

      var request = {
        method: 'DELETE',
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
        })
        .error(function(err){
          deferred.reject(err);
          return cb(err);
        }.bind(this));

      return deferred.promise;
    };

    return {
      getGroups: function(sLogin, callback){
        return getGroups('/api/users/groups/getGroups', sLogin, callback);
      },
      getGroup: function(sID, callback){
        return getGroup('/api/users/groups/getGroup', sID, callback);
      },
      setGroup: function(sID, sName, callback){
        return setGroup('/api/users/groups/setGroup', sID, sName, callback);
      },
      deleteGroup: function(sID, callback){
        return deleteGroup('/api/users/groups/deleteGroup', sID, callback);
      }
    }

  });
