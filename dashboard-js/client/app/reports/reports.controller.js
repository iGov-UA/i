(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('ReportsCtrl', reportsCtrl);

  reportsCtrl.$inject = ['$scope', 'reports', 'processes'];
  function reportsCtrl($scope, reports, processes) {
    $scope.export = {};
    $scope.export.from = '2016-11-18';
    $scope.export.to = '2016-11-25';
    $scope.export.sBP = 'dnepr_spravka_o_doxodax';
    $scope.exportURL = "/reports";
    $scope.export.bExportAll  = false;

    $scope.date = {
        options: {
            timePicker:false
        }
    };

    $scope.initExportUrl = function () {
        reports.exportLink({ from: $scope.export.from, to: $scope.export.to, sBP: $scope.export.sBP, bExportAll: $scope.export.bExportAll},
            function (result) {
                $scope.exportURL = result;
            });
    };

    $scope.getExportLink = function () {
          return $scope.exportURL;
    };

    processes.getUserProcesses().then(function (data) {
      $scope.processesList = data;

      if (typeof $scope.processesList === 'undefined') {
          $scope.processesList = "error";
      } else if (typeof $scope.processesList !== 'undefined' && $scope.processesList.length > 0) {
        $scope.export.sBP = $scope.processesList[0].sID;
        $scope.initExportUrl();
      }
    }, function () {
      $scope.processesList = "error";
    });

    $scope.processesLoaded = function() {
      return !!$scope.processesList;
    };

    $scope.processesLoadError = function() {
      return !!($scope.processesList && $scope.processesList == "error");
    }
  }
})();
