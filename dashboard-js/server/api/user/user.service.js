'use strict';

var async = require('async');
var activiti = require('../../components/activiti');

exports.getUserIDsFromGroups = function (groups, callback) {
  var usersIDs = [];
  async.forEach(groups, function (group, frCallback) {
    exports.getUsers(group.id, function(error, status, result){
      if(!error && result.data){
        usersIDs = usersIDs.concat(result.data.map(function(user){
          return user.id;
        }));
      }
      frCallback(null);
    });
  }, function (error) {
    var uniqueUsers = usersIDs.filter(function(elem, pos, arr) {
      return arr.indexOf(elem) == pos;
    });
    callback(error, uniqueUsers);
  });
};

exports.getUsers = function (groupID, callback) {
  //GET identity/users
  var options = {
    path: 'identity/users',
    query: {
      memberOfGroup: groupID
    },
    json: true
  };

  activiti.get(options, callback);
};

exports.getGroups = function (assigneeID, callback) {
  var options = {
    path: 'identity/groups',
    query: {
      member: assigneeID
    },
    json: true
  };

  activiti.get(options, callback);
};
