'use strict';

angular.module('dashboardJsApp').directive('printModal', ['$window', 'signDialog', 'generationService', '$base64', function ($window, signDialog, generationService, $base64) {
  return {
    restrict: 'E',
    link: function (scope, element, attrs, ngModel) {
      scope.signedContent = null;
      scope.dialogStyle = {};
      if (attrs.width)
        scope.dialogStyle.width = attrs.width;
      if (attrs.height)
        scope.dialogStyle.height = attrs.height;

      scope.hideModal = function () {
        scope.printModalState.show = false;
        scope.convertDisabledEnumFiedsToReadonlySimpleText();
      };

      scope.printContent = function () {
        var elementToPrint = element[0].getElementsByClassName('ng-modal-dialog-content')[0];
        var printContents = elementToPrint.innerHTML;
        var popupWin = window.open('', '_blank');
        popupWin.document.open();
        popupWin.document.write('<html><head><link rel="stylesheet" type="text/css" href="style.css" /></head><body onload="window.print()">' + printContents + '</html>');
        popupWin.document.close();
        scope.hideModal();
      };

      scope.signWithEDS = function () {
        //TODO call pdf creation here from html
        var elementToPrint = element[0].getElementsByClassName('ng-modal-dialog-content')[0];
        var printContents = elementToPrint.innerHTML;

        signDialog.signContent(generationService
          .generatePDFFromHTML(printContents)
          .then(function (pdfContent) {
            return {id: "", content: pdfContent.base64, base64encoded : true};
          }), function (signedContent) {
          scope.signedContent = {
            signedContentName :"download"
          };
          var blob = new Blob([ $base64.decode(signedContent.content) ], { type : 'application/pdf' });
          scope.signedContent.signedContentURL = (window.URL || window.webkitURL).createObjectURL( blob );
        }, function () {
          console.log('Sign Dismissed');
         //todo dissmiss sign
        }, function (error) {
          //todo react on error during sign
        }, 'ng-on-top-of-modal-dialog modal-info');
      }
    },
    templateUrl: 'components/print/PrintModal.html',
    replace: true,
    transclude: true
  };
}]);
