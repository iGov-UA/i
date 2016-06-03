/**
 * Created by ijmac on 19.05.16.
 */
angular.module('dashboardJsApp')
  .directive('groupList', function () {
    var controller = function ($scope, $modal) {

      var inProgress = false;

      var groups = [];

      var getFunc = $scope.funcs.getFunc;
      var getUsFunc = $scope.funcs.getUsFunc;
      var setFunc = $scope.funcs.setFunc;
      var deleteFunc = $scope.funcs.deleteFunc;
      var addUsFunc = $scope.funcs.addUsFunc;
      var removeUsFunc = $scope.funcs.removeUsFunc;

      var fillData = function () {
        inProgress = true;
        getFunc()
          .then(function (list) {
            groups = list;
          })
          .finally(function () {
            inProgress = false;
          });
      };

      //var fillUsData = function (group) {
      //  inProgress = true;
      //  getUsFunc(group.id)
      //    .then(function (list) {
      //      usersToGroup = list;
      //    })
      //    .finally(function () {
      //      inProgress = false;
      //    });
      //};

      var openModal = function (group) {
        var modalInstance = $modal.open({
          animation: true,
          templateUrl: 'app/groups/modal/modal.html',
          controller: 'GroupModalController',
          resolve: {
            groupToEdit: function () {
              return angular.copy(group);
            }
            ,
            getUsToGFunc: function () {
              return angular.copy(getUsFunc);
            }
          },
          size: 'lg'
        });

        modalInstance.result.then(function (editedData) {

          setFunc(editedData.groupToSave.id, editedData.groupToSave.name).then(function (createdGroup) {

            for (var i = 0; i < groups.length; i++) {
              if (groups[i].id === createdGroup.id) {
                groups[i] = createdGroup;
                return;
              }
            }
            groups.unshift(createdGroup);
          }, function (err) {});

          if(editedData.usersToAdd.length){
            for(var i = 0; i<editedData.usersToAdd.length; i++){
              addUsFunc(editedData.groupToSave.id, editedData.usersToAdd[i].sLogin).then(
                function(addedUser){

                }, function (err) {console.log('Add User To group Error');}
              );
            }
          }

          if(editedData.usersToRemove.length){
            for(var i = 0; i<editedData.usersToRemove.length; i++){
              removeUsFunc(editedData.groupToSave.id, editedData.usersToRemove[i].sLogin).then(
                function(removedUser){

                }, function (err) {console.log('Remove User From group Error');}
              );
            }
          }
        });
      };

      $scope.get = function () {
        return groups;
      };

      $scope.add = function () {
        openModal();
      };

      $scope.edit = function (group) {
        //fillUsData(group);
        openModal(group);
      };

      $scope.delete = function (group) {
        deleteFunc(group.id).then(fillData);
      };

      $scope.init = function () {
        fillData();
      };
    };

    return {
      restrict: 'EA',
      scope: {
        funcs: '='
      },
      controller: controller,
      templateUrl: 'app/groups/directives/groupList.html'
    }
  });
