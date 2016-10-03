angular.module('app').filter('fixDateFormat', function () {
  return function (date) {
      var onlyDate = date.split('T')[0];
      var splitDate = onlyDate.split('-');
      return splitDate[2] + '/' + splitDate[1] + '/' + splitDate[0]
    }
});
