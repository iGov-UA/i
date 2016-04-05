'use strict';
var activiti = require('../../components/activiti');

module.exports.getObjectCustomsList = function (req, res) {
  activiti.sendGetRequest(req, res, '/object/getObjectCustoms', req.query);
};
