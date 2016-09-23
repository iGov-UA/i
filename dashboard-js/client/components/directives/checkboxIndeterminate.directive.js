(function(){
    'use strict';

    angular.module('dashboardJsApp')
        .directive('checkboxIndeterminate', checkboxIndeterminate);

    function checkboxIndeterminate(){
        return {
            restrict: 'A',
            link: function(scope, element, attributes) {
                scope.$watch(attributes['checkboxIndeterminate'], function (value) {
                    element.prop('indeterminate', !!value);
                });
            }
        };
    }
})();