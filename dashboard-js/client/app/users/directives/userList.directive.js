/**
 * Created by ijmac on 23.05.16.
 */
angular.module('dashboardJsApp')
  .directive('userList', function () {
    var controller = function ($scope, $modal, $q) {
      var inProgress = false;
      var users = [];
      var groupsToUser = [];
      var groups = [];
      var getFunc = $scope.funcs.getFunc;
      var getGrFunc = $scope.funcs.getGrFunc;
      var setFunc = $scope.funcs.setFunc;
      var deleteFunc = $scope.funcs.deleteFunc;
      var addToGroupFunc = $scope.funcs.addFunc;
      var removeFromGroup = $scope.funcs.removeFunc;

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
              .then(function (data) {
                users = data;
              }),
            getGrFunc().then(function (data) {
              groups = data;
            })
          ])
          .then(function () {
            inProgress = false;
            $scope.model.inProgress = false;
          });

      };

      var openModal = function (user, groups, allGroups, editMode) {
        var modalInstance = $modal.open({
          animation: true,
          templateUrl: 'app/users/modal/modal.html',
          controller: 'UserModalController',
          resolve: {
            userToEdit: function () {
              return angular.copy(user);
            },
            userGroups: function () {
              return angular.copy(groups);
            },
            allGroups: function () {
              return angular.copy(allGroups);
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
          setFunc(editedData.userToSave.sLogin
            , editedData.userToSave.sPassword || user.sPassword
            , editedData.userToSave.sName
            , editedData.userToSave.sDescription
            , editedData.userToSave.sEmail).then(function (createdUser) {

            var userToAdd = {
              sLogin: createdUser.sLogin,
              sPassword: createdUser.sPassword,
              sName: createdUser.sName,
              sDescription: createdUser.sDescription,
              sEmail: createdUser.sEmail,
              FirstName: createdUser.sName || users[i].FirstName,
              LastName: createdUser.sDescription || users[i].LastName,
              Email: createdUser.sEmail || users[i].Email,
            };

            //AddUserTo Group
            if (editedData.groupsToAdd.length) {
              for (var i = 0; i < editedData.groupsToAdd.length; i++) {
                addToGroupFunc(editedData.groupsToAdd[i].id, userToAdd.sLogin).then(
                  function (addedUser) {

                  }, function (err) {
                    console.log('Add User To group Error');
                  }
                );
              }
            }

            if (editedData.groupsToRemove.length) {
              for (var i = 0; i < editedData.groupsToRemove.length; i++) {
                removeFromGroup(editedData.groupsToRemove[i].id, userToAdd.sLogin).then(
                  function (removedUser) {

                  }, function (err) {
                    console.log('Remove User From group Error');
                  }
                );
              }
            }

            for (var i = 0; i < users.length; i++) {
              if (users[i].sLogin === userToAdd.sLogin) {
                users[i] = userToAdd;
                return;
              }
            }
            userToAdd ? users.unshift(userToAdd) : null;

          }, function (err) {
            console.log('Create/Edit User Error ', err)
          });
        });
      };

      $scope.get = function () {
        return users;
      };

      $scope.add = function () {
        openModal(null, null, groups, editModes.CREATE);
      };

      $scope.edit = function (user) {
        user.sName = user.sName || user.FirstName;
        user.sDescription = user.sDescription || user.LastName;

        getGrFunc(user.sLogin).then(function (data) {
          groupsToUser = data;
        }).finally(function () {
          openModal(user, groupsToUser, groups, editModes.EDIT);
        });

      };

      $scope.delete = function (user) {
        deleteFunc(user.sLogin).then(fillData);
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
      templateUrl: 'app/users/directives/userList.html'
    }
  });
