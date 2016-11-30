(function(){
  'use strict';

  angular.module('app')
    .directive('decryptFile', decryptFile);

  function decryptFile(){
    return {
      restrict: 'EA',
      scope: {
        options: '=',
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
  function DecryptFileController($scope, $q, $location, $window, ErrorsFactory, uiUploader, ActivitiService, MessagesService){
    var vm = this;
    var nID_Server = -1;

    vm.file = {};
    vm.onSelect = onSelect;
    vm.onDownload = onDownload;

    activate();

    function activate(){
      vm.file.isUploading = false;
    }

    function onSelect($files){
      upload($files, {nID_Server:nID_Server});
    }

    function onDownload(){
      MessagesService.getSubjectMessageData(vm.options.id).then(function (res) {
        if(angular.isString(res.data)){

          var content = new Blob([res.data], {type: 'application/x-www-form-urlencoded'});
          var arrFiles = [];

          arrFiles.push(content);
          upload(arrFiles, {nID_Server:nID_Server});

        } else {
          ErrorsFactory.push({type:"danger", text: "Виникла помилка при отриманні файлу"});
        }
      });
    }

    function upload (files, oServiceData) {
      uiUploader.removeAll();
      uiUploader.addFiles(files);

      vm.file.fileName = files[0].name || vm.options.fileName;

      uiUploader.startUpload({
        url: ActivitiService.getUploadFileURL(oServiceData),
        concurrency: 1,
        onProgress: function (file) {
          vm.file.isUploading = true;
          $scope.$apply();
        },
        onCompleted: function (file, fileId) {
          var fileObj;

          try{
            fileObj = JSON.parse(fileId);
          }catch(e){
            fileObj = {};
          }

          if(!fileObj.error){
            vm.file.value = {id : fileId, signInfo: null, fromDocuments: false};
          }else{
            vm.file.error = fileObj.error;
          }
          $scope.$apply();
        },
        onCompletedAll: function () {

          if(!vm.file.error){
            vm.onFileUploadSuccess(vm.file);
          }

          vm.file.isUploading = false;
          $scope.$apply();

          if(vm.file.error){
            ErrorsFactory.push({
              type: "denger",
              oData: {
                sHead: 'Помилка сервера.',
                sBody: 'Файл не завантажено.',
                sFunc: 'DecryptFileController'
              }
            });
          }

          if(!vm.file.error){
            decrypt(vm.file);
          }
        }
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
