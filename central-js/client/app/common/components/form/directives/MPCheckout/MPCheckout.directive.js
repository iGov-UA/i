'use strict';

angular.module('app').directive('masterpassCheckout', ['MasterPassService', 'modalService', '$location', '$window',
  function (MasterPassService, modalService, $location, $window) {
  return {
    restrict: 'EA',
    templateUrl: 'app/common/components/form/directives/MPCheckout/MPCheckout.template.html',
      link: function (scope) {
        function openUrl(url, post) {
          if (post) {
            var form = $('<form/>', {action: url, method: 'POST', target: '_self', style: {display: 'none'}});
            for (var key in post) if (post.hasOwnProperty(key)) {
              form.append($('<input/>', {type: 'hidden', name: key, value: post[key]}));
            }
            form.appendTo(document.body);
            form.submit();
          } else {
            window.open(url, '_blank');
          }
        }

        scope.checkoutSpinner = false;
        scope.checkoutConfirm = {status: 'checkout'};
        scope.selectedCard = {index: null, alias: null};

        scope.selectCard = function (index, info) {
          if(scope.checkoutConfirm.status === 'checkout')
            scope.selectedCard = {index: index, alias: info};
        };

        scope.isSelectedCard = function (index) {
          return scope.selectedCard.index === index;
        };

        function searchMPCheckoutFields() {
          scope.checkoutData = MasterPassService.fillCheckoutData(scope.activitiForm.formProperties);
        }

        scope.authorizeCheckout = function () {
          scope.checkoutSpinner = true;
          scope.paymentStatus = null;
          var phoneNumber = MasterPassService.searchValidPhoneNumber(scope.data.formData.params);

          if (phoneNumber && phoneNumber.length === 12)
            MasterPassService.checkUser(phoneNumber, 'ua').then(function (res) {
              if(res) {
                scope.isOpenedCheckout = true;
                if(res.url) {
                  scope.userCards = null;
                  scope.registerLink = res.url;
                } else if(res.error) {
                  scope.paymentStatus = 4;
                  console.error(res.error);
                } else {
                  scope.userCards = res;
                  scope.registerLink = null;
                }
              }
              scope.checkoutConfirm = {status: 'checkout'};
              scope.checkoutSpinner = false;
            });
        };

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
              scope.checkoutConfirm.status = 'confirm';
              scope.checkoutSpinner = true;
              MasterPassService.removeCard(phoneNumber, card).then(function (res) {
                if(res.status === 'FAIL') {
                  modalOptions = MasterPassService.messages('remove-fail');
                  modalService.showModal(modalOptions.defaults, modalOptions.modal)
                }
                scope.authorizeCheckout();
                scope.checkoutConfirm.status = 'checkout';
              });
            });
          }
        };

        scope.getPrice = function () {
          for(var i=0; i<scope.activitiForm.formProperties.length; i++) {
            if(scope.activitiForm.formProperties[i].id.indexOf('sID_Merchant_MasterPass') === 0) {
              var object = JSON.parse(scope.activitiForm.formProperties[i].value), sum = 0;
              angular.forEach(object.p2r, function (org) {
                sum += parseFloat(org.amount);
              });
              for(var j=0; j<scope.activitiForm.formProperties.length; j++) {
                if(scope.activitiForm.formProperties[j].id.indexOf('sSum_MasterPass') === 0){
                  scope.activitiForm.formProperties[j].value = sum;
                  break;
                }
              }
              return sum / 100;
            }
          }
        };

        scope.createPayment = function () {
          scope.checkoutSpinner = true;
          var phoneNumber = MasterPassService.searchValidPhoneNumber(scope.data.formData.params);

          searchMPCheckoutFields();
          scope.checkoutData.card_alias = scope.selectedCard.alias;

          if(phoneNumber && phoneNumber.length === 12) {
            scope.checkoutConfirm.status = 'confirm';
            MasterPassService.createPayment(phoneNumber, scope.checkoutData).then(function (res) {
              if(res.pmt_status == 4) {
                scope.paymentStatus = 4;
              } else if(res.pmt_status == 0) {
                if (res.secure && res.secure === '3ds') {
                  var url = $location.protocol() + '://' + $location.host() + ':' + $location.port() + $location.path();
                  var callbackUrl = $location.protocol() + '://' + $location.host() + ':' + $location.port() + '/api/masterpass/verify3DSCallback' + '?id=' + res.pmt_id + '&url=' + url;
                  var temp = JSON.stringify({form: scope.data.formData.params, activiti: scope.activitiForm.formProperties});
                  localStorage.setItem('temporaryForm', temp);

                  openUrl(res.ascUrl, {pareq: res.pareq, md: res.md, TermUrl: callbackUrl});
                } else if(res.secure && res.secure === 'otp') {
                  scope.checkoutConfirm.status = 'confirm';
                  scope.checkoutData.payment= {otpToken: res.token, otpCode: null, invoice: res.invoice, pmt_id: res.pmt_id, verify_code: res.verify_code || null};
                }
              } else if(res.pmt_status == 5) {
                MasterPassService.paymentSale(scope.checkoutData.payment).then(function (res) {
                  if(res.pmt_status == 4) {
                    scope.paymentStatus = 4;
                  } else if(res.pmt_status == 5) {
                    scope.paymentStatus = 5;
                    for(var field in scope.data.formData.params) {
                      if(scope.data.formData.params.hasOwnProperty(field) && field.indexOf('sID_Pay_MasterPass') === 0) {
                        scope.data.formData.params[field].value = res.pmt_id;
                        scope.checkoutData.payment = {result: res.pmt_id}
                      }
                    }
                  }
                })
              }
              scope.checkoutSpinner = false;
            })
          } else {
            scope.checkoutSpinner = false;
          }
        };

        scope.otpConfirmPayment = function () {
          scope.checkoutSpinner = true;
          var test = scope.checkoutData.payment.otpCode ? scope.checkoutData.payment.otpCode : scope.checkoutData.payment.verify_code;
          MasterPassService.otpConfirm(test, scope.checkoutData.payment.otpToken).then(function (res) {
            if(res.status === 'OK') {
              MasterPassService.paymentSale(scope.checkoutData.payment).then(function (res) {
                if(res.pmt_status == 4) {
                  scope.paymentStatus = 4;
                } else if(res.pmt_status == 5) {
                  scope.paymentStatus = 5;
                  for(var field in scope.data.formData.params) {
                    if(scope.data.formData.params.hasOwnProperty(field) && field.indexOf('sID_Pay_MasterPass') === 0) {
                      scope.data.formData.params[field].value = res.pmt_id;
                      scope.checkoutData.payment = {result: res.pmt_id}
                    }
                  }
                }
              })
            } else {
              scope.paymentStatus = 4;
            }
          });
        };

        scope.chooseAnotherCard = function () {
          scope.authorizeCheckout();
          scope.checkoutConfirm = {status: 'checkout'};
          scope.checkoutData.payment = {};
        };
      }
    }
}]);
