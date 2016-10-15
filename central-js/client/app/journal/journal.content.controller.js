angular.module('journal').controller('JournalContentController', function($rootScope, $scope, $state, ServiceService, ErrorsFactory, JournalHelperService, journal) {

  var sID_Order_RegExp = /^\d-\d+$/;
  var oFuncNote = {sHead: "Пошук заявки", sFunc: "searchOrder"};

  angular.forEach(journal, function (oJournalItem) {
    try {
      oJournalItem.sDate = new Date(oJournalItem.sDate.replace(' ', 'T'));
    } catch (error) {}
  });

  $scope.getOrderStatusString = JournalHelperService.getOrderStatusString;
  $scope.getSearchHref = function (sKey, sValue) {
    var oParams = {};
    oParams[sKey] = sValue;
    return $state.href('index.search', oParams);
  };
  $scope.searchOrder = function () {
    ErrorsFactory.init(oFuncNote, {
      asParam: ['sID_Order: ' + $scope.sSearch],
      sNote: 'Формат заявки повинен бути лише із цифр та тире: X-XXXXX (де X-цифра), наприклад: 0-123456789'
    });
    ServiceService.searchOrder($scope.sSearch)
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
          if (ErrorsFactory.bSuccess(oFuncNote)) {
            $state.go('index.search', {sID_Order: oResponse.sID_Order});
          }
        }
      }, function (sError) {
        ErrorsFactory.logFail({
          sBody: 'Невідома помилка сервісу!',
          sError: sError,
          asParam: ['$scope.oOrder: ' + $scope.oOrder]
        });
      });
  };
  $scope.journal = journal;

});
