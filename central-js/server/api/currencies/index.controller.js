'use strict';
var activiti = require('../../components/activiti');

module.exports.getCurrencyList = function (req, res) {
  activiti.sendGetRequest(req, res, '/services/getCurrencies', req.query);
};
