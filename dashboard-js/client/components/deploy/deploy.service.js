/**
 * Created by GFalcon-UA on 28.04.2016.
 */
'use strict';

angular.module('dashboardJsApp').factory('deployService', function deployService($http, $q, $rootScope, uiUploader, Modal) {

  var setBP = function (url, files) {
    var deferred = $q.defer();

    var scope = $rootScope.$new(true, $rootScope);
    uiUploader.removeAll();
    uiUploader.addFiles(files);
    uiUploader.startUpload({
      url: url,
      concurrency: 1,
      onProgress: function (file) {
        scope.$apply(function () {

        });
      },
      onCompleted: function (file, response) {
        scope.$apply(function () {
          try {
            if (response === "SUCCESS") {
              deferred.resolve({
                file: file,
                response: response
              });
            } else {
              deferred.resolve({
                file: file,
                response: JSON.parse(response)
              });
            }
          } catch (e) {
            deferred.reject({
              err: response
            });
            Modal.inform.error()("При завантаженні файлу виникла помилка: " + e.message + " (response body: " + response + ")");
          } finally {
            if (response === "SUCCESS") {
              Modal.inform.success()("Завантаження файлу завершено");
            } else {
              var uploadResult = JSON.parse(response);
              if (uploadResult.code === "BUSINESS_ERR"){
                Modal.inform.error()("При завантаженні файлу виникла помилка: " + uploadResult.message);
              } else {
                Modal.inform.warning()(uploadResult.message);
              }
            }
            $rootScope.$broadcast("end-deploy-bp-file");
          }
        });
      },
    });
    return deferred.promise;
  };

  var getBP = function (url, sID, callback) {
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    var req = {
      method: 'GET',
      url: url,
      params: {
        sID: sID
      }
    };

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
  };

  var getListBP = function (url, oFilter, callback) {
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    if (!oFilter) {
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
      .success(function (response) {
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

  var removeListBP = function (url, oFilter, callback) {
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    if (!oFilter) {
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
      .success(function (response) {
        var data = angular.fromJson(response);
        deferred.resolve(data);
        return cb();
      })
      .error(function (err) {
        deferred.reject(err);
        return cb(err);
      }.bind(this));

    return deferred.promise;
  };

  return {
    setBP: function (files, sFileName) {
      return setBP('/api/deploy/setBP/' + sFileName, files);
    },

    getBP: function (sID, callback) {
      return getBP('/api/deploy/getBP', sID, callback);
    },

    getListBP: function (oFilter, callback) {
      return getListBP('/api/deploy/getListBP', oFilter, callback);
    },

    removeListBP: function (oFilter, callback) {
      return removeListBP('/api/deploy/removeListBP', oFilter, callback);
    }
  }
});
