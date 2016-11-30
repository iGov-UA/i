'use strict';

var _ = require('lodash');
var activiti = require('../../components/activiti');
var errors = require('../../components/errors');
var userService = require('../user/user.service');
var authService = require('../../auth/activiti/basic');
var async = require('async');
var tasksService = require('./tasks.service');
var environment = require('../../config/environment');
var request = require('request');
var pdfConversion = require('phantom-html-to-pdf')();

/*
 var nodeLocalStorage = require('node-localstorage').LocalStorage;
 var localStorage = new nodeLocalStorage('./scratch');
 */
function createHttpError(error, statusCode) {
  return {httpError: error, httpStatus: statusCode};
}

function step(input, lowerFunction, withoutResult) {
  return withoutResult ? function (callback) {
    lowerFunction(callback, input);
  } : function (result, callback) {
    lowerFunction(result, callback, input);
  };
}

function loadGroups(wfCallback, assigneeID) {
  userService.getGroups(assigneeID, function (error, statusCode, result) {
    if (error) {
      wfCallback(createHttpError(error, statusCode));
    } else {
      wfCallback(null, result.data);
    }
  });
}

function loadUsers(groups, wfCallback) {
  userService.getUserIDsFromGroups(groups, function (error, users) {
    wfCallback(error, users);
  });
}

function loadTasksForOtherUsers(usersIDs, wfCallback, currentUserID) {
  var tasks = [];
  usersIDs = usersIDs
    .filter(function (usersID) {
      return usersID !== currentUserID;
    });

  async.forEach(usersIDs, function (usersID, frCallback) {
    var path = 'runtime/tasks';

    var options = {
      path: path,
      query: {assignee: usersID},
      json: true
    };

    activiti.get(options, function (error, statusCode, result) {
      if (!error && result.data) {
        tasks = tasks.concat(result.data);
      }
      frCallback(null);
    });
  }, function (error) {
    wfCallback(error, tasks);
  });
}

function loadAllTasks(tasks, wfCallback, assigneeID) {
  var path = 'runtime/tasks';

  var options = {
    path: path,
    query: {candidateOrAssigned: assigneeID, size: 500},
    json: true
  };

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      wfCallback(error);
    } else {
      result.data = result.data.concat(tasks);
      wfCallback(null, result);
    }
  });
}

// Get list of tasks
exports.index = function (req, res) {
  var user = JSON.parse(req.cookies.user);
  //var user = JSON.parse(localStorage.getItem('user'));
  var query = {};
  //https://test.igov.org.ua/wf/service/runtime/tasks?size=20
  query.size = 50;
  query.start = (req.query.page || 0) * query.size;

  if (req.query.filterType === 'all') {
    async.waterfall([
      step(user.id, loadGroups, true),
      loadUsers,
      step(user.id, loadTasksForOtherUsers),
      step(user.id, loadAllTasks)
    ], function (error, result) {
      if (error) {
        res.send(error);
      } else {
        res.json(result);
      }
    });
  } else {
    var path = 'runtime/tasks';
    if (req.query.filterType === 'selfAssigned') {
      query.assignee = user.id;
      query.includeProcessVariables = true;
    } else if (req.query.filterType === 'unassigned') {
      query.candidateUser = user.id;
      query.unassigned = true;
      query.includeProcessVariables = false;
    } else if (req.query.filterType === 'finished') {
      path = 'history/historic-task-instances';
      query.taskAssignee = user.id;
    } else if (req.query.filterType === 'tickets') {
      path = 'action/flow/getFlowSlotTickets';
      query.sLogin = user.id;
      query.bEmployeeUnassigned = req.query.bEmployeeUnassigned;
      if (req.query.sDate) {
        query.sDate = req.query.sDate;
      }
    }

    var options = {
      path: path,
      query: query,
      json: true
    };

    activiti.get(options, function (error, statusCode, result) {
      if (error) {
        res.send(error);
      } else {
        if (req.query.filterType === 'tickets') {
          result = {data: result};
        }
        res.json(result);
      }
    });
  }
};

// Get list of task events
exports.getAllTaskEvents = function (req, res) {
  var options = {
    path: '/runtime/tasks/' + req.params.taskId + '/events'
  };

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.json(result);
    }
  });

};

exports.getForm = function (req, res) {
  var options = {
    path: 'form/form-data',
    query: {
      'taskId': req.params.taskId
    }
  };

  res.setHeader('Content-Type', 'application/json;charset=utf-8');

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.status(statusCode).send(result);
    }
  });
};

exports.getFormFromHistory = function (req, res) {
  var options = {
    path: 'history/historic-task-instances',
    query: {
      'taskId': req.params.taskId,
      'includeTaskLocalVariables': true,
      'includeProcessVariables': true
      //'includeProcessVariables': false
    }

  };

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.status(statusCode).json(result);
    }
  });
};

exports.uploadFile = function (req, res) {
  var options = {
    url: activiti.getRequestURL({
      path: 'object/file/upload_file_as_attachment',
      query: {
        taskId: req.params.taskId,
        description: req.query.description
      }
    })
  };

  activiti.fileupload(req, res, options);
};

exports.getAttachments = function (req, res) {
  var options = {
    path: 'runtime/tasks/' + req.params.taskId + '/attachments'
  };

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.status(statusCode).json(result);
    }
  });
};

exports.getOrderMessages = function (req, res) {
  var options = {
    path: 'action/task/getOrderMessages_Local',
    query: {
      'nID_Process': req.params.nID_Process
    }
  };

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      console.log("[getOrderMessages]:error=" + error);
      res.status(200).json("[]");
      //res.send(error);
    } else {
      console.log("[getOrderMessages]:result=" + result);
      if (statusCode !== 200) {
        res.status(200).json("[]");
      } else {
        res.status(statusCode).json(result);
      }
    }
  });
};


exports.getAttachmentContent = function (req, res) {
  var options = {
    path: 'object/file/download_file_from_db',
    query: {
      'taskId': req.params.taskId,
      'nFile': req.params.nFile
    }
  };
  activiti.filedownload(req, res, options);
};

exports.getAttachmentContentTable = function (req, res) {
  var options = {
    path: 'object/file/download_file_from_db',
    query: {
      'taskId': req.params.taskId,
      'attachmentId': req.params.attachmentId
    }
  };
  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else if (statusCode == 500) {
      console.log(statusCode, "isn't table attachment");
    } else {
      res.status(statusCode).json(result);
    }
  });
};

exports.submitForm = function (req, res) {
  var options = {
    path: 'form/form-data'
  };
  activiti.post(options, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result);
  }, req.body);
};

exports.updateTask = function (req, res) {
  var options = {
    path: 'runtime/tasks/' + req.params.taskId
  };
  activiti.put(options, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result);
  }, req.body);
};

exports.getTask = function (req, res) {
  var options = {
    path: 'runtime/tasks/' + req.params.taskId
  };
  //activiti.put(options, function (error, statusCode, result) {
  activiti.get(options, function (error, statusCode, result) {
    res.statusCode = statusCode;
    if(typeof(result) === 'number'){
      result = '' + result;
    }
    res.send(result);
  }, req.body);
};

exports.getTasksByOrder = function (req, res) {
  var options = {
    path: 'action/task/getTasksByOrder',
    query: {'nID_Order': req.params.orderId}
    //query: {'nID_Process': req.params.nID_Process}
  };
  activiti.get(options, function (error, statusCode, result) {
    error ? res.send(error) : res.status(statusCode).json(result);
  });
};

exports.getTasksByText = function (req, res) {
  var user = JSON.parse(req.cookies.user);
  //var user = JSON.parse(localStorage.getItem('user'));
  //query.bEmployeeUnassigned = req.query.bEmployeeUnassigned;
  var options = {
    path: 'action/task/getTasksByText',
    query: {
      'sFind': req.params.text,
      'sLogin': user.id,//finished,unassigned, selfAssigned
      'bAssigned': req.params.sType === 'selfAssigned' ? true : req.params.sType === 'unassigned' ? false : null //bAssigned
    }
  };
  activiti.get(options, function (error, statusCode, result) {
    error ? res.send(error) : res.status(statusCode).json(result);
    //error ? res.send(error) : res.status(statusCode).json("[\"4585243\"]");
  });
};

/*
 exports.getProcesses = function (req, res) {
 var user = JSON.parse(req.cookies.user);
 var roles = JSON.stringify(user.roles);
 //query.bEmployeeUnassigned = req.query.bEmployeeUnassigned;
 var options = {
 path: 'analytic/process/getProcesses',
 query: {
 'sID_': req.query.sID,
 'asID_Group': roles
 }
 };
 activiti.get(options, function (error, statusCode, result) {
 error ? res.send(error) : res.status(statusCode).json(result);
 //error ? res.send(error) : res.status(statusCode).json("[\"4585243\"]");
 });
 };
 */
exports.getProcesses = function (req, res) {
  var currentUser = JSON.parse(req.cookies.user);
  var userRoles = authService.getCashedUserGroups(currentUser);
  if (userRoles) {
    currentUser.roles = userRoles;
    var options = {
      path: 'analytic/process/getProcesses',
      query: {
        'sID_': req.query.sID,
        'asID_Group': currentUser.roles
      }
    };
    activiti.get(options, function (error, statusCode, result) {
      error ? res.send(error) : res.status(statusCode).json(result);
    });
  } else {
    async.waterfall([
      function (callback) {
        activiti.get({
          path: 'action/identity/getGroups',
          query: {
            sLogin: currentUser.id
          },
          json: true
        }, function (error, statusCode, result) {
          if (error) {
            callback(error, null);
          } else {
            var resultGroups;
            if ((typeof result == "object") && (result instanceof Array)) {
              currentUser['roles'] = result.map(function (group) {
                return group.id;
              });
            } else {
              currentUser['roles'] = [];
            }
            callback(null, {
              currentUser: currentUser
            });
          }
        });
      },
      function (user, callback) {
        var options = {
          path: 'analytic/process/getProcesses',
          query: {
            'sID_': req.query.sID,
            'asID_Group': currentUser.roles
          }
        };
        activiti.get(options, function (error, statusCode, result) {
          callback(error, result);
        });
      }
    ], function (error, result) {
      authService.setCashedUserGroups(currentUser, currentUser.roles);
      error ? res.send(error) : res.json(result);
    });
  }
};

exports.getFile = function (req, res) {
  var user = JSON.parse(req.cookies.user);
  var options = {
    path: 'analytic/process/getFile',
    query: {
      'sLogin': user.id,
      'nID_Attribute_File': req.params.nFile
    }
  };
  activiti.filedownload(req, res, options);
};

exports.getPatternFile = function (req, res) {
  var options = {
    path: 'object/file/getPatternFile',
    query: {
      'sPathFile': req.query.sPathFile
    }
  };

  options.query.sPathFile = options.query.sPathFile.replace(/^sPrintFormFileAsPDF=pattern\/|^sPrintFormFileAsIs=pattern\//, '');
  activiti.filedownload(req, res, options);
};

/**
 * https://github.com/e-government-ua/i/issues/1382
 * added pdf conversion if file name is sPrintFormFileAsPDF
 */
exports.upload_content_as_attachment = function (req, res) {
  async.waterfall([
    function (callback) {
      if (req.body.sFileName === 'sPrintFormFileAsPDF.pdf') {
        var options = {
          html: req.body.sContent,
          allowLocalFilesAccess: true,
          paperSize: {
            format: 'A4', orientation: 'portrait'
          },
          fitToPage: true,
          customHeaders: [],
          settings: {
            javascriptEnabled: true
          },
          format: {
            quality: 100
          }
        };
        pdfConversion(options, function (err, pdf) {
          callback(err, {content: pdf.stream, contentType: 'application/json', url: 'upload_file_as_attachment'});
        });
      } else {
        callback(null, {content: req.body.sContent, contentType: 'text/html', url: 'upload_content_as_attachment'});
      }
    },
    function (data, callback) {
      if (data.url === 'upload_content_as_attachment') {
        activiti.post({
          path: 'object/file/' + data.url,
          query: {
            nTaskId: req.params.taskId,
            sContentType: data.contentType,
            sDescription: req.body.sDescription,
            sFileName: req.body.sFileName
          },
          headers: {
            'Content-Type': data.contentType + ';charset=utf-8'
          }
        }, function (error, statusCode, result) {
          error ? res.send(error) : res.status(statusCode).json(result);
        }, data.content, false);
      }

      if (data.url === 'upload_file_as_attachment') {
        activiti.uploadStream({
          path: 'object/file/' + data.url,
          taskId: req.params.taskId,
          stream: data.content,
          description: req.body.sDescription,
          headers: {
            'Content-Type': data.contentType + ';charset=utf-8'
          }
        }, function (error, statusCode, result) {
          pdfConversion.kill();
          error ? res.send(error) : res.status(statusCode).json(result);
        });
      }
    }
  ]);
};

exports.setTaskQuestions = function (req, res) {
  activiti.get({
    path: 'action/task/setTaskQuestions',
    query: req.body
  }, function (error, statusCode, result) {
    res.statusCode = statusCode;
    res.send(result);
  });
};

// отправка комментария от чиновника, сервис работает на централе, поэтому с env конфигов берем урл.
exports.postServiceMessage = function(req, res){
  var oData = req.body;
  var oDateNew = {
    'sID_Order': environment.activiti.nID_Server + '-' + oData.nID_Process,
    'sBody': oData.sBody,
    'nID_SubjectMessageType' : 9,
    'sMail': oData.sMail,
    'soParams': oData.soParams
  };
  var central = environment.activiti_central;
  var sURL = central.prot + '://' + central.host + ':' + central.port + '/' + central.rest + '/subject/message/setServiceMessage';
  var callback = function(error, response, body) {
    res.send(body);
    res.end();
  };
  return request.post({
    'url': sURL,
    'auth': {
      'username': central.username,
      'password': central.password
    },
    'qs': oDateNew
  }, callback);
};

exports.checkAttachmentSign = function (req, res) {
  var nID_Task = req.params.taskId;
  var nID_Attach = req.params.attachmentId;

  if (!nID_Task) {
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, 'nID_Task should be specified'));
    return;
  }

  if (!nID_Attach) {
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, 'nID_Attach should be specified'));
    return;
  }

  var options = {
    path: 'object/file/check_attachment_sign',
    query: {
      nID_Task: nID_Task,
      nID_Attach: nID_Attach
    },
    json: true
  };

  activiti.get(options, function (error, statusCode, body) {
    if (error) {
      error = errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, 'Error while checking file\'s sign', error);
      res.status(500).send(error);
      return;
    }

    res.status(200).send(body);
  });
};

exports.unassign = function (req, res) {
  var nID_Task = req.params.taskId;
  if (!nID_Task) {
    res.status(400).send(errors.createError(errors.codes.INPUT_PARAMETER_ERROR, 'nID_Task should be specified'));
    return;
  }

  var options = {
    path: 'action/task/resetUserTaskAssign',
    query: {
      nID_UserTask: nID_Task
    },
    json: true
  };

  activiti.post(options, function (error, statusCode, result) {
    error ? res.send(errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, 'Can\'t unassign', error))
      : res.status(statusCode).json(result);
  });
};

/*
 exports.getTaskData = function(req, res) {
 var options = {
 path: 'action/task/getTaskData',
 query: req.query,
 json: true
 };

 activiti.get(options, function (error, statusCode, body) {
 if (error) {
 error = errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, 'Error while loading task data', error);
 res.status(500).send(error);
 return;
 }
 var currentUser = JSON.parse(req.cookies.user);
 var UserFromStorage = JSON.parse(localStorage.getItem('user'));
 currentUser.roles = UserFromStorage.roles;
 // После запуска существует вероятность, что объекта req.session еще не ссуществует и чтобы не вывалилась ошибка
 // пропускаем проверку. todo: При следующем релизе нужно удалить условие !req.session
 //var cashedGr = authService.getCashedUserGroups(currentUser);

 if (!req.session || tasksService.isTaskDataAllowedForUser(body, req.session.roles ? req.session : currentUser))
 res.status(200).send(body);
 else {
 error = errors.createError(errors.codes.FORBIDDEN_ERROR, 'Немає доступу до цієї задачі.');
 res.status(403).send(error);
 }
 });
 };
 */
exports.getTaskData = function (req, res) {
  var options = {
    path: 'action/task/getTaskData',
    query: req.query,
    json: true
  };

  var currentUser = JSON.parse(req.cookies.user);

  var userRoles = authService.getCashedUserGroups(currentUser);
  if (userRoles) {
    currentUser.roles = userRoles;
    activiti.get(options, function (error, statusCode, body) {
      if (error) {
        error = errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, 'Error while loading task data', error);
        res.status(500).send(error);
        return;
      }

      if (!req.session || tasksService.isTaskDataAllowedForUser(body, currentUser))
        res.status(200).send(body);
      else {
        error = errors.createError(errors.codes.FORBIDDEN_ERROR, 'Немає доступу до цієї задачі.');
        res.status(403).send(error);
      }
    });
  } else {
    async.waterfall([
      function (callback) {
        activiti.get({
          path: 'action/identity/getGroups',
          query: {
            sLogin: currentUser.id
          },
          json: true
        }, function (error, statusCode, result) {
          if (error) {
            callback(error, null);
          } else {
            var resultGroups;
            if ((typeof result == "object") && (result instanceof Array)) {
              currentUser['roles'] = result.map(function (group) {
                return group.id;
              });
            } else {
              currentUser['roles'] = [];
            }
            callback(null, {
              currentUser: currentUser
            });
          }
        });
      },
      function (user, callback) {
        activiti.get(options, function (error, statusCode, body) {
          callback(error, body);
        });
      }
    ], function (error, body) {
      if (error) {
        error = errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR, 'Error while loading task data', error);
        res.status(500).send(error);
        return;
      }
      authService.setCashedUserGroups(currentUser, currentUser.roles);
      if (!req.session || tasksService.isTaskDataAllowedForUser(body, currentUser))
        res.status(200).send(body);
      else {
        error = errors.createError(errors.codes.FORBIDDEN_ERROR, 'Немає доступу до цієї задачі.');
        res.status(403).send(error);
      }
    })
  }
};

exports.getMessageFile = function (req, res) {
  var options = {
    path: 'action/task/getMessageFile_Local',
    query: {
      //nID_Process: req.params.taskId,
      nID_Message: req.params.messageId
    },
    json: true
  };
  activiti.filedownload(req, res, options);
};

exports.setTaskAttachment = function (req, res) {
  async.waterfall([
    function (callback) {
      callback(null, {content: req.body.sContent, contentType: 'text/html', url: 'setTaskAttachment'});
    },
    function (data, callback) {
      if (data.url === 'setTaskAttachment') {
        activiti.post({
          path: 'object/file/' + data.url,
          query: {
            nTaskId: req.params.taskId,
            sContentType: data.contentType,
            sDescription: req.body.sDescription,
            sFileName: req.body.sFileName,
            nID_Attach: req.body.nID_Attach
          },
          headers: {
            'Content-Type': data.contentType + ';charset=utf-8'
          }
        }, function (error, statusCode, result) {
          error ? res.send(error) : res.status(statusCode).json(result);
        }, data.content, false);
      }
    }
  ]);
};
