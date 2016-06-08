/**
 * Created by GFalcon-UA on 27.04.2016.
 */
(function(){
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('DeployCtrl', deployCtrl);

  deployCtrl.$inject = ['$scope', 'deployService'];
  function deployCtrl($scope, oDeployService){

    $scope.DeployFunctions = {
      getListBP: oDeployService.getListBP(),
      removeListBP: oDeployService.removeListBP()
    };

  }
})();
