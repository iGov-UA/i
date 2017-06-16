'use strict';

angular.module('app').service('MasterPassService', ['$http', '$location', '$window', 'modalService', '$q', function ($http, $location, $window, modalService, $q) {

    var deleteCardModal = {
      closeButtonText: 'Скасувати',
      actionButtonText: 'Видалити карту',
      headerText: 'Пiдтвердження видалення карти',
      bodyText: 'Ви збираєтесь видалити карту з Вашого гаманця MasterPass. Щоб мати можливість в майбутньому використовувати цю карту в системі MasterPass, Вам необхідно буде попередньо додати її в Ваш список платіжних карт'
    },
    answerDefault = {
      backdrop: true,
      keyboard: true,
      modalFade: true,
      templateUrl: 'app/common/components/form/directives/modal.template.html'
    },
    deleteCardSuccess = {
      defaults: answerDefault,
      modal: {
        actionButtonText: 'Ок',
        headerText: 'Успiх',
        bodyText: 'Карта успiшно видалена з гаманця.'
      }
    },
    deleteCardFail = {
      defaults: answerDefault,
      modal: {
        actionButtonText: 'Ок',
        headerText: 'Помилка',
        bodyText: 'Нажаль сталася помилка, карта не видалена, спробуйте пiзнiше.'
      }
    },
    hasNoPhoneNumber = {
      defaults: answerDefault,
      modal: {
        actionButtonText: 'Ок',
        headerText: 'Увага',
        bodyText: 'Необхiдно заповнити поле з номером телефону'
      }
    },
    phoneIsNotVerified = {
      defaults: answerDefault,
      modal: {
        actionButtonText: 'Ок',
        headerText: 'Увага',
        bodyText: 'Необхiдно пiдтвердити номер телефону'
      }
    };

  return {
    isMasterPassButton: function (id, all) {
      if(id && !all)
        return id.indexOf('sID_Pay_MasterPass') === 0;
      if(!id && all) {
        for( var i=0; i<all.length; i++ ) {
          if( all[i].id.indexOf('sID_Pay_MasterPass') === 0 ) {
            return true;
          }
        }
      }
    },

    getSum: function (form) {
      var deferred = $q.defer();
      for(var i=0; i<form.length; i++) {
        if(form[i].id.indexOf('sID_Merchant_MasterPass') === 0) {
          var object = JSON.parse(form[i].value), sum = 0;
          angular.forEach(object.p2r, function (org) {
            sum += parseFloat(org.amount);
          });
          for(var j=0; j<form.length; j++) {
            if(form[j].id.indexOf('sSum_MasterPass') === 0){
              deferred.resolve({sum: sum, id: form[j].id, position: j});
            }
          }
        }
      }

      return deferred.promise;
    },

    phoneCheck: function (phone, value) {
      var params = {
        phone: phone,
        value: value
      };

      return $http.get('./api/masterpass/verifyPhoneNumber', {params: params}).then(function (res) {
        return !!(res);
      })
    },

    otpPhoneConfirm: function (phone, otp) {
      var params = {
        phone: phone,
        value: otp
      };

      return $http.get('./api/masterpass/confirmOtp', {params: params}).then(function (res) {
        if(res) {
          return res.data;
        } else {
          return false;
        }
      })
    },

    searchValidPhoneNumber: function (arr) {
      var phoneNumber;
      for(var i in arr) {
        if(arr.hasOwnProperty(i) && i === 'phone') {
          if(arr[i].value && arr[i].value !== '+380') {
            var phone = arr[i].value;
            if(phone.indexOf('+38') === 0) {
              phone = phone.slice(1);
              phoneNumber = phone.replace(/\s/g, '');
            } else {
              phoneNumber = '38' + phone.replace(/\s/g, '');
            }
            break;
          } else {
            var modalOptions = this.messages('missed-phone');
            modalService.showModal(modalOptions.defaults, modalOptions.modal);
            return false;
          }
        }
      }
      return phoneNumber;
    },

    fillCheckoutData: function (form) {
      var checkoutData = {};
      for(var i=0; i<form.length; i++) {
        var isMPService = this.isMasterPassButton(form[i].id);
        if (isMPService) {
          angular.forEach(form, function (i) {
            if (i.id.indexOf('sSum_MasterPass') === 0) checkoutData.invoice = i.value.toString();
            else if (i.id.indexOf('sID_Merchant_MasterPass') === 0) checkoutData.pmt_info = i.value;
            else if (i.id.indexOf('sDescription_MasterPass') === 0) checkoutData.pmt_desc = i.value;
          });
          break;
        }
      }
      return checkoutData;
    },

    checkUser: function (phone) {
      var params = {
        "body": {"msisdn": phone},
        "action": "Check"
      };

      return $http.post('./api/masterpass/checkUser', params).then(function (res) {
        if(res && res.data.response) {
          return res.data.response;
        }
      })
    },

    getCommission: function (sum) {
      var params = {
        "body": {
          "invoice": sum
        },
        "action": "CalcPaymentAmount"
      };

      return $http.post('./api/masterpass', params).then(function (res) {
        if(res && res.data.response) {
          return res.data.response;
        }
      })
    },

    addCard: function (phone, lang) {
      var params = {
        "body": {
          "msisdn": phone,
          "lang": lang
        },
        "action": "AddcardByURL"
      };

      return $http.post('./api/masterpass', params).then(function (res) {
        if(res && res.data.response) {
          return res.data.response.url;
        }
      })
    },

    removeCard: function (phone, card) {
      var params = {
        "body": {
          "msisdn": phone,
          "card_alias": card
        },
        "action": "DeleteCard"
      };

      return $http.post('./api/masterpass', params).then(function (res) {
        if(res && res.data.response) {
          return res.data.response;
        }
      })
    },

    createPayment: function (phone, data) {
      var params = {
        "body": {
          "invoice": data.invoice,
          "card_alias": data.card_alias,
          "pmt_info": data.pmt_info,
          "pmt_desc": data.pmt_desc,
          "msisdn": phone
        },
        "action": "PaymentCreate"
      };

      return $http.post('./api/masterpass/createSaleCancelPayment', params).then(function (res) {
        if(res && res.data.response) {
          return res.data.response;
        }
      })
    },

    // redirectForVerify3DS: function (url, res, md, callback) {
    //   var params = {
    //     "ascUrl": url,
    //     "body": {
    //       "pareq": res,
    //       "md": md,
    //       "TermUrl": callback
    //     }
    //   };
    //
    //   return $http.post('./api/masterpass/redirectForVerify3DS', params).then(function (res) {
    //     if(res && res.data.response) {
    //       return res.data.response;
    //     }
    //   })
    // },

    otpConfirm: function (otp, token, phone) {
      var params = {
        "body": {
          "value": otp,
          "token": token,
          "msisdn": phone
        },
        "action": "Otp"
      };

      return $http.post('./api/masterpass', params).then(function (res) {
        if(res && res.data.response) {
          return res.data.response;
        }
      })
    },

    paymentSale: function (data) {
      var params = {
        "body": {
          "pmt_id": data.pmt_id,
          "invoice": data.invoice
        },
        "action": "PaymentSale"
      };

      return $http.post('./api/masterpass/createSaleCancelPayment', params).then(function (res) {
        if(res && res.data.response) {
          return res.data.response;
        }
      })
    },

    paymentCancel: function (id, phone) {
      var params = {
        "body": {
          "pmt_id": id,
          "msisdn": phone
        },
        "action": "PaymentCancel"
      };

      return $http.post('./api/masterpass/createSaleCancelPayment', params).then(function (res) {
        if(res && res.data.response) {
          return res.data.response;
        }
      })
    },

    statusRequest: function (phone) {
      var params = {
        "body": {
          "msisdn": phone
        },
        "action": "StatusRequest"
      };

      return $http.post('./api/masterpass/createSaleCancelPayment', params).then(function (res) {
        if(res && res.data.response) {
          return res.data.response;
        }
      })
    },

    messages: function (type) {
      switch (type) {
        case 'remove':
          return deleteCardModal;
        case 'remove-success':
          return deleteCardSuccess;
        case 'remove-fail':
          return deleteCardFail;
        case 'answer':
          return answerDefault;
        case 'missed-phone':
          return hasNoPhoneNumber;
        case 'phone-is-not-verified':
          return phoneIsNotVerified;
      }
    },

    otpErrorMessages: function (msg) {
      var answer;
      switch (msg) {
        case 'otp max attempts':
          answer = 'Перевищена максимальна кiлькiсть спроб';
          break;
        case 'otp expired':
          answer = 'Термін дії ОТР-паролю закінчився';
          break;
        case 'wrong otp':
        case 'invalid value':
          answer = 'Невiрний ОТР';
          break;
      }
      return answer;
    },

    bankErrorMessage: function (msg) {
     var messages = {
       "invalid request structure": "Невірна структура запиту",
       "unknown field": "Невідоме поле",
       "invalid expm": "Невірний місяць закінчення дії карти",
       "invalid expy": "Невірний рік закінчення дії карти",
       "invalid cvv": "Невірний cvv код карти",
       "invalid value": "Невірне значення",
       "invalid pan": "Невірний PAN",
       "invalid auth": "Помилка в авторизації",
       "invalid auth time": "Помилка в часі авторизації",
       "access denied": "Доступ заборонено",
       "invalid msisdn": "Помилка в MSISDN",
       "invalid clientIp": "Помилка в clientIp",
       "try again later": "Спробуйте пізніше"
     };

     return messages[msg];
    }

  }
}]);
