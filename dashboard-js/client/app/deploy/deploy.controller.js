/**
 * Created by GFalcon-UA on 27.04.2016.
 */
(function(){
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('DeployCtrl', deployCtrl);

  deployCtrl.$inject = ['$scope', '$log', 'deployService'];
  function deployCtrl($scope, LOG, oDeployService){

    $scope.aListBPs = null;

    $scope.loadListBP = function(){
      LOG.info("Start load list");
      oDeployService.getListBP().then(function(data){
        LOG.info("End load list");
        $scope.aListBPs = JSON.parse(data);
        LOG.info("End parse list");
      });
    }

  }
})();
