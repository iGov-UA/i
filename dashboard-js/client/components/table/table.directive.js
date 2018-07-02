'use strict';

angular.module('dashboardJsApp').directive('tableModal', ['$window', function ($window) {
  return {
    restrict: 'E',
    link: function (scope, element, attrs, ngModel) {
      scope.dialogStyle = {};
      if (attrs.width)
        scope.dialogStyle.width = attrs.width;
      if (attrs.height)
        scope.dialogStyle.height = attrs.height;

      scope.printTable = function () {
        var elementToPrint = element[0].getElementsByClassName('table-ng-modal-dialog-content')[0];
        var printStyles = "table {width: 98%;border-spacing:0;margin-left:1%;} /n h1 {padding-bottom: 20px;}table thead tr {background: rgba(254,254,254,1);background: -moz-linear-gradient(top, rgba(254,254,254,1) 0%, rgba(242,242,242,1) 100%);background: -webkit-gradient(left top, left bottom, color-stop(0%, rgba(254,254,254,1)), color-stop(100%, rgba(242,242,242,1)));background: -webkit-linear-gradient(top, rgba(254,254,254,1) 0%, rgba(242,242,242,1) 100%);background: -o-linear-gradient(top, rgba(254,254,254,1) 0%, rgba(242,242,242,1) 100%);background: -ms-linear-gradient(top, rgba(254,254,254,1) 0%, rgba(242,242,242,1) 100%);background: linear-gradient(to bottom, rgba(254,254,254,1) 0%, rgba(242,242,242,1) 100%);filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#fefefe', endColorstr='#f2f2f2', GradientType=0 );}table td, table th {border: 1px solid #EDEDED;text-align: center;font-weight: normal;padding-bottom: 10px;padding-top: 10px;}table tbody tr:nth-child(even){background: #F2F2F2;} @media print{.no-print {display: none !important;} .for-print{display: block}}";
        var printContents = elementToPrint.innerHTML;
        var popupWin = window.open('', '_blank');
        popupWin.document.open();
        popupWin.document.write('<html><head><style>' + printStyles + '</style></head><body onload="window.print()">' + printContents + '</html>');
        popupWin.document.close();
        scope.hideModal();
      };

      scope.getEnumValue = function (object) {
        var array = [], value = null;
        var objValue = object.hasOwnProperty('value') ? 'value' : 'default';

        for (var item in object) {
          if (object.hasOwnProperty(item) && Array.isArray(object[item])) {
            array = object[item];
            break;
          }
        }

        if (array.length) {
          for (var i=0; i<array.length; i++) {
            if (array[i].id === object[objValue]) {
              value = array[i].name;
              break;
            }
          }
        }

        return value ? value : object[objValue];
      };
    },
    templateUrl: 'components/table/table.template.html'
  };
}]);
