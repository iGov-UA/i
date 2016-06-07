/**
 * Created by ijmac on 20.05.16.
 */
'use strict';

angular.module('dashboardJsApp')
  .controller('UserModalController', function ($scope, $modalInstance, userToEdit) {

    var parser = function () {
      return {
        parse: function (userToEdit) {
          if (userToEdit) {
            return {
              isNew: false,
              sLogin: userToEdit.sLogin,
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
        userToSave: $scope.data.user
      };

      console.log('!!!!!!!!!!!!!  modal save  ', dataToSave.userToSave);

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

    //  Init
    $scope.data = {user: parser.parse(userToEdit), groupsList: []};
    $scope.getGroups = {};

  });
