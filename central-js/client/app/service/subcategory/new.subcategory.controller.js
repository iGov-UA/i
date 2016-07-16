angular.module('app')
  .controller('NewSubcategoryController',
    ['$scope', '$stateParams', '$filter', '$location', '$anchorScroll', 'messageBusService', 'chosenCategory', 'EditServiceTreeFactory', 'AdminService', '$state',
      function($scope, $stateParams, $filter, $location, $anchorScroll, messageBusService, chosenCategory, EditServiceTreeFactory, AdminService, $state) {
       $scope.spinner = true;

        $scope.category = $stateParams.catID;
        $scope.subcategory = chosenCategory;
        $scope.spinner = false;
        $scope.bAdmin = AdminService.isAdmin();

        var subscribers = [];
        var subscriberId = messageBusService.subscribe('catalog:updatePending', function() {
          console.log("spinner true");
          $scope.spinner = true;
          $scope.catalog = [];
          $scope.category = null;
          $scope.subcategory = null;
        });
        subscribers.push(subscriberId);
        messageBusService.subscribe('catalog:update', function(data) {
          console.log('catalog updated, will update items');
          $scope.spinner = false;
          $scope.catalog = data;
          if ($scope.catalog) {
            $scope.subcategory = data;
          } else {
            $scope.subcategory = null;
          }
        }, false);

        subscribers.push(subscriberId);
        $scope.category = null;
        $scope.subcategory = null;
        $scope.$on('$destroy', function() {
          subscribers.forEach(function(subscriberId) {
            messageBusService.unsubscribe(subscriberId);
          });
        });

        if($scope.catalog.aService && chosenCategory.oServiceTag_Root.nID === $scope.catalog.oServiceTag_Root.nID) {
          $scope.subcategory = $scope.catalog;
        }else {
          $scope.subcategory = chosenCategory;
        }
        $scope.stateCheck = $state.params.catID;
        $anchorScroll();

        $scope.$on('$stateChangeStart', function(event, toState) {
          if (toState.resolve) {
            $scope.spinner = true;
          }
        });
        $scope.$on('$stateChangeSuccess', function(event, toState) {
          if (toState.resolve) {
            $scope.spinner = false;
          }
        });
        $scope.$on('$stateChangeError', function(event, toState) {
          if (toState.resolve) {
            $scope.spinner = false;
          }
        });
      }]);
