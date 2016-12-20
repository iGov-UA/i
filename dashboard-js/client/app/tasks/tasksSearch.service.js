'use strict';

(function (angular) {
  var tasksSearchService = function (tasks, Modal, defaultSearchHandlerService, $location, iGovNavbarHelper, $route, $q) {

    var messageMap = {'CRC Error': 'Неправильний ID', 'Record not found': 'ID не знайдено'};
    var oPreviousTextSearch = {
      value : '',
      cursor : 0,
      aIds : [],
      result : ''
    };

    var searchTaskByUserInput = function (value, tab) {
      var defer = $q.defer();
      var matches = value.match(/((\d+)-)?(\d+)/);
      if (matches) {
        tasks.getTasksByOrder(matches[3]).then(function (result) {
          if (messageMap.hasOwnProperty(result))
            searchTaskByText(value, tab, defer);
          else {
            cleanPreviousTextSearch();
            var aIds = JSON.parse(result);
            if (angular.isArray(aIds) && aIds.length > 0) {
              searchSuccess(aIds[0]);
              defer.resolve({
                aIDs : aIds,
                nCurrentIndex : 0
              });
            } else
              searchTaskByText(value, tab, defer);
          }
        }).catch(function () {
          searchTaskByText(value, tab, defer);
        })
      } else
        searchTaskByText(value, tab, defer);
      return defer.promise;
    };

    var searchTaskByText = function (value, tab, defer) {
      if (oPreviousTextSearch.value === value){
        oPreviousTextSearch.cursor++;
        if(oPreviousTextSearch.cursor == oPreviousTextSearch.aIds.length){
          oPreviousTextSearch.cursor = 0;
        }
        searchSuccess(oPreviousTextSearch.aIds[oPreviousTextSearch.cursor], tab, true);
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
                  oPreviousTextSearch.value = value;
                  oPreviousTextSearch.cursor++;
                  if(oPreviousTextSearch.cursor == aIds.length){
                    oPreviousTextSearch.cursor = 0;
                  }
                  searchSuccess(aIds[oPreviousTextSearch.cursor], tab, true);
                  defer.resolve({
                    aIDs : aIds,
                    nCurrentIndex : oPreviousTextSearch.cursor
                  });
                } else {
                  oPreviousTextSearch = {
                    value : value,
                    cursor : 0,
                    aIds: aIds,
                    result : result
                  };
                  searchSuccess(aIds[0], tab, true);
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
        cursor : 0,
        aIds : [],
        result : ''
      };
    };

    var searchTypes = ['unassigned','selfAssigned'];

    var searchSuccess = function (taskId, tab, onlyThisTab) {
      if (tab) {
        searchTaskInType(taskId, tab, onlyThisTab);
      } else {
        searchTaskInType(taskId, searchTypes[0], onlyThisTab);
      }
    };

    var searchTaskInType = function(taskId, type, onlyThisType, page) {
      if (!onlyThisType)
        onlyThisType = false;
      if (!page)
        page = 0;
      tasks.list(type, {page: page}).then(function(response){
        var taskFound = false;
        for (var i=0;i<response.data.length;i++) {
          var task = response.data[i];
          if (task.id == taskId) {
            taskFound = true;
            var newPath = '/tasks/' + type + '/' + taskId;
            if (newPath == $location.$$path)
              $route.reload();
            else
              $location.path(newPath);
            iGovNavbarHelper.load();
            break;
          }
        }

        if (!taskFound) {
          if ((response.start + response.size) < response.total)
            searchTaskInType(taskId, type, onlyThisType, page + 1);
          else if (searchTypes.indexOf(type) < searchTypes.length - 1) {
            if (!onlyThisType) searchTaskInType(taskId, searchTypes[searchTypes.indexOf(type) + 1], onlyThisType, 0);
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

  tasksSearchService.$inject = ['tasks', 'Modal', 'defaultSearchHandlerService', '$location', 'iGovNavbarHelper',
    '$route', '$q'];

  angular
    .module('dashboardJsApp')
    .factory('tasksSearchService', tasksSearchService);

})(angular);
