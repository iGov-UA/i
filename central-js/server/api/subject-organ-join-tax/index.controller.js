'use strict';
var activiti = require('../../components/activiti');

module.exports.getSubjectOrganJoinTaxList = function (req, res) {
  activiti.sendGetRequest(req, res, '/services/getSubjectOrganJoinTax', req.query);
};
