angular.module('dashboardJsApp').directive('issueForRead', ['Issue', '$http', '$sce', function (Issue, $http, $sce) {
  return {
    restrict: 'EA',
    templateUrl: 'components/issueBlock/issueForRead.template.html',
    link: function (scope) {
      scope.html = function (text) {
        return $sce.trustAsHtml(text);
      }
    }
  }
}]);
