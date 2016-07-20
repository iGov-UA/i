/**
 * Created by ijmac on 20.05.16.
 */
'use strict';

angular.module('dashboardJsApp')
  .controller('GroupModalController', function ($scope, $modalInstance, groupToEdit, getUsersFunc, userInGroup, allGroups, allUsers, editModes, editMode) {

    var parser = function () {
      return {
        parse: function (groupToEdit) {
          if (groupToEdit) {
            return {
              id: groupToEdit.id,
              name: groupToEdit.name,
              type: groupToEdit.type,
              url: groupToEdit.url,
              revision: groupToEdit.revision,
              revisionNext: groupToEdit.revisionNext
            };
          } else {
            return {
              id: null,
              name: null,
              type: null,
              url: null
            }
          }
        }
      }
    }();

    $scope.save = function () {
      var dataToSave = {
        groupToSave: $scope.data.group,
        usersToAdd: $scope.data.usersToAdd,
        usersToRemove: $scope.data.usersToRemove
      };

      $modalInstance.close(dataToSave);
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };

    $scope.initGroupModalDialog = function () {
      $(".modal-dialog").addClass("groups-modal-dialog");
    };

    function isIdTaken(id) {
      return !allGroups.some(function (group) {
        return group.id === id;
      })
    }

    $scope.idHasNotBeenUsed = function( $value ) {
      return isIdTaken($value);
    };

    //  Init
    $scope.data = {
      group: parser.parse(groupToEdit),
      userInGroup: userInGroup,
      usersToAdd: [],
      usersToRemove: [],
      allGroups: allGroups,
      allUsers: allUsers,
      editMode: editMode,
      editModes: editModes
    };
    $scope.getUsers = getUsersFunc;

  });
