(function () {
  'use strict';

  angular.module('app')
    .controller('AnswerContentController', AnswerContentController);

  function AnswerContentController($http, $scope, $state, $location, UserService) {
    var vm = this;

    vm.signedFileID = $state.params.signedFileID;
    vm.fileName = $state.params.fileName;
    vm.redirectUrl = $state.href('index.journal.answer', {error: ''});
  }
})();
