'use strict';

var chai = require('chai');
var should = require('should');
var bankidUtil = require('./bankid.util');
var appData = require('../../app.data.spec');

bankidUtil.decryptData(appData.customer);

