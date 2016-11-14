angular.module('app')
  .directive("newIgovSearch", ['CatalogService', 'statesRepository', 'RegionListFactory', 'LocalityListFactory', '$filter', 'messageBusService', 'stateStorageService', 'AdminService', '$state', '$stateParams', '$rootScope',
    function(CatalogService, statesRepository, RegionListFactory, LocalityListFactory, $filter, messageBusService, stateStorageService, AdminService, $state, $stateParams, $rootScope) {
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
          $scope.mainSearchView = false;
          $scope.catalogCounts = {};

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
            if ($scope.bShowExtSearch && $scope.data.region !== null && $scope.data.region !== "") {
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
              // временно для старого бизнеса, после реализации тегов - удалить.
              if($state.is("index.oldbusiness") || $state.is("index.subcategory")) {
                $scope.operators = CatalogService.getOperatorsOld(ctlg);
              }else {
                $scope.operators = CatalogService.getOperators(ctlg);
              }
            }
            messageBusService.publish('catalog:update', ctlg);
          }
          // получаем к-во услуг готовых/скоро/в работе
          function getCounts (category) {
            var countCategory = category && category.aService ? category : 'business';
            if(countCategory === 'business') {
              CatalogService.getModeSpecificServices(null, "", false, countCategory).then(function (res) {
                $scope.catalogCounts = CatalogService.getCatalogCounts(res)
              })
            } else {
              $scope.catalogCounts = CatalogService.getCatalogCounts(countCategory)
            }
          }
          getCounts ();

          function isFilterActive() {
            $rootScope.mainSearchView = !!(($state.is('index') || $state.is('index.catalog')) && $scope.data.region);
          }

          $scope.search = function() {
            if (sID_Order_RegExp.test($scope.sSearch)) {
              return null;
            }
            $rootScope.minSearchLength = $scope.sSearch.length < 3;
            var bShowEmptyFolders = AdminService.isAdmin();
            $scope.spinner = true;
            messageBusService.publish('catalog:updatePending');
            $scope.catalog = [];
            $scope.category = $stateParams.catID;
            $scope.subcategory = $stateParams.scatID;
            if($state.is('index.situation')){
              $scope.situation = $stateParams.sitID;
              // поиск для старого бизнеса, когда будут доработаны теги в новом - удалить.
            } else if ($state.is("index.oldbusiness") || $state.is("index.subcategory")) {
              $scope.category = 'business';
            }
            return CatalogService.getModeSpecificServices(getIDPlaces(), $scope.sSearch, bShowEmptyFolders, $scope.category, $scope.subcategory, $stateParams.sitID, $rootScope.mainFilterCatalog).then(function (result) {
              if(!$state.is('index')
                  && !$state.is('index.catalog') && !$state.is("index.oldbusiness") && !$state.is("index.subcategory")) {
                fullCatalog = result[0];
              }else if($state.is("index.oldbusiness") && result.length === 1 && result[0].aSubcategory.length > 0) {
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
              if(result.length === 0) {
                $rootScope.wasSearched = true;
              }
              $rootScope.resultsAreLoading = false;
              getCounts(fullCatalog);
            });
          };
          $scope.searching = function () {
            // проверка на минимальне к-во символов в поисковике (искать должно от 3 символов)
            if($scope.sSearch.length >= 3 && $state.is("index.oldbusiness")) {
              // после реализации тегов в бизнесе - удалить.
              $rootScope.busSpinner = true;
              $scope.overallSearch();
              $rootScope.mainSearchView = true;
              $rootScope.valid = true;
            } else if($scope.sSearch.length >= 3 && ($state.is("index") || $state.is("index.catalog"))) {
              $rootScope.resultsAreLoading = true;
              $rootScope.mainSearchView = true;
              $rootScope.busSpinner = true;
              $scope.search();
              $rootScope.valid = true;
            }else if($rootScope.valid) {
              $rootScope.resultsAreLoading = true;
              $rootScope.valid = false;
              $rootScope.mainSearchView = false;
              $scope.search();
            } else {
              $rootScope.busSpinner = true;
              $scope.search();
              $rootScope.valid = true;
            }
          };

          // глобальный поиск по Гражд. и Бизн.
          $scope.overallSearch = function () {
            $rootScope.resultsAreLoading = true;
            if (sID_Order_RegExp.test($scope.sSearch)) return null;
            $rootScope.minSearchLength = $scope.sSearch.length < 3;
            var bShowEmptyFolders = AdminService.isAdmin();
            $scope.spinner = true;
            messageBusService.publish('catalog:updatePending');
            $scope.catalog = [];
            return CatalogService.getModeSpecificServices(getIDPlaces(), $scope.sSearch, bShowEmptyFolders, 'business').then(function (result) {
              fullCatalog = result;
              if ($scope.bShowExtSearch || $scope.getOrgan) {
                $scope.filterByExtSearch();
              } else {
                updateCatalog(angular.copy(fullCatalog));
              }
              $rootScope.resultsAreLoading = false;
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
            // сейчас джава выдает другие номера статусов, поэтому меняю для работоспособности. убрать когда теги в бизнесе будут готовы.
            // убрать когда теги в бизнесе будут готовы.
            if($state.is("index.oldbusiness") || $state.is("index.subcategory")) {
              var filterCriteria = {};
              var selectedStatus;
              if($scope.selectedStatus == 0) {
                selectedStatus = 1;
              } else if ($scope.selectedStatus == 1) {
                selectedStatus = 2;
              }
              if ($scope.selectedStatus != -1) {
                filterCriteria.nStatus = selectedStatus;
              }
              if ($scope.operator != -1) {
                filterCriteria.sSubjectOperatorName = $scope.operator;
              }
              if ($scope.getOrgan) {
                filterCriteria.sSubjectOperatorName = $scope.getOrgan;
              }
              // create a copy of current fullCatalog
              var ctlg = angular.copy(fullCatalog);
              angular.forEach(ctlg, function(category) {
                angular.forEach(category.aSubcategory, function(subCategory) {
                  // leave services that match filterCriteria
                  subCategory.aService = $filter('filter')(subCategory.aService, filterCriteria);
                });
                // leave subcategories that are not empty
                category.aSubcategory = $filter('filter')(category.aSubcategory, function(subCategory) {
                  if (subCategory.aService.length > 0) {
                    return true;
                  }
                });
              });
              // leave categories that are not empty
              ctlg = $filter('filter')(ctlg, function(category) {
                if (category.aSubcategory.length >0 ) {
                  return true;
                }
              });
              updateCatalog(ctlg);
            } else {
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
            }
          };

          $scope.onExtSearchClick = function() {
            $scope.bShowExtSearch = !$scope.bShowExtSearch;
            if ($scope.bShowExtSearch) {
              $scope.searching();
            }
          };
          $scope.clear = function() {
            restoreSettings(defaultSettings);
            if($rootScope.mainFilterCatalog) $rootScope.mainFilterCatalog = false;
            $scope.searching();
          };
          $scope.loadRegionList = function(search) {
            return $scope.regionList.load(null, search);
          };
          $scope.onSelectRegionList = function($item) {
            $rootScope.resultsAreLoading = true;
            $scope.data.region = $item;
            $scope.regionList.select($item);
            $scope.data.city = null;
            $scope.localityList.reset();
            if($state.is('index') || $state.is('index.catalog')){
              $rootScope.mainFilterCatalog = true;
            }
            $scope.search();
            $scope.localityList.load(null, $item.nID, null).then(function(cities) {
              $scope.localityList.typeahead.defaultList = cities;
            });
            isFilterActive()
          };

          $scope.loadLocalityList = function(search) {
            return $scope.localityList.load(null, $scope.data.region.nID, search);
          };

          $scope.onSelectLocalityList = function($item, $model, $label) {
            $rootScope.resultsAreLoading = true;
            $scope.data.city = $item;
            $scope.localityList.select($item, $model, $label);
            $scope.search();
            isFilterActive()
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
          jQuery.fn.highlight = function (str, className) {
            var regex = new RegExp(str, "gi");
            return this.each(function () {
              $(this).contents().filter(function() {
                return this.nodeType == 3 && regex.test(this.nodeValue);
              }).replaceWith(function() {
                return (this.nodeValue || "").replace(regex, function(match) {
                  return "<span class=\"" + className + "\">" + match + "</span>";
                });
              });
            });
          };
          $rootScope.$watch('rand', function () {
            if($scope.sSearch.length >= 3) {
              setTimeout(function () {
                $(".igov-container a").highlight($scope.sSearch, "marked-string");
              }, 100)
            }
          });
          $scope.$watch('data.region', function() {
            if(!$scope.data.region) {
              $rootScope.resultsAreLoading = true;
              $rootScope.mainFilterCatalog = false;
              isFilterActive();
              $scope.searching();
            }
          });
          $scope.$on('$stateChangeSuccess', function(event, toState) {
            if (toState.resolve) {
              $scope.spinner = true;
              $scope.search();
            }
          });
        }
      };
      return directive;
    }]);
