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
};

module.exports.getObjectCustomsList = function (req, res) {
  var resourceId = req.query.sName_UA ? req.query.sName_UA.split('') : [];
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
    var reducePattern = req.query.sName_UA.split('').length > resourceId.length ?
      new RegExp(escapeRegExp(req.query.sName_UA)) : false;
    var items = response.slice(skip, stop);
    items.forEach(function (item) {
      if (!reducePattern || reducePattern.test(item.sName_UA)) {
        outputStream.write(item);
      }
    });
    outputStream.end();
  });
  cachedResource.on('fault', function () {
    if (resourceId.pop()) {
      cachedResource.run();
    } else {
      var request = activiti.sendGetRequest(req, res, sURL, req.query, noop);
      req.pipe(request);
      request.on('response', function (response) {
        resourceId = req.query.sName_UA ? req.query.sName_UA.split('') : [];
        var cursor = 0;
        prepareResponse(response).pipe(cachedResource).on('data', function (data) {
          if (stop == cursor) {
            outputStream.end()
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
