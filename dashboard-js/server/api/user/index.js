var _ = require('lodash');
var activiti = require('../../components/activiti');
var errors = require('../../components/errors');

/**
 * GET identity/users/{userId}
 * <a href="http://www.activiti.org/userguide/#_get_a_single_user">description</a>
 * @param req
 * @param res
 */
exports.index = function (req, res) {
  var userID = req.params.userID;
  var options = {
    path: 'identity/users/' + userID,
    json: true
  };

  activiti.get(options, function (error, statusCode, result) {
    if (error) {
      res.send(errors.createError(errors.codes.EXTERNAL_SERVICE_ERROR,
        'Can\'t find user by ' + userID, error));
    } else {
      res.json(result);
    }
  });
};
