'use strict';

angular.module('dashboardJsApp').directive('certificateInfo', ['$http', 'tasks', 'Auth', '$rootScope', '$timeout', 'DocumentsService', 'Modal', 'CurrentServer',
  function($http, tasks, Auth, $rootScope, $timeout, DocumentsService, Modal, CurrentServer) {
    return {
      restrict: 'EA',
      templateUrl: 'app/share/certificateInfo.html',
      link: function (scope, element, attrs) {

      }
    };
  }]);
