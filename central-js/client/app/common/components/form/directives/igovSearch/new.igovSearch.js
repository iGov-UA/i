angular.module('app')
  .directive("newIgovSearch", ['CatalogService', 'statesRepository', 'RegionListFactory', 'LocalityListFactory', '$filter', 'messageBusService', 'stateStorageService', 'AdminService', '$state', '$stateParams',
    function(CatalogService, statesRepository, RegionListFactory, LocalityListFactory, $filter, messageBusService, stateStorageService, AdminService, $state, $stateParams) {
      var directive = {
        restrict: 'E',
        scope: {},
        templateUrl: 'app/common/components/form/directives/igovSearch/new.igovSearch.html',
        link: function($scope, $el, $attr) {
          var fullCatalog = [];
          var subscriptions = [];
          var sID_Order_RegExp = /^\d$|^\d-$|^\d-\d+$/;
          var sID_Order_Full_RegExp = /^\d-\d+$/;


          $scope.isSearch = statesRepository.isSearch();
          $scope.getOrgan = statesRepository.getOrgan();
          $scope.isCentral = statesRepository.isCentral();
          $scope.regionList = new RegionListFactory();
          $scope.regionList.load(null, null);
          $scope.localityList = new LocalityListFactory();
          $scope.operators = [];
          $scope.check = false;

          // set defaults
          var defaultSettings = {
            sSearch: '',
            operator: -1,
            selectedStatus: -1,
            bShowExtSearch: false,
            data: {
              region: null,
              city: null
            }
          };
          // restore search settings (if available)
          var searchSettings = stateStorageService.getState('igovSearch');
          searchSettings = searchSettings ? searchSettings : defaultSettings;

          restoreSettings(searchSettings);

          function restoreSettings(settings) {
            // todo: iterate over keys;
            $scope.sSearch = settings.sSearch;
            $scope.operator = settings.operator;
            $scope.selectedStatus = settings.selectedStatus;
            $scope.bShowExtSearch = settings.bShowExtSearch;
            $scope.data = settings.data;
          }

          function getIDPlaces() {
            var result;
            if ($scope.bShowExtSearch && $scope.data.region !== null) {
              var places = [$scope.data.city === null ? $scope.data.region : ''].concat($scope.data.city === null ? $scope.data.region.aCity : $scope.data.city);

              result = places.map(function(e) { return e.sID_UA; });
            } else {
              result = statesRepository.getIDPlaces();
            }
            return result;
          }
          function updateCatalog(ctlg) {
            $scope.catalog = ctlg;
            if ($scope.operator == -1) {
              $scope.operators = CatalogService.getOperators(ctlg);
            }
            messageBusService.publish('catalog:update', ctlg);
          }
          $scope.search = function() {
            if (sID_Order_RegExp.test($scope.sSearch)) {
              return null;
            }
            var bShowEmptyFolders = AdminService.isAdmin();
            $scope.spinner = true;
            messageBusService.publish('catalog:updatePending');
            $scope.catalog = [];
            $scope.category = $stateParams.catID;
            $scope.subcategory = $stateParams.scatID;
            return CatalogService.getModeSpecificServices(getIDPlaces(), $scope.sSearch, bShowEmptyFolders, false, $scope.category, $scope.subcategory).then(function (result) {
              if(result.length === 1) {
                fullCatalog = result[0];
              } else {
                fullCatalog = result;
              }
              if ($scope.bShowExtSearch || $scope.getOrgan) {
                $scope.filterByExtSearch();
              } else if ($scope.check) {
                updateCatalog(angular.copy(fullCatalog));
                $scope.check = false;
              } else {
                updateCatalog(angular.copy(fullCatalog));
              }
            });
          };
          $scope.searchOrder = function () {
            if(sID_Order_Full_RegExp.test($scope.sSearch)) {
              $state.go('index.search', {sID_Order: $scope.sSearch});
            } else {
              var ngModelController = $el.find('input').first().data().$ngModelController;
              ngModelController.$setValidity('searchOrder',
                sID_Order_Full_RegExp.test($scope.sSearch) || !sID_Order_RegExp.test($scope.sSearch));
            }
          };
          // method to filter full catalog depending on current extended search parameters
          // choosen by user
          $scope.filterByExtSearch = function() {
            $scope.check = true;
            var filterCriteria = {};
            if ($scope.selectedStatus != -1) {
              filterCriteria.nStatus = $scope.selectedStatus;
            }
            if ($scope.operator != -1) {
              filterCriteria.sSubjectOperatorName = $scope.operator;
            }
            if ($scope.getOrgan) {
              filterCriteria.sSubjectOperatorName = $scope.getOrgan;
            }

            // create a copy of current fullCatalog
            var ctlg = angular.copy(fullCatalog);
            ctlg.aService = $filter('filter')(ctlg.aService, filterCriteria);
            // TODO поправить
            ctlg.aServiceTag_Child = $filter('filter')(ctlg.aServiceTag_Child, function(category) {
                return true;
            });
            updateCatalog(ctlg);
          };

          $scope.onExtSearchClick = function() {
            $scope.bShowExtSearch = !$scope.bShowExtSearch;
            if ($scope.bShowExtSearch) {
              $scope.search();
            }
          };
          $scope.clear = function() {
            restoreSettings(defaultSettings);
            $scope.search();
          };
          $scope.loadRegionList = function(search) {
            return $scope.regionList.load(null, search);
          };
          $scope.onSelectRegionList = function($item) {
            $scope.data.region = $item;
            $scope.regionList.select($item);
            $scope.data.city = null;
            $scope.localityList.reset();
            $scope.search();
            $scope.localityList.load(null, $item.nID, null).then(function(cities) {
              $scope.localityList.typeahead.defaultList = cities;
            });
          };

          $scope.loadLocalityList = function(search) {
            return $scope.localityList.load(null, $scope.data.region.nID, search);
          };

          $scope.onSelectLocalityList = function($item, $model, $label) {
            $scope.data.city = $item;
            $scope.localityList.select($item, $model, $label);
            $scope.search();
          };
          $scope.search();

          var subscriberId = messageBusService.subscribe('catalog:initUpdate', function() {
            $scope.search();
          });
          subscriptions.push(subscriberId);

          // save current state on scope destroy
          $scope.$on('$destroy', function() {
            var state = {};
            state.sSearch = $scope.sSearch;
            state.operator = $scope.operator;
            state.selectedStatus = $scope.selectedStatus;
            state.bShowExtSearch = $scope.bShowExtSearch;
            state.data = $scope.data;
            stateStorageService.setState('igovSearch', state);
            subscriptions.forEach(function(item) {
              messageBusService.unsubscribe(item);
            });
          });
        }
      };
      return directive;
    }]);
