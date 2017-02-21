'use strict';

(function (angular) {
  var tasksSearchService = function (tasks, Modal, defaultSearchHandlerService, $location, $rootScope, iGovNavbarHelper, $route, $q) {

    var messageMap = {'CRC Error': 'Неправильний ID', 'Record not found': 'ID не знайдено'};
    var oPreviousTextSearch = {
      value : '',
      onTab: '',
      cursor : 0,
      aIds : [],
      result : '',
      sortType: ''
    };

    var searchTaskByUserInput = function (value, tab, bSortReverse) {
      var defer = $q.defer();
      var matches = value.match(/((\d+)-)?(\d+)/);
      if (matches) {
        tasks.getTasksByOrder(matches[3]).then(function (result) {
          if (messageMap.hasOwnProperty(result))
            searchTaskByText(value, tab, bSortReverse, defer);
          else {
            cleanPreviousTextSearch();
            var aIds = JSON.parse(result);
            if (angular.isArray(aIds) && aIds.length > 0) {
              var params = {
                taskId : aIds[0],
                bSortReverse: bSortReverse
              };
              if (tab) {
                params.type = tab;
              }
              searchSuccess(params);
              defer.resolve({
                aIDs : aIds,
                nCurrentIndex : 0
              });
            } else
              searchTaskByText(value, tab, bSortReverse, defer);
          }
        }).catch(function () {
          searchTaskByText(value, tab, bSortReverse, defer);
        })
      } else
        searchTaskByText(value, tab, bSortReverse, defer);
      return defer.promise;
    };

    var searchTaskByText = function (value, tab, bSortReverse, defer) {
      var oSearchParams = {
        taskId: null,
        type: tab,
        onlyThisType: true,
        bSortReverse: bSortReverse
      };
      if (oPreviousTextSearch.value === value && oPreviousTextSearch.onTab === tab){
        oSearchParams.taskId = getNextTaskId(oPreviousTextSearch.aIds, bSortReverse, false);
        searchSuccess(oSearchParams);
        defer.resolve({
          aIDs : oPreviousTextSearch.aIds,
          nCurrentIndex : oPreviousTextSearch.cursor
        });
      } else {
        cleanPreviousTextSearch();
        tasks.getTasksByText(value, tab)
          .then(function (result) {
            if (messageMap.hasOwnProperty(result)) {
              Modal.inform.error()(messageMap[result]);
              defer.reject();
            } else {
              var aIds = JSON.parse(result);
              if (angular.isArray(aIds) && aIds.length > 0) {
                if (oPreviousTextSearch.result === result) {
                  oPreviousTextSearch.onTab = tab;
                  oPreviousTextSearch.value = value;

                  oSearchParams.taskId = getNextTaskId(aIds, bSortReverse, false);
                  searchSuccess(oSearchParams);
                  defer.resolve({
                    aIDs : aIds,
                    nCurrentIndex : oPreviousTextSearch.cursor
                  });
                } else {
                  oPreviousTextSearch = {
                    value : value,
                    onTab : tab,
                    cursor : 0,
                    aIds: aIds,
                    result : result,
                    sortType: bSortReverse
                  };
                  oSearchParams.taskId = getNextTaskId(aIds, bSortReverse, true);
                  searchSuccess(oSearchParams);
                  defer.resolve({
                    aIDs : aIds,
                    nCurrentIndex : oPreviousTextSearch.cursor
                  });
                }
              } else {
                Modal.inform.error()('За даним критерієм задач не знайдено');
                defer.reject();
              }
            }
          }).catch(function (response) {
          cleanPreviousTextSearch();
          defaultSearchHandlerService.handleError(response, messageMap);
          defer.reject();
        });
      }
    };

    function cleanPreviousTextSearch () {
      oPreviousTextSearch = {
        value : '',
        onTab: '',
        cursor : 0,
        aIds : [],
        result : '',
        sortType: ''
      };
    }

    function getNextTaskId(aIds, bSortReverse, isTheFirstIteration) {
      if(bSortReverse){
        oPreviousTextSearch.cursor--;
      } else {
        oPreviousTextSearch.cursor++;
      }
      if(oPreviousTextSearch.cursor < 0 || (isTheFirstIteration && bSortReverse)){
        oPreviousTextSearch.cursor = aIds.length - 1;
      }
      if(oPreviousTextSearch.cursor == aIds.length || (isTheFirstIteration && !bSortReverse)){
        oPreviousTextSearch.cursor = 0;
      }
      return aIds[oPreviousTextSearch.cursor];
    }

    var searchTypes = ['documents','unassigned','selfAssigned'];

    var searchSuccess = function (params) {
      if (!params.onlyThisType){
        params.onlyThisType = false;
      }
      if (!params.bSortReverse) {
        params.bSortReverse = false;
      }

      if (params.onlyThisType) {
        if (params.type) {
          searchTaskInType(params);
        } else {
          params.type = searchTypes[0];
          searchTaskInType(params);
        }
      } else {
        params.type = searchTypes[0];
        searchTaskInType(params);
      }
    };

    var searchTaskInType = function(params, page) {

      if (!page){
        page = 0;
      }
      tasks.list(params.type, {page: page}).then(function(response){
        var taskFound = false;

        // после перехода подгрузки и фильтра вкладки "Документы" на бек, будет не актуальным.
        if(params.type === 'documents') {
          response.data = response.data.filter(function (res) {
            return res.processDefinitionId.charAt(0) === '_' && res.processDefinitionId.split('_')[1] === 'doc';
          })
        }

        for (var i = 0; i < response.data.length; i++) {
          if (response.data[i].id == params.taskId) {
            var newPath = '/tasks/' + params.type + '/' + params.taskId;
            if (newPath == $location.$$path){
              $route.reload();
              $rootScope.$broadcast("update-search-counter");
            } else {
              $location.path(newPath);
            }
            iGovNavbarHelper.load();
            taskFound = true;
            break;
          }
        }

        if (!taskFound) {
          if ((response.start + response.size) < response.total)
            searchTaskInType(params, page + 1);
          else if (searchTypes.indexOf(params.type) < searchTypes.length - 1) {
            if (!params.onlyThisType) {
              params.type = searchTypes[searchTypes.indexOf(params.type) + 1]
              searchTaskInType(params, 0);
            }
          }
        }
      })
    };

    return {
      searchTaskByUserInput: searchTaskByUserInput,
      searchTaskByText: searchTaskByText,
      searchSuccess: searchSuccess
    }
  };

  tasksSearchService.$inject = ['tasks', 'Modal', 'defaultSearchHandlerService', '$location', '$rootScope', 'iGovNavbarHelper',
    '$route', '$q'];

  angular
    .module('dashboardJsApp')
    .factory('tasksSearchService', tasksSearchService);

})(angular);
