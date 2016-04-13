'use strict';
angular.module('dashboardJsApp', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ui.router',
  'ngRoute',
  'ngIdle',
  'ngStorage',
  'ui.bootstrap',
  'ui.uploader',
  'ui.event',
  'angularMoment',
  'ngClipboard'
]).config(function($urlRouterProvider, $locationProvider) {
  $urlRouterProvider
    .otherwise('/');
  $locationProvider.html5Mode(true);
}).run(function(amMoment, $rootScope, Modal) {
  amMoment.changeLocale('uk');
  $rootScope.$on('$stateChangeError', function () {
    Modal.inform.error()('Виникла помилка. Зверніться будь ласка у технічну підтримку.');
    console.warn('Виникла помилка. Інформація для технічної підтримки: ', arguments);
  })
});
