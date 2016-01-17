angular.module('auth').config(function ($stateProvider, statesRepositoryProvider) {
  statesRepositoryProvider.init(window.location.host);
  $stateProvider
    .state('index.auth', {
      url: 'auth'
    })
    .state('index.auth.email', {
      url: '/email',
      parent: 'index.auth'
    })
    .state('index.auth.email.verify', {
      url: '/verify',
      parent: 'index.auth.email',
      views: {
        'main@': {
          templateUrl: 'app/auth/email/verify.html',
          controller: 'AuthByEmailVerifyController'
        }
      }
    })
    .state('index.auth.email.submit', {
      url: '/submit?email',
      parent: 'index.auth.email',
      views: {
        'main@': {
          templateUrl: 'app/auth/email/submit.html',
          controller: 'AuthByEmailSubmitController'
        }
      }
    });
});

