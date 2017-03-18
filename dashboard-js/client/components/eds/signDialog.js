angular.module('dashboardJsApp')
  .factory('signDialog', function ($rootScope, $modal) {
    function openModal(scope, modalScope, modalClass) {
      modalScope = modalScope ? modalScope : $rootScope.$new();
      scope = scope || {};
      modalClass = modalClass || 'modal-info';

      angular.extend(modalScope, scope);

      return $modal.open({
        templateUrl: 'components/eds/signDialog.html',
        windowClass: modalClass,
        scope: modalScope,
        controller: SignDialogInstanceCtrl
      });
    }

    function signContent(content, scope, dismissCallback, resultCallback) {
      var modalScope = $rootScope.$new();
      modalScope.content = content;
      var signModal = openModal(scope, modalScope);

      signModal.result.then(function () {
        dismissCallback();
      }, function (signedContent) {
        resultCallback(signedContent);
      });
    }

    return {
      signContent: signContent
    }
  });

var SignDialogInstanceCtrl = function ($scope, $modalInstance, signService) {
  var initialized = signService.init();

  $scope.signedContent = {};
  var password = "";//TODO take from field

  $scope.edsContext = {
    edsStorage: undefined,
    keyList: undefined,
    selectedKey : undefined
  };

  $scope.sign = function () {
    if (initialized) {
      var edsContext = $scope.edsContext;
      signService.activate().then(function () {
        signService.selectFile().then(function (edsStorage) {
          edsContext.edsStorage = edsStorage;
          signService.openStore(edsStorage, password).then(function (keyList) {
            edsContext.keyList = keyList;
            console.log(JSON.stringify(edsContext));
            $modalInstance.close($scope.signedContent);
          });
        })
      });
    } else {

    }
  };

  $scope.cancel = function () {
    $modalInstance.dismiss();
  };
};
