/**
 * Created by GFalcon-UA on 28.04.2016.
 */
'use strict';

angular.module('dashboardJsApp').factory('deployService', function deployService($http, $q) {
  function simpleHttpPromise(req, callback) {
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    $http(req).then(
      function (response) {
        deferred.resolve(response.data);
        return cb();
      },
      function (response) {
        deferred.reject(response);
        return cb(response);
      }.bind(this));
    return deferred.promise;
  }

  return {
    setBP: function () {
      //todo добавление файла БП
    },

    getBP: function () {
      //todo загрузка файла БП
    },

    getListBP: function () {
      return simpleHttpPromise({
        method: 'GET',
        url: '/api/deploy/getListBP'
      });
    },

    removeListBP: function () {
      //todo удаление списка БП
    }
  }
});
