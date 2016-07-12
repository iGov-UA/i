angular.module('app')
  .controller('NewSubcategoryController',
    ['$scope', '$stateParams', '$filter', '$location', '$anchorScroll', 'messageBusService', 'chosenCategory', 'EditServiceTreeFactory', 'AdminService',
      function($scope, $stateParams, $filter, $location, $anchorScroll, messageBusService, chosenCategory, EditServiceTreeFactory, AdminService) {
       $scope.spinner = true;
        // var getCurrentSubcategory = function(category) {
        //   for(i = 0; i < category.length; i++) {
        //     if(category[i].oServiceTag_Root.nID === parseInt($stateParams.scatID)) {
        //       return category[i];
        //     }
        //   }
          // angular.forEach(category, function (item) {
          //   if(item.oServiceTag_Root.nID === parseInt($stateParams.scatID)){
          //     return item
          //   }
          // });
          // return $filter('filter')(category.oServiceTag_Root, {nID: parseInt($stateParams.scatID)}, true)[0];
        // };
        // $scope.commonCats = '';
        // angular.forEach(commonCategory, function(chosen) {
        //   if(chosen.oServiceTag_Root.nID === parseInt($stateParams.scatID)) {
        //     $scope.commonCats = chosen;
        //   }
        // });

        $scope.category = $stateParams.catID;
        $scope.subcategory = chosenCategory;
        $scope.subcategoryName = $scope.subcategory.aServiceTag_Child[0].sName_UA;
        $scope.spinner = false; // hard
        $scope.bAdmin = AdminService.isAdmin();
console.log($scope)
      }]);
