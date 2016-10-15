angular.module('auth').config(function ($stateProvider, statesRepositoryProvider) {
  statesRepositoryProvider.init(window.location.host);
  $stateProvider
    .state('index.auth.email', {
      url: '/email',
      parent: 'index.auth',
      views: {
        'main@': {
          templateUrl: 'app/auth/email/auth.email.html',
          controller: 'AuthByEmailController'
        }
      },
      params: {link: null}
    })
    .state('index.auth.email.verify', {
      parent: 'index.auth.email',
      templateUrl: 'app/auth/email/auth.email.verify.html'
    })
    .state('index.auth.email.submit', {
      parent: 'index.auth.email',
      templateUrl: 'app/auth/email/auth.email.submit.html'
    })
    .state('index.auth.email.enter', {
      parent: 'index.auth.email',
      templateUrl: 'app/auth/email/auth.email.enter.html'
    });
});

