var winston = require('winston')
  , uuid = require('node-uuid')
  , dateFormat = require('dateformat')
  , config = require('./../../config/environment');

var logger = config.debug ? new (winston.Logger)({
  transports: [
    new (winston.transports.Console)({
      'timestamp': function () {
        return dateFormat(new Date(), "yyyy-mm-dd_HH-MM-s.l");
      }
    })
  ]
}) : new (winston.Logger)({
  transports: [
    new (winston.transports.Console)({})
  ]
});

function createMetadata(module, metadata) {
  if (metadata) {
    return {meta: metadata, module: module.id};
  } else {
    return {module: module.id};
  }
}

module.exports.setDebugEnabled = function () {
  logger.level = 'debug';
};

module.exports.createLogger = function (module) {
  return {
    debug: function (message, metadata) {
      logger.log('debug', message, createMetadata(module, metadata));
    },
    info: function (message, metadata) {
      logger.log('info', message, createMetadata(module, metadata));
    },
    setup: function (message, metadata, execution) {
      logger.log('debug', '[\*\] ' + message + '.....', createMetadata(module, metadata));
      execution();
      logger.log('debug', '[\*\] ' + '.....' + message, createMetadata(module, metadata));
    },
    request: function (req) {
      if (req.method && req.originalUrl && req.headers && req.headers.accept && req.headers.accept.indexOf('application/json') > -1) {
        this.info('request', {reqid: req.reqid, m: req.method, originalUrl: req.originalUrl});
      } else {
        this.warning('non-json request to API', {
          reqid: req.reqid,
          m: req.method,
          originalUrl: req.originalUrl,
          accept: req.headers.accept
        });
      }
    },
    warning: function (message, metadata) {
      logger.log('warning', message, createMetadata(module, metadata));
    },
    error: function (message, metadata) {
      logger.log('error', message, createMetadata(module, metadata));
    }
  }
};
