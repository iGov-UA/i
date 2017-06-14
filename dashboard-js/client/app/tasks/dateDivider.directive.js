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
            current = scope.$index;

        var p = scope.tasks[prev] ? scope.tasks[prev].createTime.split(/[^0-9]/) : null,
            corrP = p === null ? null : new Date (p[0],p[1]-1,p[2],p[3],p[4],p[5]),
            prevMonth = scope.tasks[prev] ? corrP.getMonth() + 1 : null,
            prevDay = scope.tasks[prev] ? corrP.getDate() : null;

        var c = scope.tasks[current].createTime.split(/[^0-9]/),
            correctC = new Date (c[0],c[1]-1,c[2],c[3],c[4],c[5]),
            currentMonth = correctC.getMonth() + 1,
            currentDay = correctC.getDate(),
            currentYear = correctC.getFullYear();

        if( prevDay !== currentDay ){
          setTimeout(function () {
            var x = angular.element('<div style="font-size: 13px; position:relative; display: block;text-align:right;margin-top: 20px;padding-right: 20px;">вiд '+ currentDay + " " + dates[currentMonth]  + ' ' + currentYear + '</div>').slideDown("slow");
            angular.element(elem).before(x);
          }, 2000)
      }}
    };
  }
]);
