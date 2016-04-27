var express = require('express');
var router = express.Router();
var schema = require('./schema.js');

router.post('/validate', validateMarkers);

module.exports = router;

function validateMarkers(req, res) {
    var Ajv = require('ajv');
    var ajv = Ajv({allErrors: true});
    var validate = ajv.compile(schema.markersSchema);
    var valid = validate(req.body);
    res.send({valid:valid, errors:validate.errors});
}