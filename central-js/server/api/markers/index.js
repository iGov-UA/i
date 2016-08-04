var express = require('express');
var router = express.Router();

router.post('/validate', validateMarkers);

module.exports = router;

function validateMarkers(req, res) {
  var Ajv = require('ajv');
  if (!req.body.definitions || !req.body.definitions.schema) {
    var connect = {
      errors: [{messages: "ERROR Connection to marker validator"}]
    };
    res.send({valid: false, errors: connect.errors});
  } else {
    if (!req.body.definitions.options) {
      req.body.definitions.options = {
        allErrors: true,
        useDefaults: true
      }
    }
    var ajv = Ajv(req.body.definitions.options);
    var validate = ajv.compile(req.body.definitions.schema);
    var valid = validate(req.body.markers);
    res.send({valid: valid, errors: validate.errors});
  }
}
