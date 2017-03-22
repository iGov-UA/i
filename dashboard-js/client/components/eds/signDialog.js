angular.module('dashboardJsApp')
  .factory('signDialog', function ($rootScope, $modal, $q) {
    function openModal(modalScope, modalClass) {
      modalScope = modalScope ? modalScope : $rootScope.$new();
      modalClass = modalClass || 'modal-info';

      return $modal.open({
        templateUrl: 'components/eds/signDialog.html',
        windowClass: modalClass,
        scope: modalScope,
        controller: SignDialogInstanceCtrl
      });
    }

    function signContent(contentDataOrLoader, resultCallback, dismissCallback, errorCallback) {
      var modalScope = $rootScope.$new();
      var signModal = openModal(modalScope);

      $q.when(contentDataOrLoader).then(function (contentData) {
        modalScope.contentData = contentData;
        signModal.result.then(function (signedContent) {
          resultCallback(signedContent);
        }, function () {
          dismissCallback();
        });
      }).catch(errorCallback);
    }

    return {
      /**
       * pass contentData = { id : "id of data", content : "real data content"}
       * or pass promise that will return contentData object
       *
       * resultCallback will return :
       * {
       *    id : contentData.id,
       *    content: contentData.content,
       *    signedContentHash : signedContentHash
       *  }
       */
      signContent: signContent
    }
  });

var SignDialogInstanceCtrl = function ($scope, $modalInstance, signService, md5, $q) {
  function removeLastError() {
    $scope.lastError = undefined;
  }

  function catchLastError(error) {
    $scope.lastError = error;
  }

  var isInitialized = signService.init();

  $scope.isInitialized = isInitialized;

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
        var contentHash = md5.createHash($scope.contentData.content);
        return signService.sign(contentHash).then(function (signedContentHash) {
          console.log(signedContentHash);
          $modalInstance.close({
            id: $scope.contentData.id,
            content: $scope.contentData.content,
            signedContentHash: signedContentHash
          });
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
