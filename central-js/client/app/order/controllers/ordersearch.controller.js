angular.module('order').controller('OrderSearchController', function($rootScope, $scope,$location,$window,$state, $stateParams, ServiceService, MessagesService,BankIDService, order, $http) {

    $scope.aOrderMessage = [];
    $scope.sServerReturnOnAnswer= '';
    $scope.asServerReturnOnRequest = [];
    
    $scope.sID_Order = '';
    $scope.sToken = null;
    $scope.aOrder = [];
    $scope.sOrderCommentNew = '';
    
    $scope.bAuthenticated = false;
    $scope.bOrder = false;
    $scope.bOrderOwner = false;
    $scope.bOrderQuestion = false;

    asErrorMessages = function(asMessageDefault, oData, onCheckMessage){
        /*
        var oData = {"s":"asasas"};
        $.extend(oData,{sDTat:"dddddd"});
        var a=[];
        a=a.concat(["1"]);
        */
        //{"code":"SYSTEM_ERR","message":null}
        if(!asMessageDefault || asMessageDefault===null){
            asMessageDefault=[];
        }
        var asMessage = [];
        try{
            if (!oData) {
                asMessage=asMessage.concat(['Пуста відповідь на запит!']);
            }else{
                if (oData.hasOwnProperty('message')) {
                    if(onCheckMessage!==null){
                        var asMessageNew = onCheckMessage(oData.message);
                        if(asMessageNew!==null){
                            asMessage=asMessage.concat(asMessageNew);
                        }else{
                            asMessage=asMessage.concat(['Message: '+oData.message]);
                        }
                    }else{
                        asMessage=asMessage.concat(['Message: '+oData.message]);
                    }
                }
                if (oData.hasOwnProperty('code')) {
                    asMessage=asMessage.concat(['Code: '+oData.code]);
                }
            }
        }catch(_){
            asMessage=asMessage.concat(['Невідома помилка!','oData: '+oData]);
        }
        if(asMessage.length>0){
            asMessage=asMessageDefault.concat(asMessage);
        }
        console.log('[asErrorMessages]:asMessage='+asMessage);
    }
    
    
    $scope.loadMessages = function(sID_Order, sToken){
        $scope.asServerReturnOnRequest = [];
        $scope.aOrderMessage = [];
        if($scope.bOrderOwner){
            BankIDService.isLoggedIn().then(function() {
                $scope.bAuthenticated = true;
                if ($scope.bOrderOwner){
                    if (!!sID_Order ){
                      MessagesService.getServiceMessages(sID_Order, sToken).then(function(oData){
                        $scope.asServerReturnOnRequest = asErrorMessages(["Помилка при отримані коментарів", 'Function: loadMessages'],oData);
                        if($scope.asServerReturnOnRequest.length===0){
                            if(oData.messages && oData.messages!==null){
                                $scope.aOrderMessage = oData.messages;
                            }else{
                                $scope.asServerReturnOnRequest=['Невідома помилка при отримані коментарів!', 'Function: loadMessages','oData: '+oData,'sID_Order: '+sID_Order,'sToken: '+sToken];
                            }
                        }
                      }, function (sError){
                        $scope.asServerReturnOnRequest=['Невідома помилка при отримані коментарів!', 'Function: loadMessages','sError: '+sError,'sID_Order: '+sID_Order,'sToken: '+sToken];
                      });
                      if($scope.asServerReturnOnRequest.length>0){
                          console.log("asServerReturnOnRequest="+$scope.asServerReturnOnRequest);
                      }        
                    }
                }
            }).catch(function(sError) {
                $scope.bAuthenticated = false;
                $scope.asServerReturnOnRequest=['Невідома помилка при отримані коментарів!', 'Function: loadMessages','sError: '+sError,'sID_Order: '+sID_Order,'sToken: '+sToken];
                console.log("asServerReturnOnRequest="+$scope.asServerReturnOnRequest);
            });            
        }else{
            console.log("[loadMessages](sID_Order="+sID_Order+",sToken="+sToken+"):bOrderOwner="+$scope.bOrderOwner);
        }
  } ;


  $scope.postComment = function(){
    if (!!$scope.sOrderCommentNew){
        $scope.asServerReturnOnRequest = [];
        var sID_Order = $scope.sID_Order;
        var sToken = $scope.sToken;
        if($scope.bOrderOwner){
            //var oOrder = $scope.orders && $scope.orders!=null && $scope.orders.length > 0 ? $scope.orders[0] : null;
            if(sID_Order && sID_Order!==null && sID_Order !== ''){
              //MessagesService.postServiceMessage($scope.orders[0].sID_Order, $scope.comment);
              try{
                MessagesService.postServiceMessage(sID_Order, $scope.sOrderCommentNew, sToken);//$scope.orders[0].sID_Order
                $scope.sOrderCommentNew = "";
                $scope.loadMessages(sID_Order);//$scope.orders[0].sID_Order
              }catch(sError){
                $scope.asServerReturnOnRequest=['Невідома помилка при підправці коментаря!', 'Function: postComment','sError: '+sError,'sID_Order: '+sID_Order,'sToken: '+sToken,'sOrderCommentNew: '+$scope.sOrderCommentNew];
              }
            }else{
              $scope.asServerReturnOnRequest=['Не задані параметри при підправці коментаря!', 'Function: postComment','sID_Order: '+sID_Order,'sToken: '+sToken,'sOrderCommentNew: ',$scope.sOrderCommentNew];
            }
            if($scope.asServerReturnOnRequest.length>0){
                console.log("asServerReturnOnRequest="+$scope.asServerReturnOnRequest);
            }
        }else{
            $scope.asServerReturnOnRequest=['Немає доступу!', 'Function: postComment', 'sID_Order: '+sID_Order,'sToken: '+sToken,'bOrderOwner: '+$scope.bOrderOwner];
            console.log("asServerReturnOnRequest="+$scope.asServerReturnOnRequest);
        }
    }
  };


    if(order != null) {
       $scope.searchOrder(
               $stateParams.sID_Order !== null ? $stateParams.sID_Order : "0-" + $stateParams.nID
               , $stateParams.sToken !== null ? $stateParams.sToken : $scope.sToken
            );
    }

    $scope.searchOrder = function(sID_Order_New, sToken_New) {
        var sID_Order = sID_Order_New !== null ? sID_Order_New : $scope.sID_Order;
        var sToken = sToken_New !== null ? sToken_New : $scope.sToken;
        $scope.sID_Order = sID_Order;
        $scope.sToken = sToken;
        var aOrder = [];
        $scope.aOrder = aOrder;
        $scope.bOrder = false;
        $scope.bOrderOwner = false;
        $scope.bOrderQuestion = false;
        $scope.sServerReturnOnAnswer = '';
        $scope.asServerReturnOnRequest = [];
        ServiceService.searchOrder(sID_Order, sToken)
            .then(function(oData) {
                $scope.asServerReturnOnRequest = asErrorMessages(["Помилка при отримані заявки!", 'Function: searchOrder'],oData,function(sErrorMessage){
                    if (sErrorMessage && sErrorMessage !== null && sErrorMessage.indexOf('CRC Error') > -1) {
                        return ['Невірний номер!','sID_Order: '+sID_Order];
                    } else if (sErrorMessage && sErrorMessage !== null && sErrorMessage.indexOf('Record not found') > -1) {
                        return ['Заявку не знайдено!','sID_Order: '+sID_Order];
                    } else {
                        return ['Невідома помилка!','sErrorMessage: '+sErrorMessage,'sID_Order: '+sID_Order];
                    }                    
                });
                if($scope.asServerReturnOnRequest.length===0){
                    if (typeof oData === 'object') {
                        if (oData.soData){
                            try{
                                oData.soData = JSON.parse(oData.soData.replace(/'/g,'"'));
                            }catch(_){
                              $scope.asServerReturnOnRequest=['Помилка парсингу об`єкту з полями, у яких заууваження при отримані заявки!', 'Function: searchOrder', 'sID_Order: '+sID_Order,'sToken: '+sToken,'oData.soData: '+oData.soData];
                            }
                        }
                        aOrder = [oData];
                    }else{
                        $scope.asServerReturnOnRequest=['Помилка - повернено не об`єкт при отримані заявки!', 'Function: searchOrder', 'sID_Order: '+sID_Order,'sToken: '+sToken,'oData.soData: '+oData.soData];
                    }
                }
                if($scope.asServerReturnOnRequest.length>0){
                    console.log("asServerReturnOnRequest="+$scope.asServerReturnOnRequest);
                }else{
                    $scope.aOrder = aOrder;
                    $scope.bOrder = $scope.aOrder && $scope.aOrder !== null && $scope.aOrder.length > 0;
                    $scope.bOrderOwner = $scope.bOrder && $scope.aOrder[0].nID_Subject === $scope.aOrder[0].nID_Subject_Auth;
                    $scope.bOrderQuestion = $scope.bOrder && $scope.aOrder[0].soData && $scope.aOrder[0].soData!==null && $scope.aOrder[0].soData.length > 0;
                    $scope.loadMessages($scope.sID_Order, $scope.sToken);
                }
                return aOrder;
            }, function (sError){
              $scope.asServerReturnOnRequest=['Невідома помилка при отримані заявки!', 'Function: searchOrder','sError: '+sError, 'sID_Order: '+sID_Order,'sToken: '+sToken,'$scope.aOrder: '+$scope.aOrder];
              console.log("asServerReturnOnRequest="+$scope.asServerReturnOnRequest);
            });
    };



  $scope.sendAnswer = function () {
    //var sID_Order = $scope.orders[0].sID_Order;
    //var sID_Order = $scope.sID_Order !== null ? $scope.sID_Order : $stateParams.sID_Order;
    //var sToken = $scope.sToken !== null ? $scope.sToken : $stateParams.sToken;
    $scope.asServerReturnOnRequest = [];
    var sID_Order = $scope.sID_Order;
    var sToken = $scope.sToken;
    var oOrder = $scope.aOrder && $scope.aOrder!=null && $scope.aOrder.length > 0 ? $scope.aOrder[0] : null;
    if($scope.bOrderOwner){
        if(oOrder && oOrder!=null && sID_Order && sID_Order!==null && sID_Order !== ''){
            try{
                var oData = {
        //              sToken: sToken,//$stateParams.sToken
                  sID_Order: sID_Order,
                   //nID_Protected: $scope.orders[0].nID_Protected,
                   //nID_Process: $scope.orders[0].nID_Process,
                   //nID_Server: $scope.orders[0].nID_Server,
                  //sHead: oOrder.sHead //$scope.orders[0]
                  sBody: $scope.sOrderCommentNew //oOrder.sBody, //$scope.orders[0]
                };
                if(sToken!==null){
                    oData = $.extend(oData,{sToken: sToken});
                }

                oData['saField'] = JSON.stringify(oOrder.soData);//$scope.orders[0]
                $http.post('/api/order/setTaskAnswer', oData).success(function() {
                  $scope.sServerReturnOnAnswer = 'Ваша відповідь успішно збережена';
                });
              }catch(sError){
                $scope.asServerReturnOnRequest=['Невідома помилка при відсилці відповіді!', 'Function: sendAnswer','sError: '+sError, 'sID_Order: '+sID_Order,'sToken: '+sToken,'oOrder: '+oOrder];
              }
        }else{
          $scope.asServerReturnOnRequest=['Не задані параметри для запиту при відсилці відповіді!', 'Function: sendAnswer', 'sID_Order: '+sID_Order,'sToken: '+sToken,'oOrder: '+oOrder];
        }
        if($scope.asServerReturnOnRequest.length>0){
            console.log("asServerReturnOnRequest="+$scope.asServerReturnOnRequest);
        }
    }else{
        $scope.asServerReturnOnRequest=['Немає доступу!', 'Function: sendAnswer', 'sID_Order: '+sID_Order,'sToken: '+sToken,'bOrderOwner: '+$scope.bOrderOwner];
        console.log("asServerReturnOnRequest="+$scope.asServerReturnOnRequest);
    }
  };






  $scope.loginWithBankId = function () {
    var stateForRedirect = $state.href('index.order.search', {error: ''}) + "?sID_Order="+$scope.aOrder[0].sID_Order;
    var redirectURI = $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
    $window.location.href = './auth/bankID?link=' + redirectURI;
  };

  $scope.loginWithEds = function () {
    var stateForRedirect = $state.href('index.order.search', {error: ''}) + "?sID_Order="+$scope.aOrder[0].sID_Order;
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
    var stateForRedirect = $state.href('index.order.search', {error: ''}) + "?sID_Order="+$scope.aOrder[0].sID_Order;
    var redirectURI = $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
    $window.location.href = './auth/soccard?link=' + redirectURI;
  };
});
