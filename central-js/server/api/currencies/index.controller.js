'use strict';
var _ = require('lodash');
var activiti = require('../../components/activiti');

module.exports.getCurrencyList = function (req, res) {
  activiti.sendGetRequest(req, res, '/services/getCurrencies', _.extend(req.query, req.params));
};
