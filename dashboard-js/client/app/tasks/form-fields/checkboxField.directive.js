(function(){
    'use strict';

    angular.module('dashboardJsApp')
        .directive('checkboxField', checkboxField);

    function checkboxField(){
        return {
            restrict: 'E',
            templateUrl: 'app/tasks/form-fields/checkboxField.html'
        };
    }
})();