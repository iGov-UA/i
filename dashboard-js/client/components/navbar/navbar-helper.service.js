(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .factory('iGovNavbarHelper', iGovNavbarHelperFactory);

  function iGovNavbarHelperFactory() {
    var service = {
      areInstrumentsVisible: false,
      isCollapsed: true,
      isTest: false,
    };
    return service;
  }
})();
