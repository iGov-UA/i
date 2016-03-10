'use strict';

(function (angular) {
  var tasksSearchService = function (tasks, Modal, defaultSearchHandlerService, $location, iGovNavbarHelper, $route) {

    var messageMap = {'CRC Error': 'Неправильний ID', 'Record not found': 'ID не знайдено'};

    var searchTaskByUserInput = function (value) {
      var matches = value.match(/((\d+)-)?(\d+)/);
      if (matches) {
        tasks.getTasksByOrder(matches[3]).then(function (result) {
          if (messageMap.hasOwnProperty(result))
            searchTaskByText(value);
          else
            searchSuccess(JSON.parse(result)[0]);
        }).catch(function () {
          searchTaskByText(value);
        })
      } else
        searchTaskByText(value);
    };

    var searchTaskByText = function (value) {
      tasks.getTasksByText(value, 'selfAssigned')
        .then(function (result) {
          if (messageMap.hasOwnProperty(result))
            Modal.inform.error(messageMap[result]);
          else
            searchSuccess(JSON.parse(result)[0]);
        }).catch(function (response) {
        defaultSearchHandlerService.handleError(response, messageMap)
      });
    };

    var searchTypes = ['unassigned','selfAssigned'];

    var searchSuccess = function (taskId) {
      searchTaskInType(taskId, searchTypes[0]);
    };

    var searchTaskInType = function(taskId, type, page) {
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
            searchTaskInType(taskId, type, page + 1);
          else if (searchTypes.indexOf(type) < searchTypes.length - 1) {
            searchTaskInType(taskId, searchTypes[searchTypes.indexOf(type) + 1], 0);
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

  tasksSearchService.$inject = ['tasks', 'Modal', 'defaultSearchHandlerService', '$location', 'iGovNavbarHelper', '$route'];

  angular
    .module('dashboardJsApp')
    .factory('tasksSearchService', tasksSearchService);

})(angular);
