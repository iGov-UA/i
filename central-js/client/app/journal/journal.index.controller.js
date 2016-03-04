angular.module('journal').controller('JournalController', function ($rootScope, $scope, $location, $state, $window, ErrorsFactory) {

  function getRedirectURI() {
    var stateForRedirect = $state.href('index.journal', {error: ''});
    return $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
  }

  $scope.getRedirectUrl = getRedirectURI;

  $scope.getAuthMethods = function () {
    return "BankID,EDS,KK"
  };

  //if ($state.is('index.journal')) {
    if ($state.params.error) {

        var oFuncNote = {sHead:"Журнал", sFunc:"JournalBankIdController"};
        ErrorsFactory.init(oFuncNote, {asParam:['$state.params.error: '+$state.params.error]});

        var sErrorText = $state.params.error;
        try {
          sErrorText = JSON.parse($state.params.error).error;
          ErrorsFactory.addFail({sBody:'Помилка контролера!',asParam:['sErrorText: '+sErrorText]});
        } catch (sError) {
          ErrorsFactory.addFail({sBody:'Помилка парсінгу помилки контролера!',sError:sError});
        }

        /*var errorText;
        try {
          errorText = JSON.parse($state.params.error).error;
        } catch (error) {
          errorText = $state.params.error;
        }

        ErrorsFactory.push({
          type: "danger",
          text:  errorText
        });*/

        //$state.go('index.journal', {});
    //}
  }
});
