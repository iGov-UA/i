angular.module('dashboardJsApp').service('taskFilterService', ['$filter', '$rootScope', 'processes', function ($filter, $rootScope, processes) {
  var taskDefinitions = [
    {name: 'Всі', id: 'all'},
    {name: 'Старт', id: 'usertask1'},
    {name: 'Обробка', id: 'usertask2'}
  ];
  var userProcesses = [
    {sID: 'all', sName: 'Всі'}
  ];
  var service = {
    getFilteredTasks: function (tasks, model) {
      var filteredTasks = this.filterTaskDefinitions(tasks, model.taskDefinition);
      filteredTasks = this.filterUserProcess(filteredTasks, model.userProcess);
      var strictTaskDefinitions = this.getProcessTaskDefinitions(filteredTasks);
      $rootScope.$broadcast('taskFilter:strictTaskDefinitions:update', strictTaskDefinitions);
      filteredTasks = this.filterStrictTaskDefinitions(filteredTasks, model.strictTaskDefinitions);
      return filteredTasks;
    },
    filterTaskDefinitions: function (tasks, taskDefinition) {
      if (tasks === null) {
        return null;
      }
      if (tasks.length == 0) {
        return [];
      }
      if (!taskDefinition) {
        return tasks;
      }
      var filteredTasks = tasks;
      switch (taskDefinition.id) {
        case 'all':
          return tasks;
          break;
        case 'usertask1':
        case 'usertask2':
          filteredTasks = tasks.filter(function (task, index) {
            if (!task.taskDefinitionKey) {
              return false;
            }
            if (task.taskDefinitionKey.substr(task.taskDefinitionKey.length - 9) == taskDefinition.id) {
              return true;
            }
          });
          return filteredTasks;
          break;
          return null;
      }
    },
    filterStrictTaskDefinitions: function (tasks, taskDefinition) {
      if (tasks === null) {
        return null;
      }
      if (tasks.length == 0) {
        return [];
      }
      if (!taskDefinition) {
        return tasks;
      }
      var filteredTasks = tasks.filter(function (task, index) {
        if (!task.taskDefinitionKey) {
          return false;
        }
        if (task.taskDefinitionKey == taskDefinition.id) {
          return true;
        }
      });
      return filteredTasks;
    },
    filterUserProcess: function (tasks, userProcess) {
      if (tasks === null) {
        return null;
      }
      if (tasks.length == 0) {
        return [];
      }
      if (!userProcess) {
        return tasks;
      }

      if (userProcess.sID == 'all') {
        return tasks;
      }
      // do actual filering
      var filteredTasks = tasks.filter(function (task) {
        var processDefinitionId = task.processDefinitionId;
        if (!processDefinitionId) {
          return false;
        }
        // processDefinitionId in task contains version after a colon. We don't need that here
        processDefinitionId = processDefinitionId.substring(0, processDefinitionId.indexOf(':'));
        if (userProcess.sID == processDefinitionId) {
          return true;
        }
        return false;
      });
      console.log('filteredTasks', filteredTasks);
      return filteredTasks;
    },
    // method to get all available task definitions like 'usertask1', 'usertask2' from provided tasks
    getProcessTaskDefinitions: function (tasks) {
      var definitions = [];
      definitions.push(taskDefinitions[0]); // add default taskDefinition for 'all'
      if (tasks) {
        tasks.forEach(function (item) {
          if (!item.taskDefinitionKey) {
            return;
          }
          if (!definitions.some(function (definition) {
              if (definition.id == item.taskDefinitionKey) {
                return true;
              }
            })) {
            definitions.push({id: item.taskDefinitionKey, name: item.name});
          }
        });
      }
      return definitions;
    },
    getTaskDefinitions: function () {
      return taskDefinitions;
    },
    getDefaultProcesses: function () {
      return userProcesses;
    },
    getProcesses: function () {
      var promise = processes.getUserProcesses().then(function (data) {
        var retval = userProcesses.concat(data);
        return retval;
      });
      return promise;
    }
  };
  return service;
}]);
