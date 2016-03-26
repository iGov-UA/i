'use strict';
var zlib = require('zlib');
var activiti = require('../../components/activiti');
var cache = require('../../components/cache');

var sURL = '/object/getObjectCustoms';

function noop () {
  // Empty callback
}

function prepareResponse (res) {
  return (/gzip|deflate/.test(res.headers['content-encoding']) ? res.pipe(zlib.createUnzip()) : res);
}

module.exports.getObjectCustomsList = function (req, res) {
  cache(
    function id () {
      return sURL + '?sName_UA=' + req.query.sName_UA;
    },
    function hit (response) {
      return response;
    },
    function fault (output) {
      var request = activiti.sendGetRequest(req, res, sURL, req.query, noop);
      req.pipe(request);
      request.on('response', function (response) {
        prepareResponse(response).pipe(output);
      });
    },
    function final (response) {
      res.writeHead(200, response.headers);
      return response.pipe(res);
    }
  );
};
