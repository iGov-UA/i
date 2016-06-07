/**
 * Created by ijmac on 23.05.16.
 */
angular.module('dashboardJsApp')
  .directive('userSimpleList', function () {
    var controller = function ($scope) {
      var inProgress = false;
      var initialUsersInGroup = [];
      var initialUsersOutGroup = [];
      var usersAll = [];
      var existingUsers = [];
      var userToAdd = null;
      var userToRemove = null;


      var usersToAdd = $scope.data.usersToAdd;
      var usersToRemove = $scope.data.usersToRemove;
      var currentGroup = $scope.data.group;
      var getFunc = $scope.funcs;

      var fillData = function () {
        inProgress = true;
        if(currentGroup.id){
          getFunc(currentGroup.id)
            .then(function (list) {
              existingUsers = list;
              initialUsersInGroup = list.slice();
              getFunc()
                .then(function (listAll) {
                  usersAll = listAll.filter(function (elemObj) {
                    for (var i = 0; i < existingUsers.length; i++) {
                      if ((elemObj.FirstName === existingUsers[i].FirstName)
                        && (elemObj.LastName === existingUsers[i].LastName)
                        && (elemObj.Email === existingUsers[i].Email)) {
                        return false;
                      }
                    }
                    return true;
                  });
                  initialUsersOutGroup = usersAll.slice();
                });
            })
            .finally(function () {
              inProgress = false;
            });
        }else{
          getFunc()
            .then(function (list) {
              usersAll = list;
              initialUsersOutGroup = list.slice();
            })
            .finally(function () {
              inProgress = false;
            });
        }
      };

      var isUserInInitGroup = function(item){
        return !(initialUsersInGroup.indexOf(item) < 0);
      };

      var addUserToArr = function(usArray, item){
        if(usArray.indexOf(item) < 0){
          usArray.push(item);
        }
      };

      var removeUserFromArr = function(usArray, item){
        var index = usArray.indexOf(item);
        if(index >= 0){
          usArray.splice(index, 1);
        }
      };

      $scope.get = function () {
        return existingUsers;
      };

      $scope.getAll = function () {
        return usersAll;
      };

      $scope.init = function () {
        fillData(currentGroup.id);
        $scope.aClass = "disabled";
        $scope.rClass = "disabled";
      };


      $scope.markUserToAdd = function (user) {
        userToAdd = user;
        $scope.aClass = "";
        $scope.rClass = "disabled";
      };

      $scope.addUser = function () {

        if (userToAdd) {
          var index = usersAll.indexOf(userToAdd);
          var removed = usersAll.splice(index, 1);
          existingUsers.push(removed[0]);

          removeUserFromArr(usersToRemove, userToAdd);
          if(!isUserInInitGroup(userToAdd)){
            addUserToArr(usersToAdd, userToAdd);
          }

        }

        $scope.aClass = "disabled";
        $scope.rClass = "disabled";

        userToAdd = null;
      };

      $scope.removeUser = function () {

        if (userToRemove) {
          var index = existingUsers.indexOf(userToRemove);
          var removed = existingUsers.splice(index, 1);
          usersAll.push(removed[0]);

          removeUserFromArr(usersToAdd, userToRemove);
          if(isUserInInitGroup(userToRemove)){
            addUserToArr(usersToRemove, userToRemove);
          }
        }

        $scope.aClass = "disabled";
        $scope.rClass = "disabled";

        userToRemove = null;
      };

      $scope.markUserToRemove = function (user) {
        userToRemove = user;
        $scope.aClass = "disabled";
        $scope.rClass = "";
      };
    };
    return {
      restrict: 'EA',
      scope: {
        funcs: '=',
        data: '='
      },
      controller: controller,
      templateUrl: 'app/groups/directives/userSimpleList.html'
    }
  });
