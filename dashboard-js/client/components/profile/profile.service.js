'use strict';

angular.module('dashboardJsApp')
  .factory('Profile', function Profile($location, $rootScope, $http, Base64, $cookieStore, $q) {

    var currentUser = {};
    var sessionSettings;

    if ($cookieStore.get('user')) {
      currentUser = $cookieStore.get('user');
    }

    if ($cookieStore.get('sessionSettings')) {
      sessionSettings = $cookieStore.get('sessionSettings');
    }

    return {
      getSubjects: function(userLogin, callback){
        var cb = callback || angular.noop;
        var deferred = $q.defer();
        var req = {
          method: 'GET',
          url: '/api/profile/getSubjects/'+userLogin+'/1'
        };

        $http(req).
        success(function (data) {
          deferred.resolve(data);
          return cb();
        }).
        error(function (err) {
          this.logout();
          deferred.reject(err);
          return cb(err);
        }.bind(this));

        return deferred.promise;
      },
      changePassword: function (sLoginOwner, sPasswordOld, sPasswordNew, callback) {
        var cb = callback || angular.noop;
        var deferred = $q.defer();

        $http.post('/api/profile/changePassword' , {
          sLoginOwner: sLoginOwner,
          sPasswordOld: sPasswordOld,
          sPasswordNew: sPasswordNew
        }).
        success(function (data) {
          deferred.resolve(data);
          return cb();
        }).
        error(function (err) {
          deferred.reject(err);
          return cb(err);
        }.bind(this));

        return deferred.promise;
      }
    };
  });
