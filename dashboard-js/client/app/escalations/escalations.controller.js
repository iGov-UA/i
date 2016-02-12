(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('EscalationsCtrl', escalationsCtrl);

  escalationsCtrl.$inject = ['$scope', '$modal', 'escalationsService'];
  function escalationsCtrl($scope, $modal, escalationsService) {
    $scope.dataFunctions = {
      //getFunc: escalationsService.getRule,
      setFunc: escalationsService.setRule,
      getAllFunc: escalationsService.getAllRules,
      deleteFunc: escalationsService.deleteRule,
      getAllFunctionsFunc:  escalationsService.getAllEscalationFunctions,
      setRuleFunctionFunc:  escalationsService.setEscalationFunctionFunc,
      deleteRuleFunctionFunc:  escalationsService.deleteEscalationFunctionFunc,
    };
  }
})();
