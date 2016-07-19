/**
 * Created by ijmac on 20.05.16.
 */
'use strict';

angular.module('dashboardJsApp')
  .controller('UserModalController',function ($scope, $modalInstance, userToEdit, userGroups, allGroups, allUsers, editModes, editMode) {

    var parser = function () {
      return {
        parse: function (userToEdit) {
          if (userToEdit) {
            return {
              isNew: false,
              sLogin: userToEdit.sLogin,
              oldPassword: userToEdit.sPassword,
              sName: userToEdit.sName,
              sDescription: userToEdit.sDescription,
              sEmail: userToEdit.sEmail,
              FirstName: userToEdit.FirstName,
              LastName: userToEdit.LastName,
              Email: userToEdit.Email,
              Picture: userToEdit.Picture
            };
          } else {
            return {
              isNew: true,
              sLogin: null,
              sPassword: null,
              sName: null,
              sDescription: null,
              sEmail: null,
              FirstName: null,
              LastName: null,
              Email: null,
              Picture: null
            }
          }
        }
      }
    }();

    $scope.showSave = function () {
      return true;
    };

    $scope.save = function () {
      var dataToSave = {
        userToSave: $scope.data.user,
        groupsToAdd: $scope.data.groupsToAdd,
        groupsToRemove: $scope.data.groupsToRemove
      };
      $modalInstance.close(dataToSave);
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };

    $scope.initUserModalDialog = function () {
      //$(".modal-dialog").addClass("groups-modal-dialog");
    };

    $scope.isNewUser = function(){
      return $scope.data.user.isNew;
    };

    function isLoginTaken(login) {
      return allUsers.some(function (user) {
        return user.sLogin === login;
      });
    }

    $scope.loginHasNotBeenUsed = function( $value ) {
      return $scope.data.editMode !== $scope.data.editModes.EDIT ? !isLoginTaken($value) : true;
    };

    //  Init
    $scope.data = {
      user: parser.parse(userToEdit),
      groupsList: userGroups,
      allGroupsList: allGroups,
      groupsToAdd: [],
      groupsToRemove: [],
      editMode: editMode,
      editModes: editModes
    };
    $scope.getGroups = {};

  });
