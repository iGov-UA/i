(function () {
  'use strict';
  angular.module('dashboardJsApp').service('ScannerService', ['$rootScope', function ($rootScope) {

    var sStorageName = 'iGovTwainSettings';

    this.getTwainServerUrl = function () {
/*
      if(!window.localStorage.getItem(sStorageName)){
        createNewStorageItem();
        return 'http://127.0.0.1:9005/TWAIN@Web/';
      }
*/
      return 'http://127.0.0.1:9005/TWAIN@Web/';
    };

    function createNewStorageItem() {

    }

  }])
})();
