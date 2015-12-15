'use strict';
var activiti = require('../../components/activiti');

module.exports.getObjectEarthTargets = function (req, res) {
  activiti.sendGetRequest(req, res, '/services/getObjectEarthTargets', req.query);
};
