(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .directive('todo', todoDirective);

  todoDirective.$inject = [];
  function todoDirective() {
    var directive = {
      replace: true,
      scope: {
        iGovTitle: '=',
      },
      templateUrl: 'components/todo/todo.html',
    };
    return directive;
  }

})();
