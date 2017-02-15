(function () {
  'use strict';

  angular.module('app')
    .controller('AnswerContentController', AnswerContentController);

  function AnswerContentController($http, $scope, $state, $location, UserService) {
    var vm = this;

    vm.signedFileID = $state.params.signedFileID;
    vm.fileName = $state.params.fileName;
    vm.redirectUrl = $state.href('index.journal.answer', {error: ''});

    vm.parseDate = function (date) {
      console.warn('ДАТА!!!Контроллер - '+ date);
      if(date) return date.replace(/^(\d{2})(\d{2})(\d{4})$/, '$1.$2.$3');
    }
  }
})();
