angular.module('app')
  .controller('ServiceController',
  ['$scope', '$rootScope', '$timeout', 'CatalogService', 'AdminService', '$filter', 'statesRepository', 'RegionListFactory', 'LocalityListFactory', 'messageBusService', 'EditServiceTreeFactory', '$location', '$stateParams', '$state', '$anchorScroll', 'TitleChangeService',
  function($scope, $rootScope, $timeout, CatalogService, AdminService, $filter, statesRepository, RegionListFactory, LocalityListFactory, messageBusService, EditServiceTreeFactory, $location, $stateParams, $state, $anchorScroll, TitleChangeService) {
    $rootScope.isOldStyleView = !!statesRepository.isDFS();
    if ($rootScope.isOldStyleView) $scope.spinner = true;
    $rootScope.catalogTab = 1;
    $scope.catalog = [];
    // $scope.catalogCounts = {0: 0, 1: 0, 2: 0};
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
    $scope.mainSpinner = true;
    $scope.isKyivCity = !!statesRepository.isKyivCity();

    /*$scope.isCatalogCategoryShowAll = function(nID){
        return statesRepository.isSearch(nID);
    };*/

    $scope.categoryEditor = EditServiceTreeFactory.category;
    $scope.subcategoryEditor = EditServiceTreeFactory.subcategory;
    $scope.serviceEditor = EditServiceTreeFactory.service;

    var subscriptions = [];
    var subscriberId = messageBusService.subscribe('catalog:update', function(data) {
      $scope.mainSpinner = false;
      $rootScope.fullCatalog = data;
      $scope.catalog = data;
      getKeywoeds();
      if ($rootScope.isOldStyleView) {
        $rootScope.busSpinner = false;
        $scope.spinner = false;
      }
      $rootScope.rand = (Math.random() * 10).toFixed(2);
    }, false);
    subscriptions.push(subscriberId);


    subscriberId = messageBusService.subscribe('catalog:updatePending', function() {
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
      getKeywoeds();
    };

    $scope.isSfs = function() {
      if(location.hostname.indexOf('sfs') >= 0){
        $('.sfs-favicon').remove();
        $('<link/>',{rel:'shortcut icon', href:'../../assets/images/icons/favicon-sfs.png', class:'sfs-favicon'}).appendTo('head');
        return true
      } else {
        return false
      }
    };

    $scope.stateCheck = $state.params.catID;

    $scope.changeCategory = function (num) {
      if(num){
        $rootScope.catalogTab = num;
      }
      else if($state.params) {
        $rootScope.catalogTab = $state.params.catID;
        return $rootScope.catalogTab;
      }
      else {
        return $rootScope.catalogTab;
      }
    };

    $scope.isSubdomain = function () {
      var idPlaces = statesRepository.getIDPlaces();
      return idPlaces.length > 0
    };

    $scope.goToService = function (nID) {
      $location.path("/service/"+nID+"/general");
    };

    function getKeywoeds() {
      if($scope.catalog.aService){
        angular.forEach($scope.catalog.aService, function (oService) {
          oService.aKeywords = getKeywordsArray(oService.saKeyword);
        })
      }
    }
    function getKeywordsArray(str) {
      var arr = str.split(",");
      for(var keyInd = 0; keyInd < arr.length; keyInd++){
        arr[keyInd] = $.trim(arr[keyInd]);
        if(arr[keyInd].indexOf('#') !== 0){
          arr[keyInd] = '#' + arr[keyInd];
        }
      }
      if(angular.isArray(arr) && arr.length > 0){
        while (arr.indexOf('#') >= 0){
          arr.splice(arr.indexOf('#'), 1);
        }
      }
      return arr;
    }

    $scope.$on('$stateChangeStart', function(event, toState) {
      $scope.spinner = true;
      if(toState.name === 'index') {
        CatalogService.getCatalogTreeTag(1).then(function (res) {
          $scope.catalog = res;
          getKeywoeds();
          $scope.changeCategory();
          $scope.spinner = false;
          $scope.mainSpinner = false;
          TitleChangeService.defaultTitle();
        });
      }
      if (toState.resolve) {
        $scope.spinner = true;
      }
    });
    $scope.$on('$stateChangeError', function(event, toState) {
      if (toState.resolve) {
        $scope.spinner = false;
      }
    });
    $rootScope.$watch('catalog', function () {
      if ($scope.catalog.length !== 0) $scope.spinner = false;
    });
    $anchorScroll();
  }]);
