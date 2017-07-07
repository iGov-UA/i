'use strict';
angular.module('dashboardJsApp', [
  'base64',
  'angular-md5',
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
  'ui.validate',
  'ui.select',
  'iGovTable',
  'datepickerService',
  'autocompleteService',
  'datetimepicker',
  'ea.treeview',
  'cryptoPlugin',
  'textAngular',
  'angularSpectrumColorpicker',
  'snap'
]).config(function($urlRouterProvider, $locationProvider, $compileProvider) {
  $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|tel|file|blob):/);
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
}).config([
  'datetimepickerProvider',
  function (datetimepickerProvider) {
    datetimepickerProvider.setOptions({
      locale: 'uk',
      toolbarPlacement: 'bottom',
      showClear: true,
      format: 'DD/MM/YYYY',
      tooltips:{
        clear: 'Очистити',
        selectMonth: 'Обрати мiсяць',
        prevMonth: 'Попереднiй мiсяць',
        nextMonth: 'Наступний мiсяць',
        selectYear: 'Обрати рiк',
        prevYear: 'Попереднiй рiк',
        nextYear: 'Наступний рiк',
        selectDecade: 'Обрати десятиліття',
        prevDecade: 'Попереднє десятиліття',
        nextDecade: 'Наступне десятиліття',
        prevCentury: 'Попереднє століття',
        nextCentury: 'Наступне століття'
      }
    });
  }
]);
