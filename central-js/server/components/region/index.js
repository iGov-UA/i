'use strict';

var compose = require('composable-middleware')
  , NodeCache = require("node-cache")
  , errors = require('../errors')
  , subjectService = require('../../api/subject/subject.service');

var serversCache = new NodeCache();

function addRegion(req, sHost, isCacheUsed, nID_Server){
  req.region = {};
  req.region.sHost = sHost.sURL;
  req.region.nID_Server = nID_Server;
  req.region.isCacheUsed = isCacheUsed;
}

function _searchForHost (req, res, next) {
  var nID_Server = req.query.nID_Server;
  if(!nID_Server){
    nID_Server = req.body.nID_Server;
  }

  if (nID_Server !== undefined && nID_Server !== null && nID_Server !== "") {
    serversCache.get(nID_Server, function (err, value) {
      if (!err) {
        if (!value) {
          subjectService.getServerRegion(nID_Server, function (httpError, sHost) {
            if (!httpError) {
              serversCache.set(nID_Server, sHost, 100000, function (cacheError, success) {
                if (!cacheError && success) {
                  addRegion(req, sHost, false, nID_Server);
                  next();
                } else {
                  res.status(400).json(errors.createLogicServiceError('Problems with setting value ' + sHost + ' for ' + nID_Server + 'in cache'));
                }
              });
            } else {
              res.status(400).json(errors.createExternalServiceError('Can\' find server host by nID_Server=' + value));
            }
          });
        } else {
          addRegion(req, value, true, nID_Server);
          next();
        }
      } else {
        res.status(400).json(errors.createExternalServiceError('Can\' find server host by nID_Server=' + value, err));
      }
    });
  } else {
    next();
  }
}

module.exports.searchForHost = function () {
  return compose().use(_searchForHost);
};

module.exports._searchForHost = _searchForHost;
