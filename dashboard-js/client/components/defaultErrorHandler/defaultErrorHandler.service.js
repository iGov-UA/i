'use strict';

(function (angular) {

  var defaultSearchHandlerService = function (Modal) {
    return {
      handleError: function (response, msgMapping) {
        var msg = response.status + ' ' + response.statusText + '\n' + response.data;
        try {
          var data = null;
          try {
            data = JSON.parse(response.data);
          } catch (e) {
            data = response.data;
          }
          if (data !== null && data !== undefined && ('code' in data) && ('message' in data)) {
            if (msgMapping !== undefined && data.message in msgMapping)
              msg = msgMapping[data.message];
            else
              msg = data.code + ' ' + data.message;
          }
        } catch (e) {
          console.log(e);
        }
        Modal.inform.error()(msg);
      }
    }
  };

  defaultSearchHandlerService.$inject = ['Modal'];

  angular
    .module('dashboardJsApp')
    .factory('defaultSearchHandlerService', defaultSearchHandlerService);
})(angular);
