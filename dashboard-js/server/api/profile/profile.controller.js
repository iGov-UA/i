'use strict';

var _ = require('lodash');
var activiti = require('../../components/activiti');
var errors = require('../../components/errors');
var async = require('async');

function createHttpError(error, statusCode) {
  return {httpError: error, httpStatus: statusCode};
}


exports.getSubjects = function (req, res) {
  var saAccount = req.params.saAccount;
  var nID_SubjectAccountType = req.params.nID_SubjectAccountType;

  var options = {
    path: ' https://test.igov.org.ua/wf/service/subject/getSubjectsBy?saAccount=["'+saAccount+'"]&nID_SubjectAccountType=' + nID_SubjectAccountType,
    json: true,
    doNotUseActivityConfigUrl: true
  };

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      console.log(result);
      console.log(error);
      res.send(errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR,
        'Can\'t find user by ' + userID, error));
    } else {
      console.log(result);
      res.json(result);
    }
  });
};


exports.changePassword = function (req, res) {
  console.log(req.body);
  activiti.post({
    path: 'action/task/changePassword',
    query: {
      sLoginOwner: req.body.sLoginOwner,
      sPasswordOld: req.body.sPasswordOld,
      sPasswordNew: req.body.sPasswordNew,
      sContentType: 'text/html'
    },
    headers: {
      'Content-Type': 'text/html;charset=utf-8'
    }
  }, function (error, statusCode, result) {
    console.log(error);
    console.log(statusCode);
    console.log(result);
    error ? res.send(error) : res.status(statusCode).json(result);
  }, req.body.sContent, false);
};

