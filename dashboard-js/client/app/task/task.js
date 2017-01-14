(function () {
  'use strict';
  angular
    .module('dashboardJsApp')
    .config([
      '$stateProvider',
      function ($stateProvider) {
        $stateProvider
          .state('baTask', {
            url: '/task',
            access: {
              requiresLogin: true
            },
            resolve: {
              taskData: [
                'tasks',
                '$location',
                function (tasks, $location) {
                  var params = angular.copy($location.search());
                  if (params.nID_Order) {
                    params.nID_Process = params.nID_Order.substring(params.nID_Order.length - 1);
                  }
                  return tasks.getTaskData(params, true);
                }
              ],
              stateModel: [
                function () {
                  return {
                    printTemplate: null,
                    taskDefinition: null,
                    strictTaskDefinition: null,
                    userProcess: null
                  }
                }
              ]
            },
            views: {
              '@': {
                controller: 'baTask',
                templateUrl: 'app/task/baTask.html'
              },
              'task-view@baTask': {
                templateUrl: 'app/tasks/taskForm.html',
                controller: 'TaskViewCtrl',
                resolve: {
                  oTask: [
                    'tasks',
                    '$stateParams',
                    '$q',
                    'taskData',
                    '$location',
                    function (tasks, $stateParams, $q, taskData, $location) {
                      return tasks.getTask(taskData.nID_Task);
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
                    'tasks',
                    'taskForm',
                    function (oTask, tasks, taskForm) {
                      var searchResult = taskForm.filter(function (item) {
                        return item.id === 'sKey_Step_Document';
                      });
                      if(searchResult.length !== 0) {
                        return tasks.getDocumentStepRights(oTask.processInstanceId);
                      } else {
                        return false;
                      }
                    }
                  ],
                  documentLogins: [
                    'oTask',
                    'tasks',
                    'documentRights',
                    function (oTask, tasks, documentRights) {
                      if(documentRights) {
                        return tasks.getDocumentStepLogins(oTask.processInstanceId);
                      }
                    }
                  ],
                  processSubject: [
                    'oTask',
                    'tasks',
                    'documentRights',
                    'taskForm',
                    function (oTask, tasks) {
                      return tasks.getProcessSubject(oTask.processInstanceId);
                    }
                  ]
                }
              },
              'task-view-history@baTask': {
                templateUrl: 'app/tasks/taskFormHistory.html'
              }
            }
          })
      }
    ]);
})();
