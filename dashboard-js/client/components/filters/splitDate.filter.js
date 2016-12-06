angular.module('dashboardJsApp')
  .filter('fixDate', function () {
    return function (value) {
      return  value.split('.')[0];
    }
  })

  // отображает дату формата d.m.yyyy как dd/mm/yyyy
  .filter('checkDate', function () {
    return function (value) {
      if(value){
        var split = value.split('.');
        if(split[0].length === 1 && split[1].length === 1) return 0 + split[0] + '/' + 0 + split[1] + '/' + split[2];
        else if(split[0].length === 2 && split[1].length === 1) return split[0] + '/' + 0 + split[1] + '/' + split[2];
        else if(split[0].length === 1 && split[1].length === 2) return 0 + split[0] + '/' + split[1] + '/' + split[2];
        else if(split[0].length === 2 && split[1].length === 2) return value;
        else if(split.length !== 3) return value;
      }
    }
  })

  // отображает дату формата yyyy-mm-dd hh:mm:ss как yyyy-mm-dd
  .filter('removeHoursFromDate', function () {
    return function (value) {
      var date = value.split(' ');
      if(date.length !== 2) return value;
      else if(date.length === 2){
        var check = date[0].split('-');
        if(check.length === 3) {
          if(check[0].length === 4 && check[1].length === 2 && check[2].length === 2) return date[0];
        } else {
          return value
        }
      }
    }
  });
