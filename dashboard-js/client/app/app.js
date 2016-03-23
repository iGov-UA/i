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
}).run(function(amMoment) {
  amMoment.changeLocale('uk');
});
