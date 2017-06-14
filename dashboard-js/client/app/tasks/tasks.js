(function() {
  'use strict';

  angular
    .module('dashboardJsApp')
    .config(tasksConfig);

  tasksConfig.$inject = ['$stateProvider'];
  function tasksConfig($stateProvider) {
    $stateProvider
      .state('tasks', {
        url: '/tasks',
        controller: 'TasksBaseCtrl',
        access: {
          requiresLogin: true
        },
        resolve: {
          tasksStateModel: function () {
            return {};
          },
          stateModel: function () {
            return {
              printTemplate: null,
              taskDefinition: null,
              strictTaskDefinition: null,
              userProcess: null
            }
          },
          processesList: [
            'processes',
            function (processes) {
              return processes.list();
            }
          ]
        }
      })
      .state('tasks.typeof', {
        url: '/:type',
        access: {
          requiresLogin: true
        },
        views: {
          '@': {
            templateUrl: 'app/tasks/tasks.html',
            controller: 'TasksCtrl'
          }
        }
      })
      .state('tasks.typeof.ecp', {
        url: '/ecp',
        templateUrl: 'app/tasks/edsView.html',
        controller: 'edsViewCtrl',
        access: {
          requiresLogin: true
        },
        resolve: {
          unsignedDocsList: [
            'DocumentsService',
            function (DocumentsService) {
              return DocumentsService.getUnsignedDocsList();
            }
          ]
        }
      })
      .state('tasks.typeof.create', {
        url: '/create:tab',
        templateUrl: 'app/tasks/createView.html',
        controller: 'createView',
        access: {
          requiresLogin: true
        }
      })
      .state('tasks.typeof.newtask', {
        url: '/new/:id',
        templateUrl: 'app/tasks/createTask.html',
        controller: 'createTask',
        access: {
          requiresLogin: true
        }
      })
      .state('tasks.typeof.view', {
        url: '/:id',
        templateUrl: 'app/tasks/taskView.html',
        controller: 'TaskViewCtrl',
        access: {
          requiresLogin: true
        },
        resolve: {
          taskData: [
            'tasks',
            '$stateParams',
            function(tasks, $stateParams) {
              var params = {
                nID_Task: $stateParams.id,
                bIncludeGroups: true,
                bIncludeStartForm: true,
                bIncludeAttachments: true,
                bIncludeProcessVariables: $stateParams.type === 'documents',
                bIncludeMessages: true
              };
              return tasks.getTaskData(params, false)
            }
          ],
          oTask: [
            'tasks',
            '$stateParams',
            'tasksStateModel',
            '$q',
            function (tasks, $stateParams, tasksStateModel, $q) {
              tasksStateModel.taskId = $stateParams.id;
              if ($stateParams.type == 'finished'){
                var defer = $q.defer();
                tasks.taskFormFromHistory($stateParams.id).then(function(response){
                  defer.resolve(JSON.parse(response).data[0]);
                }, defer.reject);
                return defer.promise;
              }
              else {
                return tasks.getTask($stateParams.id);
              }
            }
          ],
          taskForm: [
            'oTask',
            'tasks',
            '$q',
            function (oTask, tasks, $q) {
              var defer = $q.defer();
              if (oTask.endTime) {
                tasks.taskFormFromHistory(oTask.id).then(function (result) {
                  defer.resolve(JSON.parse(result).data[0].variables)
                }, defer.reject)
              } else {
                tasks.taskForm(oTask.id).then(function (result) {
                  defer.resolve(result.formProperties);
                }, defer.reject);
              }
              return defer.promise;
            }
          ],
          documentRights: [
            'oTask',
            'DocumentsService',
            'taskForm',
            function (oTask, DocumentsService, taskForm) {
            var searchResult = taskForm.filter(function (item) {
              return item.id === 'sKey_Step_Document';
            });
            if(searchResult.length !== 0) {
              return DocumentsService.getDocumentStepRights(oTask.processInstanceId);
            } else {
              return false;
            }
            }
          ],
          documentLogins: [
            'oTask',
            'DocumentsService',
            'documentRights',
            function (oTask, DocumentsService, documentRights) {
              if(documentRights) {
                return DocumentsService.getDocumentStepLogins(oTask.processInstanceId);
              }
            }
          ],
          processSubject: [
            'oTask',
            'DocumentsService',
            'documentRights',
            'taskForm',
            function (oTask, DocumentsService, documentRights) {
              // if(documentRights) {
                return DocumentsService.getProcessSubject(oTask.processInstanceId);
              // }
            }
          ]
        }
      })
      .state('tasks.typeof.history', {
        url: '/:id/history',
        templateUrl: 'app/tasks/taskFormHistoryNew.html',
        controller: 'historyCtrl',
        access: {
          requiresLogin: true
        },
        resolve: {
          taskData: [
            'tasks',
            '$stateParams',
            function(tasks, $stateParams) {
              var params = {
                nID_Task: $stateParams.id,
                bIncludeGroups: true,
                bIncludeStartForm: true,
                bIncludeAttachments: true,
                bIncludeProcessVariables: $stateParams.type === 'documents',
                bIncludeMessages: true
              };
              return tasks.getTaskData(params, false)
            }
          ]
        }
      })
      .state('tasks.typeof.fulfillment', {
        url: '/:id/fulfillment',
        templateUrl: 'app/tasks/fulfillment.html',
        controller: 'fulfillmentCtrl',
        access: {
          requiresLogin: true
        },
        resolve: {
          oTask: [
            'tasks',
            '$stateParams',
            'tasksStateModel',
            '$q',
            function (tasks, $stateParams, tasksStateModel, $q) {
              tasksStateModel.taskId = $stateParams.id;
              if ($stateParams.type == 'finished'){
                var defer = $q.defer();
                tasks.taskFormFromHistory($stateParams.id).then(function(response){
                  defer.resolve(JSON.parse(response).data[0]);
                }, defer.reject);
                return defer.promise;
              }
              else {
                return tasks.getTask($stateParams.id);
              }
            }
          ],
          taskData: [
            'tasks',
            '$stateParams',
            function(tasks, $stateParams) {
              var params = {
                nID_Task: $stateParams.id,
                bIncludeGroups: true,
                bIncludeStartForm: true,
                bIncludeAttachments: true,
                bIncludeProcessVariables: $stateParams.type === 'documents',
                bIncludeMessages: true
              };
              return tasks.getTaskData(params, false)
            }
          ]
        }
      })
  }
})();
