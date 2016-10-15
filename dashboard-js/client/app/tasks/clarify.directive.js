angular.module('dashboardJsApp').directive('clarifyDirective', function () {
  return {
    restrict : 'E',
    templateUrl : 'app/tasks/clarify.html',
    link : function ($scope) {
      $scope.isChecked = false;
      $scope.clarifyIsChecked = function () {
        $scope.isChecked = false;
        angular.forEach($scope.clarifyFields, function (field) {
          if(field.clarify === true) {
            $scope.isChecked = true;
          }
        })
      };
    }
  }
});
