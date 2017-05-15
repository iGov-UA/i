angular.module('dashboardJsApp')
  .factory('signDialog', function ($rootScope, $modal, $q, $base64) {
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

    function signContent(contentDataOrLoader, resultCallback, dismissCallback, errorCallback, modalClass) {
      $q.when(contentDataOrLoader).then(function (contentData) {
        var modalScope = $rootScope.$new();
        modalScope.contentData = contentData;
        var signModal = openModal(modalScope, modalClass);
        signModal.result.then(function (signedContent) {
          resultCallback(signedContent);
        }, function () {
          dismissCallback();
        });
      }).catch(errorCallback);
    }

    function signManuallySelectedFile(resultCallback, dismissCallback, errorCallback) {
      var modalScope = $rootScope.$new();
      modalScope._isManuallySelectedFile = true;
      var signModal = openModal(modalScope);

      signModal.result.then(function (signedContent) {
        resultCallback(signedContent);
      }, function () {
        dismissCallback();
      }).catch(errorCallback);
    }

    return {
      /**
       * pass contentData = { id : "id of data", content : "real data content", base64encoded: "true/false"}
       * or pass promise that will return contentData object
       *
       * resultCallback will return :
       * {
       *    id : contentData.id,
       *    content: contentData.content,
       *    certificate: certificate in base64
       *    sign : sign in base64 (CMS sign result that can be saved as pdf)
       *  }
       */
      signContent: signContent,
      signManuallySelectedFile: signManuallySelectedFile
    }
  });

var SignDialogInstanceCtrl = function ($scope, $modalInstance, signService, md5, $q, $base64) {
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

  $scope.isManuallySelectedFile = function () {
    return $scope._isManuallySelectedFile;
  };

  $scope.fileChanged = function (element) {
    $scope.$apply(function (scope) {
      var selectedFile = element.files[0];
      var reader = new FileReader();
      reader.onload = function (e) {
        var loadedContent = e.target.result.split("base64,")[1];
        var id = selectedFile.name;
        scope.contentData = {id: id, content: loadedContent, base64encoded: true};
      };
      reader.readAsDataURL(selectedFile);
    });
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

        return signService.signCMS($scope.contentData.content, !$scope.contentData.base64encoded)
          .then(function (signResult) {
            var sign = signResult.sign;
            var certBase64 = signResult.certificate;

            $modalInstance.close({
              id: $scope.contentData.id,
              content: $scope.contentData.content,
              certificate: certBase64,
              sign: sign
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
