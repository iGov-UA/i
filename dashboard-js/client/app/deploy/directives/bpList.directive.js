/**
 * Created by GFalcon on 08.06.2016.
 */
angular.module('dashboardJsApp')
  .directive('bpList', function () {
    var controller = function ($scope, $modal) {
      var inProgress = false;
      var aBPs = [];
      var getFunc = $scope.funcs.getListBP;
      var deleteFunc = $scope.funcs.removeListBP;

      var fillData = function () {
        inProgress = true;
        getFunc()
          .then(function (list) {
            aBPs = list;
          })
          .finally(function () {
            inProgress = false;
          });
      };
/*
      var openModal = function (user) {
        var modalInstance = $modal.open({
          animation: true,
          templateUrl: 'app/users/modal/modal.html',
          controller: 'UserModalController',
          resolve: {
            userToEdit: function () {
              return angular.copy(user);
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

              for (var i = 0; i < users.length; i++) {
                if (users[i].sLogin === userToAdd.sLogin) {
                  users[i] = userToAdd;
                  return;
                }
              }
              console.log('!!!!!  userToAdd', userToAdd);
              console.log(users[20]);

              userToAdd ? users.unshift(userToAdd) : null;

            }, function (err) {
              console.log('Create/Edit User Error ', err)
            });
        });
      };
*/
      $scope.get = function () {
        return aBPs;
      };

      $scope.add = function () {
        openModal();
      };

      $scope.edit = function (user) {
        user.sName = user.sName || user.FirstName;
        user.sDescription = user.sDescription || user.LastName;
        openModal(user);
      };

      $scope.delete = function (user) {
        deleteFunc(user.sLogin).then(fillData);
      };

      $scope.init = function () {
        fillData();
        debugger;
      };
    };
    return {
      restrict: 'EA',
      scope: {
        funcs: '='
      },
      controller: controller,
      templateUrl: 'app/deploy/directives/bpList.html'
    }
  });
