'use strict';

var express = require('express');
var router = express.Router();
var controller = require('./index.controller');

router.get('/', controller.index);
router.get('/:nID([0-9]+)/feedback', controller.getServiceFeedback);
router.post('/:nID([0-9]+)/feedback', controller.postServiceFeedback);
router.post('/:nID([0-9]+)/feedbackAnswer', controller.postServiceFeedbackAnswer);
router.get('/:nID([0-9]+)/statistics', controller.getServiceStatistics);
router.get('/getServiceHistoryReport', controller.getServiceHistoryReport);
router.post('/', controller.setService);
router.post('/getPatternFilled', controller.getPatternFilled);
router.delete('/', controller.removeServiceData);

module.exports = router;
