(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('NavbarCtrl', navbarCtrl);

  navbarCtrl.$inject = ['$scope', '$location', 'Auth', 'envConfigService', 'iGovNavbarHelper', 'tasksSearchService',
                        '$state', 'tasks', 'lunaService', 'Modal', '$stateParams'];
  function navbarCtrl($scope, $location, Auth, envConfigService, iGovNavbarHelper, tasksSearchService,
                      $state, tasks, lunaService, Modal, $stateParams) {
    $scope.menu = [{
      'title': 'Задачі',
      'link': '/tasks'
    }];

    envConfigService.loadConfig(function (config) {
      iGovNavbarHelper.isTest = config.bTest;
    });

    $scope.isAdmin = Auth.isAdmin;
    $scope.areInstrumentsVisible = false;
    $scope.iGovNavbarHelper = iGovNavbarHelper;

    $scope.isVisible = function(menuType){
      //$scope.menus = [{
      if(menuType === 'all'){
        return Auth.isLoggedIn() && Auth.hasOneOfRoles('manager', 'admin', 'kermit');
      }
      if(menuType === 'finished'){
        return Auth.isLoggedIn() && Auth.hasOneOfRoles('manager', 'admin', 'kermit', 'supervisor');
      }
      return Auth.isLoggedIn();
    };

    $scope.isVisibleInstrument = function(menuType){
      if(menuType === 'tools.users'){
        return Auth.isLoggedIn() && Auth.hasOneOfRoles('superadmin');
      }
      if(menuType === 'tools.groups'){
        return Auth.isLoggedIn() && Auth.hasOneOfRoles('superadmin');
      }
      return Auth.isLoggedIn();
    };

    $scope.getCurrentUserName = function() {
      var user = Auth.getCurrentUser();
      return user.firstName + ' ' + user.lastName;
    };

    $scope.goToServices = function() {
      $location.path('/services');
    };

    $scope.goToEscalations = function() {
      $location.path('/escalations');
    };

    $scope.goToReports = function () {
      $location.path('/reports');
    };

    $scope.logout = function() {
      Auth.logout();
      $location.path('/login');
    };

    $scope.isActive = function(route) {
      return route === $location.path();
    };

    $scope.goToUsers = function () {
      $location.path('/users');
    };

    $scope.goToGroups = function () {
      $location.path('/groups');
    };

    $scope.goToDeploy = function () {
      $location.path('/deploy');
    };

    $scope.goToProfile = function () {
      $location.path('/profile');
    };

    $scope.tasksSearch = iGovNavbarHelper.tasksSearch;

    $scope.searchInputKeyup = function ($event) {
      if ($event.keyCode === 13 && $scope.tasksSearch.value) {
        $scope.tasksSearch.loading=true;
        $scope.tasksSearch.count=0;
        $scope.tasksSearch.submited=true;
        if($scope.tasksSearch.archive) {
          tasks.getProcesses($scope.tasksSearch.value).then(function (res) {
            var response = JSON.parse(res);
            $scope.archive = response[0];
            $scope.archive.aVisibleAttributes = [];
            angular.forEach($scope.archive.aAttribute, function (oAttribute) {
              if (oAttribute.oAttributeName.nOrder !== -1){
                $scope.archive.aVisibleAttributes.push(oAttribute);
              }
            });
            $scope.archive.aVisibleAttributes.sort(function (a, b) {
              return a.oAttributeName.nOrder - b.oAttributeName.nOrder;
            });
            $scope.switchArchive = true;
          })
        } else {
          tasksSearchService.searchTaskByUserInput($scope.tasksSearch.value)
            .then(function(res) {
              if(res.aIDs.length > 1){
                $scope.tasksSearch.count = (res.nCurrentIndex + 1) + ' / ' + res.aIDs.length;
              } else {
                $scope.tasksSearch.count = res.aIDs.length;
              }
            })
            .finally(function(res) {
              $scope.tasksSearch.loading=false;
           });
        }
      }
      if($event.keyCode === 8 || $event.keyCode === 46) {
        $scope.switchArchive = false;
      }
    };

    $scope.closeArchive = function () {
      $scope.switchArchive = false;
    };

    $scope.archiveTextValue = function () {
      return isNaN($scope.tasksSearch.value);
    };

    $scope.isSelectedInstrumentsMenu = function(menuItem) {
      return menuItem.state==$state.current.name;
    };

    $scope.assignTask = function (id) {

      tasks.assignTask(id, Auth.getCurrentUser().id)
        .then(function (result) {
          Modal.assignDocument(function (event) {
            $state.go('tasks.typeof.view', {type:'selfAssigned'});
          }, 'Документ успiшно створено');
        })
        .catch(function (e) {
          Modal.assignDocument(function (event) {

          }, 'Документ успiшно створено');
        });
    };

    $scope.usersDocumentsBPs = [];
    $scope.showOrHideSelect = false;
    $scope.hasDocuments = function () {
      var user = Auth.getCurrentUser().id;
      if(user) {
        tasks.isUserHasDocuments(user).then(function (res) {
          if(Array.isArray(res) && res.length > 0) {
            $scope.usersDocumentsBPs = res.filter(function (item) {
              return item.sID.charAt(0) === '_' && item.sID.split('_')[1] === 'doc';
            })
          }
        })
      }
    };
    $scope.hasDocuments();

    $scope.document = {};
    $scope.openCloseUsersSelect = function () {
      $scope.showOrHideSelect = !$scope.showOrHideSelect;
    };

    $scope.showCreateDocButton = function () {
      return $stateParams.type === "unassigned" || $stateParams.type === "selfAssigned";
    };

    $scope.onSelectDocList = function (item) {
      tasks.createNewDocument(item.sID).then(function (res) {
        if(res.snID_Process) {
          var val = res.snID_Process + lunaService.getLunaValue(res.snID_Process);
          tasksSearchService.searchTaskByUserInput(val)
            .then(function(res) {
              $scope.assignTask(res.aIDs[0]);
            });
          $scope.showOrHideSelect = false;
        }
      });
    };
  }
})();
