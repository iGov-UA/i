'use strict';

var compose = require('composable-middleware')
  , errors = require('../errors');

module.exports.assertQueryParams = function () {
  var _arguments = arguments;
  return compose().use(function (req, res, next) {
    var query = req.query;
    for (var i = 0; i < _arguments.length; i++) {
      if (!query.hasOwnProperty(_arguments[i])) {
        res.status(400).send(errors.createInputParameterError(_arguments[i] + ' should be specified in query'));
        return;
      }
    }
    next();
  });
};
