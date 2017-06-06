'use strict';

angular.module('app').service('modalService', ['$modal', function ($modal) {

    var modalDefaults = {
      backdrop: true,
      keyboard: true,
      modalFade: true,
      templateUrl: 'app/common/components/form/directives/modal-choices.template.html'
    };

    var modalOptions = {
      closeButtonText: 'Скасувати',
      actionButtonText: 'Підтвердити',
      headerText: 'Попередження',
      bodyText: 'Ви бажаєте підтвердити операцію?'
    };

    this.showModal = function (customModalDefaults, customModalOptions) {
      if (!customModalDefaults) customModalDefaults = {};
      customModalDefaults.backdrop = 'static';
      return this.show(customModalDefaults, customModalOptions);
    };

    this.show = function (customModalDefaults, customModalOptions) {
      var tempModalDefaults = {};
      var tempModalOptions = {};

      angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);
      angular.extend(tempModalOptions, modalOptions, customModalOptions);

      if (!tempModalDefaults.controller) {
        tempModalDefaults.controller = ['$scope', '$modalInstance', function ($scope, $modalInstance) {
          $scope.modalOptions = tempModalOptions;
          $scope.modalOptions.ok = function (result) {
            $modalInstance.close(result);
          };
          $scope.modalOptions.close = function (result) {
            $modalInstance.dismiss('cancel');
          };
        }]
      }

      return $modal.open(tempModalDefaults).result;
    };

  }]);
