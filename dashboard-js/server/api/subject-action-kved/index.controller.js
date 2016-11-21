/**
 * Created by Oleksii Khalikov on 29.07.2016.
 */
'use strict';
var activiti = require('../../components/activiti');

module.exports.getActionKVEDList = function (req, res) {
  activiti.sendGetRequest(req, res, '/subject/getActionKVED', req.query);
};
