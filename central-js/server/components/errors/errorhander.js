var logger = require('../logger').createLogger(module)
  , errors = require('./index');

module.exports.logErrorHandler = function (err, req, res, next) {
  logger.error(err.message, {
    sid: req.session.id,
    reqid: req.reqid,
    stack: err.stack
  });
  next(err);
};

module.exports.errorHandler = function (err, req, res, next) {
  res.status(500).send({reqid: req.reqid, error: errors.createLogicServiceError(err.message, {})});
};
