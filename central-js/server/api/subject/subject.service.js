var request = require('request')
  , config = require('../../config/environment/index')
  , activiti = require('../../components/activiti/index')
  , url = require('url')
  , _ = require('lodash');

module.exports.syncBySCodeAndHumanIDType = function (sCode_Subject, nID_SubjectHumanIdType, callback) {
  activiti.get('/subject/syncSubject', {
    sCode_Subject: sCode_Subject,
    nID_SubjectHumanIdType: nID_SubjectHumanIdType
  }, callback);
};

module.exports.sync = function (inn, callback) {
  activiti.get('/subject/syncSubject', {
    sINN: inn
  }, callback);
};

module.exports.getSubjectOrgan = function (nID_Subject, callback) {
  activiti.get('/subject/getSubjectOrgan', {nID_Subject: nID_Subject}, callback);
};

module.exports.getSubjectHuman = function (nID_Subject, callback) {
  activiti.get('/subject/getSubjectHuman', {nID_Subject: nID_Subject}, callback);
};

module.exports.getSubjectOrganJoinTaxList = function (req, res) {
  activiti.sendGetRequest(req, res, '/subject/getSubjectOrganJoinTax', req.query);
};
