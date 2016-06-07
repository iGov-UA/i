/**
 * Created by ijmac on 23.05.16.
 */
angular.module('dashboardJsApp')
  .controller('UsersCtrl', function($scope, user){
    $scope.list = {
      getFunc: user.getUsers,
      setFunc: user.setUser,
      deleteFunc: user.deleteUser,
      addFunc: user.addUser,
      removeFunc: user.removeUser
    };
  });
