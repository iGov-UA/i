angular.module('journal').controller('JournalContentController', function($rootScope, $scope, $state, ServiceService, ErrorsFactory, JournalHelperService, journal) {

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

  $scope.journal = journal;

});
