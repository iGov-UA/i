(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('ProfileCtrl', profileCtrl);

  profileCtrl.$inject = ['$scope', 'Auth','Profile','Modal', 'accountSubjects'];
  function profileCtrl($scope, Auth,Profile, Modal, accountSubjects) {
    $scope.iGovTitle = "Профіль";

    var user = Auth.getCurrentUser();
    $scope.user = user;
    $scope.contacts = {
      phone: '',
      email: ''
    };

    $scope.oldPassword = "";
    $scope.newPassword = "";
    $scope.newPassword2 = "";

    var subjectContactIdToPropertyMap = {
      0: 'phone',
      1: 'email'
    };

    angular.forEach(accountSubjects, function(subjects){
      angular.forEach(subjects, function(subject) {
        angular.forEach(subject.oSubject.aSubjectAccountContact, function(sac){
          if (sac.sValue && subjectContactIdToPropertyMap.hasOwnProperty(sac.oSubjectContactType.nID)) {
            $scope.contacts[subjectContactIdToPropertyMap[sac.oSubjectContactType.nID]] = sac.sValue;
          }
        });
      });
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
  }
})();
