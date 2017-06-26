angular.module('dashboardJsApp').directive('issueForRead', ['Issue', '$http', '$sce', function (Issue, $http, $sce) {
  return {
    restrict: 'EA',
    templateUrl: 'components/issueBlock/issueForRead.template.html',
    link: function (scope) {
      scope.html = function (text) {
        return $sce.trustAsHtml(text);
      };
      scope.getIssueType = function (type) {
        switch (type) {
          case 'fileHTML':
            return 'Документ';
          case 'textArea':
            return 'Текстове повiдомлення';
          case 'file':
            return 'Файл';
        }
      };
      scope.convertDate = function (i) {
        var date = i.split(' ')[0];
        var splittedDate = date.split('-');
        return splittedDate[2] + '.' + splittedDate[1] + '.' + splittedDate[0];
      };
    }
  }
}]);
