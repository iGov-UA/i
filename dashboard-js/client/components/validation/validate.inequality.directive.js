(function() {
  'use strict';

  angular
    .module('dashboardJsApp')
    .directive('validateInequality', validateInequalityDirective);

  function validateInequalityDirective() {

    return {
      restrict: 'A',
      require: 'ngModel',
      link: linkFunction
    };

    function linkFunction(scope, elm, attrs, ctrl) {
      function validateInequality(otherValue) {
        if (ctrl.$modelValue === otherValue) {
          ctrl.$setValidity('unequal', false);
          return false;
        } else {
          ctrl.$setValidity('unequal', true);
          return true;
        }
      }

      scope.$watch(attrs.validateInequality, function (otherModelValue) {
        validateInequality(otherModelValue);
      });

      scope.$watch(function(){
        return ctrl.$modelValue;
      },function(){
        return validateInequality(scope.$eval(attrs.validateInequality));
      });
    }
  }
})();

