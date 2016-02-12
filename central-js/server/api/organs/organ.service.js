'use strict';

var request = require('request')
  , config = require('../../config/environment')
  , activiti = require('../../components/activiti');


/**
 * https://test.igov.org.ua/wf/service/subject/getSubjectOrgan?nID_Subject=1
 *
 *
 * @param nID_Subject
 * @param callback
 */
module.exports.getSubjectOrgan = function (nID_Subject, callback) {
  activiti.get('/subject/getSubjectOrgan', {nID_Subject: nID_Subject}, callback);
};

module.exports.getSubjectHuman = function (nID_Subject, callback) {
  activiti.get('/subject/getSubjectHuman', {nID_Subject: nID_Subject}, callback);
};
