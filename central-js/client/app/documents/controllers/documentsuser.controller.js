angular.module('documents').controller('DocumentsUserController', function($scope, $state, $location, $window, BankIDService, ErrorsFactory) {

  $scope.authProcess = false;

  if ($state.params.error) {
    var errorText;
    try {
      errorText = JSON.parse($state.params.error).error;
    } catch (error) {
      errorText = $state.params.error;
    }

    ErrorsFactory.push({
      type: "danger",
      text:  errorText
    });
  }

  BankIDService.isLoggedIn().then(function() {
    $scope.loading = true;
    return $state.go('index.documents.content').finally(function() {
      $scope.loading = false;
    });
  }).catch(function() {
    return $state.go('index.documents.bankid');
  });

});
