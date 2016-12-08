'use strict';
angular.module("iGovErrors").directive("errorsContainer", function ($modal) {

  return {
    restrict: "A",
    controller: "ErrorsController",
    link: function ($scope) {
      $scope.$watch(function () {
        return $scope.errors.length;
      }, function (value) {
        if (value > 0) {
          var modalInstance = $modal.open({
            animation: true,
            size: 'md',
            //templateUrl: '../../public-js/errors/modal/errorsModal.html',
            controller: 'ErrorsModelController',
            resolve: {
              error: function () {
                return $scope.errors[0];
              }
            },
            template: '' +
            '<div class="modal-header bg-{{error.sType}}">' +
            '  <h3 class="modal-title text-{{error.sType}}">{{error.sHead}}</h3>' +
            '</div>' +
            '<div class="modal-body">' +
            '  <p ng-show="error.nID" class="text-{{error.sType}}">' +
            '    <b>Відправлено на сервер під номером: {{error.nID}}</b>' +
            '  </p>' +
            '  <b>' +
            '    <p ng-bind-html="error.sBody" class="text-{{error.sType}}"></p>' +
            '  </b>' +
            '  <p ng-bind-html="error.sNote" class="text-{{error.sType}}"></p><br>' +
            '  <p class="text-{{error.sType}}" ng-if="error.isNoSuccessType">' +
            '    <span>Час: {{error.sDate ? error.sDate : "(невідомо)"}}</span>' +
            '    <span ng-if="error.oData.sFunc">, Функція: {{error.oData.sFunc}}</span>' +
            '  </p>' +
            '  <p ng-if="error.oData.sError" class="text-{{error.sType}}">Виключна ситуація: {{error.oData.sError}}</p>' +
            '  <div ng-if="error.oData.oResponse" class="text-{{error.sType}}">' +
            '    <b>Відповідь сервера:</b><br>' +
            '    <label ng-if="error.oData.oResponse.sMessage" class="text-{{error.sType}}">Повідомлення: {{error.oData.oResponse.sMessage}}</label>' +
            '    <label ng-if="error.oData.oResponse.sCode" class="text-{{error.sType}}">Код: {{error.oData.oResponse.sCode}}</label>' +
            '    <label ng-if="error.oData.oResponse.soData" class="text-{{error.sType}}">Інші дані (обь`єкт): {{error.oData.oResponse.soData}}</label>' +
            '    <label ng-if="error.oData.oResponse.sData" class="text-{{error.sType}}">Інші дані (строка): {{error.oData.oResponse.sData}}</label>' +
            '  </div>' +
            '  <div ng-if="error.oData.asParam && error.isNoSuccessType" class="text-{{error.sType}}">' +
            '    <b>Значення параметрів:</b><br>' +
            '    <label ng-repeat="sParam in error.oData.asParam" class="text-{{error.sType}}">{{sParam}},</label><br>' +
            '  </div>' +
            '  <p ng-show="false && error.bSending" class="text-{{error.sType}}">Дані відсилаються на сервер...</p>' +
            '</div>' +
            '<div class="modal-footer">' +
            '  <button class="btn btn-default" type="button" ng-click="close(error)">Закрити</button>' +
            '</div>'
          });

          modalInstance.result.then(function (el) {
            $scope.errors.splice($scope.errors.indexOf(el), 1);
          });
        }
      });
    }
  }
})
;
