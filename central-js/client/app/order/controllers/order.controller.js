angular.module('order', []).controller('OrderController', function($scope, $state, MessagesService) {
    //if ($state.is('index.order')) {
    //    return $state.go('index.order.search');
    //}
  MessagesService.getServiceMessages("0-97200051").then(function(data){
    console.log(data);
  }, function (error){
    console.log(error);
  });
});
