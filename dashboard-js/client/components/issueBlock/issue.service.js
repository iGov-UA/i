angular.module('dashboardJsApp').service('Issue', ['tasks', '$q', function (tasks, $q) {
  this.issues = [];

  this.clearIssues = function () {
    this.issues = [];
  };

  this.getIssues = function () {
    return this.issues;
  };

  this.isIssue = function (fields) {
    var deferred = $q.defer();

    function searchSelectParams(param) {
      for(var i=0; i<fields.length; i++) {
        if(fields[i].id === param) {
          return fields[i].value;
        }
      }
    }

    for( var i=0; i<fields.length; i++ ) {
      var params = {selectExecutors: {}};
      if(fields[i].type === 'table' && fields[i].id.indexOf('oProcessSubject_Executor') === 0 && fields[i].aRow) {
        angular.forEach(fields[i].aRow, function (e) {
          angular.forEach(e.aField, function (field) {
            if(field.type === 'select' && field.name.indexOf('sID_BP') > -1) {
              var split = field.name.split(';'), options = split[2].split(',');
              params.name  = split[0];

              for(var j=0; j<options.length; j++) {
                var val = options[j].split('=');
                if(val[0].indexOf('sID_BP') === 0) {
                  params.bp = val[1];
                } else if(val[0].indexOf('sID_Group_Activiti') === 0) {
                  params.selectExecutors.activiti = searchSelectParams(val[1]);
                } else if(val[0].indexOf('nDeepLevel') === 0) {
                  params.selectExecutors.deep = searchSelectParams(val[1]);
                }
              }

              for( var o=0; o<fields.length; o++ ) {
                if (fields[o].type === 'select' && fields[o].id.indexOf('oProcessSubject_Controller') === 0) {
                  var splitted = fields[o].name.split(';'), ctrlOptions = splitted[2].split(',');
                  params.controllerSelect = {};

                  for (var p = 0; p < ctrlOptions.length; p++) {
                    var item = ctrlOptions[p].split('=');
                    if (item[0].indexOf('sID_Group_Activiti') === 0) {
                      params.controllerSelect.activiti = searchSelectParams(item[1]);
                    } else if (item[0].indexOf('nDeepLevel') === 0) {
                      params.controllerSelect.deep = searchSelectParams(item[1]);
                    }
                  }
                  deferred.resolve(params);
                }
              }
            }
          })
        })
      }
    }
    return deferred.promise;
  };

  this.addIssue = function () {
    var isValid = this.validate();

    if(isValid) {
      if(this.issues.length > 0) {
        var copy = angular.copy(this.issues[0]);
        for(var field in copy) {
          if(copy.hasOwnProperty(field)) {
            switch(field) {
              case 'task':
                copy[field] = this.issues.length + 1;
                break;
              case 'taskTerm':
                copy[field].property = 'calendar';
                copy[field].value = '';
                break;
              case 'taskExecutor':
                copy[field] = [{value: '', isMain: true}];
                break;
              default:
                copy[field] = '';
                break;
            }
          }
        }
        this.issues.push(copy);
        return true;
      } else {
        this.issues.push({task: 1, taskName: '', taskContents: '', taskTerm: {property: 'calendar', value: ''},
          taskForm: '', taskController: '', taskExecutor: [{value: '', isMain: true}]});
        return true;
      }
    } else {
      return false;
    }
  };

  this.validate = function () {
    var isValid = true;

    for(var i=0; i<this.issues.length; i++) {
      for(var elem in this.issues[i]) {
        if(this.issues[i].hasOwnProperty(elem) && elem !== 'taskTerm' && !this.issues[i][elem]) {
          isValid = false;
        } else if(this.issues[i].hasOwnProperty(elem) && elem === 'taskTerm') {
          angular.forEach(this.issues[i][elem], function (param) {
            if(!param)
              isValid = false;
          })
        }
      }
    }

    return isValid;
  };

  this.removeIssue = function (index) {
    this.issues.splice(index, 1);
  };

  this.addExecutor = function (index) {
    var isValid = true;

    for( var i=0; i<this.issues[index].taskExecutor.length; i++ ) {
      if(!this.issues[index].taskExecutor[i].value) {
        isValid = false;
        break;
      }
    }

    if(isValid) {
      this.issues[index].taskExecutor.push({value: '', isMain: false});
    }

    return isValid;
  };

  this.removeExecutor = function (issue, index) {
    if(this.issues[issue].taskExecutor.length > 1) {
      this.issues[issue].taskExecutor.splice(index, 1);
      for( var i=0; i<this.issues[issue].taskExecutor.length; i++ ){
        if(!this.issues[issue].taskExecutor[i].isMain && i + 1 === this.issues[issue].taskExecutor.length) {
          this.issues[issue].taskExecutor[0].isMain = true;
        }
      }
    }
  };

  this.convertDate = function (date) {
    var splitDate = date.split('/');
    return splitDate[2] + '-' + splitDate[1] + '-' + splitDate[0];
  };

  this.buildIssueObject = function (issue, taskData) {
    var deferred = $q.defer();
    var isIssueValid = this.validate();
    var that = this;

    if(isIssueValid) {
      var items = this.getIssues();
      var filledArray = [], itemPromises = [], itemDeferred = [];

      for(var i=0; i<items.length; i++) {
        itemDeferred[i] = $q.defer();
        itemPromises[i] = itemDeferred[i].promise;
      }

      angular.forEach(items, function (item, key) {
        tasks.uploadFileHtml('issue content', item.taskContents).then(function (res) {
          var datePlan = that.convertDate(item.taskTerm.value);
          var exeCopy = angular.copy(item.taskExecutor);
          var obj = {
            sID_BP: issue.bp,
            snID_Process_Activiti_Root: taskData.oProcess.nID.toString(),
            sHead: item.taskName,
            sActionType: 'set',
            sBody: res,
            sReportType: item.taskForm,
            aProcessSubject: [{
              sLogin: item.taskController.sLogin,
              sLoginRole: 'Controller',
              sDatePlan: datePlan
            }]
          };

          for( var exec in exeCopy) {
            if(exeCopy.hasOwnProperty(exec)) {
              var execObj = {
                sLogin: exeCopy[exec].value.sLogin,
                sLoginRole: 'Executor',
                sDatePlan: datePlan
              };

              if (exeCopy[exec].isMain) {
                obj.aProcessSubject.splice(1, 0, execObj);
              } else {
                obj.aProcessSubject.push(execObj);
              }
            }
          }
          filledArray.push(obj);
          itemDeferred[key].resolve();
        });
        var Promises = $q.all(itemPromises);
        $q.all([Promises]).then(function () {
          deferred.resolve(filledArray);
        })
      });
    } else {
      deferred.resolve(false);
    }

    return deferred.promise;
  };
}]);
