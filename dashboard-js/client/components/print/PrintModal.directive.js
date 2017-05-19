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
        scope.signedContent = null;
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

      scope.printFormLinkedToFileField = undefined;
      scope.isPrintFormNeverUploaded = true;
      scope.showSignAndUploadButton = function () {
        var aFileFields = scope.taskForm.filter(function (field) {
          return field.type === 'file' && field.options.hasOwnProperty('sID_Field_Printform_ForECP');
        });
        for(var j = 0; j < aFileFields.length; j++){
          for(var i = 0; i < scope.taskForm.length; i++){
            if(aFileFields[j].options['sID_Field_Printform_ForECP'] === scope.taskForm[i].id){
              scope.printFormLinkedToFileField = aFileFields[j].id;
              if(aFileFields[j].value && aFileFields[j].value.length > 0){
                scope.isPrintFormNeverUploaded = false;
              }
              return true
            }
          }
        }
        return false;
      };


      scope.signAndUpload = function (propertyId) {
        var elementToPrint = element[0].getElementsByClassName('ng-modal-dialog-content')[0];
        var printContents = '<html><head><meta charset="utf-8"></head><body>' + elementToPrint.innerHTML + '</body></html>';

        generationService
          .generatePDFFromHTML(printContents)
          .then(function (pdfContent) {
            var toSign = {id: "", content: pdfContent.base64, base64encoded: true};
            signDialog.signContent(toSign,
              function (signedContent) {
                scope.signedContent = {
                  signedContentName: "document" + new Date().getMilliseconds()
                };
                var aFiles = [];
                var file = generationService.getSignedFile(signedContent.sign, propertyId);
                aFiles.push(file);
                scope.upload(aFiles, propertyId);
                scope.isPrintFormNeverUploaded = false
              }, function () {
                console.log('Sign Dismissed');
                //todo dissmiss sign
              }, function (error) {
                //todo react on error during sign
              }, 'ng-on-top-of-modal-dialog modal-info');
          });
      };


      scope.signWithEDS = function () {
        var elementToPrint = element[0].getElementsByClassName('ng-modal-dialog-content')[0];
        var printContents = '<html><head><meta charset="utf-8"></head><body>' + elementToPrint.innerHTML + '</body></html>';

        generationService
          .generatePDFFromHTML(printContents)
          .then(function (pdfContent) {
            var toSign = {id: "", content: pdfContent.base64, base64encoded: true};
            signDialog.signContent(toSign,
              function (signedContent) {
                scope.signedContent = {
                  signedContentName: "document" + new Date().getMilliseconds()
                };
                scope.signedContent.signedContentURL = generationService.getSignedFileLink(signedContent.sign);
              }, function () {
                console.log('Sign Dismissed');
                //todo dissmiss sign
              }, function (error) {
                //todo react on error during sign
              }, 'ng-on-top-of-modal-dialog modal-info');
          });
      }
    },
    templateUrl: 'components/print/PrintModal.html',
    replace: true,
    transclude: true
  };
}]);
