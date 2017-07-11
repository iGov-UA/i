angular.module('dashboardJsApp').directive('issueBlock', ['Issue', '$http', function (Issue, $http) {
  return {
    restrict: 'EA',
    templateUrl: 'components/issueBlock/issue.template.html',
    link: function (scope) {
      Issue.clearIssues();

      scope.issues = Issue.getIssues();
      scope.selectExe = {params: {sSubjectType: 'Human', sID_Group_Activiti: scope.issue.selectExecutors.activiti, nDeepLevel: scope.issue.selectExecutors.deep}};
      scope.selectCtrl = {params: {sSubjectType: 'Human', sID_Group_Activiti: scope.issue.controllerSelect.activiti, nDeepLevel: scope.issue.controllerSelect.deep}};
      scope.valid = true;

      scope.remove = function (i) {
        Issue.removeIssue(i);
      };

      var subjectUserFilter = function (arr) {
        var allUsers = [], filteredUsers = [], logins = [];

        (function loop(arr) {
          angular.forEach(arr, function(item) {
            if(item.aUser) {
              angular.forEach(item.aUser, function(user) {
                allUsers.push(user);
              })
            }
            if(item.aSubjectGroupChilds && item.aSubjectGroupChilds.length > 0){
              loop(item.aSubjectGroupChilds)
            }
          })
        })(arr);

        for(var i=0; i<allUsers.length; i++) {
          if(logins.indexOf(allUsers[i].sLogin) === -1) {
            filteredUsers.push(allUsers[i]);
            logins.push(allUsers[i].sLogin);
          }
        }
        return filteredUsers;
      };

      function loadSelects() {

        $http.get('./api/subject-role', scope.selectExe).then(function (res) {
          if(typeof res.data === 'object') {
            var response = subjectUserFilter(res.data.aSubjectGroupTree);
            angular.forEach(response, function (user) {
              user.sName = user.sFirstName + " " + user.sLastName;
            })
          }
          scope.executors = response;
        });
        $http.get('./api/subject-role', scope.selectCtrl).then(function (res) {
          if(typeof res.data === 'object') {
            var response = subjectUserFilter(res.data.aSubjectGroupTree);
            angular.forEach(response, function (user) {
              user.sName = user.sFirstName + " " + user.sLastName;
            })
          }
          scope.ctrl = response;
        });
      }loadSelects();

      scope.addNewExecutor = function (issue) {
        scope.valid = Issue.addExecutor(issue);
      };

      scope.removeExecutor = function (issue, index) {
        Issue.removeExecutor(issue, index);
      };

      scope.updateExecutorsSelect = function(contact, index) {
        _.each(scope.issues[index].taskExecutor, function (x) {
          x.isMain = (x.value.sLogin === contact.value.sLogin);
        });
      };
    }
  }
}]);
