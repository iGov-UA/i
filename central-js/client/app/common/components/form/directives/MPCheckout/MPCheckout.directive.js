'use strict';

angular.module('app').directive('masterpassCheckout', ['MasterPassService', 'modalService', '$location', '$window', '$rootScope',
  function (MasterPassService, modalService, $location, $window, $rootScope) {
  return {
    restrict: 'EA',
    templateUrl: 'app/common/components/form/directives/MPCheckout/MPCheckout.template.html',
      link: function (scope) {

        (function () {
          var phoneNumber = MasterPassService.searchValidPhoneNumber(scope.data.formData.params);

          MasterPassService.getSum(scope.activitiForm.formProperties).then(function (res) {
            scope.data.formData.params[res.id].value = res.sum/100 + ' грн';
            scope.activitiForm.formProperties[res.position].value = res.sum;
            scope.sum = {price: res.sum/100};

            MasterPassService.getCommission(res.sum, phoneNumber).then(function (response) {
              scope.sum.commission = +(response.amount/100 - scope.sum.price).toFixed(2);
              scope.sum.total = +(scope.sum.price + scope.sum.commission).toFixed(2);
            })
          });
        })();

        scope.addNewCardToWallet = function () {
          var phoneNumber = MasterPassService.searchValidPhoneNumber(scope.data.formData.params);

          if(phoneNumber && phoneNumber.length === 12) {
            var win = $window.open();
            MasterPassService.addCard(phoneNumber, 'ua').then(function (url) {
              if(url)
                win.document.location = url;
            });
          }
        };

        scope.removeCardFromWallet = function (card) {
          var phoneNumber = MasterPassService.searchValidPhoneNumber(scope.data.formData.params);

          if(phoneNumber && phoneNumber.length === 12) {
            var modalOptions = MasterPassService.messages('remove'), modalDefaults = {};

            modalService.showModal(modalDefaults, modalOptions).then(function () {
              scope.checkoutSpinner = true;
              MasterPassService.removeCard(phoneNumber, card).then(function (res) {
                if(res.status === 'FAIL') {
                  modalOptions = MasterPassService.messages('remove-fail');
                  modalService.showModal(modalOptions.defaults, modalOptions.modal)
                }
                scope.authorizeCheckout();
                scope.checkoutConfirm.status = 'checkout';
                scope.checkoutSpinner = false;
              });
            });
          }
        };
      }
    }
}]);
