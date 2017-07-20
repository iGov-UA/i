/*Заглушка для кнопок. например в кнопке несколько наименований которые отображаются в зависимости от ситуации
* если же по какой-то причине ни одно найменование не отработало и кнопка пустая - включается заглушка.
* при добавлении директивы указываем текст заглушки.*/

angular.module('dashboardJsApp').directive('stub', function ($timeout) {
  return {
    restrict: 'A',
    link: function (scope, elem, attrs) {
      $timeout(function() {
        if(!elem.context.children[0]) {
          angular.element(elem).append('<span>' + attrs.stub + '</span>')
        }
      })
    }
  }
});
