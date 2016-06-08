/**
 * Created by ijmac on 19.05.16.
 */
'use strict';

angular.module('dashboardJsApp')
  .controller('GroupsCtrl', function ($scope, group, user) {

    $scope.list = {
      getFunc: group.getGroups,
      setFunc: group.setGroup,
      deleteFunc: group.deleteGroup,
      getUsFunc: user.getUsers,
      setUsFunc: user.setUser,
      delUsFunc: user.deleteUser,
      addUsFunc: user.addUser,
      removeUsFunc: user.removeUser
    };
  });
