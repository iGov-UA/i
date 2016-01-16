angular.module('order', []).controller('OrderController', function($scope, $state, $location, MessagesService) {
    //if ($state.is('index.order')) {
    //    return $state.go('index.order.search');
    //}
  $scope.loadMessages = function(order){
    if (!!order){
      MessagesService.getServiceMessages(order).then(function(data){
        $scope.serviceMessages = data;
        console.log(data);
      }, function (error){
        console.log(error);
      });
    }
  } ;

  $scope.postComment = function(){
    $scope.comment = "";
    $scope.loadMessages(order);
  };



  var order = $location.search().sID_Order;

  $scope.serviceMessages = [];

  $scope.comment = "";

  $scope.loadMessages(order);
});
