angular.module('dashboardJsApp').directive('dateDivider', [function() {
    return {
      restrict: 'A',
      link: function(scope, elem, attr) {
        var dates = {
          '1': 'січ',
          '2': 'лют',
          '3': 'бер',
          '4': 'квіт',
          '5': 'трав',
          '6': 'черв',
          '7':'лип',
          '8':'серп',
          '9': 'вер',
          '10': 'жовт',
          '11': 'лист',
          '12': 'груд'
        };

        var prev = scope.$index - 1,
            prevMonth = scope.tasks[prev] ? new Date(scope.tasks[prev].createTime).getMonth() + 1 : null,
            prevDay = scope.tasks[prev] ? new Date(scope.tasks[prev].createTime).getDate() : null;

        var current = scope.$index,
            currentMonth = new Date(scope.tasks[current].createTime).getMonth() + 1,
            currentDay = new Date(scope.tasks[current].createTime).getDate(),
            currentYear = new Date(scope.tasks[current].createTime).getFullYear();

        if( prevDay !== currentDay ){
          setTimeout(function () {
            var x = angular.element('<div style="font-size: 13px; position:relative; display: block;text-align:right;margin-top: 20px;padding-right: 20px;">до '+ currentDay + " " + dates[currentMonth]  + ' ' + currentYear + '</div>').slideDown("slow");
            angular.element(elem).before(x);
          }, 1000)
      }}
    };
  }
]);
