angular.module('dashboardJsApp')
  .filter('fixDate', function () {
    return function (value) {
      return  value.split('.')[0];
    }
  });
