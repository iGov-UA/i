'use strict';

angular.module('dashboardJsApp')
  .directive('validateEquality', function () {
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function (scope, elm, attrs, ctrl) {

        function validateEquality(otherValue) {
          if (ctrl.$modelValue === otherValue) {
            ctrl.$setValidity('equal', true);
            return ctrl.$modelValue;
          } else {
            ctrl.$setValidity('equal', false);
            return undefined;
          }
        }

        scope.$watch(attrs.validateEquality, function (otherModelValue) {
          validateEquality(otherModelValue);
        });

        scope.$watch(function(){
          return ctrl.$modelValue;
        },function(){
          return validateEquality(scope.$eval(attrs.validateEquality));
        });
      }
    };
  });

