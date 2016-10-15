var activiti = require('../../components/activiti');

module.exports.index = function (req, res) {
  var sHost = req.region.sHost;
  activiti.get('/service/repository/process-definitions', {
    latest: true,
    size: 1000
  }, function (error, response, body) {
    res.send(body);
  }, sHost);
};
