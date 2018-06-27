'use strict';

angular.module('dashboardJsApp')
  .factory('Share', function Share($location, $rootScope, $http, Base64, $cookieStore, $q) {

     return {
      simpleHttpPromise: function (req, callback) {
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
      },

      createDocumentPDF: function (document, callback) {
        var cb = callback || angular.noop;
        var deferred = $q.defer();

        $http.post('/api/share/createDocumentPDF', {
          file: document.file,
          sID_Token: document.sID_Token
        }).success(function (data) {
          deferred.resolve(data);
          return cb();
        }).error(function (err) {
          deferred.reject(err);
          return cb(err);
        }.bind(this));

        return deferred.promise;
      },
      getDocumentPDF: function (document, callback) {
        var cb = callback || angular.noop;
        var deferred = $q.defer();

        $http.get('./api/share/getDocumentPDF', document
        ).success(function (data, header) {
          deferred.resolve(data, header);
         // console.log(data, header);
          return cb();
        }).error(function (err) {
          deferred.reject(err);
          return cb(err);
        }.bind(this));

        return deferred.promise;

      },
      getProcessAttach: function (document, callback) {
        var cb = callback || angular.noop;
        var deferred = $q.defer();

        $http.get('./api/share/getProcessAttach', document
        ).success(function (data) {
          deferred.resolve(data);
          return cb();
        }).error(function (err) {
          deferred.reject(err);
          return cb(err);
        }.bind(this));

        return deferred.promise;

      },
       getDocumentImageFileSigned: function (document, callback) {
        var cb = callback || angular.noop;
        var deferred = $q.defer();

        $http.get('./api/share/getDocumentImageFileSigned', document
        ).success(function (data) {
          deferred.resolve(data);
          return cb();
        }).error(function (err) {
          deferred.reject(err);
          return cb(err);
        }.bind(this));

        return deferred.promise;

      },
       setDocumentImageFileSign: function (document, callback) {
        var cb = callback || angular.noop;
        var deferred = $q.defer();

        $http.post('./api/share/setDocumentImageFileSign', {
          sSign: document.sSign,
          sID_SignType: document.sID_SignType,
          sSignData_JSON: document.sSignData_JSON,
          nID_DocumentImageFile: document.nID_DocumentImageFile,
          sSecret_DocumentImageFile: document.sSecret_DocumentImageFile
        }).success(function (data) {
          deferred.resolve(data);
          return cb();
        }).error(function (err) {
          deferred.reject(err);
          return cb(err);
        }.bind(this));

        return deferred.promise;

      },
      getDocumentImageFileVO: function (document, callback) {
        var cb = callback || angular.noop;
        var deferred = $q.defer();

        $http.get('./api/share/getDocumentImageFileVO', document)
          .success(function (data) {
            deferred.resolve(data);
            return cb();
          }).error(function (err) {
          deferred.reject(err);
          return cb(err);
        }.bind(this));

        return deferred.promise;
      },

      setDocumentImageFile: function (document, callback) {
        var cb = callback || angular.noop;
        var deferred = $q.defer();

        // var fd = new FormData();
        // fd.append('file', document.file);
        //
        // var obj = {file: fd, sID_Token: document.sID_Token};

        $http.post('./api/share/setDocumentImageFile', document)
          .success(function (data) {
            deferred.resolve(data);
            return cb();
          }).error(function (err) {
          deferred.reject(err);
          return cb(err);
        }.bind(this));

        return deferred.promise;
      }
    }
});
