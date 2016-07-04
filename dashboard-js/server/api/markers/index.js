var express = require('express');
var router = express.Router();

router.post('/validate', validateMarkers);

module.exports = router;

function validateMarkers(req, res) {
    var Ajv = require('ajv');
    var ajv = Ajv({allErrors: true});
    var validate = ajv.compile(req.body.schema);
    var valid = validate(req.body.markers);
    res.send({valid:valid, errors:validate.errors});
}
