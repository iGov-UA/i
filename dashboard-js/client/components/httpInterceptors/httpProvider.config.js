/**
 * Created by gfalcon on 01.02.17.
 */
(function () {
  'use strict';
  angular.module('dashboardJsApp')
    .config(['$httpProvider',
      function ($httpProvider) {
    $httpProvider.interceptors.push('errorInterceptor');
  }])
})();
