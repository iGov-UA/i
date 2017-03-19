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

var SignDialogInstanceCtrl = function ($scope, $modalInstance, signService, md5, $q) {
  var initialized = signService.init();

  function removeLastError() {
    $scope.lastError = undefined;
  }

  function catchLastError(error) {
    $scope.lastError = error;
  }

  $scope.signedContent = {};

  $scope.edsContext = {
    edsStorage: {
      name: "",
      file: "",
      password: ""
    },
    keyList: [],
    selectedKey: {
      key: undefined,
      password: undefined,
      needPassword: true
    },
    lastError: undefined
  };

  $scope.chooseEDSFile = function () {
    removeLastError();
    var edsContext = $scope.edsContext;
    signService.activate().then(function () {
      return signService.selectFile().then(function (edsStorage) {
        edsContext.edsStorage.file = edsStorage;
        var filePath = edsStorage.filePath;
        edsContext.edsStorage.name = filePath.substr(filePath.lastIndexOf("/") + 1)
      });
    }).catch(catchLastError);
  };

  $scope.findKeys = function () {
    removeLastError();
    var edsContext = $scope.edsContext;
    signService.openStore(edsContext.edsStorage.file.filePath, edsContext.edsStorage.password).then(function (keyList) {
      edsContext.keyList = keyList;
      if (keyList.length === 1) {
        edsContext.selectedKey.key = keyList[0];
        edsContext.selectedKey.needPassword = keyList[0].needPassword;
        if (!edsContext.selectedKey.needPassword) {
          return signService.selectKey(edsContext.selectedKey.key, "");
        }
      }
    }).catch(catchLastError);
  };

  $scope.isNoChoice = function () {
    return $scope.edsContext.keyList.length < 2;
  };

  $scope.sign = function () {
    removeLastError();
    var edsContext = $scope.edsContext;
    if (edsContext.selectedKey.password || !edsContext.selectedKey.needPassword) {
      $q.when(edsContext.selectedKey.needPassword && edsContext.selectedKey.password ?
        signService.selectKey(edsContext.selectedKey.key, edsContext.selectedKey.password)
        : true).then(function () {
        var contentHash = md5.createHash($scope.content);
        return signService.sign(contentHash).then(function (signedContent) {
          $scope.signedContent = signedContent;
          console.log($scope.signedContent);
          $modalInstance.close($scope.signedContent);
        });
      }).catch(catchLastError);
    } else {
      catchLastError({msg: 'Потрібно ввести пароль до ключа'});
    }
  };

  $scope.cancel = function () {
    $modalInstance.dismiss();
  };
};
