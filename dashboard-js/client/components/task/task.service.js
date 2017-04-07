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
        documents: 'documents',
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

      getDocumentStepRights: function (nID_Process) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getDocumentStepRights',
          params: {
            nID_Process: nID_Process
          }
        })
      },

      getDocumentStepLogins: function (nID_Process) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getDocumentStepLogins',
          params: {
            nID_Process: nID_Process
          }
        })
      },

      getProcessSubject: function (id) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getProcessSubject',
          params: {
            snID_Process_Activiti: id,
            nDeepLevel: 1
          }
        })
      },

      getProcessSubjectTree: function (id) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getProcessSubjectTree',
          params: {
            snID_Process_Activiti: id,
            nDeepLevel: 0
          }
        })
      },

      getTableOrFileAttachment: function (taskId, attachId, isNewService) {
        // old and new services requests
        if(isNewService) {
          return simpleHttpPromise({
            method: 'GET',
            url: '/api/tasks/download/' + taskId + '/attachment/' + attachId
          })
        } else {
          return simpleHttpPromise({
            method: 'GET',
            url: '/api/tasks/' + taskId + '/attachments/' + attachId + '/table'
          })
        }
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
        var self = this,
            deferred = $q.defer(),
            promises = [],
            tablePromises = [],
            items = 0,
            def = [],
            tablePromisesReal = [];

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

        // upload tables sync
        var syncTableUpload = function (i, table, defs) {
            if (i < table.length) {
              self.uploadTable(table[i].table, table[i].taskID, table[i].tableId, table[i].desc, table[i].isNew).then(function(resp) {
                defs[i].resolve();
                ++i;
                syncTableUpload(i, table, defs);
              })
            }
        };

        var tableFields = $filter('filter')(formProperties, function(prop){
          return prop.type === 'table' || prop.type === 'fileHTML';
        });

        if(tableFields.length > 0) {
          for(var t=0; t<tableFields.length; t++) {
            var table = tableFields[t];
          /*
            in old service we need to check that we are saving new table or update old, so if id table from form field
            is equal attach id -> update, otherwise save as new. later it can be removed..
          */
            var isNotEqualsAttachments = function (table, num) {
                var checkForNewService = table.name.split(';');
                if (checkForNewService.length === 3 && checkForNewService[2].indexOf('bNew=true') > -1 || table.type === 'fileHTML') {
                  // tablePromises.push(self.uploadTable(table, task.processInstanceId, table.id, null, true));
                  def[num] = $q.defer();
                  tablePromises[num] = {table:table, taskID:task.processInstanceId, tableId:table.id, desc:null, isNew:true};
                  tablePromisesReal[num] = def[num].promise;
                } else {
                  var tableName = table.name.split(';')[0];
                  // tablePromises.push(self.uploadTable(table, taskId, null, tableName));
                  def[num] = $q.defer();
                  tablePromises[num] = {table:table, taskID:taskId, tableId:null, desc:tableName, isNew:false};
                  tablePromisesReal[num] = def[num].promise;
                }
            };

            if(attachments.length > 0) {
              var theSameAttachments = attachments.filter(function (item) {
                var matchTableId = item.description.match(/(\[id=(\w+)\])/);
                var x = item.description.indexOf('[table]') !== -1 && matchTableId !== null;
                if(x) {
                  var name = matchTableId[2];
                  return name.toLowerCase() === table.id.toLowerCase()
                }
              });

              if(theSameAttachments.length !== 0) {
                theSameAttachments.map(function (a) {
                  var description = a.description.split('[')[0];
                  // tablePromises.push(self.uploadTable(table, taskId, a.id, description));
                  def[t] = $q.defer();
                  tablePromises[t] = {table:table, taskID:taskId, tableId:a.id, desc:description, isNew:false};
                  tablePromisesReal[t] = def[t].promise;
                });
              } else {
                isNotEqualsAttachments(table, t);
              }
            } else {
              isNotEqualsAttachments(table, t);
            }
          }
        }

        syncTableUpload(items, tablePromises, def);

        // upload files before form submitting
        promises.push(this.uploadTaskFiles(formProperties, task, taskId));
        var filesProm = $q.all(promises);
        var tableProm = $q.all(tablePromisesReal);

        $q.all([filesProm, tableProm]).then(function () {
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

      uploadTable: function(files, taskId, attachmentID, description, isNewService) {
        var deferred = $q.defer(),
            tableId = files.id,
            stringifyTable = files.type === 'table' ? JSON.stringify(files) : files.value,
            data = {},
            url,
            ext;

        if(files.type === 'table') {
          ext = '.json'
        } else if (files.type === 'fileHTML') {
          ext = '.html'
        }

        if(isNewService) {
          data = {
            sFileNameAndExt: tableId + ext,
            sContent: stringifyTable,
            nID_Process: taskId,
            nID_Attach: attachmentID
          };
          url = '/api/tasks/' + taskId + '/setTaskAttachmentNew';
        } else {
          data = {
            sDescription: description + '[table][id='+ tableId +']',
            sFileName: tableId + ext,
            sContent: stringifyTable,
            nID_Attach: attachmentID
          };
          url = '/api/tasks/' + taskId + '/setTaskAttachment';
        }

        $http.post(url, data).success(function(uploadResult){
          var parsedResponse = JSON.parse(uploadResult);
          if(parsedResponse && parsedResponse.sKey && parsedResponse.sID_StorageType){
            files.value = uploadResult;
          }else {
            files.value = parsedResponse.id;
          }
          deferred.resolve(uploadResult);
        });

        return deferred.promise;
      },

      saveChangesTaskForm: function (taskId, formProperties, task, attachments) {
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
                var matchTableId = attachment.description.match(/(\[id=(\w+)\])/);
                if(attachment.description.indexOf('[table]') !== -1 && matchTableId !== null){
                  var name = matchTableId[2];
                  if(name.toLowerCase() === table.id.toLowerCase()) {
                    var description = attachment.description.split('[')[0];
                    promises.push(self.uploadTable(table, taskId, attachment.id, description));
                  }
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
            url: '/api/tasks/action/task/saveForm',
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
      upload: function(files, taskId, sID_Field, newUpload) {
        var deferred = $q.defer();

        var self = this;
        var scope = $rootScope.$new(true, $rootScope);
        var url;

        if(newUpload && taskId) {
          url = '/api/uploadfile?nID_Process=' + taskId + '&sID_Field=' + sID_Field + '&sFileNameAndExt=' + files[0].name;
        } else if(newUpload && !taskId) {
          url = '/api/uploadfile?sID_Field=' + sID_Field + '&sFileNameAndExt=' + files[0].name;
        } else {
          url = '/api/tasks/' + taskId + '/attachments/' + sID_Field + '/upload';
        }
        uiUploader.removeAll();
        uiUploader.addFiles(files);
        uiUploader.startUpload({
          url: url,
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
                if(self.value){
                  self.value.signInfo = null;
                } else {
                  self.value = {
                    signInfo : null
                  }
                }
              }
              if(oCheckSignReq.taskId && oCheckSignReq.id ||
                 oCheckSignReq.sKey && oCheckSignReq.sID_StorageType ||
                 oCheckSignReq.sID_Field && oCheckSignReq.sID_Process){

                self.value = {id : oCheckSignReq.id ? oCheckSignReq.id : null, signInfo: null, fromDocuments: false};
                var params = {url:null, query:null};

                if(oCheckSignReq.sKey && oCheckSignReq.sID_StorageType){
                  params.url = '/api/tasks/sign/checkAttachmentSignNew';
                  params.query = {
                    sID_StorageType: oCheckSignReq.sID_StorageType,
                    sKey: oCheckSignReq.sKey,
                    sID_Process: oCheckSignReq.sID_Process,
                    sID_Field: oCheckSignReq.sID_Field,
                    sFileNameAndExt: oCheckSignReq.sFileNameAndExt
                  }
                } else {
                  params.url = '/api/tasks/' + oCheckSignReq.taskId + '/attachments/' + oCheckSignReq.id + '/checkAttachmentSign';
                }

                simpleHttpPromise({
                    method: 'GET',
                    url: params.url,
                    params: params.query
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
                  if(self.value){
                    self.value.signInfo = null;
                  } else {
                    self.value = {
                      signInfo : null
                    }
                  }
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
        var isDocPrintFormPresent = !formProperties.sendDefaultPrintForm;
        angular.forEach(formProperties, function (field) {
          if(field.id.match(/^PrintForm_/) && field.options.sPrintFormFileAsPDF){
            var printFormName = field.options.sPrintFormFileAsPDF.split('/');
            var ind = printFormName.length - 1 < 0 ? 0 : printFormName.length - 1;
            if(printFormName[ind].match(/^_doc_/)){
              isDocPrintFormPresent = true;
            }
          }
        });
        if(formProperties.sendDefaultPrintForm && !isDocPrintFormPresent){
          filesDefers.push($q.resolve({
            fileField: null,
            template: '<html><head><meta charset="utf-8"><link rel="stylesheet" type="text/css" href="style.css" /></head><body">' + $(".ng-modal-dialog-content")[0].innerHTML + '</html>'
          }));
        }
        // компиляция и отправка html
        $q.all(filesDefers).then(function (results) {
          var uploadPromises = [],
              printforms = [],
              printPromises = [],
              printDefer = [],
              counter = 0;

          angular.forEach(results, function (templateResult, key) {
            var scope = $rootScope.$new();
            scope.selectedTask = task;
            scope.taskForm = formProperties;
            //scope.getPrintTemplate = function(){return PrintTemplateProcessor.getPrintTemplate(task, formProperties, templateResult.template, scope.lunaService);},
            scope.getPrintTemplate = function () {
              return PrintTemplateProcessor.getPrintTemplate(task, formProperties, templateResult.template);
            };
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

            /**
             * https://github.com/e-government-ua/i/issues/1382
             * parse name string property to get file names sPrintFormFileAsPDF and sPrintFormFileAsIs
             */
            var fileName = null;
            var fileNameTemp = null;
            var sFileFieldID = null;
            var sOutputFileType = null;
            var html = null;
            var sKey_Step_field = formProperties.filter(function (item) {
              return item.id === "sKey_Step_Document";
            })[0];
            if(sKey_Step_field){
              var sKey_Step = sKey_Step_field.value
            }

            if(templateResult.fileField) {
              if (typeof templateResult.fileField.name === 'string') {
                fileNameTemp = templateResult.fileField.name.split(/;/).reduce(function (prev, current) {
                  var reduceResult = prev += current.match(/sPrintFormFileAsPDF/i) || current.match(/sPrintFormFileAsIs/i) || [];
                  if (reduceResult !== '') {
                    var parts = current.split(',');
                    angular.forEach(parts, function (el) {
                      if (el.match(/^sFileName=/)) {
                        fileName = el.split('=')[1];
                      }
                    })
                  }
                  return reduceResult;
                }, '');

                fileName = fileName || fileNameTemp;

                if (fileNameTemp === 'sPrintFormFileAsPDF') {
                  fileName = fileName + '.pdf';
                  sOutputFileType = 'pdf';
                  if(templateResult.fileField.options.sPrintFormFileAsPDF){
                    var printFormName = templateResult.fileField.options.sPrintFormFileAsPDF.split('/');
                    var ind = printFormName.length - 1 < 0 ? 0 : printFormName.length - 1;
                    if(printFormName[ind].match(/^_doc_/)&& task.processInstanceId.match(/^_doc_/)){
                      formProperties.isSendAsDocument = true;
                      formProperties.skipSendingPrintForm = true;
                    } else {
                      formProperties.isSendAsDocument = false;
                    }
                  }
                }

                if (fileNameTemp === 'sPrintFormFileAsIs') {
                  fileName = fileName + '.html';
                  sOutputFileType = 'html';
                  formProperties.isSendAsDocument = false;
                }

                sFileFieldID = templateResult.fileField.id;
              }
              var description = templateResult.fileField.name.split(";")[0];
            } else {
              sOutputFileType = 'pdf';
              fileName = 'form.pdf';
              html = templateResult.template;
            }

            uploadPromises.push($timeout(function(){
              if(!html){
                html = '<html><head><meta charset="utf-8"></head><body>' + compiled.find('.print-modal-content').html() + '</body></html>';
              }
            var data = {
              sDescription: description,
              sFileNameAndExt: fileName || 'User form.html',
              sID_Field: sFileFieldID,
              sContent: html,
              sOutputFileType: sOutputFileType,
              sKey_Step: sKey_Step,
              isSendAsDocument: formProperties.sendDefaultPrintForm || formProperties.isSendAsDocument,
              skipSendingPrintForm: formProperties.skipSendingPrintForm
            };

            printDefer[key] = $q.defer();
            printforms[key] = {html:html, data:data};
            printPromises[key] = printDefer[key].promise;
            }));

          });

          var asyncPrintUpload = function (i, print, defs) {
            if (i < print.length) {
              if(!print[i].data.sID_Field && print[i].data.skipSendingPrintForm){
                defs[i].resolve();
                return asyncPrintUpload(i+1, print, defs);
              } else {
                return $http.post('/api/tasks/' + task.processInstanceId + '/upload_content_as_attachment', print[i].data)
                  .then(function (uploadResult) {
                    if(results[i].fileField && results[i].fileField.value){
                      results[i].fileField.value = uploadResult.data;
                    } else {
                      results[i]['uploadDefaultPrintForm'] = uploadResult.data;
                    }
                    defs[i].resolve();
                    return asyncPrintUpload(i+1, print, defs);
                  });
              }
            }
          };

          var first = $q.all(uploadPromises).then(function () {
            return asyncPrintUpload(counter, printforms, printDefer);
          });

          $q.all([first, printPromises]).then(function (uploadResults) {
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
      checkAttachmentSign: function (nID_Task, nID_Attach, isNewService) {
        if(isNewService) {
          return simpleHttpPromise({
            method: 'GET',
            url: '/api/tasks/sign/checkAttachmentSignNew',
            params: {
              sID_Process:nID_Task,
              sID_Field:nID_Attach
            }
          })
        } else {
          // old ecp check service, remove it later. Now checkAttachmentSignNew is new service.
          return simpleHttpPromise({
              method: 'GET',
              url: '/api/tasks/' + nID_Task + '/attachments/' + nID_Attach + '/checkAttachmentSign'
            }
          );
        }
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
      },
      isUserHasDocuments: function (login) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getBPs_ForReferent',
          params: {
            sLogin: login
          }
        })
      },
      createNewDocument: function (bpID) {
        return simpleHttpPromise({
          method: 'GET',
          url: 'api/documents/setDocument',
          params: {
            sID_BP: bpID
          }
        })
      },
      /*вытягиваем шаблон бизнес-процесса для последующего заполнения*/
      createNewTask: function (bpID) {
        return simpleHttpPromise({
          method: 'GET',
          url: 'api/create-task/createTask',
          params: {
            sID_BP: bpID
          }
        })
      },
      /*сохраняем ранее заполненный шаблон как таску, сохранив перед этим таблицы в редис*/
      submitNewCreatedTask: function (task, bpID) {
        var self = this, def = [], tables = [], tablePromises = [], items = 0, deferred = $q.defer();

        var createProperties = function (formProperties) {
          var properties = [];
          for (var i = 0; i < formProperties.length; i++) {
            var formProperty = formProperties[i];
            if (formProperty && formProperty.writable) {
              if(formProperty.hasOwnProperty('aRow')) {

              }
              properties.push({
                id: formProperty.id,
                value: formProperty.value
              });
            }
          }
          return properties;
        };

        var tableFields = $filter('filter')(task.formProperties, function(prop){
          return prop.type == 'table';
        });

        var syncTableUpload = function (i, table, defs) {
          if (i < table.length) {
            self.uploadTable(table[i].table, table[i].taskID, table[i].tableId, table[i].desc, table[i].isNew)
              .then(function(resp) {
                defs[i].resolve();
                ++i;
                syncTableUpload(i, table, defs);
            })
          }
        };

        var isNotEqualsAttachments = function (table, num) {
          def[num] = $q.defer();
          tables[num] = {table:table, taskID:null, tableId:table.id, desc:null, isNew:true};
          tablePromises[num] = def[num].promise;
        };

        if(tableFields.length > 0) {
          for (var i=0; i<tableFields.length; i++) {
            isNotEqualsAttachments(tableFields[i], i);
          }
        }

        syncTableUpload(items, tables, def);

        $q.all(tablePromises).then(function () {
          var qs = {
            properties : createProperties(task.formProperties)
          };

          simpleHttpPromise({
            method: 'POST',
            params: {sID_BP: bpID},
            url: '/api/create-task/saveCreatedTask',
            data: qs
          }).then(function (result) {
              deferred.resolve(result);
            },
            function(result) {
              deferred.resolve(result);
            });
        });
        return deferred.promise;
      },
      getFilterFieldsList: function (login) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/fields-list',
          params: {
            sLogin: login
          }
        })
      },
      getOrganizationData : function (code) {
        if(code)
          return $http.get('./api/organization-info', {
            params : {
              code : code
            }
          })
        }
      }
  });
