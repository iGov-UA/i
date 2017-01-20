'use strict';
angular.module('dashboardJsApp').directive('fileField', function() {
  return {
    require: 'ngModel',
    restrict: 'E',
    link: function(scope, element, attrs, ngModel) {
      var fileField = element.find('input');

      fileField.bind('change', function(event) {
        scope.$apply(function() {
          scope.upload(event.target.files, attrs.name);
        });
      });

      fileField.bind('click', function(e) {
        e.stopPropagation();
      });
      element.bind('click', function(e) {
        e.preventDefault();
        fileField[0].click();
      });
    },
    template: '<form>' +
                '<button type="button" ng-disabled="isFileProcessUploading.bState" ng-class="{\'btn-igov\':field && field.value, \'btn-link attach-btn\':!field, \'btn-default\':field && !field.value}" class="btn">' +
                  '<span ng-disabled="isFormPropertyDisabled(item)">{{field && field.value ? "Завантажити iнший файл" : "Завантажити файл"}}</span>' +
                  '<input type="file" style="display:none" ng-disabled="isFormPropertyDisabled(item)">' +
                '</button>' +
                '<span ng-if="item.fileName || field.fileName">Файл: <label>{{item.fileName || field.fileName}}</label></span>' +
                '<br>' +
                '<span ng-if="field.signInfo">Пiдпис: <label>{{field.signInfo.customer.signatureData.name || field.signInfo.name}}</label></span>' +
              '</form>',
    replace: true,
    transclude: true
  };
});
