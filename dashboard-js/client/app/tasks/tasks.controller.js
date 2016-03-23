(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('TasksCtrl', tasksCtrl);

  tasksCtrl.$inject = [
    '$scope', '$window', 'tasks', 'processes', 'Modal', 'Auth', 'identityUser', '$localStorage', '$filter', 'lunaService',
    'PrintTemplateService', 'taskFilterService', 'MarkersFactory', 'iGovNavbarHelper', '$location', 'defaultSearchHandlerService',
    '$stateParams', '$q', '$timeout'
  ];
  function tasksCtrl(
    $scope, $window, tasks, processes, Modal, Auth, identityUser, $localStorage, $filter, lunaService,
    PrintTemplateService, taskFilterService, MarkersFactory, iGovNavbarHelper, $location, defaultSearchHandlerService,
    $stateParams, $q, $timeout
  ) {

    $scope.tasks = null;
    $scope.selectedTasks = {};
    $scope.sSelectedTask = "";
    $scope.taskFormLoaded = false;
    $scope.checkSignState = {inProcess: false, show: false, signInfo: null, attachmentName: null};
    $scope.printTemplateList = [];
    $scope.printModalState = {show: false}; // wrapping in object required for 2-way binding
    $scope.taskDefinitions = taskFilterService.getTaskDefinitions();
    $scope.model = {
      printTemplate: null,
      taskDefinition: null,
      strictTaskDefinition: null,
      userProcess: null
    };
    $scope.userProcesses = taskFilterService.getDefaultProcesses();
    $scope.model.userProcess = $scope.userProcesses[0];
    $scope.applyTaskFilters = function() {

    };
    $scope.resetTaskDefinition = function() {
      $scope.model.taskDefinition = $scope.taskDefinitions[0];
      $scope.taskDefinitionsFilterChange();
    };
    $scope.resetStrictTaskDefinition = function() {
      $scope.model.strictTaskDefinition = $scope.strictTaskDefinitions[0];
      $scope.strictTaskDefinitionFilterChange();
    };
    $scope.resetUserProcess = function() {
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
    taskFilterService.getProcesses().then(function (data) {
      $scope.userProcesses = data;
      $scope.userProcessesLoaded = true;
      console.log('userProcesses', data);
      restoreUserProcessesFilter();
      $scope.userProcessFilterChange();
    });
    function restoreUserProcessesFilter() {
      var storedUserProcess = $scope.$storage[$scope.$storage['menuType'] + 'UserProcessFilter'];
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

    console.log("$scope.userProcesses", $scope.userProcesses);

    $scope.filterTypes = tasks.filterTypes;
    $scope.filteredTasks = null;
    $scope.$storage = $localStorage.$default({
      menuType: tasks.filterTypes.selfAssigned,
      selfAssignedTaskDefinitionFilter: $scope.taskDefinitions[0],
      unassignedTaskDefinitionFilter: $scope.taskDefinitions[0]
    });

    function restoreTaskDefinitionFilter() {
      $scope.model.taskDefinition = $scope.$storage[$scope.$storage['menuType'] + 'TaskDefinitionFilter'];
    }

    var filterLoadedTasks = function() {
      $scope.filteredTasks = taskFilterService.getFilteredTasks($scope.tasks, $scope.model);

      $timeout(function(){
        // trigger scroll event to load more tasks
        $('#tasks-list-holder').trigger('scroll');
      });
    };

    restoreTaskDefinitionFilter();
    $scope.taskDefinitionsFilterChange = function () {
      $scope.$storage[$scope.$storage['menuType'] + 'TaskDefinitionFilter'] = $scope.model.taskDefinition;
      filterLoadedTasks();
    };
    $scope.userProcessFilterChange = function () {
      $scope.$storage[$scope.$storage['menuType'] + 'UserProcessFilter'] = $scope.model.userProcess;
      filterLoadedTasks();
    };
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
          if ($scope.$storage.menuType == tasks.filterTypes.finished) $scope.predicate = 'startTime';
          else $scope.predicate = 'createTime';
          $scope.reverse = false;
          break;
        case 'datetime_desc':
          if ($scope.$storage.menuType == tasks.filterTypes.finished) $scope.predicate = 'startTime';
          else $scope.predicate = 'createTime';
          $scope.reverse = true;
          break;
      }
    };

    $scope.print = function () {
      if ($scope.selectedTask && $scope.taskForm) {
        if ($scope.hasUnPopulatedFields()) {
          Modal.inform.error()('Не всі поля заповнені!');
          return;
        }
        $scope.printModalState.show = !$scope.printModalState.show;
      }
    };

    $scope.hasUnPopulatedFields = function () {
      if ($scope.selectedTask && $scope.taskForm) {
        var unpopulated = $scope.taskForm.filter(function (item) {
          return (item.value === undefined || item.value === null || item.value.trim() === "") && (item.required || $scope.isCommentAfterReject(item));//&& item.type !== 'file'
        });
        return unpopulated.length > 0;
      } else {
        return true;
      }
    };

    $scope.unpopulatedFields = function () {
      if ($scope.selectedTask && $scope.taskForm) {
        var unpopulated = $scope.taskForm.filter(function (item) {
          return (item.value === undefined || item.value === null || item.value.trim() === "") && (item.required || $scope.isCommentAfterReject(item));//&& item.type !== 'file'
        });
        return unpopulated;
      } else {
        return [];
      }
    };

    $scope.isFormPropertyDisabled = function (formProperty) {
      if (!($scope.selectedTask && $scope.selectedTask !== null)) {
        return true;
      }
      if ($scope.selectedTask.assignee === null) {
        return true;
      }
      if ($scope.sSelectedTask === null) {
        return true;
      }
      if (formProperty === null) {
        return true;
      }
      if ($scope.sSelectedTask === 'finished') {
        return true;
      }
      var sID_Field = formProperty.id;
      if (sID_Field === null) {
        return true;
      }
      //console.log("sID_Field=" + sID_Field + ",formProperty.writable=" + formProperty.writable);
      if (!formProperty.writable) {
        return true;
      }
      var bNotBankID = sID_Field.indexOf("bankId") !== 0;
      //console.log("sID_Field=" + sID_Field + ",bNotBankID=" + bNotBankID);
      var bEditable = bNotBankID;
      var sFieldName = formProperty.name;
      if (sFieldName === null) {
        return true;
      }
      var as = sFieldName.split(";");
      //console.log("sID_Field=" + sID_Field + ",as=" + as + ",as.length=" + as.length);
      if (as.length > 2) {
        bEditable = as[2] === "writable=true" ? true : as[2] === "writable=false" ? false : bEditable;
      }
      //console.log("sID_Field=" + sID_Field + ",bEditable=" + bEditable);

      return !bEditable;//false
    };

    $scope.isTaskFilterActive = function (taskType) {
      $scope.sSelectedTask = $scope.$storage.menuType;
      return $scope.$storage.menuType === taskType;
    };

    $scope.isTaskSelected = function (task) {
      return $scope.selectedTask && $scope.selectedTask.id === task.id;
    };

    $scope.hasAttachment = function () {
      return $scope.taskAttachments !== undefined && $scope.taskAttachments !== null && $scope.taskAttachments.length !== 0;
    };

    $scope.downloadAttachment = function () {
      tasks.downloadDocument($scope.selectedTask.id);
    };

    var tasksPage = 0;
    var totalTasks = null;

    var loadNextTasksPage = function() {
      var defer = $q.defer();
      var data = {
        page: tasksPage
      };
      if ($scope.$storage.menuType == 'tickets') {
        data.bEmployeeUnassigned = $scope.ticketsFilter.bEmployeeUnassigned;
        if ($scope.ticketsFilter.dateMode == 'date' && $scope.ticketsFilter.sDate) {
          data.sDate = $filter('date')($scope.ticketsFilter.sDate, 'yyyy-MM-dd');
        }
      }

      $scope.tasksLoading = true;

      tasks.list($scope.$storage.menuType, data)
        .then(function (oResult) {
          try {
            if (oResult.data !== null && oResult.data !== undefined) {
              // build tasks array
              var aTaskFiltered = _.filter(oResult.data, function (oTask) {
                return oTask.endTime !== null;
              });
              if (!$scope.tasks)
                $scope.tasks = [];
              for (var i = 0; i < aTaskFiltered.length; i++)
                $scope.tasks.push(aTaskFiltered[i]);
              totalTasks = oResult.total;
              // build filtered tasks array
              filterLoadedTasks();

              defer.resolve(aTaskFiltered);
              tasksPage ++;
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

    $scope.applyTaskFilter = function (menuType, nID_Task, resetSelectedTask) {
      tasksPage = 0;
      $scope.tasks = $scope.filteredTasks = null;
      //$scope.goToTasks(menuType);//"selfAssigned"
      $scope.sSelectedTask = $scope.$storage.menuType;
      $scope.selectedTask = resetSelectedTask ? null : $scope.selectedTasks[menuType];
      $scope.$storage.menuType = menuType;
      restoreTaskDefinitionFilter();
      restoreUserProcessesFilter();
      $scope.taskForm = null;
      $scope.taskId = null;
      $scope.nID_Process = null; //task.processInstanceId;
      $scope.attachments = null;
      $scope.aOrderMessage = null;
      $scope.error = null;
      $scope.taskAttachments = null;
      $scope.taskFormLoaded = false;

      if (menuType == tasks.filterTypes.finished){
        $scope.predicate = 'startTime';
      }

      loadNextTasksPage().then(function(tasks){
        updateTaskSelection(nID_Task, tasks);
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

    $scope.unassign = function () {
      tasks.unassign($scope.selectedTask.id)
        .then(function () {
          $scope.selectTask($scope.selectedTask);
        })
        .then(function () {
          return tasks.getTask($scope.selectedTask.id);
        })
        .then(function (updatedTaskResult) {
          angular.copy(updatedTaskResult, $scope.selectedTask);
        })
        .catch(defaultErrorHandler);
    };

    $scope.selectTask = function (oTask) {
      $scope.printTemplateList = [];
      $scope.model.printTemplate = null;
      $scope.taskFormLoaded = false;
      $scope.sSelectedTask = $scope.$storage.menuType;

      $scope.taskForm = null;
      $scope.attachments = null;
      $scope.aOrderMessage = null;
      $scope.error = null;
      $scope.taskAttachments = null;
      $scope.clarify = false;
      $scope.clarifyFields = {};
      if (!(oTask && oTask !== null && oTask !== undefined)) {
        return;
      }

      $scope.selectedTask = oTask;
      $scope.selectedTasks[$scope.$storage.menuType] = oTask;
      $scope.taskId = oTask.id;
      $scope.nID_Process = oTask.processInstanceId;

      var setTaskForm = function(formProperties){
        $scope.taskForm = formProperties;
        $scope.taskForm = addIndexForFileItems($scope.taskForm);
        $scope.printTemplateList = PrintTemplateService.getTemplates($scope.taskForm);
        if ($scope.printTemplateList.length > 0) {
          $scope.model.printTemplate = $scope.printTemplateList[0];
        }
        if ($scope.selectedTask) {
          tasks.getTaskData($scope.selectedTask.id).then( function(taskData) {
            $scope.taskFormLoaded = true;
            $scope.taskForm.taskData = taskData;
          });
        }
      };

      if (oTask.endTime) {
        tasks
          .taskFormFromHistory(oTask.id)
          .then(function (result) {
            result = JSON.parse(result);
            setTaskForm(result.data[0].variables);
          })
          .catch(defaultErrorHandler);
      } else {
        tasks
          .taskForm(oTask.id)
          .then(function (result) {
            result = JSON.parse(result);
            setTaskForm(result.formProperties);
            $scope.taskForm.forEach(function (field) {
              if (field.type === 'markers' && $.trim(field.value)) {
                var sourceObj = null;
                try {
                  sourceObj = JSON.parse(field.value);
                } catch (ex) {
                  console.log('markers attribute ' + field.name + ' contain bad formatted json\n' + ex.name + ', ' + ex.message + '\nfield.value: ' + field.value);
                }
                if (sourceObj !== null) {
                  _.merge(MarkersFactory.getMarkers(), sourceObj, function (destVal, sourceVal) {
                    if (_.isArray(sourceVal)) {
                      return sourceVal;
                    }
                  });
                }
              }
            });
          })
          .catch(defaultErrorHandler);
      }

      tasks
        .taskAttachments(oTask.id)
        .then(function (result) {
          result = JSON.parse(result);
          $scope.attachments = result;
        })
        .catch(defaultErrorHandler);


      tasks
        .getOrderMessages(oTask.processInstanceId)
        .then(function (result) {
          result = JSON.parse(result);
          angular.forEach(result, function(message) {
            if (message.hasOwnProperty('sData') && message.sData.length > 1) {
              message.osData = JSON.parse(message.sData);
            }
          });
          $scope.aOrderMessage = result;
        })
        .catch(defaultErrorHandler);


      tasks.getTaskAttachments(oTask.id)
        .then(function (result) {
          $scope.taskAttachments = result;
        })
        .catch(defaultErrorHandler);


    };

    $scope.submitTask = function () {
      if ($scope.selectedTask && $scope.taskForm) {
        $scope.taskForm.isSubmitted = true;

        var unpopulatedFields = $scope.unpopulatedFields();
        if (unpopulatedFields.length > 0) {
          var errorMessage = 'Будь ласка, заповніть поля: ';

          if (unpopulatedFields.length == 1) {

            var nameToAdd = unpopulatedFields[0].name;
            if (nameToAdd.length > 50) {
              nameToAdd = nameToAdd.substr(0, 50) + "...";
            }

            errorMessage = "Будь ласка, заповніть полe '" + nameToAdd + "'";
          }
          else {
            unpopulatedFields.forEach(function (field) {

              var nameToAdd = field.name;
              if (nameToAdd.length > 50) {
                nameToAdd = nameToAdd.substr(0, 50) + "...";
              }
              errorMessage = errorMessage + "'" + nameToAdd + "',<br />";
            });
            var comaIndex = errorMessage.lastIndexOf(',');
            errorMessage = errorMessage.substr(0, comaIndex);
          }
          Modal.inform.error()(errorMessage);
          return;
        }

        $scope.taskForm.isInProcess = true;

        tasks.submitTaskForm($scope.selectedTask.id, $scope.taskForm, $scope.selectedTask)
          .then(function (result) {
            //selectedTask
            // $scope.taskForm
            var sMessage = "Форму відправлено.";
            angular.forEach($scope.taskForm, function (oField) {
              if (oField.id === "sNotifyEvent_AfterSubmit") {
                sMessage = oField.value;
              }
            });


            Modal.inform.success(function (result) {
              $scope.lightweightRefreshAfterSubmit();
            })(sMessage + " " + (result && result.length > 0 ? (': ' + result) : ''));

          })
          .catch(defaultErrorHandler);
      }
    };

    $scope.assignTask = function () {
      $scope.taskForm.isInProcess = true;

      tasks.assignTask($scope.selectedTask.id, Auth.getCurrentUser().id)
        .then(function (result) {
          Modal.assignTask(function (event) {
            //$scope.lightweightRefreshAfterSubmit();
            $scope.selectedTasks['unassigned'] = null;
             //NavbarCtrl.goToTasks("selfAssigned");
            $location.path('/tasks/' + "selfAssigned");
            $scope.applyTaskFilter(iGovNavbarHelper.menus[1].type, $scope.selectedTask.id);
          }, 'Задача у вас в роботі', $scope.lightweightRefreshAfterSubmit);

        })
        .catch(defaultErrorHandler());

    };

    $scope.upload = function (files, propertyID) {
      tasks.upload(files, $scope.taskId).then(function (result) {
        var filterResult = $scope.taskForm.filter(function (property) {
          return property.id === propertyID;
        });
        if (filterResult && filterResult.length === 1) {
          filterResult[0].value = result.response.id;
          filterResult[0].fileName = result.response.name;
        }
      }).catch(function (err) {
        Modal.inform.error()('Помилка. ' + err.code + ' ' + err.message);
      });
    };

    $scope.lightweightRefreshAfterSubmit = function () {
      //lightweight refresh only deletes the submitted task from the array of current type of tasks
      //so we don't need to refresh the whole page
      $scope.selectedTasks[$scope.$storage.menuType] = null;
      iGovNavbarHelper.loadTaskCounters();
      $scope.tasks = $.grep($scope.tasks, function (e) {
        return e.id != $scope.selectedTask.id;
      });
      filterLoadedTasks();
      $scope.taskForm.isInProcess = false;
      $scope.taskForm.isSuccessfullySubmitted = true;
      if (!$scope.tasks || !$scope.tasks[0]) {
        $scope.selectedTask = null;
      }
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

    $scope.sFieldLabel = function (sField) {
      var s = '';
      if (sField !== null) {
        var a = sField.split(';');
        s = a[0].trim();
      }
      return s;
    };

    $scope.nID_FlowSlotTicket_FieldQueueData = function (sValue) {
      var nAt = sValue.indexOf(":");
      var nTo = sValue.indexOf(",");
      var s = sValue.substring(nAt + 1, nTo);
      var nID_FlowSlotTicket = 0;
      try {
        nID_FlowSlotTicket = s;
      } catch (_) {
        nID_FlowSlotTicket = 1;
      }
      return nID_FlowSlotTicket;
    };

    $scope.sDate_FieldQueueData = function (sValue) {
      var nAt = sValue.indexOf("sDate");
      var nTo = sValue.indexOf("}");
      var s = sValue.substring(nAt + 5 + 1 + 1 + 1, nTo - 1 - 6);
      var sDate = "Дата назначена!";
      try {
        sDate = s;
      } catch (_) {
        sDate = "Дата назначена!";
      }
      return sDate;
    };


    $scope.sEnumValue = function (aItem, sID) {
      var s = sID;
      _.forEach(aItem, function (oItem) {
        if (oItem.id == sID) {
          s = oItem.name;
        }
      });
      return s;
    };


    $scope.sFieldNotes = function (sField) {
      var s = null;
      if (sField !== null) {
        var a = sField.split(';');
        if (a.length > 1) {
          s = a[1].trim();
          if (s === '') {
            s = null;
          }
        }
      }
      return s;
    };

    $scope.getProcessName = function (processDefinitionId) {
      return processes.getProcessName(processDefinitionId);
    };

    $scope.init = function () {
      var tab = $location.path().substr('/tasks/'.length) || 'tickets';
      $scope.taskFormLoaded = false;
      $scope.autoScrollTaskId = $stateParams.id;

      loadSelfAssignedTasks().then(function(){
        if ($stateParams.type) {
          $scope.applyTaskFilter($stateParams.type, $stateParams.id);
        } else {
          _.each(iGovNavbarHelper.menus, function (menu) {
            if (menu.tab === tab) {
              $scope.applyTaskFilter(menu.type);
            }
          });
        }
      });
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
      $scope.applyTaskFilter($scope.$storage.menuType, null, true);
    };

    $scope.setTicketsDateMode = function (mode) {
      $scope.ticketsFilter.dateMode = mode;
      $scope.applyTicketsFilter();
    };

    $scope.lunaService = lunaService;

    var searchResult = {
      tasks: []
    };

    function loadSelfAssignedTasks() {
      return processes.list().then(function (processesDefinitions) {
        console.log("[loadSelfAssignedTasks]processesDefinitions=" + processesDefinitions);
        //$scope.applyTaskFilter($scope.$storage.menuType);
      }).catch(defaultErrorHandler);
    }

    function addIndexForFileItems(val) {
      var idx = 0;
      return (val || []).map(function (item) {
        if (item.type === 'file') {
          item.nFileIdx = idx;
          idx++;
        }
        return item;
      });
    }

    function updateTaskSelection(nID_Task, tasks) {
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
            updateTaskSelection(nID_Task, nextTasks);
          });
      } else
        initDefaultTaskSelection();
    }

    var initDefaultTaskSelection = function () {
      if ($scope.selectedTask)
        $scope.selectTask($scope.selectedTask);
      else if ($scope.filteredTasks && $scope.filteredTasks[0])
        $scope.selectTask($scope.filteredTasks[0]);
    };

    var defaultErrorHandler = function (response, msgMapping) {
      defaultSearchHandlerService.handleError(response, msgMapping);
      if ($scope.taskForm) {
        $scope.taskForm.isSuccessfullySubmitted = false;
        $scope.taskForm.isInProcess = false;
      }
    };

    $scope.isCommentAfterReject = function (item) {
      if (item.id != "comment") return false;

      var decision = $.grep($scope.taskForm, function (e) {
        return e.id == "decide";
      });

      if (decision.length == 0) {
        // no decision
      } else if (decision.length == 1) {
        if (decision[0].value == "reject") return true;
      }
      return false;
    };

    $scope.isRequired = function (item) {
      return !$scope.isFormPropertyDisabled(item) && (item.required || $scope.isCommentAfterReject(item)); //item.writable
    };

    $scope.isTaskSubmitted = function (item) {
      return $scope.taskForm.isSubmitted;
    };

    $scope.isTaskSuccessfullySubmitted = function () {
      if ($scope.selectedTask && $scope.taskForm) {
        if ($scope.taskForm.isSuccessfullySubmitted != undefined && $scope.taskForm.isSuccessfullySubmitted)
          return true;
      }
      return false;
    };


    $scope.isTaskInProcess = function () {
      if ($scope.selectedTask && $scope.taskForm) {
        if ($scope.taskForm.isInProcess != undefined && $scope.taskForm.isInProcess)
          return true;
      }
      return false;
    };

    $scope.clarify = false;

    $scope.clarifyToggle = function () {
      $scope.clarify = !$scope.clarify;
    };

    $scope.clarifyFields = {};
    $scope.clarifyModel = {
      sBody: ''
    };

      $scope.getCurrentUserName = function() {
        var user = Auth.getCurrentUser();
        return user.firstName + ' ' + user.lastName;
      };

    $scope.clarifySend = function () {

      var oData = {
        //nID_Protected: $scope.taskId,
        //nID_Order: $scope.nID_Process,
        nID_Process: $scope.nID_Process,
        saField: '',
        soParams: '',
        sMail: '',
        sBody: $scope.clarifyModel.sBody
      };

      var soParams = {sEmployerFIO:$scope.getCurrentUserName};
      var aFields = [];
      var sClientFIO=null;
      var sClientName=null;
      var sClientSurname=null;

      angular.forEach($scope.taskForm, function (item) {
        if (angular.isDefined($scope.clarifyFields[item.id]) && $scope.clarifyFields[item.id].clarify)
          aFields.push({
            sID: item.id,
            sName: $scope.sFieldLabel(item.name),
            sType: item.type,
            sValue: item.value,
            sValueNew: item.value,
            sNotify: $scope.clarifyFields[item.id].text
          });

          if (item.id === 'email'){
            oData.sMail = item.value;
          }
          //<activiti:formProperty id="bankIdfirstName" name="Ім'я" type="string" ></activiti:formProperty>
          //<activiti:formProperty id="bankIdmiddleName" name="По Батькові" type="string" ></activiti:formProperty>
          if (item.id === 'bankIdfirstName'){
              sClientName = item.value;
          }
          if (item.id === 'bankIdmiddleName'){
              sClientSurname = item.value;
          }
      });

      if($scope.clarifyModel.sBody.trim().length===0 && aFields.length===0){
          Modal.inform.warning()('Треба ввести коментар або обрати поле/ля');
        //Modal.inform.success(function () {
        //})('Треба ввести коментар або обрати поле/ля');
        return;
      }
          //Modal.inform.warning()(signInfo.message);


      if(sClientName!==null){
          sClientFIO = sClientName;
          if(sClientSurname!==null){
              sClientFIO+=" "+sClientSurname;
          }
      }
      if(sClientFIO!==null){
          //angular.extend(soParams, {"sClientFIO":sClientFIO});
          soParams["sClientFIO"] = sClientFIO;
      }

      oData.saField = JSON.stringify(aFields);
      oData.soParams = JSON.stringify(soParams);
      tasks.setTaskQuestions(oData).then(function () {
        $scope.clarify = false;
        Modal.inform.success(function () {
        })('Зауваження відправлено успішно');
      });
    };

    $scope.checkAttachmentSign = function (nID_Task, nID_Attach, attachmentName) {
      $scope.checkSignState.inProcess = true;
      tasks.checkAttachmentSign(nID_Task, nID_Attach).then(function (signInfo) {
        if (signInfo.customer) {
          $scope.checkSignState.show = !$scope.checkSignState.show;
          $scope.checkSignState.signInfo = signInfo;
          $scope.checkSignState.attachmentName = attachmentName;
        } else if (signInfo.code) {
          $scope.checkSignState.show = false;
          $scope.checkSignState.signInfo = null;
          $scope.checkSignState.attachmentName = null;
          Modal.inform.warning()(signInfo.message);
        } else {
          $scope.checkSignState.show = false;
          $scope.checkSignState.signInfo = null;
          $scope.checkSignState.attachmentName = null;
          Modal.inform.warning()('Немає підпису');
        }
      }).catch(function (error) {
        $scope.checkSignState.show = false;
        $scope.checkSignState.signInfo = null;
        $scope.checkSignState.attachmentName = null;
        Modal.inform.error()(error.message);
      }).finally(function () {
        $scope.checkSignState.inProcess = false;
      });
    }

      $scope.getMessageFileUrl = function (oMessage, oFile) {
        return './api/tasks/' + $scope.nID_Process + '/getMessageFile/' + oMessage.nID + '/' + oFile.sFileName;
    }

    $scope.whenScrolled = function() {
      if ($scope.tasksLoading===false && $scope.isLoadMoreAvailable())
        $scope.loadMoreTasks();
    };

    $scope.isLoadMoreAvailable = function () {
      return $scope.tasks !== null && $scope.tasks.length < totalTasks;
    };

    $scope.loadMoreTasks = function() {
      loadNextTasksPage();
    };
  }
})();
