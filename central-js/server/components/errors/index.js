'use strict';

module.exports.codes = {
  EXTERNAL_SERVICE_ERROR: 'ESE',
  INPUT_PARAMETER_ERROR: 'IPE',
  LOGIC_SERVICE_ERROR: 'LSE'
};

module.exports.createExternalServiceError = function (error_description, error) {
  return this.createError(this.codes.EXTERNAL_SERVICE_ERROR, error);
};

module.exports.createInputParameterError = function (error_description, error) {
  return this.createError(this.codes.INPUT_PARAMETER_ERROR, error);
};

module.exports.createLogicServiceError = function (error_description, error) {
  return this.createError(this.codes.LOGIC_SERVICE_ERROR, error);
};

module.exports.createError = function (code, error_description, error) {
  return {
    code: code,
    message: error_description,
    nested: error
  };
};

module.exports[404] = function pageNotFound(req, res) {
  var viewFilePath = '404';
  var statusCode = 404;
  var result = {
    status: statusCode
  };

  res.status(result.status);
  res.render(viewFilePath, function (err) {
    if (err) {
      return res.json(result, result.status);
    }

    res.render(viewFilePath);
  });
};
