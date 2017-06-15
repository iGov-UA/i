'use strict';

angular.module('dashboardJsApp').directive('delegateButton', ['$http', 'tasks', 'Auth', '$rootScope', '$timeout', 'DocumentsService',
  function($http, tasks, Auth, $rootScope, $timeout, DocumentsService) {
  return {
    restrict: 'EA',
    templateUrl: 'components/delegateButton/delegate.html',
    link: function (scope, element, attrs, ngModel) {
      scope.delegated = {
        selected : null
      };
      scope.sKey_Group = null;

      var queryParams = {params:{}};
      var login = Auth.getCurrentUser();

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

      var getData = function () {

        for( var j=0; j<scope.documentLogins.length; j++ ) {
          var users = scope.documentLogins[j].aUser;
          for( var l=0; l<users.length; l++ ){
            if(users[l].sLogin === login.id) {
              queryParams.params.sID_Group_Activiti = scope.sKey_Group = users[l].sID_Group;
              queryParams.params.sSubjectType = 'Human';
              queryParams.params.nDeepLevel = 0;
              break;
            }
          }
        }

        $http.get('./api/subject-role', queryParams).then(function (res) {
          if(typeof res.data === 'object') {
            var response = subjectUserFilter(res.data.aSubjectGroupTree);
              angular.forEach(response, function (user) {
                user.sName = user.sFirstName + " " + user.sLastName;
              })
          }
          scope.delegateUsersList = response;
        });
      };
      getData();

      scope.onSelectUser = function (user) {
        $rootScope.delegateSelectMenu = false;

        var params = {
          snID_Process_Activiti : scope.taskData.oProcess.nID,
          sKey_Group : scope.sKey_Group,
          sKey_Group_Delegate : user.sLogin,
          sKey_Step : scope.sKey_Step
        };

        for( var i=0; i<scope.taskForm.length; i++ ) {
          if(scope.taskForm[i].id.indexOf('sKey_Step') === 0) {
            params.sKey_Step = scope.taskForm[i].value;
            break;
          }
        }

        DocumentsService.delegateDocToUser(params).then(function () {
          $timeout(function(){
            scope.$apply();
            Modal.inform.success()('Документ успішно делеговано');
            $rootScope.$broadcast("refresh-task-view-after-delegate");
          });
        });

      };

    }
  };
}]);
