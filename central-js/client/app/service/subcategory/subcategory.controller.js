angular.module('app')
  .controller('SubcategoryController',
  ['$scope', '$stateParams', '$filter', '$location', '$anchorScroll', 'messageBusService', 'catalog', 'EditServiceTreeFactory', 'AdminService', '$rootScope', 'TitleChangeService',
    function($scope, $stateParams, $filter, $location, $anchorScroll, messageBusService, catalog, EditServiceTreeFactory, AdminService, $rootScope, TitleChangeService) {
      var getCurrentCategory = function(catalog) {
        return $filter('filter')(catalog, {nID: parseInt($stateParams.catID)}, true)[0]
      };

      var getCurrentSubcategory = function(category) {
        return $filter('filter')(category.aSubcategory, {nID: parseInt($stateParams.scatID)}, true)[0];
      };
  var category = getCurrentCategory(catalog);
  var subcategory = getCurrentSubcategory(category);
  $scope.categoryName = category.sName;
  $scope.subcategoryName = subcategory.sName;
  $scope.spinner = true;
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
  subscriberId = messageBusService.subscribe('catalog:update', function(data) {
    console.log('catalog updated, will update items');
    $scope.spinner = false;
    $scope.catalog = data;
    if ($scope.catalog.length > 0) {
      $scope.category = getCurrentCategory($scope.catalog);
    } else {
      $scope.category = null;
    }
    if ($scope.category) {
      $scope.subcategory = getCurrentSubcategory($scope.category);
    } else {
      $scope.subcategory = null;
    }
    $rootScope.busSpinner = false;
  }, false);
  subscribers.push(subscriberId);

  $scope.$on('$destroy', function() {
    subscribers.forEach(function(subscriberId) {
      messageBusService.unsubscribe(subscriberId);
    });
  });

  if(!$scope.subcategory) {
    category = getCurrentCategory(catalog);
    $scope.subcategory = getCurrentSubcategory(category);
    $scope.spinner = false;
  }
  var tag = $scope.subcategory.sName + ' / Бізнес';
  TitleChangeService.setTitle(tag);

  // Scroll to the top of the section - issues/589
  // After Angular upgrade (current version which is used is 1.3.15),
  // it can be used with parameter: $anchorScroll('top') (which will also keep location hash intact)
  // https://github.com/angular/angular.js/pull/9596/files
  $anchorScroll();
}]);
