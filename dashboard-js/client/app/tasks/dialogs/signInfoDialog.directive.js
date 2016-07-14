'use strict';
angular.module('dashboardJsApp').directive('signInfoDialog', [
  function (scope, element, attrs) {
    return {
      restrict: 'E',
      templateUrl: 'app/tasks/dialogs/signInfoDialog.html',
      replace: true,
      transclude: true,
      link: function (scope, element, attrs) {
        scope.dialogStyle = {};
        if (attrs.width)
          scope.dialogStyle.width = attrs.width;
        if (attrs.height)
          scope.dialogStyle.height = attrs.height;

        scope.hideSignInfoModal = function () {
          scope.checkSignState.show = false;
        };

        scope.printSignInfo = function () {
          var parent = this.$parent.$parent;
          var elementToPrint = element[0].getElementsByClassName('full-sign-info-content')[0];
          var nOrderID;
          var sCreateData;
          try {
            nOrderID = "" + scope.selectedTask.processInstanceId + parent.lunaService.getLunaValue(scope.selectedTask.processInstanceId);
            sCreateData = scope.selectedTask.createTime ? parent.sDateShort(scope.selectedTask.createTime) : parent.sDateShort(scope.selectedTask.startTime);
          } catch (e){
            alert("Помилка: " + e.message);
          }
          var printHeader = '<div style="text-align: right"><span>Звернення № ' + nOrderID + '</span><br><span>від ' + sCreateData + '</span></div><br>';
          var printContents = elementToPrint.innerHTML;
          var popupWin = window.open('', '_blank');
          popupWin.document.open();
          popupWin.document.write('<html><head><link rel="stylesheet" type="text/css" href="style.css" /></head><body onload="window.print()">' + printHeader + printContents + '</html>');
          popupWin.document.close();
          scope.hideModal();
        }
      }
    };
  }
]);
