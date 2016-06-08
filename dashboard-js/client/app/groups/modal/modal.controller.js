/**
 * Created by ijmac on 20.05.16.
 */
'use strict';

angular.module('dashboardJsApp')
  .controller('GroupModalController', function ($scope, $modalInstance, groupToEdit, getUsToGFunc) {

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

    $scope.showSave = function () {
      return true;
    };

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

    //  Init
    $scope.data = {group: parser.parse(groupToEdit), usersToAdd: [], usersToRemove: []};
    $scope.getUsers = getUsToGFunc;

  });
