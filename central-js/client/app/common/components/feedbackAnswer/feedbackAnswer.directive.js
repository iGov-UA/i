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
        parentFeedback: '=parentFeedback',
        sendHandler: '&onSend'
      },
      controller: FeedbackAnswerCtrl,
      controllerAs: 'vm',
      bindToController: true
    };
  }

  /* @ngInject */
  function FeedbackAnswerCtrl(UserService, $filter) {
    var vm = this;
    var userName;

    vm.sendAnswer = sendAnswer;

    activate();

    function activate() {
      UserService.isLoggedIn().then(function (result) {
        if (result) {
          UserService.fio().then(function (res) {
            userName = capitalize(res.firstName)
              + " " +
              capitalize(res.middleName)
              + " " +
              capitalize(res.lastName);
          });
        }
      });
    }

    function sendAnswer() {
      var currentFeedbackDraft = angular.copy(vm.currentFeedback);
      var currDate = $filter('date')(new Date(), "yyyy-MM-dd HH:mm:ss");

      var answer = {
        "sAuthorFIO":userName || ''
        ,"bSelf":false
        ,"sDate":currDate
        ,"sText":vm.answerBody
      };

      angular.extend(currentFeedbackDraft, {sAnswer: answer, nID_Service: vm.currentFeedback.nID_Service});
      vm.sendHandler({answerData: currentFeedbackDraft});
    }

    function capitalize(string) {
      return string !== null && string !== undefined ? string.charAt(0).toUpperCase() + string.slice(1).toLowerCase() : '';
    }
  }

})();
