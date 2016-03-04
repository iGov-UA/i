angular.module('journal').controller('JournalContentController', function($rootScope, $scope, $state, journal) {
  $scope.journal = [];
  var journalHash = [];
  var journalHashValues = [];
  var notOrderItems = [];
  var sID_OrderRegex = /№(\d-)?(\d+)/;
  angular.forEach(journal, function(item, index) {
    var journal = {};
    item.sDate = new Date(item.sDate.replace(' ', 'T'));
    var aID_Order = item.sMessage.match(sID_OrderRegex);
    if(angular.isArray(aID_Order)) {
      aID_Order[1] = aID_Order[1] || '0-';
      var sID_Order = aID_Order[1] + aID_Order[2];
      var journalHashIndex = journalHash.indexOf(sID_Order);
      if(journalHashIndex === -1){
        journalHash.push(sID_Order);
        journalHashValues.push(item);
      } else if (journalHashValues[journalHashIndex].sDate.getTime() < item.sDate.getTime()) {
        journalHashValues[journalHashIndex] = item;
      }
      item.groupped = true;
    } else {
      item.groupped = false;
      notOrderItems.push(item);
    }
  });
  $scope.journal = journalHashValues.concat(notOrderItems);
  //console.log(journalHash, journalHashValues);
});
