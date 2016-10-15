/**
 * Created by ijmac on 19.05.16.
 */
angular.module('dashboardJsApp')
  .directive('groupList', function () {
    var controller = function ($scope, $modal, $q, Modal) {

      var inProgress = false;

      var groups = [];
      var users = [];
      var userInGroup = [];

      var getFunc = $scope.funcs.getFunc;
      var getUsFunc = $scope.funcs.getUsFunc;
      var setFunc = $scope.funcs.setFunc;
      var deleteFunc = $scope.funcs.deleteFunc;
      var addUsFunc = $scope.funcs.addUsFunc;
      var removeUsFunc = $scope.funcs.removeUsFunc;

      var editModes = {
        CREATE: 1,
        EDIT: 2
      };

      $scope.model = {inProgress: false};

      var fillData = function () {
        inProgress = true;
        $scope.model.inProgress = true;

        $q.all([
            getFunc()
              .then(function (list) {
                groups = list;
              }),
            getUsFunc()
              .then(function(list){
                users = list;
              })
        ])
          .then(function(){
            inProgress = false;
            $scope.model.inProgress = false;
          });
      };

      var openModal = function (group, userInGroup, editMode) {
        var modalInstance = $modal.open({
          animation: true,
          templateUrl: 'app/groups/modal/modal.html',
          controller: 'GroupModalController',
          resolve: {
            groupToEdit: function () {
              return angular.copy(group);
            },
            getUsersFunc: function () {
              return angular.copy(getUsFunc);
            },
            userInGroup: function(){
              return angular.copy(userInGroup);
            },
            allGroups: function(){
              return angular.copy(groups);
            },
            allUsers: function(){
              return angular.copy(users);
            },
            editModes: function(){
              return angular.copy(editModes);
            },
            editMode: function(){
              return angular.copy(editMode);
            }
          },
          size: 'lg'
        });

        modalInstance.result.then(function (editedData) {

          setFunc(editedData.groupToSave.id, editedData.groupToSave.name)
            .then(function (createdGroup) {

              if(editedData.usersToAdd.length){
                for(var i = 0; i<editedData.usersToAdd.length; i++){
                  addUsFunc(createdGroup.id, editedData.usersToAdd[i].sLogin).then(
                    function(addedUser){

                    }, function (err) {console.log('Add User To group Error');}
                  );
                }
              }

              if(editedData.usersToRemove.length){
                for(var i = 0; i<editedData.usersToRemove.length; i++){
                  removeUsFunc(createdGroup.id, editedData.usersToRemove[i].sLogin).then(
                    function(removedUser){

                    }, function (err) {console.log('Remove User From group Error');}
                  );
                }
              }

            for (var i = 0; i < groups.length; i++) {
              if (groups[i].id === createdGroup.id) {
                groups[i] = createdGroup;
                return;
              }
            }
            groups.unshift(createdGroup);
          }, function (err) {console.log('Edit Group Error');});

        });
      };

      $scope.get = function () {
        return groups;
      };

      $scope.add = function () {
        openModal(null, null, editModes.CREATE);
      };

      $scope.edit = function (group) {

        getUsFunc(group.id).then(function (data) {
          userInGroup = data;
        }).finally(function () {
          openModal(group, userInGroup, editModes.EDIT);
        });
      };

      $scope.delete = function (group) {
        deleteFunc(group.id).then(function(data){
          if(data.code === '500'){
            Modal.inform.error()(data.message);
          }else{
            fillData();
          }
        });
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
