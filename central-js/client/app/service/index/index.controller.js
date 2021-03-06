angular.module('app').controller('IndexController', function ($scope, $rootScope, UserService, FeedbackService) {
  // See why it's needed for navbar:
  // http://stackoverflow.com/questions/14741988/twitter-bootstrap-navbar-with-angular-js-collapse-not-functioning
  $scope.navBarIsCollapsed = true;
  $scope.navBarStatusVisible = false;
  $scope.logout = function () {
    UserService.logout();
    $scope.navBarStatusVisible = false;
    $rootScope.$broadcast('logout.event', {
      isLogged: false
    });
  };

  $scope.$on('event.logout.without.session', function () {
    $scope.logout();
  });

  UserService.isLoggedIn().then(function (result) {
    $scope.navBarStatusVisible = result;
    UserService.fio().then(function (res) {
      $scope.userName = capitalize(res.firstName)
        + " " +
        capitalize(res.middleName)
        + " " +
        capitalize(res.lastName);
    });
  }, function () {
    $scope.navBarStatusVisible = false;
  });

  UserService.isLoggedIn().then(function (result) {
    UserService.account().then(function (res) {
    });
  }, function () {
  });

  function capitalize(string) {
    return string !== null && string !== undefined ? string.charAt(0).toUpperCase() + string.slice(1).toLowerCase() : '';
  }

});
