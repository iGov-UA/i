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
          BankIDLogin: function ($q, $state, $location, $stateParams, BankIDService) {
             return BankIDService.isLoggedIn()
               .catch(function () {
                 return false;
               });
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
      .state('index.journal.search', {
        url: '/{searchKey:order|document}={searchValue:.*}?sToken',
        parent: 'index.journal',
        resolve: {
          order: function($q, $stateParams, ServiceService) {
            switch($stateParams.searchKey) {
              case 'document'://This is not correct branch,
              //should using specific api in the future.
              //For example: ServiceService.searchDocument(...
              case 'order':
                return ServiceService.searchOrder($stateParams.searchValue, $stateParams.sToken);
              default:
                return $q.when(null);
            };
          }
        },
        views: {
          'main@': {
            templateUrl: 'app/journal/journal.content.html',
            controller: 'JournalSearchController'
          }
        }
      })
      .state('index.journal.content', {
        parent: 'index.journal',
        resolve: {
          journal: function ($q, $state, ServiceService) {
            return ServiceService.getJournalEvents();
          }
        },
        views: {
          'main@': {
            templateUrl: 'app/journal/journal.content.html',
            controller: 'JournalContentController'
          }
        }
      });
//  }
});
