'use strict';

var async = require('async');
var activiti = require('../../components/activiti');

exports.getUserIDsFromGroups = function (groups, callback) {
  var usersIDs = [];
  async.forEach(groups, function (group, frCallback) {
    exports.getUsers(group.id, function (error, status, result) {
      if (!error && result.data) {
        usersIDs = usersIDs.concat(result.data.map(function (user) {
          return user.id;
        }));
      }
      frCallback(null);
    });
  }, function (error) {
    var uniqueUsers = usersIDs.filter(function (elem, pos, arr) {
      return arr.indexOf(elem) == pos;
    });
    callback(error, uniqueUsers);
  });
};

exports.getUsers = function (groupID, callback) {
  //GET identity/users
  var options = {
    path: 'action/identity/getUsers',
    //query: {memberOfGroup: groupID},
    query: groupID ? {sID_Group: groupID} : {},
    json: true
  };
  activiti.get(options, callback);
};

exports.setUser = function (params, callback) {
  //GET identity/users
  var options = {
    path: 'action/identity/setUser',
    query: {
      sLogin: params.sLogin,
      sPassword: params.sPassword,
      sName: params.sName,
      sDescription: params.sDescription,
      sEmail: params.sEmail
    },
    json: true
  };

  activiti.get(options, callback);
};

exports.removeUser = function (params, callback) {
  var options = {
    path: 'action/identity/removeUser',
    query: {
      sLogin: params.sLogin
    },
    json: true
  };

  activiti.del(options, callback);
};

exports.setUserGroup = function (params, callback) {
  //GET identity/users
  var options = {
    path: 'action/identity/setUserGroup',
    query: {
      sID_Group: params.sID_Group,
      sLogin: params.sLogin
    },
    json: true
  };

  activiti.post(options, callback);
};

exports.removeUserGroup = function (params, callback) {
  //GET identity/users
  var options = {
    path: 'action/identity/removeUserGroup',
    query: {
      sID_Group: params.sID_Group,
      sLogin: params.sLogin
    },
    json: true
  };

  activiti.del(options, callback);
};

exports.getGroups = function (assigneeID, callback) {
  var options = {
    path: 'action/identity/getGroups',
    //path: 'identity/groups',
    //query: {member: assigneeID},
    query: assigneeID ? {sLogin: assigneeID} : {},
    json: true
  };

  activiti.get(options, callback);
};

exports.setGroup = function (groupID, groupName, callback) {
  var options = {
    path: 'action/identity/setGroup',
    query: {
      sID: groupID,
      sName: groupName
    },
    json: true
  };

  activiti.get(options, callback);
};

exports.delGroup = function (groupID, callback) {
  var options = {
    path: 'action/identity/removeGroup',
    query: {
      sID: groupID
    },
    json: true
  };
  activiti.del(options, callback);
};

