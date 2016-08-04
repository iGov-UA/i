angular.module('journal').config(function ($stateProvider, statesRepositoryProvider) {
  statesRepositoryProvider.init(window.location.host);
//  if (statesRepositoryProvider.isCentral()) {
    $stateProvider
      .state('index.journal', {
        url: 'journal?error',
        views: {
          'main@': {
            templateUrl: 'app/journal/journal.index.html',
            controller: 'JournalController'
          }
        },
        resolve: {
          BankIDLogin: function ($q, $state, $location, $stateParams, UserService) {
             return UserService.isLoggedIn()
               .catch(function () {
                 return false;
               });
          },
          title: function (TitleChangeService) {
            TitleChangeService.defaultTitle();
          }
        },
        onEnter: function ($state, $q, BankIDLogin) {
          $q.when($state.transition).then(function (state) {
            if(BankIDLogin && state.name === 'index.journal'){
              $state.go('.content');
            }
          });
        }
      })
      .state('index.journal.content', {
        parent: 'index.journal',
        resolve: {
          journal: function ($q, $state, ServiceService) {
            return ServiceService.getJournalEvents();
          },
          title: function (TitleChangeService) {
            TitleChangeService.defaultTitle();
          }
        },
        views: {
          'main@': {
            templateUrl: 'app/journal/journal.content.html',
            controller: 'JournalContentController'
          }
        }
      })
      .state('index.search', {
        url: 'search?sID_Order&nID&sToken',
        //parent: 'index.journal',
        resolve: {
          order: function($q, $stateParams, ServiceService) {
            if ($stateParams.nID) {
              //This is not correct branch,
              //should using specific api in the future.
              //For example: ServiceService.searchDocument(...
              return ServiceService
                .searchOrder($stateParams.nID, $stateParams.sToken)
                .catch(angular.noop);
            }
            else if ($stateParams.sID_Order) {
              return ServiceService
                .searchOrder($stateParams.sID_Order, $stateParams.sToken)
                .catch(angular.noop);
            }
            else {
              return $q.when(null);
            }
          },
          events: function ($q, BankIDLogin, order, ServiceService) {
            if (order && BankIDLogin) {
              return ServiceService.getJournalEvents(order.nID);
            } else {
              return $q.when(null);
            }
          },
          BankIDLogin: function ($q, $state, $location, $stateParams, UserService) {
             return UserService.isLoggedIn()
               .catch(function () {
                 return false;
               });
          }
        },
        views: {
          'main@': {
            templateUrl: 'app/journal/journal.search.html',
            controller: 'JournalSearchController'
          }
        }/*,
        onExit: function ($state, $q, BankIDLogin) {
          $q.when($state.transition).then(function (state) {
            if(BankIDLogin && state.name === 'index.journal'){
              $state.go('.content');
            }
          });
        }*/
      });
//  }
});
