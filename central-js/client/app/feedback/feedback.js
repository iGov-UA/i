angular.module('feedback').config(function ($stateProvider, statesRepositoryProvider) {
  statesRepositoryProvider.init(window.location.host);
  $stateProvider
    .state('index.feedback', {
      url: 'feedback',
      views: {
        'main@': {
          templateUrl: 'app/feedback/index.html',
          controller: 'FeedbackController'
        }
      }
    });
});
