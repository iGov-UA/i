'use strict';

var activiti = require('../../components/activiti');

var config = require('../../config/environment');
//var config = require('../../config');

/*
var sHostPrefix = config.server.sServerRegion;
console.log('1)sHostPrefix='+sHostPrefix);

var sHost = sHostPrefix + "/wf/service";

function buildSHost(sHostPrefix) {
  console.log('2)sHostPrefix='+sHostPrefix);
  return sHostPrefix + "service";
}*/

module.exports.getFlowSlots_ServiceData = function (req, res) {

//        var data = {
//          //sURL: scope.serviceData.sURL,
//          nID_Server: scope.serviceData.nID_Server,
//          nID_Service: (scope && scope.service && scope.service!==null ? scope.service.nID : null)
//        };
//        return $http.get('/api/service/flow/' + scope.serviceData.nID, {params:data}).then(function(response) {

    var nID_Server = req.query.nID_Server;
    activiti.getServerRegionHost(nID_Server, function(sHost){
    //  sHost = sHost+'/service';

      //sHost = buildSHost(req.query.sURL);
      //activiti.sendGetRequest(req, res, '/action/flow/getFlowSlots_ServiceData', {
      activiti.sendGetRequest(req, res, '/service/action/flow/getFlowSlots_ServiceData', {
        nID_Service: req.query.nID_Service,
        nID_ServiceData: req.params.nID,
        nID_SubjectOrganDepartment: req.query.nID_SubjectOrganDepartment,
        nSlots: req.query.nSlots,
        nDiffDays: req.query.nDiffDays
      }, null, sHost);
    });
};

module.exports.setFlowSlot_ServiceData = function (req, res) {

//          //$http.post('/api/service/flow/set/' + newValue.nID + '?sURL=' + scope.serviceData.sURL).then(function(response) {
//          $http.post('/api/service/flow/set/' + newValue.nID + '?nID_Server=' + scope.serviceData.nID_Server).then(function(response) {

    var nID_Server = req.query.nID_Server;
    var nSlots = req.query.nSlots;
    activiti.getServerRegionHost(nID_Server, function(sHost){
        //var sURL = sHost+'/service/object/file/upload_file_to_redis';
        //console.log("sURL="+sURL);
    //  sHost = sHost+'/service';

      //sHost = buildSHost(req.query.sURL);
    //	activiti.sendPostRequest(req, res, '/action/flow/setFlowSlot_ServiceData', {
        activiti.sendPostRequest(req, res, '/service/action/flow/setFlowSlot_ServiceData', {
                nID_FlowSlot: req.params.nID,
                nSlots: nSlots
        }, null, sHost);
    });
};

module.exports.getSlotsDMS = function (req, res) {
  var nID_Server = req.query.nID_Server;
  var nID_Service_Private = req.query.nID_Service_Private;
  activiti.getServerRegionHost(nID_Server, function(sHost){
    activiti.sendPostRequest(req, res, '/service/action/flow/DMS/getSlots', {
      nID_Service_Private: nID_Service_Private,
      nDays: 7
    }, null, sHost);
  });
};

module.exports.setSlotHoldDMS = function (req, res) {
  var nID_Server = req.body.nID_Server;
  var oData = req.body;
  activiti.getServerRegionHost(nID_Server, function(sHost){
    activiti.sendPostRequest(req, res, '/service/action/flow/DMS/setSlotHold', {
      nID_Service_Private: oData.nID_Service_Private,
      sDateTime: oData.sDateTime,
      sSubjectFamily: oData.sSubjectFamily,
      sSubjectName: oData.sSubjectName,
      sSubjectSurname: oData.sSubjectSurname,
      sSubjectPassport: oData.sSubjectPassport,
      sSubjectPhone: oData.sSubjectPhone
    }, null, sHost);
  });
};

module.exports.setSlotDMS = function (req, res) {
  var nID_Server = req.body.nID_Server;
  var oData = req.body;
  activiti.getServerRegionHost(nID_Server, function(sHost){
    activiti.sendPostRequest(req, res, '/service/action/flow/DMS/setSlot', {
      nID_SlotHold: oData.nID_SlotHold
    }, null, sHost);
  });
};
