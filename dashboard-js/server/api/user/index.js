var _ = require('lodash');
var activiti = require('../../components/activiti');
var errors = require('../../components/errors');
var express = require('express');
var controller = require('./user.controller');
var router = express.Router();


/**
 * GET identity/users/{userId}
 * <a href="http://www.activiti.org/userguide/#_get_a_single_user">description</a>
 * @param req
 * @param res
 */

router.get('/', controller.index);
router.get('/groups/getGroups', controller.getGroups);
router.post('/groups/setGroup', controller.setGroup);
router.post('/setUserGroup',controller.setUserGroup);
router.delete('/removeUserGroup',controller.removeUserGroup);
router.delete('/groups/deleteGroup', controller.deleteGroup);
router.get('/getUsers', controller.getUsers);
router.post('/setUser', controller.setUser);
router.delete('/deleteUser', controller.deleteUser);



module.exports = router;
