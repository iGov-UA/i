/**
 * Created by gfalcon on 01.02.17.
 */
(function () {
  'use strict';
  angular.module('dashboardJsApp')
    .factory('errorInterceptor', ['$q', '$rootScope', '$injector', '$sce',
      function ($q, $rootScope, $injector, $sce) {

      var httpInterceptors = {

        'requestError': function(rejection) {
          var Modal= $injector.get('Modal');
          Modal.inform.error()(rejection.data.message || rejection.data.serverMessage || (rejection.statusText === '' ? 'Виникла помилка: ' + rejection.status : rejection.statusText));
          return $q.reject(rejection);
        },

        'response': function(response) {
          var Modal= $injector.get('Modal');
          if(response.status < 400 || (response.data && response.data.statusCode < 400)) {
            return response || $q.when(response);
          }

          Modal.inform.warning()(response.data.message || response.data.serverMessage || angular.toJson(response));
          return response || $q.when(response);
        },

        'responseError': function(rejection) {

          var Modal= $injector.get('Modal');
          var $http = $injector.get('$http');

          /**
           * Потеряна связь с сервером NodeJS
           * либо отключился интернет
           */
          if(rejection.status == -1) {
            alert("Виникла помилка при спробі звернення до сервера");
            return rejection;
          }

          if(rejection.status == 401) {
            console.log("Unauthorized");
            return rejection;
          } else if (rejection.data && rejection.data.statusCode == 401) {
            return $q.reject(rejection);
          }
          try {
            rejection.data = angular.fromJson(rejection.data);
          } catch (e) {
            
          }
          Modal.inform.error()(rejection.data.message || rejection.data.serverMessage || (rejection.statusText === '' ? 'Виникла помилка: ' + rejection.status : rejection.statusText));
          return $q.reject(rejection);
        }
      };

      return httpInterceptors;
    }])
})();
