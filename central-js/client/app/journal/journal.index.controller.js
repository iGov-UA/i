angular.module('journal').controller('JournalController', function ($rootScope, $scope, $location, $state, $window,
                                                                    ErrorsFactory, ServiceService, BankIDLogin) {

  function getRedirectURI() {
    var stateForRedirect = $state.href('index.journal', {error: ''});
    return $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
  }

  activate();
//https://github.com/e-government-ua/i/issues/1422
  function activate() {
    if(BankIDLogin){
      $state.go('.content');
    }
  }

  var bExist = function (oValue) {
    return oValue && oValue !== null && oValue !== undefined && !!oValue;
  };

  var bExistNotSpace = function (oValue) {
    return bExist(oValue) && oValue.trim() !== "";
  };

  $scope.getRedirectUrl = getRedirectURI;
  $scope.sSearch = '';

  $scope.getAuthMethods = function () {
    if($rootScope.profile.isKyivCity){
      return "BankID,EDS,BankID-NBU"
    }
    return "BankID,EDS,mpbds,KK,BankID-NBU"
  };
  $scope.searchOrder = function (sID_Order_New, sToken_New) {//arguments.callee.toString()
    var oFuncNote = {sHead: "Пошук заявки", sFunc: "searchOrder"};
    var sID_Order = bExist(sID_Order_New) ? sID_Order_New : $scope.sID_Order;
    var sToken = bExist(sToken_New) ? sToken_New : $scope.sToken;
    ErrorsFactory.init(oFuncNote, {
      asParam: ['sID_Order: ' + sID_Order, 'sToken: ' + sToken],
      sNote: 'Формат заявки повинен бути лише із цифр та тире: X-XXXXX (де X-цифра), наприклад: 0-123456789'
    });
    if (bExistNotSpace(sID_Order)) {
      if (sID_Order.indexOf("-") < 0) {
        if (!/^\d+$/.test(sID_Order)) {
          ErrorsFactory.logWarn({sBody: 'Не вірний формат із символами!")'});
        } else {
          ErrorsFactory.logWarn({
            sBody: 'Старий формат!',
            sNote: 'Необхідно перед номером доповнити префікс "0-". (тобто "0-' + sID_Order + '", замість "' + sID_Order + '")'
          });
          $scope.searchOrder("0-" + sID_Order, sToken);
        }
        return null;
      }
      ServiceService.searchOrder(sID_Order, sToken)
        .then(function (oResponse) {
          if (ErrorsFactory.bSuccessResponse(oResponse, function (oThis, doMerge, sMessage, aCode, sResponse) {
              if (!sMessage) {
                doMerge(oThis, {sType: "warning"});
              } else if (sMessage.indexOf(['CRC Error']) > -1) {
                doMerge(oThis, {sType: "warning", sBody: 'Невірний номер заявки по контрольній суммі!'});
              } else if (sMessage.indexOf(['sID_Order has incorrect format!']) > -1) {
                doMerge(oThis, {sType: "warning", sBody: 'Невірний формат заявки!'});
              } else if (sMessage.indexOf(['not found']) > -1) {
                doMerge(oThis, {sType: "warning", sBody: 'Заявку не знайдено!'});
              }
            })) {
              $state.go('index.search', {sID_Order: sID_Order, sToken: sToken});
          }
        }, function (sError) {
          ErrorsFactory.logFail({
            sBody: 'Невідома помилка сервісу!',
            sError: sError,
            asParam: ['$scope.oOrder: ' + $scope.oOrder]
          });
        });
    } else {
      ErrorsFactory.logInfo({sBody: 'Не задані параметри!'});
    }
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
