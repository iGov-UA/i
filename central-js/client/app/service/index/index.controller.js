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

  $scope.isLoggedInUpdater = $interval(function() {
    UserService.isLoggedIn().then(function (result) {
      $scope.navBarStatusVisible = result;
    }, function () {
      $scope.navBarStatusVisible = false;
    });
  }, 1000*60);        // 60 sec

  $scope.$on("$destroy", function() {
    if ($scope.isLoggedInUpdater) {
      $interval.cancel($scope.isLoggedInUpdater);
    }
  });

  function capitalize(string) {
    return string !== null && string !== undefined ? string.charAt(0).toUpperCase() + string.slice(1).toLowerCase() : '';
  }

  $scope.logout = function () {
    UserService.logout();
    $scope.navBarStatusVisible = false;
    if ($scope.isLoggedInUpdater) {
      $interval.cancel($scope.isLoggedInUpdater);
    }
  };
});
