'use strict';
var activiti = require('../../components/activiti');

module.exports.getSubjectOrganJoinTaxList = function (req, res) {
  activiti.sendGetRequest(req, res, '/subject/getSubjectOrganJoinTax', req.query);
};
