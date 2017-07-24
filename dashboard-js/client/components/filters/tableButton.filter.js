/*
 * в названии аттачей при наличии в name [table][if=ID] оставляет только имя
 */
angular.module('dashboardJsApp')
  .filter('tableButtonFilter', function () {
    return function (value) {
      if(value)
        return value.split('[')[0];
    }
  });
