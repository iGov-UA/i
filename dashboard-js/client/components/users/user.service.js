/**
 * Created by ijmac on 18.05.16.
 */
'use strict';

angular.module('dashboardJsApp')
  .factory('user', function services($http, $q){
    var getUsers = function(url, sID_Group, callback){
      var cb = callback || angular.noop;
      var deferred = $q.defer();

      var request = {
        method: 'GET',
        url: url,
        data: {},
        params: {
          sID_Group: sID_Group
        }
      };

      $http(request)
        .success(function(data){
          var users = angular.fromJson(data);
          deferred.resolve(users);
          return cb();
        })
        .error(function(err){
          deferred.reject(err);
          return cb(err);
        }.bind(this));

      return deferred.promise;
    };

    var getUser = function(url, sLogin, callback){
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
          var users = angular.fromJson(data);
          deferred.resolve(users);
          return cb();
        })
        .error(function(err){
          deferred.reject(err);
          return cb(err);
        }.bind(this));

      return deferred.promise;
    };

    var setUser = function(url, sLogin, sPassword, sName, sDescription, sEmail, callback){
      var cb = callback || angular.noop;
      var deferred = $q.defer();

      var request = {
        method: 'POST',
        url: url,
        data: {},
        params: {
          sLogin: sLogin,
          sPassword: sPassword,
          sName: sName,
          sDescription: sDescription,
          sEmail: sEmail
        }
      };

      $http(request)
        .success(function(data){
          var users = angular.fromJson(data);
          deferred.resolve(users);
          return cb();
        })
        .error(function(err){
          deferred.reject(err);
          return cb(err);
        }.bind(this));

      return deferred.promise;

    };

    var addUser = function(url, sID_Group, sLogin, callback){
      var cb = callback || angular.noop;
      var deferred = $q.defer();

      var request = {
        method: 'POST',
        url: url,
        data: {},
        params: {
          sID_Group: sID_Group,
          sLogin: sLogin
        }
      };

      $http(request)
        .success(function(data){
          var users = angular.fromJson(data);
          deferred.resolve(users);
          return cb();
        })
        .error(function(err){
          deferred.reject(err);
          return cb(err);
        }.bind(this));

      return deferred.promise;

    };

    var removeUser = function(url, sID_Group, sLogin, callback){

      var cb = callback || angular.noop;
      var deferred = $q.defer();

      var request = {
        method: 'DELETE',
        url: url,
        data: {},
        params: {
          sID_Group: sID_Group,
          sLogin: sLogin
        }
      };

      $http(request)
        .success(function(data){
          var users = angular.fromJson(data);
          deferred.resolve(users);
          return cb();
        })
        .error(function(err){
          deferred.reject(err);
          return cb(err);
        }.bind(this));

      return deferred.promise;

    };

    var deleteUser = function(url, sLogin, callback){

      var cb = callback || angular.noop;
      var deferred = $q.defer();

      var request = {
        method: 'DELETE',
        url: url,
        data: {},
        params: {
          sLogin: sLogin
        }
      };

      $http(request)
        .success(function(data){
          var users = angular.fromJson(data);
          deferred.resolve(users);
          return cb();
        })
        .error(function(err){
          deferred.reject(err);
          return cb(err);
        }.bind(this));

      return deferred.promise;

    };

    return {
      getUsers: function(sID_Group, callback){
        return getUsers('/api/users/getUsers', sID_Group, callback);
      },
      getUser: function(sLogin, callback){
        return getUser('/api/users/getUser', sLogin, callback);
      },
      setUser: function(sLogin, sPassword, sName, sDescription, sEmail, callback){
        return setUser('/api/users/setUser', sLogin, sPassword, sName, sDescription, sEmail, callback);
      },
      addUser: function(sID_Group, sLogin, callback){
        return addUser('/api/users/setUserGroup', sID_Group, sLogin, callback);
      },
      removeUser: function(sID_Group, sLogin, callback){
        return removeUser('/api/users/removeUserGroup', sID_Group, sLogin, callback);
      },
      deleteUser: function(sLogin, callback){
        return deleteUser('/api/users/deleteUser', sLogin, callback);
      }
    };
  });
