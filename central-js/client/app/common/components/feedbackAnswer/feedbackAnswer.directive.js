(function () {
  'use strict';

  angular.module('app')
    .directive('feedbackAnswer', feedbackAnswer);

  function feedbackAnswer() {
    return {
      restrict: 'EA',
      templateUrl: 'app/common/components/feedbackAnswer/feedbackAnswer.html',
      scope: {
        currentFeedback: '=currentFeedback',
        sendHandler: '&onSend'
      },
      controller: FeedbackAnswerCtrl,
      controllerAs: 'vm',
      bindToController: true
    };
  }

  /* @ngInject */
  function FeedbackAnswerCtrl(ServiceService, FeedbackService) {
    var vm = this;

    vm.sendAnswer = sendAnswer;

    activate();

    function activate() {
    }

    function sendAnswer() {
      var currentFeedbackDraft = angular.copy(vm.currentFeedback);

      angular.extend(currentFeedbackDraft, {sAnswer: vm.answerBody});

      vm.sendHandler({answerData: currentFeedbackDraft});
    }
  }

})();
