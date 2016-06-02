/**
 * Created by ijmac on 23.05.16.
 */
angular.module('dashboardJsApp')
  .directive('userList', function () {
    var controller = function ($scope, $modal) {
      var inProgress = false;
      var users = [];
      var getFunc = $scope.funcs.getFunc;

      var fillData = function () {
        inProgress = true;
        getFunc()
          .then(function (list) {
            users = list;
          })
          .finally(function () {
            inProgress = false;
          });
      };

      $scope.get = function () {
        return users;
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
