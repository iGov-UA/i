'use strict';
var activiti = require('../../components/activiti');

module.exports.getObjectEarthTargets = function (req, res) {
  activiti.sendGetRequest(req, res, '/object/getObjectEarthTargets', req.query);
};
