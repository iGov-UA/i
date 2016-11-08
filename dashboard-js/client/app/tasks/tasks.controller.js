(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('TasksCtrl', tasksCtrl);

  tasksCtrl.$inject = [
    '$scope', 'tasks', 'processes', 'Modal', 'identityUser', '$localStorage', '$filter', 'lunaService',
    'taskFilterService', 'defaultSearchHandlerService',
    '$stateParams', '$q', '$timeout', '$state', 'tasksStateModel', 'stateModel'
  ];
  function tasksCtrl($scope, tasks, processes, Modal, identityUser, $localStorage, $filter, lunaService,
                     taskFilterService, defaultSearchHandlerService,
                     $stateParams, $q, $timeout, $state, tasksStateModel, stateModel) {

    $scope.tasks = null;
    $scope.sSelectedTask = $stateParams.type;

    $scope.printModalState = {show: false}; // wrapping in object required for 2-way binding
    $scope.taskDefinitions = taskFilterService.getTaskDefinitions();
    $scope.model = stateModel;
    $scope.userProcesses = taskFilterService.getDefaultProcesses();
    $scope.model.userProcess = $scope.userProcesses[0];

    taskFilterService.getProcesses().then(function (userProcesses) {
      $scope.userProcesses = userProcesses;
    });

    $scope.filteredTasks = null;
    $scope.$storage = $localStorage.$default({
      selfAssignedTaskDefinitionFilter: $scope.taskDefinitions[0],
      unassignedTaskDefinitionFilter: $scope.taskDefinitions[0]
    });

    $scope.resetTaskDefinition = function () {
      $scope.model.taskDefinition = $scope.taskDefinitions[0];
      $scope.taskDefinitionsFilterChange();
    };
    $scope.resetStrictTaskDefinition = function () {
      $scope.model.strictTaskDefinition = $scope.strictTaskDefinitions[0];
      $scope.strictTaskDefinitionFilterChange();
    };
    $scope.resetUserProcess = function () {
      $scope.model.userProcess = $scope.userProcesses[0];
      $scope.userProcessFilterChange();
    };
    $scope.resetTaskFilters = function () {
      $scope.model.taskDefinition = $scope.taskDefinitions[0];
      $scope.model.strictTaskDefinition = $scope.strictTaskDefinitions[0];
      $scope.resetUserProcess();
    };
    $scope.$on('taskFilter:strictTaskDefinitions:update', function (ev, data) {
      $scope.strictTaskDefinitions = data;
      // check that current model.strictTaskDefinition is present in data
      if (!data.some(function (taskDefinition) {
          if (!taskDefinition || !$scope.model.strictTaskDefinition) {
            return false;
          }
          if (taskDefinition.id == $scope.model.strictTaskDefinition.id
            && taskDefinition.name == $scope.model.strictTaskDefinition.name) {
            return true;
          }
        })) {
        $scope.model.strictTaskDefinition = data[0];
      }
    });

    function restoreUserProcessesFilter() {
      var storedUserProcess = $scope.$storage[$stateParams.type + 'UserProcessFilter'];
      if (!storedUserProcess) {
        return;
      }
      // check if stored userProcess is presented in selected userprocesses
      if ($scope.userProcesses.some(function (process) {
          if (process.sID == storedUserProcess.sID) {
            return true;
          }
        })) {
        $scope.model.userProcess = storedUserProcess;
      } else {
        $scope.model.userProcess = $scope.userProcesses[0];
      }
    }

    restoreUserProcessesFilter();

    function restoreTaskDefinitionFilter() {
      $scope.model.taskDefinition = $scope.$storage[$stateParams.type + 'TaskDefinitionFilter'];
    }

    var filterLoadedTasks = function () {
      $scope.filteredTasks = taskFilterService.getFilteredTasks($scope.tasks, $scope.model);

      $timeout(function () {
        // trigger scroll event to load more tasks
        $('#tasks-list-holder').trigger('scroll');
      });
    };

    $scope.$on('task-submitted', function (e, task) {
      $scope.tasks = $filter('filter')($scope.tasks, {id: '!' + task.id});
      filterLoadedTasks();
    });

    restoreTaskDefinitionFilter();
    $scope.taskDefinitionsFilterChange = function () {
      $scope.$storage[$stateParams.type + 'TaskDefinitionFilter'] = $scope.model.taskDefinition;
      filterLoadedTasks();
    };
    $scope.userProcessFilterChange = function () {
      $scope.$storage[$stateParams.type + 'UserProcessFilter'] = $scope.model.userProcess;
      filterLoadedTasks();
    };
    $scope.userProcessFilterChange();
    $scope.strictTaskDefinitionFilterChange = function () {
      filterLoadedTasks();
    };
    $scope.selectedSortOrder = {
      selected: "datetime_asc"
    };

    $scope.predicate = 'createTime';
    $scope.reverse = false;

    $scope.sortOrderOptions = [{"value": 'datetime_asc', "text": "Від найдавніших"},
      {"value": 'datetime_desc', "text": "Від найновіших"}];

    $scope.selectedSortOrderChanged = function () {
      switch ($scope.selectedSortOrder.selected) {
        case 'datetime_asc':
          $scope.selectedSortOrder.selected = "datetime_desc";
          if ($stateParams.type == tasks.filterTypes.finished) $scope.predicate = 'startTime';
          else $scope.predicate = 'createTime';
          $scope.reverse = true;
          break;
        case 'datetime_desc':
          $scope.selectedSortOrder.selected = "datetime_asc";
          if ($stateParams.type == tasks.filterTypes.finished) $scope.predicate = 'startTime';
          else $scope.predicate = 'createTime';
          $scope.reverse = false;
          break;
      }
    };

    $scope.isTaskFilterActive = function (taskType) {
      return $stateParams.type === taskType;
    };

    $scope.isTaskSelected = function (task) {
      return tasksStateModel.taskId == task.id;
    };

    var tasksPage = 0;
    var lastTasksResult = null;

    var loadNextTasksPage = function () {
      var defer = $q.defer();
      var data = {
        page: tasksPage
      };
      if ($stateParams.type == 'tickets') {
        data.bEmployeeUnassigned = $scope.ticketsFilter.bEmployeeUnassigned;
        if ($scope.ticketsFilter.dateMode == 'date' && $scope.ticketsFilter.sDate) {
          data.sDate = $filter('date')($scope.ticketsFilter.sDate, 'yyyy-MM-dd');
        }

        // прерываем постраничную загрузку на вкладке с тикетами, т.к. они отдаются все сразу
        if (tasksPage > 0) {
          defer.resolve([]);
          return defer.promise;
        }
      }

      $scope.tasksLoading = true;

      tasks.list($stateParams.type, data)
        .then(function (oResult) {
          try {
            if (oResult.data.code) {
              var e = new Error(oResult.data.message);
              e.name = oResult.data.code;

              throw e;
            }

            if (oResult.data !== null && oResult.data !== undefined) {
              // build tasks array
              var aTaskFiltered = _.filter(oResult.data, function (oTask) {
                return oTask.endTime !== null;
              });
              if (!$scope.tasks)
                $scope.tasks = [];
              for (var i = 0; i < aTaskFiltered.length; i++)
                $scope.tasks.push(aTaskFiltered[i]);
              lastTasksResult = oResult;
              // build filtered tasks array
              filterLoadedTasks();

              defer.resolve(aTaskFiltered);
              tasksPage++;
            }

          } catch (e) {
            Modal.inform.error()(e);
            defer.reject(e);
          }
        })
        .catch(function (err) {
          Modal.inform.error()(err);
          defer.reject(err);
        })
        .finally(function () {
          $scope.tasksLoading = false;
        });

      return defer.promise;
    };

    $scope.applyTaskFilter = function () {
      tasksPage = 0;
      $scope.tasks = $scope.filteredTasks = null;
      $scope.sSelectedTask = $stateParams.type;
      $scope.selectedTask = null;
      restoreTaskDefinitionFilter();
      restoreUserProcessesFilter();
      $scope.error = null;

      if ($stateParams.type == tasks.filterTypes.finished) {
        $scope.predicate = 'startTime';
      }

      loadNextTasksPage().then(function (tasks) {
        // загружаем список пока не будет найдена задача из стейта tasks.typeof.view
        // tasksStateModel.taskId устанавливается при резолве этого стейта
        updateTaskSelection(tasks, tasksStateModel.taskId);
      });
    };

    $scope.getUserName = function () {
      identityUser
        .getUserInfo($scope.selectedTask.assignee)
        .then(function (userInfo) {
          return "".concat(userInfo.firstName, " ", userInfo.lastName);
        }).catch(function () {
        return $scope.selectedTask.assignee;
      });
    };

    $scope.selectTask = function (oTask) {
      $state.go('tasks.typeof.view', {id: oTask.id});
    };

    $scope.sDateShort = function (sDateLong) {
      if (sDateLong !== null) {
        var o = new Date(sDateLong); //'2015-04-27T13:19:44.098+03:00'
        return o.getFullYear() + '-' + ((o.getMonth() + 1) > 9 ? '' : '0') + (o.getMonth() + 1) + '-' + (o.getDate() > 9 ? '' : '0') + o.getDate() + ' ' + (o.getHours() > 9 ? '' : '0') + o.getHours() + ':' + (o.getMinutes() > 9 ? '' : '0') + o.getMinutes();
      }
    };

    function endsWith(s, sSuffix) {
      if (s == null) {
        return false;
      }
      return s.indexOf(sSuffix, s.length - sSuffix.length) !== -1;
    }

    $scope.sTaskClass = function (sUserTask) {
      //"_10" - подкрашивать строку - красным цветом
      //"_5" - подкрашивать строку - желтым цветом
      //"_1" - подкрашивать строку - зеленым цветом
      var sClass = "";
      if (endsWith(sUserTask, "_red")) {
        return "bg_red";
      }
      if (endsWith(sUserTask, "_yellow")) {
        return "bg_yellow";
      }
      if (endsWith(sUserTask, "_green")) {
        return "bg_green";
      }
      if (endsWith(sUserTask, "usertask1")) {
        return "bg_first";
      }
    };

    $scope.getProcessName = function (task) {
      var result = processes.getProcessName(task.processDefinitionId);
      if (angular.isDefined(task.variables)) {
        for (var i = 0; i < task.variables.length; i++) {
          var v = task.variables[i];
          if (v.name == 'sPlace' && v.type == 'string')
            result = v.value + ' - ' + result;
        }
      }
      return result;
    };

    /**
     * Check if task in status
     * @param {object} task Task data
     * @param {string} status Status to check
     * @returns {boolean} True if task is in status otherwise false
     */
    $scope.hasTaskStatus = function(task, status) {
      var saTaskStatusVarData = getTaskVariable(task.variables, 'saTaskStatus');
      return hasTaskStatus(saTaskStatusVarData, status);
    };

    $scope.getTaskTitle = function (task) {
      return '№' + task.processInstanceId + lunaService.getLunaValue(task.processInstanceId)
        + ' ' + $scope.getProcessName(task) + ' | ' + task.name;
    };

    $scope.getTaskDateTimeTitle = function (task) {
      var result = task.createTime ? $scope.sDateShort(task.createTime) : $scope.sDateShort(task.startTime);
      if (task.endTime)
        result += ' - ' + $scope.sDateShort(task.endTime);
      return result;
    };

    $scope.ticketsFilter = {
      dateMode: 'date',
      dateModeList: [
        {key: 'all', title: 'Всі дати'},
        {key: 'date', title: 'Обрати дату'}
      ],
      sDate: moment().format('YYYY-MM-DD'),
      options: {
        timePicker: false
      },
      bEmployeeUnassigned: false
    };

    $scope.applyTicketsFilter = function () {
      $scope.applyTaskFilter();
    };

    $scope.setTicketsDateMode = function (mode) {
      $scope.ticketsFilter.dateMode = mode;
      $scope.applyTicketsFilter();
    };

    $scope.lunaService = lunaService;

    var updateTaskSelection = function (tasks, nID_Task) {
      if (nID_Task && tasks && tasks.length > 0) {
        var foundTask = null;
        for (var i = 0; i < tasks.length; i++) {
          var task = tasks[i];
          if (task.id == nID_Task) {
            foundTask = task;
            break;
          }
        }
        if (foundTask)
          $scope.selectTask(foundTask);
        else
          loadNextTasksPage().then(function (nextTasks) {
            updateTaskSelection(nextTasks, nID_Task);
          });
      } else if ($state.current.name != 'tasks.typeof.view')
        initDefaultTaskSelection();
    };

    var initDefaultTaskSelection = function () {
      if ($scope.selectedTask)
        $scope.selectTask($scope.selectedTask);
      else if ($scope.filteredTasks && $scope.filteredTasks[0])
        $scope.selectTask($scope.filteredTasks[0]);
    };

    var defaultErrorHandler = function (response, msgMapping) {
      defaultSearchHandlerService.handleError(response, msgMapping);
    };

    $scope.whenScrolled = function () {
      if ($scope.tasksLoading === false && $scope.isLoadMoreAvailable())
        $scope.loadMoreTasks();
    };

    $scope.isLoadMoreAvailable = function () {
      return lastTasksResult !== null && lastTasksResult.start + lastTasksResult.size < lastTasksResult.total;
    };

    $scope.loadMoreTasks = function () {
      loadNextTasksPage();
    };

    $scope.applyTaskFilter();
  }

  /**
   * Returns task variable data
   * @param {array} variables Task variables
   * @param {string} varName variable name
   */
  function getTaskVariable(variables, varName) {
    if (angular.isDefined(variables)) {
      for (var i = 0; i < variables.length; i++) {
        var v = variables[i];

        if (v.name == varName)
          return v;
      }
    }

    return null;
  }

  /**
   * Check task is in status
   * @param {object} variableData
   * @status {string} Status to check
   */
  function hasTaskStatus(variableData, status) {
    return (variableData && variableData.value) ? variableData.value.indexOf(status) >= 0 : false;
  }
})();
