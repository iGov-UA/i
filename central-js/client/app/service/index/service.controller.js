angular.module('app')
  .controller('ServiceController',
  ['$scope', '$rootScope', '$timeout', 'CatalogService', 'AdminService', '$filter', 'statesRepository', 'RegionListFactory', 'LocalityListFactory', 'messageBusService', 'EditServiceTreeFactory', '$location',
  function($scope, $rootScope, $timeout, CatalogService, AdminService, $filter, statesRepository, RegionListFactory, LocalityListFactory, messageBusService, EditServiceTreeFactory, $location) {

    $scope.catalog = [];
    $scope.catalogCounts = {0: 0, 1: 0, 2: 0};
    $scope.limit = 4;
    $scope.nLimitCategory = function(nID){
        if(statesRepository.isCatalogCategoryShowAll(nID)){
            return 999;
        }else{
            return 4;
        }
    };
    $scope.bAdmin = AdminService.isAdmin();
    $scope.recalcCounts = true;
    $scope.spinner = true;

    /*$scope.isCatalogCategoryShowAll = function(nID){
        return statesRepository.isSearch(nID);
    };*/

    $scope.categoryEditor = EditServiceTreeFactory.category;
    $scope.subcategoryEditor = EditServiceTreeFactory.subcategory;
    $scope.serviceEditor = EditServiceTreeFactory.service;

    var subscriptions = [];
    var subscriberId = messageBusService.subscribe('catalog:update', function(data) {
      $scope.spinner = false;
      $scope.fullCatalog = data;
      $scope.catalog = data;
      console.log('new catalog', $scope.catalog);
      // TODO: move other handlers here, like update counters, etc
      $scope.catalogCounts = CatalogService.getCatalogCounts(data);
    }, false);
    subscriptions.push(subscriberId);


    subscriberId = messageBusService.subscribe('catalog:updatePending', function() {
      $scope.spinner = true;
      $scope.catalog = [];
    });
    subscriptions.push(subscriberId);
    $scope.$on('$destroy', function() {
      subscriptions.forEach(function(item) {
        messageBusService.unsubscribe(item);
      });
    });

    $scope.filterByStatus = function(status) {
      $scope.selectedStatus = status;
      var ctlg = angular.copy($scope.fullCatalog);
      angular.forEach(ctlg, function(item) {
        angular.forEach(item.aSubcategory, function(subItem) {
          subItem.aService = $filter('filter')(subItem.aService, {nStatus: status});
        });
      });
      $scope.catalog = ctlg;
    };

    // Check that the sub-domain where the user is located - sfs.
    // This is necessary because in other domains / sub-domains " Податки " should not be displayed in 3 columns.
    $scope.sfsSubdomainCheck = function(){
      var subDomainSearch = $location.host().split('.');
      for(i=0; i<subDomainSearch.length; i++){
        if(subDomainSearch[i] == 'sfs'){
          return true
        }
      }
    };
  }]);
