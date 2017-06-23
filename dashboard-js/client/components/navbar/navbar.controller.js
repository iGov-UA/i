(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('NavbarCtrl', navbarCtrl);

  navbarCtrl.$inject = ['$scope', '$rootScope', '$location', 'Auth', 'envConfigService', 'iGovNavbarHelper', 'tasksSearchService',
    '$state', 'tasks', 'lunaService', 'Modal', '$stateParams', 'processes', '$localStorage', 'signDialog'];
  function navbarCtrl($scope, $rootScope, $location, Auth, envConfigService, iGovNavbarHelper, tasksSearchService,
                      $state, tasks, lunaService, Modal, $stateParams, processes, $localStorage, signDialog) {
    $scope.menu = [{
      'title': 'Задачі',
      'link': '/tasks'
    }];

    $scope.navBarIsCollapsed = true;
    $scope.openCloseMenu = function () {
      $scope.navBarIsCollapsed = !$scope.navBarIsCollapsed;
    };

    envConfigService.loadConfig(function (config) {
      iGovNavbarHelper.isTest = config.bTest;
      $rootScope.config = config;
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

    var bSelectedTasksSortReverse = false;

    $scope.$on('set-sort-order-reverse-true', function () {
      bSelectedTasksSortReverse = true;
    });

    $scope.$on('set-sort-order-reverse-false', function () {
      bSelectedTasksSortReverse = false;
    });

    //$scope.tasksSearch = iGovNavbarHelper.tasksSearch;
    var tempCountValue = 0;

    $scope.searchInputKeyup = function ($event) {
      if ($event.keyCode === 13 && $rootScope.tasksSearch.value) {
        runSearchingProcess();
      }
      if($event.keyCode === 8 || $event.keyCode === 46) {
        $scope.switchArchive = false;
      }
    };

    // запуск поиска для автотестов
    $rootScope.runSearchingProcess = function () {
      console.log('Start searching process');
      $rootScope.tasksSearch.value = $('.searched-text')["0"].value;
      console.log('$rootScope.tasksSearch.value = ' + $rootScope.tasksSearch.value);
      if ($rootScope.tasksSearch.value) {
        runSearchingProcess();
      }
      console.log('End searching process');
    };

    function runSearchingProcess() {
      $rootScope.tasksSearch.loading=true;
      $rootScope.tasksSearch.count=0;
      $rootScope.tasksSearch.submited=true;
      if($rootScope.tasksSearch.archive) {
        tasks.getProcesses($rootScope.tasksSearch.value).then(function (res) {
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
        tasksSearchService.searchTaskByUserInput($rootScope.tasksSearch.value, $scope.iGovNavbarHelper.currentTab, bSelectedTasksSortReverse)
          .then(function(res) {
            if(res.aIDs.length > 1){
              if(bSelectedTasksSortReverse){
                tempCountValue = (res.aIDs.length - res.nCurrentIndex) + ' / ' + res.aIDs.length;
              } else {
                tempCountValue = (res.nCurrentIndex + 1) + ' / ' + res.aIDs.length;
              }
              $rootScope.tasksSearch.count = '... / ' + res.aIDs.length;
            } else {
              tempCountValue = res.aIDs.length;
              $rootScope.tasksSearch.count = res.aIDs.length;
            }
          })
          .finally(function(res) {
            $rootScope.tasksSearch.loading=false;
          });
      }
    }

    $scope.$on('update-search-counter', function () {
      $rootScope.tasksSearch.count = tempCountValue;
    });

    $scope.closeArchive = function () {
      $scope.switchArchive = false;
    };

    $scope.archiveTextValue = function () {
      return isNaN($rootScope.tasksSearch.value);
    };

    $scope.isSelectedInstrumentsMenu = function(menuItem) {
      return menuItem.state==$state.current.name;
    };

    $scope.assignTask = function (id) {

      tasks.assignTask(id, Auth.getCurrentUser().id)
        .then(function (result) {
          /*Modal.assignDocument(function (event) {

          }, 'Документ успiшно створено');*/
        })
        .catch(function (e) {
          /*Modal.assignDocument(function (event) {

          }, 'Документ успiшно створено');*/
        });
    };

    $scope.showOrHideSelect = {show:false,type:''};
    $scope.hasDocuments = function () {
      var user = Auth.getCurrentUser().id;
      if(user) {
        if($rootScope.sUserOnTab){
          if($rootScope.sUserOnTab === user){
            // skip doing request
          } else {
            fillHasDocuments(user);
          }
        } else {
          fillHasDocuments(user);
        }
      }
    };
    function fillHasDocuments(user) {
      $rootScope.sUserOnTab = user;
      tasks.isUserHasDocuments(user).then(function (res) {
        $rootScope.usersDocumentsBPs = [];
        $rootScope.userTasksBPs = [];
        if(Array.isArray(res) && res.length > 0) {
          $rootScope.usersDocumentsBPs = res.filter(function (item) {
            return item.oSubjectRightBP.sID_BP.charAt(0) === '_' && item.oSubjectRightBP.sID_BP.split('_')[1] === 'doc';
          });
          $rootScope.userTasksBPs = res.filter(function (item) {
            return item.oSubjectRightBP.sID_BP.indexOf('_doc_') !== 0;
          })
        }
      })
    }
    $scope.hasDocuments();

    $scope.document = {};
    $scope.openCloseUsersSelect = function (type) {
      $scope.showOrHideSelect.type = type;
      $scope.showOrHideSelect.show = !$scope.showOrHideSelect.show;
    };

    $scope.showCreateDocButton = function () {
      return $stateParams.type === "unassigned" || $stateParams.type === "selfAssigned" || $stateParams.type === 'documents';
    };

    $scope.hideNaviWhenLoginPage = function () {
      return $location.path() === '/';
    };

    $scope.onSelectDocument = function (item) {
      tasks.createNewDocument(item.oSubjectRightBP.sID_BP).then(function (res) {
        if(res.snID_Process) {
          tempCountValue = 0;
          var val = res.snID_Process + lunaService.getLunaValue(res.snID_Process);
          tasksSearchService.searchTaskByUserInput(val, 'documents')
            .then(function(res) {
              $scope.assignTask(res.aIDs[0], val)
            });
          $scope.showOrHideSelect.show = false;
        }
      });
    };

    $scope.onSelectTask = function (task) {
      tasks.createNewTask(task.oSubjectRightBP.sID_BP).then(function (res) {
        localStorage.setItem('creating', JSON.stringify(res.data[0]));
        $state.go('tasks.typeof.create', {id:res.data[0].deploymentId});
        $scope.showOrHideSelect.show = false;
      })
    };

    function setEcpStatusToLS(status) {
      var stringifyStatus = JSON.stringify(status);
      localStorage.setItem('auto-ecp-status', stringifyStatus);
    }

    var ecpStatusInLS = localStorage.getItem('auto-ecp-status');

    if(ecpStatusInLS !== null) {
      $rootScope.checkboxForAutoECP = JSON.parse(ecpStatusInLS);
    }else {
      $rootScope.checkboxForAutoECP = {status : true};
      setEcpStatusToLS($rootScope.checkboxForAutoECP);
    }

    $scope.$watch('checkboxForAutoECP.status', function (newVal) {
      var savedStatus = localStorage.getItem('auto-ecp-status');
      var res = savedStatus !== null ? JSON.parse(savedStatus) : null;
      if(res && newVal !== undefined && res.status !== newVal) {
        setEcpStatusToLS($rootScope.checkboxForAutoECP);
      }
    });

    $scope.showSignDialog = function () {
      signDialog.signManuallySelectedFile(function (signedContent) {
        console.log('PDF Content:' + signedContent.content);
      }, function () {
        console.log('Sign Dismissed');
      })
    }

  }
})();
