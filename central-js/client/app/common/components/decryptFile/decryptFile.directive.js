(function(){
  'use strict';

  angular.module('app')
    .directive('decryptFile', decryptFile);

  function decryptFile(){
    return {
      restrict: 'EA',
      scope: {
        oServiceData: '=',
        onFileUploadSuccess: '&'
      },
      templateUrl: 'app/common/components/decryptFile/decryptFile.directive.html',
      controller: DecryptFileController,
      controllerAs: 'vm',
      bindToController: true,
      transclude: true
    };
  }

  /* @ngInject */
  function DecryptFileController($scope, $q, $location, $window, uiUploader, ActivitiService){
    var vm = this;
    var nID_Server = 5;

    vm.file = {};
    vm.onSelect = onSelect;

    activate();

    function activate(){
      $scope.$watch('vm.file.isUploading', function(){
      })
    }

    function onSelect($files){
      upload($files, {nID_Server:5});
    }

    function upload (files, oServiceData) {
      uiUploader.removeAll();
      uiUploader.addFiles(files);

      vm.file.fileName = files[0].name;

      uiUploader.startUpload({
        url: ActivitiService.getUploadFileURL(oServiceData),
        concurrency: 1,
        onProgress: function (file) {
          vm.file.isUploading = true;
        },
        onCompleted: function (file, fileid) {
          vm.file.value = {id : fileid, signInfo: null, fromDocuments: false};
        },
        onCompletedAll: function () {
          vm.file.isUploading = false;
          vm.onFileUploadSuccess(vm.file);
          $scope.$apply();

          decrypt(vm.file);

          console.log('All files loaded successfully');}
      });
    }

    function decrypt(result){
      $window.location.href = $location.protocol() + '://' +
        $location.host() + ':' +
        $location.port() + '/api/sign-content/decrypt?formID=' +
        result.value.id + '&nID_Server=' +
        nID_Server + '&sName=' + vm.file.fileName + '&restoreUrl=' + $location.absUrl();
    }
  }
})();
