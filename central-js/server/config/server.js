'use strict';

var fs = require('fs')
  , config = require('./environment')
  , request = require('request')
  , loggerFactory = require('../components/logger')
  , logger = loggerFactory.createLogger(module);

module.exports = function (app) {
  if (config.debug) {
    process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
    // request.debug = true;
    loggerFactory.setDebugEnabled();
  }

  function setupServer() {
    var server = null;

    switch (config.server.protocol) {
      case 'https':
        var credentials = {
          key: fs.readFileSync(config.server.key).toString(),
          cert: fs.readFileSync(config.server.cert).toString()
        };

        server = require('https').createServer(credentials, app);
        break;
      case 'http':
      default:
        server = require('http').createServer(app);
    }

    server.listen(config.server.port, function () {
      logger.info('Express server listening on port', {port: config.server.port});
    });
  }

  logger.setup('server setup', {}, setupServer);
};
