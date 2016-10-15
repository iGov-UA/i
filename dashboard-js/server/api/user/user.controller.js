'use strict';

var service = require('./user.service');

module.exports.index = function (req, res) {
  var callback = function (error, statusCode, result) {
    if (error) {
    }
    res.json(result);
  };
  service.getUsers(null, callback);
};

module.exports.getGroups = function (req, res) {

  var callback = function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.status(statusCode).json(result);
    }
  };
  service.getGroups(req.query.sLogin, callback);
};

module.exports.setGroup = function (req, res) {

  var callback = function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      var jsonRes = result ? result : {id: req.query.sID, name: req.query.sName};
      res.status(statusCode).json(jsonRes);
    }
  };

  service.setGroup(req.query.sID, req.query.sName, callback);
};

module.exports.setUserGroup = function (req, res) {
  var callback = function (error, statusCode, result) {
    if (error) {
      res.send(error);
    }
    var jsonRes = result ? result : {
      sID_Group: req.query.sID_Group,
      sLogin: req.query.sLogin
    };
    res.status(statusCode).json(jsonRes);
  };

  service.setUserGroup({
      sID_Group: req.query.sID_Group,
      sLogin: req.query.sLogin
    },
    callback);
};

module.exports.removeUserGroup = function (req, res) {
  var callback = function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      var jsonRes = result ? result : {
        sID_Group: req.query.sID_Group,
        sLogin: req.query.sLogin
      };
      res.status(statusCode).json(jsonRes);
    }
  };

  var params = {
    sID_Group: req.query.sID_Group,
    sLogin: req.query.sLogin
  };
  service.removeUserGroup(params, callback);
};


module.exports.deleteGroup = function (req, res) {

  var callback = function (error, statusCode, result) {
    if (error || (result && result.code === '500')) {
      res.send(error || result);
      res.end();
    } else {
      var jsonRes = result ? result : {id: req.query.sID};
      res.status(statusCode).json(jsonRes);
    }
  };

  service.delGroup(req.query.sID, callback);
};

module.exports.getUsers = function (req, res) {
  var callback = function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      res.status(statusCode).json(result);
    }
  };
  service.getUsers(req.query.sID_Group, callback);
};

module.exports.setUser = function (req, res) {
  var callback = function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      var jsonRes = result ? result : {
        sLogin: req.query.sLogin,
        sPassword: req.query.sPassword,
        sName: req.query.sName,
        sDescription: req.query.sDescription,
        sEmail: req.query.sEmail
      };
      res.status(statusCode).json(jsonRes);
    }
  };

  service.setUser({
      sLogin: req.query.sLogin,
      sPassword: req.query.sPassword,
      sName: req.query.sName,
      sDescription: req.query.sDescription,
      sEmail: req.query.sEmail
    },
    callback);
};


module.exports.deleteUser = function (req, res) {
  var callback = function (error, statusCode, result) {
    if (error) {
      res.send(error);
    } else {
      var jsonRes = result ? result : {
        sLogin: req.query.sLogin
      };
      res.status(statusCode).json(jsonRes);
    }
  };
  service.removeUser(
    {sLogin: req.query.sLogin},
    callback);
};
