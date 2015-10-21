angular.module('dashboardJsApp').service('taskFilterService', ['$filter', 'processes', function($filter, processes) {
  var taskDefinitions = [
      {name: 'Всі', id: 'all'},
      {name: 'Старт', id: 'usertask1'},
      {name: 'Обробка', id: 'usertask2'}
    ];
  var userProcesses = [
    {sID: 'all', sName: 'Всі'}
  ];
  var service = {
    getFilteredTasks: function(tasks, model) {
      var filteredTasks = this.filterTaskDefinitions(tasks, model.taskDefinition);
      filteredTasks = this.filterUserProcess(filteredTasks, model.userProcess);
      return filteredTasks;
    },
    filterTaskDefinitions: function(tasks, taskDefinition) {
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
          filteredTasks = tasks.filter(function(task, index) {
            if (!task.taskDefinitionKey) {
              return false;
            }
            if (task.taskDefinitionKey.substr(task.taskDefinitionKey.length-9) == taskDefinition.id) {
              return true;
            }
          });
          return filteredTasks;
        break;
        return null;
      }
    },
    filterUserProcess: function(tasks, userProcess) {
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
      var filteredTasks = tasks.filter(function(task) {
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
    getProcessTaskDefinitions: function(tasks) {
      var definitions = [];
      tasks.forEach(function (item) {
        if (item.taskDefinitionKey && definitions.indexOf(item.taskDefinitionKey) < 0) {
          definitions.push(item.taskDefinitionKey);
        }
      });
      return definitions;
    },
    getTaskDefinitions: function() {
      return taskDefinitions;
    },
    getDefaultProcesses: function() {
      return userProcesses;
    },
    getProcesses: function() {
      var promise = processes.getUserProcesses().then(function(data){
        var retval = userProcesses.concat(data);
        return retval;
      });
      return promise;
    }
  };
  return service;
}]);