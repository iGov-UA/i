'use strict';

angular.module('dashboardJsApp')
  .factory('processes', function processes($http, $q) {
    var idToProcessMap = function (data) {
      var map = {};
      for (var i = 0; i < data.length; i++) {
          var sKey=data[i].key;
          map[sKey] = data[i];
      }
      return map;
    };

    var processesDefinitions = null;

    return {
      getUserProcesses: function (isOldService) {
        var params = isOldService ? {params: {isOld: isOldService}} : '';

        return $http.get('/api/processes/getLoginBPs', params)
          .then(function (response) {
            try {
              var result = JSON.parse(response.data);
              return result;
            }
            catch (error) {
              return result;
            }
          });
      },

      list: function () {
        var deferred = $q.defer();

        if (processesDefinitions !== null) {
          deferred.resolve(processesDefinitions);
        } else {
          var req = {
            method: 'GET',
            url: '/api/processes',
            cache: true,
            data: {}
          };

          $http(req).
            success(function (result) {
              processesDefinitions = idToProcessMap(result.data);
              deferred.resolve(processesDefinitions);
            }).
            error(function (err) {
              deferred.reject(err);
            }.bind(this));

        }

        return deferred.promise;
      },

      getProcessName: function (processDefinitionId) {
            var sID=processDefinitionId;
            if(sID === null || sID === undefined){
                return sID;
            }
              var nAt=sID.indexOf("\:");
              if(nAt>=0){
                sID=sID.substr(0,nAt);
              }
            if (processesDefinitions && processesDefinitions[sID]) {
              return processesDefinitions[sID].name;
            } else {
              return sID;
            }
      },

      getBPs_ForExport: function () {

        return $http.get('/api/processes/getBPs_ForExport')
          .then(function (response) {
            try {
              var result = JSON.parse(response.data);
              return result;
            }
            catch (error) {
              return result;
            }
          });
      },

      getProcessID: function (sID) {
        return processesDefinitions[sID].id;
      }

    };
  });
