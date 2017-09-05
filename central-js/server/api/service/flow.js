'use strict';

var express = require('express');
var router = express.Router();
var flow = require('./flow.controller');
var auth = require('../../auth/auth.service.js');

router.get('/:nID', flow.getFlowSlots_ServiceData);
router.post('/set/:nID', flow.setFlowSlot_ServiceData);

router.get('/DMS/getSlots', flow.getSlotsDMS);
router.post('/DMS/setSlotHold', flow.setSlotHoldDMS);
router.post('/DMS/canselSlotHold', flow.canselSlotHoldDMS);
router.post('/DMS/setSlot', flow.setSlotDMS);

router.get('/Qlogic/getSlots', flow.getSlotsQlogic);
router.post('/Qlogic/setSlot', flow.setSlotQlogic);

module.exports = router;
