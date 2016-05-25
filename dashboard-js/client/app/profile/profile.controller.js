(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('ProfileCtrl', profileCtrl);

  profileCtrl.$inject = ['$scope', 'Auth','Profile','Modal'];
  function profileCtrl($scope, Auth,Profile, Modal) {
    // $scope.userName = "test";
    $scope.iGovTitle = "Профіль";
    // $scope.login = "test";


    var user = Auth.getCurrentUser();
    $scope.userName = user.firstName + ' ' + user.lastName;
    $scope.login = user.id;
    $scope.email = user.email;

    $scope.oldPassword = "";
    $scope.newPassword = "";
    $scope.newPassword2 = "";

    Profile.getSubjects(user.id).then(function(data){
      if (data.aSubjectAccount && data.aSubjectAccount.length > 0 ){
        (data.aSubjectAccount[0].oSubject.aSubjectAccountContact).forEach(function(contact) {
          if(contact.oSubjectContactType.nID==0){
            $scope.phone=contact.sValue;
          }
        });
      }
    }, function (err) {
      console.log(err);
    });

    $scope.changePassword = function () {
        if($scope.newPassword2 == $scope.newPassword){
          Profile.changePassword(user.id, $scope.oldPassword, $scope.newPassword).then(function(data){
            Modal.inform.info()("Пароль змінено");
            $scope.cancel();
          }, function (err) {
            Modal.inform.error()(JSON.parse(err).message);
          });
        } else {
          Modal.inform.error()("Нові паролі не збігаються");
        }
    };

    $scope.cancel = function () {
      $scope.oldPassword = "";
      $scope.newPassword = "";
      $scope.newPassword2 = "";
    };

    $scope.login = user.firstName;
     // user.firstName + ' ' + user.lastName;

  }
})();
