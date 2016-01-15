angular.module('order', []).controller('OrderController', function($scope, $state, $location, MessagesService) {
    //if ($state.is('index.order')) {
    //    return $state.go('index.order.search');
    //}

  $scope.serviceMessages = [];
  var order = $location.search().sID_Order;
  if (!!order){
    MessagesService.getServiceMessages(order).then(function(data){
      $scope.serviceMessages = data;
      console.log(data);
    }, function (error){
      console.log(error);
    });
  }
});
