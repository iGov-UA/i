angular.module('dashboardJsApp')
  .filter('fixDate', function () {
    return function (value) {
      return  value.split('.')[0];
    }
  })
  .filter('checkDate', function () {
    return function (value) {
      var split = value.split('.');
      if(split[0].length === 1 && split[1].length === 1) return 0 + split[0] + '/' + 0 + split[1] + '/' + split[2];
      else if(split[0].length === 2 && split[1].length === 1) return split[0] + '/' + 0 + split[1] + '/' + split[2];
      else if(split[0].length === 1 && split[1].length === 2) return 0 + split[0] + '/' + split[1] + '/' + split[2];
      else if(split[0].length === 2 && split[1].length === 2) return value;
      else if(split.length !== 3) return value;
    }
  });
