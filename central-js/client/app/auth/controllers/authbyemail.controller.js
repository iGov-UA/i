angular.module('auth')
  .controller(
    'AuthByEmailVerifyController',
    function ($rootScope, $scope, $location, $state, ServiceService, ErrorsFactory) {

      $scope.authorizeByEmail = function (sAuthEmail) {
        var oFuncNote = {sHead: "Авторизація через елетрону адресу", sFunc: "authorizeByEmail"};
        ErrorsFactory.init(oFuncNote, {asParam: ['sAuthEmail: ' + sAuthEmail]});
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
          /*if (oResponse.bVerified == "true") {
           $state.go('index.auth.email.submit', sAuthEmail);
           } else {
           ErrorsFactory.push({
           type: "danger",
           text: "Невалідна електронна адреса"
           });
           }*/
        });
      };

    }).controller(
  'AuthByEmailSubmitController',
  function ($rootScope, $scope, $location, $state, ServiceService, ErrorsFactory, $stateParams) {

    $scope.authByEmail = {
      email: $stateParams.email
    };

    $scope.authorizeByEmailAndCode = function (sAuthEmail) {
      var oFuncNote = {sHead: "Авторизація через елетрону адресу(2)", sFunc: "authorizeByEmail(2)"};
      ErrorsFactory.init(oFuncNote, {asParam: ['sAuthEmail: ' + sAuthEmail]});
      ServiceService.verifyContactEmailAndCode(sAuthEmail).then(function (oResponse) {
        if (ErrorsFactory.bSuccessResponse(oResponse)) {
          if (oResponse.bVerified === "true") {
            console.log("true");
            // TODO
          } else {
            ErrorsFactory.addFail({
              sBody: 'Невалідна електронна адреса!',
              asParam: ['soResponse: ' + JSON.stringify(oResponse)]
            });
          }
        }
      });
    };

    $scope.editEmail = function () {
      $state.go('index.auth.email.validate');
    };
  });
