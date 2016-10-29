'use strict';

angular.module('appBoilerPlate', ['ngCookies',
  'ngResource',
  'ngSanitize',
  'ui.router',
  'ui.bootstrap',
  'ui.scroll',
  'ngMessages',
  'ui.uploader',
  'ui.event',
  'ui.select',
  'angularMoment',
  'ngClipboard',
  'ngJsonEditor',
  'dialogs.main',
  'pascalprecht.translate',
  'dialogs.default-translations',
  'textAngular',
  'iGovMarkers']);

angular.module('documents', ['appBoilerPlate']);
angular.module('auth', ['appBoilerPlate']);
angular.module('journal', ['appBoilerPlate']);
angular.module('order', ['appBoilerPlate']);
angular.module('about', ['appBoilerPlate']);
angular.module('feedback', ['appBoilerPlate']);

angular.module('app', [
  'documents',
  'auth',
  'journal',
  'order',
  'about',
  'feedback'
]).config(function ($urlRouterProvider, $locationProvider, datepickerConfig, datepickerPopupConfig) {
  $urlRouterProvider.otherwise('/');
  $locationProvider.html5Mode(true);
  datepickerConfig.datepickerMode = 'year';
  datepickerConfig.formatMonthTitle = 'yyyy';
  datepickerConfig.formatYearTitle = 'Рік';
  datepickerConfig.formatDayTitle = 'MMM yyyy';
  datepickerConfig.formatDay = 'd';
  datepickerConfig.formatMonth = 'MMM';
  datepickerConfig.startingDay = 1;
  datepickerPopupConfig.clearText = 'Очистити';
}).run(function ($rootScope, $state, statesRepository) {
  $rootScope.state = $state;
  $rootScope.profile = {
    isKyivCity : !!statesRepository.isKyivCity()
  };
  $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error) {
    if (error && error.data) {
      console.error('stateChangeError', error.data);
      //TODO: Заменить на нормальный див-диалог из ErrorFactory
      alert("Невідома помилка: " + error.data);
    } else {
      console.error('stateChangeError', error);
      alert("Невідома помилка: " + error);
    }
  });
});
