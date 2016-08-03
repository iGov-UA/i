angular.module('app').controller('IndexController', function ($scope, $interval, UserService) {
  // See why it's needed for navbar:
  // http://stackoverflow.com/questions/14741988/twitter-bootstrap-navbar-with-angular-js-collapse-not-functioning
  $scope.navBarIsCollapsed = true;
  $scope.navBarStatusVisible = false;

  UserService.isLoggedIn().then(function (result) {
    $scope.navBarStatusVisible = result;
    if (result) {
      UserService.fio().then(function (res) {
        $scope.userName = capitalize(res.firstName)
          + " " +
          capitalize(res.middleName)
          + " " +
          capitalize(res.lastName);
      });
    }
  }, function () {
    $scope.navBarStatusVisible = false;
  });

  $scope.$on('userService:loggedIn', function(event) {
    if($scope.navBarStatusVisible !== true) {
      $scope.navBarStatusVisible = true;
    }
  });
  $scope.$on('userService:loggedOut', function(event) {
    if($scope.navBarStatusVisible !== false) {
      $scope.navBarStatusVisible = false;
    }
  });

  function capitalize(string) {
    return string !== null && string !== undefined ? string.charAt(0).toUpperCase() + string.slice(1).toLowerCase() : '';
  }

  $scope.logout = function () {
    UserService.logout();
  };
});
