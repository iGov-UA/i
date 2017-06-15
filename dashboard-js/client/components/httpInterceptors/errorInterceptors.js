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
            if(rejection.config.url.indexOf('TWAIN@Web') > 0){
              Modal.inform.warning()('Для роботи зі сканером необхідно встановити програму TWAIN@Web та правильно налаштувати посилання на IP-адресу та номер порту компьютера, до якого пыдключено сканер (за замовчуванням це http://127.0.0.1:9005). Якщо сканер пыдключено не до локального компьютера, перевірте налаштування брендмауера. Програму TWAIN@Web рекомендуємо скачати з нашого репозиторію за наступним посиланням: https://github.com/e-government-ua/i/tree/test/twain-scanner/install');
            } else {
              alert("Виникла помилка при спробі звернення до сервера");
            }
            return rejection;
          }

          if(rejection.status == 401) {
            console.log("Unauthorized");
            return rejection;
          } else if (isTemplateFileNotFound(rejection)){
            return rejection;
          } else if (rejection.data && rejection.data.statusCode == 401) {
            return $q.reject(rejection);
          }
          try {
            rejection.data = angular.fromJson(rejection.data);
          } catch (e) {

          }
          Modal.inform.error()(rejection.data.message || rejection.data.serverMessage || (rejection.statusText === '' ? 'Виникла помилка: ' + rejection.status : rejection.statusText));

          function isTemplateFileNotFound(rejection){
            var isTrue = true;
            isTrue = isTrue && rejection.status == 403;
            isTrue = isTrue && rejection.data && rejection.data.code && rejection.data.code === 'BUSINESS_ERR';
            isTrue = isTrue && rejection.data && rejection.data.message && rejection.data.message === 'oURL == null';
            isTrue = isTrue && rejection.config.url.indexOf('api/reports/template') >= 0;
            return isTrue;
          }

          return $q.reject(rejection);
        }
      };

      return httpInterceptors;
    }])
})();
