angular.module('auth').controller('AuthByEmailController', function ($window, $scope, $location, $state, $stateParams, ServiceService, ErrorsFactory) {

  if(!$stateParams.link){
    $state.go('index');
    return;
  }

  $scope.isProcessing = false;

  function startProcessing() {
    $scope.isProcessing = true;
  }

  function stopProcessing() {
    $scope.isProcessing = false;
  }

  $scope.isDisabled = function (form) {
    return form.$invalid || $scope.isProcessing;
  };

  $scope.authByEmail = {
    email: '',
    code: '',
    firstName: '',
    middleName: '',
    lastName: '',
    link: $stateParams.link
  };

  $scope.authorizeByEmail = function (sAuthEmail) {
    var oFuncNote = {sHead: "Авторизація через елетрону адресу", sFunc: "authorizeByEmail"};
    ErrorsFactory.init(oFuncNote, {asParam: ['sAuthEmail: ' + sAuthEmail]});
    startProcessing();
    ServiceService.verifyContactEmail(sAuthEmail).then(function (oResponse) {
      if (ErrorsFactory.bSuccessResponse(oResponse)) {
        if (oResponse.bVerified === "true") {
          $state.go('index.auth.email.submit', sAuthEmail);
        } else {
          ErrorsFactory.addFail({
            sBody: 'Невалідна електронна адреса!',
            asParam: ['soResponse: ' + JSON.stringify(oResponse)]
          });
        }
      }
    }).finally(function () {
      stopProcessing();
    });
  };

  $scope.authorizeByEmailAndCode = function (sAuthEmail) {
    var oFuncNote = {sHead: "Авторизація через елетрону адресу(2)", sFunc: "authorizeByEmail(2)"};
    ErrorsFactory.init(oFuncNote, {asParam: ['sAuthEmail: ' + sAuthEmail]});
    startProcessing();
    ServiceService.verifyContactEmailAndCode(sAuthEmail).then(function (oResponse) {
      if (ErrorsFactory.bSuccessResponse(oResponse)) {
        if (oResponse.verified) {
          if (oResponse.firstName) {
            $scope.authByEmail.firstName = firstName;
          }
          if (oResponse.middleName) {
            $scope.authByEmail.middleName = middleName;
          }
          if (oResponse.lastName) {
            $scope.authByEmail.lastName = lastName;
          }

          $state.go('index.auth.email.enter');
        } else {
          ErrorsFactory.addFail({
            sBody: 'Невалідна електронна адреса!',
            asParam: ['soResponse: ' + JSON.stringify(oResponse)]
          });
        }
      }
    }, function (errResp) {
      alert(errResp.data.message);
    }).finally(function () {
      stopProcessing();
    });
  };

  $scope.editNamesInEmailAuth = function (sAuthEmail) {
    startProcessing();
    ServiceService.editNamesInEmailAuth(sAuthEmail).then(function (result) {
      $window.location.href = './auth/email';
    }).finally(function () {
      stopProcessing();
    });
  };

  $scope.editEmail = function () {
    $state.go('index.auth.email.verify');
  };

  $state.go('index.auth.email.verify');
});
