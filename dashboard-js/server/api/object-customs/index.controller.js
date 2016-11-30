'use strict';
var zlib = require('zlib');
var activiti = require('../../components/activiti');
var cache = require('../../components/cache');
var JSONStream = require('JSONStream');
var etag = require('etag');
var sURL = '/object/getObjectCustoms';

function noop () {
  // Empty callback
}

function prepareResponse (res) {
  return (/gzip|deflate/.test(res.headers['content-encoding']) ?
    res.pipe(zlib.createUnzip()) :
    res).pipe(JSONStream.parse('*'));
}

function escapeRegExp (text) {
  return text.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
}

module.exports.getObjectCustomsList = function (req, res) {
  var resourceId = req.query.sFind ? req.query.sFind.split('') : [];
  var outputStream = JSONStream.stringify('[', ',', ']');
  var cachedResource = cache('nID', function () {
    return sURL + '?sName_UA=' + resourceId.join('');
  });
  res.setHeader('Content-Type', 'application/json;charset=utf-8');
  var skip = Math.abs(parseInt(req.query.skip, 10)) || 0;
  var stop = Math.abs(parseInt(req.query.count, 10)) + skip || 5 + skip;
  outputStream.pipe(res);

  cachedResource.on('hit', function (response, ids) {
    var resTag = etag(JSON.stringify(ids));
    var reqTag = req.headers['if-none-match'];
    if (resTag === reqTag) {
      res.status(304);
      res.end();
      return;
    }
    res.setHeader('ETag', resTag);
    var reducePattern = req.query.sFind.split('').length > resourceId.length ?
      new RegExp(escapeRegExp(req.query.sFind)) : false;
    var selectedCount = 0;
    var cursor = 0;
    response.filter(function (item, index) {
      if ((!reducePattern || (reducePattern.test(item.sName_UA) || reducePattern.test(item.sID_UA)))
            && (cursor++ >= skip) && (selectedCount++ < req.query.count)) {
          outputStream.write(item);
      }
    });
    outputStream.end();
  });
  cachedResource.on('fault', function () {
    if (resourceId.pop()) {
      cachedResource.run();
    } else {
      var request = activiti.sendGetRequest(req, res, sURL, {sName_UA : req.query.sFind}, noop);
      req.pipe(request);
      request.on('response', function (response) {
        resourceId = req.query.sFind ? req.query.sFind.split('') : [];
        var cursor = 0;
        prepareResponse(response).pipe(cachedResource).on('data', function (data) {
          if (stop == cursor) {
            outputStream.end();
          } else if (skip <= cursor) {
            outputStream.write(data);
          }
          cursor++;
        });
      });
    }
  });
  cachedResource.on('error', function (err) {
    res.send(err.stack);
  });
  cachedResource.run();
};
