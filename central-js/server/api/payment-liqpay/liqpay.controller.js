/**
 * Created by Oleksii Khalikov on 04.08.2016.
 */
'use strict';
var activiti = require('../../components/activiti');

module.exports.getRedirectPaymentLiqpay = function (req, res) {
  activiti.sendGetRequest(req, res, '/finance/redirectPaymentLiqpay', req.query);
 // debugger;
};
