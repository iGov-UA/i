angular.module('journal').controller('JournalSearchController', function (
  $rootScope,
  $scope,
  $location,
  $modal,
  $window,
  $state,
  $stateParams,
  ServiceService,
  MessagesService,
  UserService,
  BankIDLogin,
  order,
  events,
  $http,
  ErrorsFactory,
  JournalHelperService,
  DatepickerFactory,
  ActivitiService,
  moment
) {

  $scope.getOrderStatusString = JournalHelperService.getOrderStatusString;
  $scope.getDocumentLink = ServiceService.getAttachmentLink;

  $scope.aOrderMessages = [];
  $scope.sServerReturnOnAnswer = '';

  $scope.sID_Order = '';
  $scope.sToken = null;
  $scope.oOrder = {};
  $scope.aField = [];
  $scope.sOrderCommentNew = '';
  $scope.sOrderAnswerCommentNew = '';
  $scope.bAuth = UserService.isLoggedIn().then(function () {
    $scope.bAuth = true;
  }).catch(function () {
    $scope.bAuth = false;
  });
  $scope.bOrder = false;
  $scope.bOrderOwner = false;
  $scope.bOrderQuestion = false;

  var bExist = function (oValue) {
    return oValue && oValue !== null && oValue !== undefined && !!oValue;
  };

  var bExistNotSpace = function (oValue) {
    return bExist(oValue) && oValue.trim() !== "";
  };

  $scope.searchOrder = function (sID_Order_New, sToken_New) {//arguments.callee.toString()
    var oFuncNote = {sHead: "Пошук заявки", sFunc: "searchOrder"};
    var sID_Order = bExist(sID_Order_New) ? sID_Order_New : $scope.sID_Order;
    var sToken = bExist(sToken_New) ? sToken_New : $scope.sToken;
    ErrorsFactory.init(oFuncNote, {
      asParam: ['sID_Order: ' + sID_Order, 'sToken: ' + sToken],
      sNote: 'Формат заявки повинен бути лише із цифр та тире: X-XXXXX (де X-цифра), наприклад: 0-123456789'
    });
    $scope.sID_Order = sID_Order;
    $scope.sToken = sToken;
    var oOrder = {};
    $scope.aField = [];
    $scope.oOrder = oOrder;
    $scope.bOrder = false;
    $scope.bOrderOwner = false;
    $scope.bOrderQuestion = false;
    $scope.sServerReturnOnAnswer = '';
    if (bExistNotSpace(sID_Order)) {
      if (sID_Order.indexOf("-") < 0) {
        if (!/^\d+$/.test(sID_Order)) {
          //Modal.inform.error()('ID має складатися тільки з цифр!');
          ErrorsFactory.logWarn({sBody: 'Не вірний формат із символами!")'});
          //ErrorsFactory.reset();
          //$scope.searchOrder("0-"+sID_Order, sToken);
        } else {
          ErrorsFactory.logWarn({
            sBody: 'Старий формат!',
            sNote: 'Необхідно перед номером доповнити префікс "0-". (тобто "0-' + sID_Order + '", замість "' + sID_Order + '")'
          });
          //ErrorsFactory.reset();
          $scope.searchOrder("0-" + sID_Order, sToken);
        }
        return null;
      }
      ServiceService.searchOrder(sID_Order, sToken)
        .then(function (oResponse) {
          //if (ErrorsFactory.bSuccessResponse(oResponse, function (oThis, doMerge, sMessage, aCode, sResponse) {
            // console.log(oThis);
            //   if (!sMessage) {
            //     doMerge(oThis, {sType: "warning"});
            //   } else if (sMessage.indexOf(['CRC Error']) > -1) {
            //     doMerge(oThis, {sType: "warning", sBody: 'Невірний номер заявки по контрольній суммі!'});
            //   } else if (sMessage.indexOf(['sID_Order has incorrect format!']) > -1) {
            //     doMerge(oThis, {sType: "warning", sBody: 'Невірний формат заявки!'});
            //   } else if (sMessage.indexOf(['not found']) > -1) {
            //     doMerge(oThis, {sType: "warning", sBody: 'Заявку не знайдено!'});
            //   }
            // })) {
            oOrder = oResponse;
            if (/*ErrorsFactory.bSuccess(oFuncNote)*/!oResponse.message || oResponse.message.indexOf('not found') === -1) {
              $scope.oOrder = oOrder;
              $scope.oOrder.sDate = new Date (oOrder.sDate.replace(' ', 'T'));
              $scope.bOrder = bExist(oOrder) && bExist(oOrder.nID);
              $scope.bOrderOwner = $scope.bOrder && bExist(oOrder.nID_Subject) && oOrder.nID_Subject === oOrder.nID_Subject_Auth;
              $scope.bOrderQuestion = $scope.bOrder && ($scope.aField.length > 0 || $scope.oOrder.nID_StatusType === 3);
              $scope.loadMessages($scope.sID_Order, $scope.sToken);
              return oOrder;
            }
          // }
        }, function (sError) {
          ErrorsFactory.logFail({
            sBody: 'Невідома помилка сервісу!',
            sError: sError,
            asParam: ['$scope.oOrder: ' + $scope.oOrder]
          });
        });
    } else {
      ErrorsFactory.logInfo({sBody: 'Не задані параметри!'});
    }
  };


  $scope.loadMessages = function (sID_Order, sToken) {
    var oFuncNote = {sHead: "Завантаженя історії та коментарів", sFunc: "loadMessages"};
    ErrorsFactory.init(oFuncNote, {asParam: ['sID_Order: ' + sID_Order, 'sToken: ' + sToken]});
    $scope.aOrderMessage = [];
    UserService.isLoggedIn().then(function () {
      $scope.bAuth = true;
      if ($scope.bOrderOwner) {
        MessagesService.getServiceMessages(sID_Order, sToken).then(function (oResponse) {
          if (ErrorsFactory.bSuccessResponse(oResponse)) {
            if (bExist(oResponse.messages)) {
              $scope.aOrderMessages = oResponse.messages;
              if (events) {
                $scope.aOrderMessages = $scope.aOrderMessages.concat(events);
              }
              angular.forEach($scope.aOrderMessages, function (oOrderMessage, nIndex) {
                oOrderMessage.sDate = moment(oOrderMessage.sDate).toDate();
                  if (oOrderMessage.sData) {
                    try {
                      var aField = JSON.parse(oOrderMessage.sData.replace(/'/g, '\''));
                      angular.forEach(aField, function (oField) {
                        // если выполяеться данное условие, то в объекте будет только "общий" коммент от чиновника
                        // плюс тип - string, поэтому остальные поля в любом случае будут undefined, в этом мне кажется нет необходимости

                        // if (!bExist(oField.sID)) {
                        //   oField.sID = oField.id;
                        //   oField.sName = oField.id;
                        //   oField.sType = oField.type;
                        //   oField.sValue = oField.value;
                        //   oField.sValueNew = oField.value;
                        //   oField.sNotify = oField.value;
                        //   oField.id = "";
                        //   oField.type = "";
                        //   oField.value = "";
                        // }
                        if (oField.sType === "date") {
                          oField.oFactory = DatepickerFactory.prototype.createFactory();
                          oField.oFactory.value = oField.sValueNew;
                          oField.oFactory.required = true;
                        }
                      });
                      oOrderMessage.aData = aField;
                    } catch (sError) {
                      ErrorsFactory.addFail({
                        sBody: 'Помилка десереалізації об`єкту з полями, у яких зауваження!',
                        sError: sError,
                        asParam: ['oData.soData: ' + oResponse.soData]
                      });
                    }
                  }
              });
            } else {
              ErrorsFactory.addFail({
                sBody: 'Отриман пустий під-об`єкт!',
                asParam: ['soResponse: ' + JSON.stringify(oResponse)]
              });
            }
          }
        }, function (sError) {
          ErrorsFactory.addFail({sBody: 'Невідома помилка отримання!', sError: sError});
        });
      } else {
        ErrorsFactory.logInfo({sBody: 'Немає доступу!', asParam: ['bOrderOwner: ' + $scope.bOrderOwner]});
      }
      ErrorsFactory.log();
    }).catch(function (sError) {
      $scope.bAuth = false;
      //ErrorsFactory.logInfo({sBody: 'Невідома помилка авторизації!', sError: sError});
    });
  };

  $scope.postComment = function () {
    var oFuncNote = {sHead: "Відсилка коментаря", sFunc: "postComment"};
    var sID_Order = $scope.sID_Order;
    var sToken = $scope.sToken;
    ErrorsFactory.init(oFuncNote, {asParam: ['sID_Order: ' + sID_Order, 'sToken: ' + sToken]});
    if (bExistNotSpace($scope.sOrderCommentNew)) {
      if ($scope.bOrderOwner) {
        if (bExistNotSpace(sID_Order)) {
          try {
            MessagesService.postServiceMessage(sID_Order, $scope.sOrderCommentNew, sToken, $scope.uploadedFile)
              .then(function(response) {
                $scope.sOrderCommentNew = "";
                $scope.loadMessages(sID_Order, sToken);

              });
          } catch (sError) {
            ErrorsFactory.addFail({
              sBody: 'Невідома помилка сервісу!',
              sError: sError,
              asParam: ['sOrderCommentNew: ' + $scope.sOrderCommentNew]
            });
          }
        } else {
          ErrorsFactory.addFail({
            sBody: 'Не задані параметри!',
            asParam: ['sOrderCommentNew: ' + $scope.sOrderCommentNew]
          });
        }
      } else {
        ErrorsFactory.addFail({sBody: 'Немає доступу!', asParam: ['bOrderOwner: ' + $scope.bOrderOwner]});
      }
      ErrorsFactory.log();
    } else {
      ErrorsFactory.logInfo({sBody: 'Пустий коментар!'});
    }
  };

  $scope.sendAnswer = function () {
    var oFuncNote = {sHead: "Відсилка відповіді", sFunc: "sendAnswer"};
    var sID_Order = $scope.sID_Order;
    var sToken = $scope.sToken;
    ErrorsFactory.init(oFuncNote, {asParam: ['sID_Order: ' + sID_Order, 'sToken: ' + sToken]});
    var oOrder = bExist($scope.oOrder) && bExist($scope.oOrder.nID) ? $scope.oOrder : null;
    if ($scope.bOrderOwner) {
      if (bExistNotSpace(sID_Order) && bExist(oOrder)) {
        try {
          var oData = {
            sID_Order: sID_Order,
            sBody: $scope.sOrderAnswerCommentNew
          };
          if (sToken !== null) {
            oData = $.extend(oData, {sToken: sToken});
          }
          if ($scope.aOrderMessages[0].aData) {
            try {
              angular.forEach($scope.aOrderMessages[0].aData, function (oField) {
                if (oField.sType === "date") {
                  oField.sValueNew = oField.oFactory.value ? oField.oFactory.get() : oField.sValueNew;//.value
                  oField.oFactory = null;
                }
              });
              oData.saField = JSON.stringify($scope.aOrderMessages[0].aData);
            } catch (sError) {
              ErrorsFactory.addFail({
                sBody: 'Помилка сереалізації об`єкту з полями, у яких відповіді на зауваження!',
                sError: sError,
                asParam: ['oData.saField: ' + $scope.aOrderMessages[0].aData]
              });
            }
          }
          $http.post('/api/order/setTaskAnswer', oData).success(function () {
            $scope.sOrderAnswerCommentNew = "";
            $scope.sServerReturnOnAnswer = 'Ваша відповідь успішно збережена';
            $scope.loadMessages(sID_Order, sToken);
          });
        } catch (sError) {
          ErrorsFactory.addFail({
            sBody: 'Невідома помилка сервісу!',
            sError: sError,
            asParam: ['soOrder: ' + JSON.stringify(oOrder)]
          });
        }
      } else {
        ErrorsFactory.addFail({
          sBody: 'Не задані параметри для запиту!',
          asParam: ['soOrder: ' + JSON.stringify(oOrder)]
        });
      }
    } else {
      ErrorsFactory.addFail({sBody: 'Немає доступу!', asParam: ['bOrderOwner: ' + $scope.bOrderOwner]});
    }
    ErrorsFactory.log();
  };

  function getRedirectURI() {
    var stateForRedirect = $state.href('index.search', {sID_Order: $scope.sID_Order});
    return $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
  }

  $scope.getAuthMethods = function () {
    if($rootScope.profile.isKyivCity){
      return ["BankID","EDS"]
    }
    return ["BankID","EDS","mpbds"];
  };

  $scope.getRedirectUrl = getRedirectURI;

  if (order !== null) {
    $scope.searchOrder(
      bExist($stateParams.sID_Order) ? $stateParams.sID_Order : bExist($stateParams.nID) ? "0-" + $stateParams.nID : $scope.sID_Order
      , bExist($stateParams.sToken) ? $stateParams.sToken : $scope.sToken
    );
  }

  $scope.uploadedFile = null;

  $scope.onFileUploadSuccess = function ($file) {
    $scope.uploadedFile = $file;
  };

  $scope.getFileUploadUrl = function () {
    return ActivitiService.getUploadFileURLByServer(order.nID_Server);
  };

  $scope.openLetter = function(nID) {
    MessagesService.getSubjectMessageData(nID).then(function (res) {
      if(angular.isString(res.data)){

        //ErrorsFactory.push({type:"info", text: res.data.replace(new RegExp('table width="800"','g'),'table width="568"').replace(new RegExp('width="765"','g'),'width="568"')});

        var modalInstance = $modal.open({
          animation: true,
          size: 'lg',
          templateUrl: 'app/journal/letterModal.html',
          controller: function ($scope, $modalInstance, message, $sce) {
            $scope.message = $sce.trustAsHtml(message);
            $scope.close = function () {
              $modalInstance.close();
            }
          },
          resolve: {
            message: function () {
              return res.data;
            }
          }
        });

      } else {
        ErrorsFactory.push({type:"danger", text: "Виникла помилка при отриманні тексту листа"});
      }
    })
  };
});
