angular.module('order').controller('OrderSearchController', function($rootScope, $scope,$location,$window,$state, $stateParams, ServiceService, MessagesService,BankIDService, order, $http) {
  $scope.loadMessages = function(orderp){
    if ($scope.orders[0].nID_Subject === $scope.orders[0].nID_Subject_Auth){
        BankIDService.isLoggedIn().then(function() {
          if (!!orderp ){
            MessagesService.getServiceMessages(orderp).then(function(datap){
              if(!datap.messages.code ){

                //if (datap.nID_Subject === $scope.orders[0].nID_Subject_Auth){
                  $scope.showComments = true;
                  $scope.serviceMessages = datap.messages;
                //}
              }

            }, function (error){
              $scope.showComments = false;
            });
          }
            $scope.authenticated = true;
        }).catch(function(error) {
          console.log(error);
          $scope.showComments = false;
            $scope.authenticated = false;
        });
    }
    

  } ;




  $scope.postComment = function(){
    if (!!$scope.comment){
      MessagesService.postServiceMessage($scope.orders[0].sID_Order, $scope.comment);
      $scope.loadMessages($scope.orders[0].sID_Order);
      $scope.comment = "";
    }
  };


    $scope.sID_Order = '';
    $scope.orders = {};
    $scope.serviceMessages = [];
    $scope.comment = "";
    $scope.showComments = false;
    $scope.authenticated = true;

    if(order != null) {
      //TODO: Temporary (back compatibility)
      $scope.sID_Order = $stateParams.sID_Order !== null ? $stateParams.sID_Order : $stateParams.nID;
      $scope.loadMessages(order.sID_Order);

      //$scope.nID_Subject = data.nID_Subject;
      $scope.messages = {};
      $scope.orders = {};
      if (!order) {
        $scope.messages = ['Невірний номер!'];
      } else if (order.hasOwnProperty('message')) {
        if (order.message.indexOf('CRC Error') > -1) {
          $scope.messages = ['Невірний номер!'];
        } else if (order.message.indexOf('Record not found') > -1) {
          $scope.messages = ['Заявку не знайдено'];
        } else {
          $scope.messages = ['Заявку не знайдено'];
        }
      } else {
        if (typeof order === 'object') {
          if (order.soData){
              try{
                    order.soData = JSON.parse(order.soData.replace(/'/g,'"'));
              }catch(_){
                console.log('[OrderSearchController](order.soData='+order.soData+'):'+_);
              }
          }
          //order.sDateEdit = new Date();
          //order.sDateEdit = order.sDate;
          order = [order];


        }
        $scope.orders = order;
      }
    }

    $scope.searchOrder = function(sID_Order) {
        ServiceService.searchOrder(sID_Order)
            .then(function(data) {
                $scope.messages = {};
                $scope.orders = {};
                if (!data) {
                    $scope.messages = ['Невірний номер!'];
                  $scope.showComments = false;
                } else if (data.hasOwnProperty('message')) {
                    if (data.message.indexOf('CRC Error') > -1) {
                        $scope.messages = ['Невірний номер!'];
                    } else if (data.message.indexOf('Record not found') > -1) {
                        $scope.messages = ['Заявку не знайдено'];
                    } else {
                        $scope.messages = ['Заявку не знайдено'];
                    }
                  $scope.showComments = false;
                } else {
                    if (typeof data === 'object') {
                      if (data.soData)
                        data.soData = JSON.parse(data.soData.replace(/'/g,'"'));
                        //data.sDateEdit = new Date();
                        //data.sDateEdit = data.sDate;
                      $scope.loadMessages(data.sID_Order);
                      //$scope.nID_Subject = data.nID_Subject;
                        data = [data];
                    }
                    $scope.orders = data;

                }

                return data;
            });
    };




  $scope.sendAnswer = function () {

    var sID_Order = $scope.orders[0].sID_Order;

    //TODO: Temporary (back compatibility)
    var nID_Order = $scope.orders[0].nID_Process;
    if(nID_Order===null || nID_Order === ""){
        nID_Order=$scope.orders[0].nID_Protected;
    }
    var nID_Server = $scope.orders[0].nID_Server;
    if(nID_Server===null || nID_Server === ""){
        nID_Server=0;
    }
    if(sID_Order===null || sID_Order === ""){
        sID_Order=nID_Server+"-"+nID_Order;
    }else if(sID_Order.indexOf("-")<0){
        sID_Order=nID_Server+"-"+sID_Order;
    }

    var data = {
      sToken: $stateParams.sToken,
      sID_Order: sID_Order,
       //nID_Protected: $scope.orders[0].nID_Protected,
       //nID_Process: $scope.orders[0].nID_Process,
       //nID_Server: $scope.orders[0].nID_Server,
      sBody: $scope.orders[0].sBody,
      sHead: $scope.orders[0].sHead
    };
    data['saField'] = JSON.stringify($scope.orders[0].soData);
    $http.post('/api/order/setTaskAnswer', data).success(function() {
      $scope.sendAnswerResult = 'Ваша відповідь успішно збережена';
    });
  };

  $scope.loginWithBankId = function () {
    var stateForRedirect = $state.href('index.order.search', {error: ''}) + "?nID="+$scope.orders[0].sID_Order;
    var redirectURI = $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
    $window.location.href = './auth/bankID?link=' + redirectURI;
  };

  $scope.loginWithEds = function () {
    var stateForRedirect = $state.href('index.order.search', {error: ''}) + "?nID="+$scope.orders[0].sID_Order;
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
    var stateForRedirect = $state.href('index.order.search', {error: ''}) + "?nID="+$scope.orders[0].sID_Order;
    var redirectURI = $location.protocol() +
      '://' + $location.host() + ':'
      + $location.port()
      + stateForRedirect;
    $window.location.href = './auth/soccard?link=' + redirectURI;
  };
});
