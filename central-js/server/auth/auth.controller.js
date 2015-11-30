'use strict';

module.exports.isAuthenticated = function(req, res){
  res.status(200).end();
};

module.exports.logout = function(req, res){
  req.session = null;
  res.status(200).end();
};
