angular.module('auth').config(function ($stateProvider, statesRepositoryProvider) {
  statesRepositoryProvider.init(window.location.host);
  $stateProvider
    .state('index.auth', {
      url: 'authorization'
    });
});

