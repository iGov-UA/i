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
      scope.convertInitials = function (init) {
        var split = init.split('');
        var result = [split[0]];
        for (var i=0; i<split.length; i++) {
          if(split[i] === ' '){
            result.push(y[i + 1] + '.');
            break;
          }
        }
        return result.join('.');
      }
    }
  }
}]);
