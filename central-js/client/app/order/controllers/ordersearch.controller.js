angular.module('order').controller('OrderSearchController', function($rootScope, $scope,$location,$window,$state, $stateParams, ServiceService, MessagesService,BankIDService, order, $http, ErrorsFactory) {

    $scope.aOrderMessage = [];
    $scope.sServerReturnOnAnswer= '';
    
    $scope.sID_Order = '';
    $scope.sToken = null;
    $scope.oOrder = {};
    $scope.aField = [];
    $scope.sOrderCommentNew = '';
    $scope.sOrderAnswerCommentNew = '';
    
    $scope.bAuth = false;
    $scope.bOrder = false;
    $scope.bOrderOwner = false;
    $scope.bOrderQuestion = false;

   
    var bExist = function(oValue){
        return oValue && oValue !== null && oValue !== undefined && !!oValue;
    };
    
    var bExistNotSpace = function(oValue){
        return bExist(oValue) && oValue.trim()!=="";
    };
   
   
/*   
  $scope.htmldecode = function(encodedhtml)
  {
    var map = {
      '&amp;'     :   '&',
      '&gt;'      :   '>',
      '&lt;'      :   '<',
      '&quot;'    :   '"',
      '&#39;'     :   "'"
    };

    var result = angular.copy(encodedhtml);
    angular.forEach(map, function(value, key)
    {
      while(result.indexOf(key) > -1)
        result = result.replace(key, value);
    });

    return result;
  };

  $scope.getHtml = function(html) {
    return $sce.trustAsHtml(html);
  };
*/
    
    
    $scope.searchOrder = function(sID_Order_New, sToken_New) {
        var oFuncNote = {sHead:"Пошук заявки", sFunc:"searchOrder"};//arguments.callee.toString() //"searchOrder" //var myName = arguments.callee.toString();        
        ErrorsFactory.init(oFuncNote);
        var sID_Order = bExist(sID_Order_New) ? sID_Order_New : $scope.sID_Order;
        var sToken = bExist(sToken_New) ? sToken_New : $scope.sToken;
        $scope.sID_Order = sID_Order;
        $scope.sToken = sToken;
        var oOrder = {};
        $scope.aField = [];
        $scope.oOrder = oOrder;
        $scope.bOrder = false;
        $scope.bOrderOwner = false;
        $scope.bOrderQuestion = false;
        $scope.sServerReturnOnAnswer = '';
        if(bExistNotSpace(sID_Order)){
            ServiceService.searchOrder(sID_Order, sToken)
                .then(function(oData) {
                    if(ErrorsFactory.bSuccessResponse(oData,function(sResponseMessage){
                        if (sResponseMessage && sResponseMessage.indexOf('CRC Error') > -1) {
                            return {sType: "warning", sBody: 'Невірний номер заявки!',asParam:['sID_Order: '+sID_Order, 'sToken: '+sToken]};
                        } else if (sResponseMessage && sResponseMessage.indexOf('Record not found') > -1) {
                            //return ['Заявку не знайдено!','sID_Order: '+sID_Order];
                            return {sType: "warning", sBody: 'Заявку не знайдено!',asParam:['sID_Order: '+sID_Order, 'sToken: '+sToken]};
                        } else if (sResponseMessage) {
                            //return ['Невідома помилка!','sErrorMessage: '+sErrorMessage,'sID_Order: '+sID_Order];
                            return {sType: "error", sBody: 'Невідома помилка сервісу!', asParam:['sID_Order: '+sID_Order, 'sToken: '+sToken]};
                        } else {
                            return {sType: "error", asParam:['sID_Order: '+sID_Order, 'sToken: '+sToken]};
                        }                    
                    })){
                        if (typeof oData === 'object') {
                            if (oData.soData){
                                try{
                                    /*
                                    sID: item.id,
                                    sName: item.name,
                                    sType: item.type,
                                    sValue: item.value,
                                    sValueNew: "",
                                    sNotify: $scope.clarifyFields[item.id].text
                                    */
                                    var aField = JSON.parse(oData.soData.replace(/'/g,'"'));
                                    angular.forEach(aField, function(oField){
                                        if(!bExist(oField.sID)){
                                            oField.sID=oField.id;
                                            oField.sName=oField.id;
                                            oField.sType=oField.type;
                                            oField.sValue=oField.value;
                                            oField.sValueNew=oField.value;
                                            oField.sNotify=oField.value;
                                            oField.id="";
                                            oField.type="";
                                            oField.value="";
                                        }
                                    });
                                    $scope.aField = aField;
                                }catch(sError){
                                  ErrorsFactory.addFail({sBody:'Помилка десереалізації об`єкту з полями, у яких зауваження!', sError: sError, asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'oData.soData: '+oData.soData]});
                                }
                            }
                            oOrder = oData;
                        }else{
                            ErrorsFactory.addFail({sBody:'Помилка - повернено не об`єкт!', asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'oData: '+oData]});
                        }
                    }
                    if(ErrorsFactory.bSuccess(oFuncNote)){
                        $scope.oOrder = oOrder;
                        $scope.bOrder = bExist(oOrder) && bExist(oOrder.nID);
                        $scope.bOrderOwner = $scope.bOrder && bExist(oOrder.nID_Subject) && oOrder.nID_Subject === oOrder.nID_Subject_Auth;
                        $scope.bOrderQuestion = $scope.bOrder && $scope.aField.length > 0;
                        $scope.loadMessages($scope.sID_Order, $scope.sToken);
                    }
                    return oOrder;
                }, function (sError){
                    ErrorsFactory.logFail({sBody:'Невідома помилка сервісу!', sError: sError, asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'$scope.oOrder: '+$scope.oOrder]});
                });            
        }else{
            ErrorsFactory.logInfo({sBody:'Не задані параметри!'},{asParam:['sID_Order: '+sID_Order,'sToken: '+sToken]});
        }
    };

    
    $scope.loadMessages = function(sID_Order, sToken){
        var oFuncNote = {sHead:"Завантаженя історії та коментарів", sFunc:"loadMessages"};//arguments.callee.toString()
        ErrorsFactory.init(oFuncNote);
        $scope.aOrderMessage = [];
        BankIDService.isLoggedIn().then(function() {
            $scope.bAuth = true;
            if ($scope.bOrderOwner){
                MessagesService.getServiceMessages(sID_Order, sToken).then(function(oData){
                  if(ErrorsFactory.bSuccessResponse(oData)){
                      if(bExist(oData.messages)){
                          $scope.aOrderMessage = oData.messages;
                      }else{
                          ErrorsFactory.addFail({sBody:'Отриман пустий об`єкт!'},{asParam:['oData: '+oData,'sID_Order: '+sID_Order,'sToken: '+sToken]});
                      }
                  }
                }, function (sError){
                  ErrorsFactory.addFail({sBody:'Невідома помилка отримання!', sError: sError, asParam:['sID_Order: '+sID_Order,'sToken: '+sToken]});
                });
            }else{
                ErrorsFactory.logInfo({sBody:'Немає доступу!'},{asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'bOrderOwner: '+$scope.bOrderOwner]});
            }
            ErrorsFactory.log();
        }).catch(function(sError) {
            $scope.bAuth = false;
            ErrorsFactory.logInfo({sBody:'Невідома помилка авторизації!', sError: sError, asParam:['sID_Order: '+sID_Order,'sToken: '+sToken]});
        });            
  } ;

  $scope.postComment = function(){
    var oFuncNote = {sHead:"Відсилка коментаря", sFunc:"postComment"};//arguments.callee.toString()
    ErrorsFactory.init(oFuncNote);
    if (bExistNotSpace($scope.sOrderCommentNew)){
        var sID_Order = $scope.sID_Order;
        var sToken = $scope.sToken;
        if($scope.bOrderOwner){
            if(bExistNotSpace(sID_Order)){
              try{
                MessagesService.postServiceMessage(sID_Order, $scope.sOrderCommentNew, sToken);//$scope.orders[0].sID_Order
                $scope.sOrderCommentNew = "";
                $scope.loadMessages(sID_Order, sToken);
              }catch(sError){
                ErrorsFactory.addFail({sBody:'Невідома помилка сервісу!', sError: sError, asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'sOrderCommentNew: '+$scope.sOrderCommentNew]});
              }
            }else{
              ErrorsFactory.addFail({sBody:'Не задані параметри!'},{asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'sOrderCommentNew: ',$scope.sOrderCommentNew]});
            }
        }else{
            ErrorsFactory.addFail({sBody:'Немає доступу!'},{asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'bOrderOwner: '+$scope.bOrderOwner]});
        }
        ErrorsFactory.log();
    }else{
        ErrorsFactory.logInfo({sBody:'Пустий коментар!'},{asParam:['sID_Order: '+sID_Order,'sToken: '+sToken]});
    }
  };

  $scope.sendAnswer = function () {
    var oFuncNote = {sHead:"Відсилка відповіді", sFunc:"sendAnswer"};//arguments.callee.toString()
    ErrorsFactory.init(oFuncNote);
    var sID_Order = $scope.sID_Order;
    var sToken = $scope.sToken;
    var oOrder = bExist($scope.oOrder) && bExist($scope.oOrder.nID) ? $scope.oOrder : null;
    if($scope.bOrderOwner){
        if(bExistNotSpace(sID_Order) && bExist(oOrder)){
            try{
                var oData = {
                  sID_Order: sID_Order,
                  sBody: $scope.sOrderAnswerCommentNew
                };
                if(sToken!==null){
                    oData = $.extend(oData,{sToken: sToken});
                }
                if ($scope.aField){
                    try{
                        oData.saField = JSON.stringify($scope.aField);
                    }catch(sError){
                        ErrorsFactory.addFail({sBody:'Помилка сереалізації об`єкту з полями, у яких відповіді на зауваження!', sError: sError, asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'oData.soData: '+oData.soData]});
                    }
                }
                $http.post('/api/order/setTaskAnswer', oData).success(function() {
                  $scope.sOrderAnswerCommentNew = "";
                  $scope.sServerReturnOnAnswer = 'Ваша відповідь успішно збережена';
                  //$scope.loadMessages(sID_Order, sToken);
                });
            }catch(sError){
                ErrorsFactory.addFail({sBody:'Невідома помилка сервісу!', sError: sError, asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'oOrder: '+oOrder]});
            }
        }else{
          ErrorsFactory.addFail({sBody:'Не задані параметри для запиту!'},{asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'oOrder: '+oOrder]});
        }
    }else{
        ErrorsFactory.addFail({sBody:'Немає доступу!'},{asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'bOrderOwner: '+$scope.bOrderOwner]});
    }
    ErrorsFactory.log();
  };






  $scope.loginWithBankId = function () {
    var stateForRedirect = $state.href('index.order.search', {error: ''}) + "?sID_Order="+$scope.sID_Order;
    var redirectURI = $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
    $window.location.href = './auth/bankID?link=' + redirectURI;
  };

  $scope.loginWithEds = function () {
    var stateForRedirect = $state.href('index.order.search', {error: ''}) + "?sID_Order="+$scope.sID_Order;
    var redirectURI = $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
    $window.location.href = './auth/eds?link=' + redirectURI;
  };

    $scope.loginWithEmail = function () {
        $state.go('index.auth.email.verify');
    };

  $scope.loginWithSoccard = function () {
    var stateForRedirect = $state.href('index.order.search', {error: ''}) + "?sID_Order="+$scope.sID_Order;
    var redirectURI = $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
    $window.location.href = './auth/soccard?link=' + redirectURI;
  };

        
        
        
    if(order !== null) {
       $scope.searchOrder(
               bExist($stateParams.sID_Order) ? $stateParams.sID_Order : bExist($stateParams.nID) ? "0-" + $stateParams.nID : $scope.sID_Order
               , bExist($stateParams.sToken) ? $stateParams.sToken : $scope.sToken
            );
    }
  
});
