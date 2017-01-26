'use strict';

(function (angular) {
  var tasksSearchService = function (tasks, Modal, defaultSearchHandlerService, $location, iGovNavbarHelper, $route, $q) {

    var messageMap = {'CRC Error': 'Неправильний ID', 'Record not found': 'ID не знайдено'};
    var oPreviousTextSearch = {
      value : '',
      onTab: '',
      cursor : 0,
      aIds : [],
      result : ''
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
              !tab ? searchSuccess(aIds[0]) : searchSuccess(aIds[0], tab);
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
      if (oPreviousTextSearch.value === value && oPreviousTextSearch.onTab === tab){
        oPreviousTextSearch.cursor++;
        if(oPreviousTextSearch.cursor == oPreviousTextSearch.aIds.length){
          oPreviousTextSearch.cursor = 0;
        }
        searchSuccess(oPreviousTextSearch.aIds[oPreviousTextSearch.cursor], tab, true, bSortReverse);
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
                  oPreviousTextSearch.cursor++;
                  if(oPreviousTextSearch.cursor == aIds.length){
                    oPreviousTextSearch.cursor = 0;
                  }
                  searchSuccess(aIds[oPreviousTextSearch.cursor], tab, true, bSortReverse);
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
                    result : result
                  };
                  searchSuccess(aIds[0], tab, true, bSortReverse);
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

    var cleanPreviousTextSearch = function () {
      oPreviousTextSearch = {
        value : '',
        onTab: '',
        cursor : 0,
        aIds : [],
        result : ''
      };
    };

    var searchTypes = ['unassigned','selfAssigned'];

    var searchSuccess = function (taskId, tab, onlyThisTab, bSortReverse) {
      if (!onlyThisTab)
        onlyThisTab = false;
      if (onlyThisTab) {
        if (tab) {
          searchTaskInType(taskId, tab, onlyThisTab, bSortReverse);
        } else {
          searchTaskInType(taskId, searchTypes[0], onlyThisTab, bSortReverse);
        }
      } else {
        searchTaskInType(taskId, searchTypes[0], onlyThisTab, bSortReverse);
      }
    };

    var searchTaskInType = function(taskId, type, onlyThisType, page, bSortReverse) {
      if (!onlyThisType)
        onlyThisType = false;
      if (!page)
        page = 0;
      tasks.list(type, {page: page}).then(function(response){
        var taskFound = false;
        var i = 0;
        if(bSortReverse){
          for (i = response.data.length - 1; i >= 0 ; i--) {
            if(checkAndGoToTheTask(response.data[i], type, taskId)){
              taskFound = true;
              break;
            }
          }
        } else {
          for (i = 0; i < response.data.length; i++) {
            if(checkAndGoToTheTask(response.data[i], type, taskId)){
              taskFound = true;
              break;
            }
          }
        }


        if (!taskFound) {
          if ((response.start + response.size) < response.total)
            searchTaskInType(taskId, type, onlyThisType, page + 1, bSortReverse);
          else if (searchTypes.indexOf(type) < searchTypes.length - 1) {
            if (!onlyThisType) searchTaskInType(taskId, searchTypes[searchTypes.indexOf(type) + 1], onlyThisType, 0, bSortReverse);
          }
        }
      })
    };

    function checkAndGoToTheTask(task, type, taskId) {
      if (task.id == taskId) {
        var newPath = '/tasks/' + type + '/' + taskId;
        if (newPath == $location.$$path)
          $route.reload();
        else
          $location.path(newPath);
        iGovNavbarHelper.load();
        return true;
      } else {
        return false;
      }
    }

    return {
      searchTaskByUserInput: searchTaskByUserInput,
      searchTaskByText: searchTaskByText,
      searchSuccess: searchSuccess
    }
  };

  tasksSearchService.$inject = ['tasks', 'Modal', 'defaultSearchHandlerService', '$location', 'iGovNavbarHelper',
    '$route', '$q'];

  angular
    .module('dashboardJsApp')
    .factory('tasksSearchService', tasksSearchService);

})(angular);
