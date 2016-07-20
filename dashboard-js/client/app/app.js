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
  'ngClipboard',
  'iGovMarkers',
  'ngMessages',
  'smart-table',
  'ui.validate'
]).config(function($urlRouterProvider, $locationProvider) {
  $urlRouterProvider
    .otherwise('/');
  $locationProvider.html5Mode(true);
}).run(function(amMoment, $rootScope, Modal) {
  amMoment.changeLocale('uk');
  $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error) {
    var message;
    if (error.status == 403)
      message = error.data.message;
    Modal.inform.error()(message || 'Виникла помилка. Зверніться будь ласка у технічну підтримку.');
    console.warn('Виникла помилка. Інформація для технічної підтримки: ', arguments);
  })
});
