angular.module('app')
  .controller('NewSubcategoryController',
    ['$scope', '$stateParams', '$filter', '$location', '$anchorScroll', 'messageBusService', 'chosenCategory', 'EditServiceTreeFactory', 'AdminService',
      function($scope, $stateParams, $filter, $location, $anchorScroll, messageBusService, chosenCategory, EditServiceTreeFactory, AdminService) {
       $scope.spinner = true;

        $scope.category = $stateParams.catID;
        $scope.subcategory = chosenCategory;
        $scope.spinner = false;
        $scope.bAdmin = AdminService.isAdmin();
console.log($scope)
      }]);
