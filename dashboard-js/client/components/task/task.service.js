'use strict';

angular.module('dashboardJsApp')
  .factory('tasks', function tasks($http, $q, $rootScope, uiUploader, $compile, $timeout, processes, $filter,
                                   PrintTemplateProcessor, Auth) {
    function simpleHttpPromise(req, callback) {
      var cb = callback || angular.noop;
      var deferred = $q.defer();

      $http(req).then(
        function (response) {
          deferred.resolve(response.data);
          return cb();
        },
        function (response) {
          deferred.reject(response);
          return cb(response);
        }.bind(this));
      return deferred.promise;
    }

    return {
      filterTypes: {
        selfAssigned: 'selfAssigned',
        unassigned: 'unassigned',
        finished: 'finished',
        tickets: 'tickets',
        all: 'all'
      },
      /**
       * Get list of tasks
       *
       * @param  {Function} callback - optional
       * @return {Promise}
       */
      list: function (filterType, params) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/tasks',
          params: angular.merge({filterType: filterType}, params)
        });
      },
      getEventMap: function () {
        var deferred = $q.defer();
        var eventMap = {
          'AddAttachment': {},
          'AddComment': {
            'messageTemplate': '${ user.name } відповів(ла): ${ message }',
            'getMessageOptions': function (messageObject) {
              return !_.isEmpty(messageObject) ? messageObject[0] : '';
            },
            'getFullMessage': function (user, messageObject) {
              return _.template(
                eventMap.AddComment.messageTemplate, {
                  'user': {
                    'name': user.name
                  },
                  'message': eventMap.AddComment.getMessageOptions(messageObject)
                }
              );
            }
          },
          'AddGroupLink': {},
          'AddUserLink': {
            'messageTemplate': '${ user.name } призначив(ла) : ${ message }',
            'getMessageOptions': function (messageObject) {
              return !_.isEmpty(messageObject) ? messageObject[0] : '';
            },
            'getFullMessage': function (user, messageObject) {
              return _.template(
                eventMap.AddUserLink.messageTemplate, {
                  'user': {
                    'name': user.name
                  },
                  'message': eventMap.AddUserLink.getMessageOptions(messageObject)
                }
              );
            }
          },
          'DeleteAttachment': {},
          'DeleteGroupLink': {},
          'DeleteUserLink': {}
        };

        deferred.resolve(eventMap);

        return deferred.promise;
      },

      assignTask: function (taskId, userId, callback) {
        return simpleHttpPromise({
          method: 'PUT',
          url: '/api/tasks/' + taskId,
          data: {
            assignee: userId
          }
        }, callback);
      },

      downloadDocument: function (taskId, callback) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/tasks/' + taskId + '/document'
        }, callback);
      },


      getOrderMessages: function (processId, callback) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/tasks/' + processId + '/getOrderMessages'
        }, callback);
      },

      taskForm: function (taskId, callback) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/tasks/' + taskId + '/form'
        }, callback);
      },

      getTableAttachment: function (taskId, attachId) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/tasks/' + taskId + '/attachments/' + attachId + '/table'
        })
      },

      taskFormFromHistory: function (taskId) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/tasks/' + taskId + '/form-from-history'
        });
      },

      taskAttachments: function (taskId, callback) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/tasks/' + taskId + '/attachments'
        }, callback);
      },

      submitTaskForm: function (taskId, formProperties, task, attachments) {
        var self = this;
        var promises = [];
        var createProperties = function (formProperties) {
          var properties = new Array();
          for (var i = 0; i < formProperties.length; i++) {
            var formProperty = formProperties[i];
            if (formProperty && formProperty.writable) {
              properties.push({
                id: formProperty.id,
                value: formProperty.value
              });
            }
          }
          return properties;
        };

        var tableFields = $filter('filter')(formProperties, function(prop){
          return prop.type == 'table';
        });

        if(tableFields.length > 0) {
          angular.forEach(tableFields, function (table) {
            if(attachments.length > 0) {
              angular.forEach(attachments, function (attachment) {
                var name = attachment.description.match(/(\[id=(\w+)\])/)[2];
                if(name.toLowerCase() === table.id.toLowerCase()) {
                  var description = attachment.description.split('[')[0];
                  promises.push(self.uploadTable(table, taskId, attachment.id, description));
                }
              });
            } else {
              var name = table.name.split(';')[0];
              promises.push(self.uploadTable(table, taskId, null, name));
            }
          })
        }
        var deferred = $q.defer();

        // upload files before form submitting
        promises.push(this.uploadTaskFiles(formProperties, task, taskId));

        $q.all(promises).then(function () {
          var submitTaskFormData = {
            'taskId': taskId,
            'properties': createProperties(formProperties)
          };

          var req = {
            method: 'POST',
            url: '/api/tasks/' + taskId + '/form',
            data: submitTaskFormData
          };

          simpleHttpPromise(req).then(function (result) {
            deferred.resolve(result);
          },
            function(result) {
              deferred.resolve(result);
          });
        });

        return deferred.promise;
      },

      uploadTable: function(files, taskId, attachmentID, description) {
        var deferred = $q.defer();
        var tableId = files.id;
        var stringifyTable = JSON.stringify(files);
        var data = {
          sDescription: description + '[table][id='+ tableId +']',
          sFileName: tableId + '.json',
          sContent: stringifyTable,
          nID_Attach: attachmentID
        };

        $http.post('/api/tasks/' + taskId + '/setTaskAttachment', data).success(function(uploadResult){
          files.value = JSON.parse(uploadResult).id;
          deferred.resolve();
        });

        return deferred.promise;
      },

      saveChangesTaskForm: function(taskId, formProperties, task) {
        var self = this;
        var createProperties = function(formProperties) {
          var properties = new Array();
          for (var i = 0; i < formProperties.length; i++) {
            var formProperty = formProperties[i];
            if (formProperty && formProperty.writable) {
              properties.push({
                id: formProperty.id,
                value: formProperty.value
              });
            }
          }
          return properties;
        };

        var deferred = $q.defer();

        // upload files before form submitting
        this.uploadTaskFiles(formProperties, task, taskId).then(function()
        {
          var submitTaskFormData = {
            'taskId': taskId,
            'properties': createProperties(formProperties)
          };

          var req = {
            method: 'POST',
            url: '/api/tasks/action/task/saveForm',
            data: submitTaskFormData
          };
          simpleHttpPromise(req).then(
            function(result) {
            deferred.resolve(result);
          },
            function(result) {
              deferred.resolve(result);
          });
        });

        return deferred.promise;
      },
      upload: function(files, taskId) {
        var deferred = $q.defer();

        var self = this;
        var scope = $rootScope.$new(true, $rootScope);
        uiUploader.removeAll();
        uiUploader.addFiles(files);
        uiUploader.startUpload({
          url: '/api/tasks/' + taskId + '/attachments',
          concurrency: 1,
          onProgress: function (file) {
            scope.$apply(function () {

            });
          },
          onCompleted: function (file, response) {
            scope.$apply(function () {
              /*
              try {
                deferred.resolve({
                  file: file,
                  response: JSON.parse(response)
                });
              } catch (e) {
                deferred.reject({
                  err: response
                });
              }
              */

              var oCheckSignReq = {};
              try{
                oCheckSignReq = angular.fromJson(response);
              } catch (errParse){
                self.value.signInfo = null;
              }
              if(oCheckSignReq.taskId && oCheckSignReq.id){
                self.value = {id : oCheckSignReq.id, signInfo: null, fromDocuments: false};
                simpleHttpPromise({
                    method: 'GET',
                    url: '/api/tasks/' + oCheckSignReq.taskId + '/attachments/' + oCheckSignReq.id + '/checkAttachmentSign'
                  }
                ).then(function (signInfo) {
                  //self.value.signInfo = Object.keys(signInfo).length === 0 ? null : signInfo;
                  try {
                    deferred.resolve({
                      file: file,
                      response: JSON.parse(response),
                      signInfo: Object.keys(signInfo).length === 0 ? null : signInfo
                    });
                  } catch (e) {
                    deferred.reject({
                      err: response
                    });
                  }
                }, function (err) {
                  self.value.signInfo = null;
                })
              }
            });
          }
        });

        return deferred.promise;
      },

      /**
       * Ф-ция загрузки файлов из принт-диалога в виде аттачей к форме
       * @param formProperties
       * @param task
       * @param taskId
       * @returns {deferred.promise|{then, always}}
       */
      uploadTaskFiles: function (formProperties, task, taskId) {
        // нужно найти все поля с тимом "file" и id, начинающимся с "PrintForm_"
        var filesFields = $filter('filter')(formProperties, function (prop) {
          return prop.type == 'file' && /^PrintForm_/.test(prop.id);
        });
        // удалить после теста. пока что нет БП с таким полем и используем все поля с типом "файл".
        //if (filesFields.length == 0)
        //  filesFields = $filter('filter')(formProperties, {type:'file'});
        //
        var self = this;
        var deferred = $q.defer();
        var filesDefers = [];
        // загрузить все шаблоны
        angular.forEach(filesFields, function (fileField) {
          var defer = $q.defer();
          filesDefers.push(defer.promise);
          var patternFileName = fileField.name.split(';')[2];
          if (patternFileName) {
            patternFileName = patternFileName.replace(/^pattern\//, '');
            self.getPatternFile(patternFileName).then(function (result) {
              defer.resolve({
                fileField: fileField,
                template: result
              });
            });
          } else
            defer.resolve({
              fileField: fileField,
              template: ''
            });
        });
        // компиляция и отправка html
        $q.all(filesDefers).then(function (results) {
          var uploadPromises = [];
          angular.forEach(results, function (templateResult) {
            var scope = $rootScope.$new();
            scope.selectedTask = task;
            scope.taskForm = formProperties;
            //scope.getPrintTemplate = function(){return PrintTemplateProcessor.getPrintTemplate(task, formProperties, templateResult.template, scope.lunaService);},
            scope.getPrintTemplate = function () {
              return PrintTemplateProcessor.getPrintTemplate(task, formProperties, templateResult.template);
            },
              scope.containsPrintTemplate = function () {
                return templateResult.template != '';
              };
            scope.getProcessName = processes.getProcessName;
            scope.sDateShort = function (sDateLong) {
              if (sDateLong !== null) {
                var o = new Date(sDateLong);
                return o.getFullYear() + '-' + ((o.getMonth() + 1) > 9 ? '' : '0') + (o.getMonth() + 1) + '-' + (o.getDate() > 9 ? '' : '0') + o.getDate() + ' ' + (o.getHours() > 9 ? '' : '0') + o.getHours() + ':' + (o.getMinutes() > 9 ? '' : '0') + o.getMinutes();
              }
            };
            scope.sFieldLabel = function (sField) {
              var s = '';
              if (sField !== null) {
                var a = sField.split(';');
                s = a[0].trim();
              }
              return s;
            };
            scope.sEnumValue = function (aItem, sID) {
              var s = sID;
              _.forEach(aItem, function (oItem) {
                if (oItem.id == sID) {
                  s = oItem.name;
                }
              });
              return s;
            };
            var compiled = $compile('<print-dialog></print-dialog>')(scope);
            var defer = $q.defer();

            /**
             * https://github.com/e-government-ua/i/issues/1382
             * parse name string property to get file names sPrintFormFileAsPDF and sPrintFormFileAsIs
             */
            var fileName = null;

            if (typeof templateResult.fileField.name === 'string') {
              fileName = templateResult.fileField.name.split(/;/).reduce(function (prev, current) {
                return prev += current.match(/sPrintFormFileAsPDF/i) || current.match(/sPrintFormFileAsIs/i) || [];
              }, '');

              if(fileName === 'sPrintFormFileAsPDF'){
                fileName = fileName + '.pdf';
              }

              if(fileName === 'sPrintFormFileAsIs'){
                fileName = fileName + '.html';
              }
            }

            $timeout(function () {
              var html = '<html><head><meta charset="utf-8"></head><body>' + compiled.find('.print-modal-content').html() + '</body></html>';
              var data = {
                sDescription: 'User form',
                sFileName: fileName || 'User form.html',
                sContent: html
              };

              $http.post('/api/tasks/' + taskId + '/upload_content_as_attachment', data)
                .success(function (uploadResult) {
                  templateResult.fileField.value = JSON.parse(uploadResult).id;
                  defer.resolve();
                })
            });

            uploadPromises.push(defer.promise);
          });

          $q.all(uploadPromises).then(function (uploadResults) {
            deferred.resolve();
          });

        });

        return deferred.promise;
      },

      getTask: function (taskId) {
        var deferred = $q.defer();

        var req = {
          method: 'GET',
          url: '/api/tasks/' + taskId,
          data: {}
        };

        $http(req).success(function (data) {
          deferred.resolve(data);
        }).error(function (err) {
          deferred.reject(err);
        }.bind(this));

        return deferred.promise;
      },
      getTasksByOrder: function (nID_Order) {
        return simpleHttpPromise({
            method: 'GET',
            url: '/api/tasks/search/byOrder/' + nID_Order
          }
        );
      },
      getTasksByText: function (sFind, sType) {
        return simpleHttpPromise({
            method: 'GET',
            url: '/api/tasks/search/byText/' + sFind + "/type/" + sType
          }
        );
      },
      getProcesses: function (sID) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/tasks/getProcesses',
          params: {
            sID: sID
          }
        });
      },
      getPatternFile: function (sPathFile) {
        return simpleHttpPromise({
            method: 'GET',
            url: '/api/tasks/getPatternFile?sPathFile=' + sPathFile
          }
        );
      },
      setTaskQuestions: function (params) {
        return simpleHttpPromise({
            method: 'POST',
            url: '/api/tasks/setTaskQuestions',
            data: params
          }
        );
      },
      postServiceMessages: function (params) {
        return simpleHttpPromise({
          method: 'POST',
          url: 'api/tasks/postServiceMessages',
          data: params
        })
      },
      checkAttachmentSign: function (nID_Task, nID_Attach) {
        return simpleHttpPromise({
            method: 'GET',
            url: '/api/tasks/' + nID_Task + '/attachments/' + nID_Attach + '/checkAttachmentSign'
          }
        );
      },
      unassign: function (nID_Task) {
        return simpleHttpPromise({
            method: 'PUT',
            url: '/api/tasks/' + nID_Task + '/unassign'
          }
        );
      },
      getTaskData: function (params, allData) {
        var requestParams = angular.copy(params);
        if (allData === true)
          angular.merge(requestParams, {
            bIncludeGroups: true,
            bIncludeStartForm: true,
            bIncludeAttachments: true,
            bIncludeMessages: true
          });
        return simpleHttpPromise({
            method: 'GET',
            url: '/api/tasks/getTaskData',
            params: requestParams
          }
        ).then(function (data) {
          // Костыль. Удалить когда будет приходить массив вместо строки
          if (angular.isString(data.aMessage))
            data.aMessage = JSON.parse(data.aMessage);
          angular.forEach(data.aMessage, function (message) {
            if (angular.isString(message.sData) && message.sData.length > 1) {
              try {
                message.osData = JSON.parse(message.sData);
              } catch (e) {
                message.osData = {};
              }
            }
          });
          return data;
        });
      },
      /**
       * Реализовать открытие по урл-у "расширенного профиля задачи" и ссылку для админа из "обычного профиля" #1015
       * @param taskData
       * @returns {boolean}
       */
      isFullProfileAvailableForCurrentUser: function (taskData) {
        var currentUser = Auth.getCurrentUser();
        // 4.1) отображать тем, кто входит в группу: admin,super-admin
        if (currentUser.roles.indexOf('admin') || currentUser.roles.indexOf('super_admin'))
          return true;
        // 4.2) а также тем на кого эта таска ассйнута
        if (taskData.sLoginAssigned == currentUser.id)
          return true;
        // 4.3) а так-же тем, кто входит в группу, в которую входит эта таска и одновременно - когда она не ассайнута
        // или когда он входит в группу manager и она ассайнута на другого т.е.
        // (входит в группу, в которую входит эта таска) && (она не ассайнута || (он входит в группу manager && она ассайнута на другого))
        var groups = $.grep(taskData.aGroup || taskData.aGroup, function (group) {
          return currentUser.roles.indexOf(group) > -1;
        });
        if (groups.length > 0 && (!taskData.sLoginAssigned || currentUser.roles.indexOf('manager') > -1)) {
          return true;
        }
        return false;
      }
    };
  });
