var request = require('request')
  , config = require('../../config/environment')
  , activiti = require('../../components/activiti')
  , url = require('url')
  , _ = require('lodash');


function getURL(pathname) {
  return url.format({
    protocol: config.activiti.protocol,
    hostname: config.activiti.hostname,
    pathname: config.activiti.path + pathname
  });
}

var requestBase = {
  url: getURL('/subject/syncSubject'),
  auth: {
    username: config.activiti.username,
    password: config.activiti.password
  },
  json: true
};

module.exports.syncBySCodeAndHumanIDType = function (sCode_Subject, nID_SubjectHumanIdType, callback) {
  return request.get(_.extend(requestBase, {
    qs: {
      sCode_Subject : sCode_Subject,
      nID_SubjectHumanIdType: nID_SubjectHumanIdType
    }
  }), callback);
};

module.exports.sync = function (inn, callback) {
  return request.get(_.extend(requestBase, {
    qs: {
      sINN: inn
    }
  }), callback);
};
