/**
 * Created by Oleksii Khalikov on 01.08.2016.
 */
'use strict';
var activiti = require('../../components/activiti');

module.exports.getObjectPlaceUAList = function (req, res) {
  activiti.sendGetRequest(req, res, '/object/place/getObjectPlace_UA', req.query);
};
