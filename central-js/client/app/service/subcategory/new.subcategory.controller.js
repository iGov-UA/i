angular.module('app')
  .controller('NewSubcategoryController',
    ['$scope', '$stateParams', '$filter', '$location', '$anchorScroll', 'messageBusService', 'chosenCategory', 'EditServiceTreeFactory', 'AdminService', '$state', '$rootScope', 'TitleChangeService',
      function($scope, $stateParams, $filter, $location, $anchorScroll, messageBusService, chosenCategory, EditServiceTreeFactory, AdminService, $state, $rootScope, TitleChangeService) {
        $scope.category = $stateParams.catID;
        $scope.subcategory = Array.isArray(chosenCategory) ? chosenCategory[0] : chosenCategory;
        // $scope.spinner = false;
        $scope.bAdmin = AdminService.isAdmin();

        var subscribers = [];
        var subscriberId = messageBusService.subscribe('catalog:updatePending', function() {
          $scope.spinner = true;
          $scope.catalog = [];
          $scope.category = null;
          $scope.subcategory = null;
        });
        subscribers.push(subscriberId);
        messageBusService.subscribe('catalog:update', function(data) {
          $scope.spinner = false;
          $scope.catalog = data;
          if ($scope.catalog) {
            $scope.subcategory = Array.isArray(data) ? data[0] : data;
          } else {
            $scope.subcategory = null;
          }
          $scope.spinner = false;
          $rootScope.rand = (Math.random()*10).toFixed(2);
        }, false);

        subscribers.push(subscriberId);
        $scope.category = null;
        $scope.subcategory = null;
        $scope.$on('$destroy', function() {
          subscribers.forEach(function(subscriberId) {
            messageBusService.unsubscribe(subscriberId);
          });
        });

        if($scope.catalog &&
          $scope.catalog.aService
          && chosenCategory[0].oServiceTag_Root.nID === $scope.catalog.oServiceTag_Root.nID
          || $rootScope.wasSearched) {
          $scope.subcategory = $scope.catalog;
          $rootScope.wasSearched = false;
        }else {
          $scope.subcategory = Array.isArray(chosenCategory) ? chosenCategory[0] : chosenCategory;
        }
        $scope.stateCheck = $state.params.catID;

        $scope.$on('$stateChangeStart', function(event, toState) {
          if (toState.resolve) {
            $scope.spinner = true;
          }
        });
        $scope.$on('$stateChangeError', function(event, toState) {
          if (toState.resolve) {
            $scope.spinner = false;
          }
        });
        try {
          var tag = $scope.subcategory.oServiceTag_Root.sName_UA;
          TitleChangeService.setTitle(tag);
        }catch (e) {}
        $anchorScroll();
      }]);
