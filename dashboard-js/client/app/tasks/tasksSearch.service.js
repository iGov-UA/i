'use strict';

(function (angular) {
  var tasksSearchService = function (tasks, Modal, defaultSearchHandlerService) {

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
            console.log(result);
          return;

          searchResult.text = $scope.searchTask.text;
          searchResult.tasks = JSON.parse(result);
          var taskId = searchResult.tasks[0];
          var taskFound = $scope.tasks.some(function (task) {
            if (task.id === taskId) {
              $scope.selectTask(task);
            }
            return task.id === taskId;
          });
        }).catch(function (response) {
        defaultSearchHandlerService.handleError(response, messageMap)
      });
    };

    var searchSuccess = function (taskId) {
      tasks.getTask(taskId).then(function (task) {
        console.log(task);
      });
    };

    return {
      searchTaskByUserInput: searchTaskByUserInput,
      searchTaskByText: searchTaskByText,
      searchSuccess: searchSuccess
    }
  };

  tasksSearchService.$inject = ['tasks', 'Modal', 'defaultSearchHandlerService'];

  angular
    .module('dashboardJsApp')
    .factory('tasksSearchService', tasksSearchService);

})(angular);
