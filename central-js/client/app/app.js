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
  'angularSpectrumColorpicker',
  'iGovMarkers',
  'autocompleteService',
  'datepickerService',
  'iGovTable',
  'datetimepicker']);

angular.module('documents', ['appBoilerPlate']);
angular.module('auth', ['appBoilerPlate']);
angular.module('journal', ['appBoilerPlate']);
angular.module('order', ['appBoilerPlate']);
angular.module('about', ['appBoilerPlate']);
angular.module('feedback', ['appBoilerPlate']);

angular.module('popUp', ['ui.bootstrap']);

angular.module('app', [
  'documents',
  'auth',
  'journal',
  'order',
  'about',
  'feedback',
  'popUp'
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
}).run(function ($rootScope, $state, $injector, statesRepository) {
  $rootScope.state = $state;
  $rootScope.profile = {
    nID_Place_Profile: statesRepository.getID_Place_Profile(),
    isKyivCity: !!statesRepository.isKyivCity()
  };


  function getErrorText(error) {
    if (error && error.code && error.message) {
      return 'Помилка \'' + error.message +'\'';
    } else {
      if (error && error.data) {
        return 'Невідома помилка \'' + JSON.stringify(error.data) + '\'';
      } else {
        return 'Невідома помилка \'' + JSON.stringify(error) + '\'';
      }
    }
  }

  var errorsFactory;

  $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error) {
    event.preventDefault();

    if (!errorsFactory) {
      errorsFactory = $injector.get('ErrorsFactory');
    }

    var errorText = getErrorText(error);
    if (errorsFactory) {
      errorsFactory.push({
        type: 'danger',
        text: errorText
      });
    } else {
      alert(errorText);
    }
  });
}).config([
  'datetimepickerProvider',
  function (datetimepickerProvider) {
    datetimepickerProvider.setOptions({
      locale: 'uk',
      toolbarPlacement: 'default',
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
