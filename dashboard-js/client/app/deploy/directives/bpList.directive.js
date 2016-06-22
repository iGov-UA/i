/**
 * Created by GFalcon on 08.06.2016.
 */
angular.module('dashboardJsApp')
  .directive('bpList', function () {
    var controller = function ($scope, Modal) {
      $scope.inProgress = false;
      $scope.isBPfileUploading = false;
      $scope.aBPs = [];

      var uploadFunc = $scope.funcs.setBP;
      var downloadFunc = $scope.funcs.getBP;
      var getFunc = $scope.funcs.getListBP;
      var deleteFunc = $scope.funcs.removeListBP;

      $scope.bViewFilterSetting = false;
      $scope.bSettingDeleteFilter = false;
      $scope.oFilter = {
        sID_BP: '',
        sFieldType: '',
        sID_Field: '',
        sVersion: ''
      };

      var fillData = function (filter) {
        $scope.inProgress = true;
        getFunc(filter)
          .then(function (list) {
            $scope.aBPs = list;
          })
          .finally(function () {
            $scope.inProgress = false;
            $scope.bViewFilterSetting = false;
            $scope.bSettingDeleteFilter = false;
          });
      };

      $scope.openViewFilter = function(){
        if ($scope.bViewFilterSetting && $scope.bSettingDeleteFilter){
          $scope.bSettingDeleteFilter = false;
        } else {
          $scope.bViewFilterSetting = !$scope.bViewFilterSetting;
          $scope.bSettingDeleteFilter = false;
        }
      };

      $scope.openRemoveFilter = function(){
        if ($scope.bViewFilterSetting && !$scope.bSettingDeleteFilter){
          $scope.bSettingDeleteFilter = true;
        } else if($scope.bViewFilterSetting) {
          $scope.bViewFilterSetting = false;
          $scope.bSettingDeleteFilter = false;
        } else {
          $scope.bViewFilterSetting = true;
          $scope.bSettingDeleteFilter = true;
        }
      };

      $scope.closeFilter = function(){
        $scope.bViewFilterSetting = false;
        $scope.bSettingDeleteFilter = false;
      };

      $scope.delete = function (filter) {
        Modal.confirm.delete(function (event) {
          deleteFunc(filter)
            .then($scope.closeFilter());
        })('кілька бізнес-процесів, які відповідають налаштуванню фільтра!');
      };

      $scope.init = function (filter) {
        fillData(filter);
      };

      var fileUploadField = $("#file-field");

      fileUploadField.bind('change', function(event) {
        var targetFiles = event.target.files;
        if(targetFiles[0].name){
          var sFileExt = targetFiles[0].name.split('.').pop().toLowerCase();
          if (sFileExt === "bpmn"){
            $scope.isBPfileUploading = true;
            uploadFunc(targetFiles, targetFiles[0].name);
          } else {
            Modal.inform.error()('Не підтримуємий формат файлу');
          }
        }
      });

      fileUploadField.bind('click', function(e) {
        e.stopPropagation();
      });

      $("#uploader").bind('click', function(e) {
        e.preventDefault();
        fileUploadField[0].click();
      });

      $scope.$on("end-deploy-bp-file", function(){
        $scope.isBPfileUploading = false;
        if($scope.aBPs > 0){
          fillData($scope.oFilter);
        }
      });

      $scope.downloadItem = function(oBPinTable){
        downloadFunc(oBPinTable.id);
      };

      $scope.removeItem = function(oBPinTable){
        var index = $scope.aBPs.indexOf(oBPinTable);
        if (index !== -1) {
          $scope.aBPs.splice(index, 1);
          var remObj = {
            sID_BP: oBPinTable.id
          };
          Modal.confirm.delete(function (event) {
            deleteFunc(remObj)
              .then(fillData($scope.oFilter));
          })('бізнес-процес "' + oBPinTable.name + '" (ID ' + oBPinTable.id + ')');
        } else {
          Modal.inform.warning()('Цей бізнес-процес вже видалений. Будь ласка, перезавантажте фільтр для оновлення відображаємої інформації.');
        }
      }

    };
    return {
      restrict: 'EA',
      scope: {
        funcs: '='
      },
      controller: controller,
      templateUrl: 'app/deploy/directives/bpList.html'
    }
  });
