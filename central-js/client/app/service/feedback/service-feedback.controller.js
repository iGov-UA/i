(function () {

  'use strict';

  angular.module('app')
  .controller('ServiceFeedbackController', ServiceFeedbackController);

  /* @ngInject */
  function ServiceFeedbackController(SimpleErrorsFactory, $state, $stateParams, $scope, service,
                                     ServiceService, FeedbackService, ErrorsFactory,
                                     AdminService, UserService) {

    $scope.nID = null;
    $scope.sID_Token = null;
    $scope.submitted = false;
    $scope.feedback = {
      messageBody: '',
      messageList: [],
      allowLeaveFeedback: false,
      feedbackError: false,
      postFeedback: postFeedback,
      rateFunction: rateFunction,
      sendAnswer: sendAnswer,
      answer: answer,
      hideAnswer: hideAnswer,
      rating: 3,
      exist: false,
      readonly: true,
      isAdmin: false,
      showAnswer: false,
      relativeTime: relativeTime
    };

    activate();

    function activate() {

      if (!ServiceService.oService.nID) {
        SimpleErrorsFactory.push({
          type: "denger",
          oData: {
            sHead: 'Послуга не існує!',
            sBody: 'Виберіть, будьласка, існуючу послугу.'
          }
        });
        return;
      }

      UserService.isLoggedIn().then(function (result) {
        if (result) {
          UserService.fio().then(function (res) {
            $scope.feedback.isAdmin = AdminService.isAdmin();
          });
        }
      });

      $scope.$on('logout.event', function (event, data) {
        $scope.feedback.isAdmin = data.isLogged;
      });

      $scope.nID = $stateParams.nID;
      $scope.sID_Token = $stateParams.sID_Token;
      $scope.feedback.sSubjectOperatorName = service.sSubjectOperatorName;

      if ($scope.nID && $scope.sID_Token) {
        $scope.feedback.allowLeaveFeedback = true;
      }
      refresh();
    }

    $scope.loadMoreFeedbackMessagesAvailable = false;

    $scope.messagesLoadingProgress = false;

    function loadNextMessages() {
      $scope.messagesLoadingProgress = true;
      var nRowsMax = 20;
      var nID__LessThen_Filter;
      if ($scope.feedback.messageList.length)
        nID__LessThen_Filter = $scope.feedback.messageList[$scope.feedback.messageList.length - 1].nID;
      FeedbackService.getFeedbackListForService(ServiceService.oService.nID, nRowsMax, nID__LessThen_Filter)
        .then(function (response) {
          var newMessages = response.data;

          newMessages.forEach(function(item){
            var sMail = item.sMail ? item.sMail.trim().match(/null/gi) : []
              , sPlace = item.sPlace ? item.sPlace.trim().match(/null/gi) : [];

            item.sMail = Array.isArray(sMail) && sMail[0] ? '' : item.sMail;
            item.sPlace = Array.isArray(sPlace) && sPlace[0] ? '' : item.sPlace;
          });

          $scope.loadMoreFeedbackMessagesAvailable = newMessages.length >= nRowsMax;

          newMessages = _.filter(newMessages, function (o) {
            var filters = o.sAuthorFIO.trim().match(/null/gi);

            return ((typeof o.sBody) === 'string' ? !!o.sBody.trim() : false)
              && !(Array.isArray(filters) && filters[0] ? filters[0].trim() === 'null' : false);
          });

          $scope.feedback.messageList = $scope.feedback.messageList.concat(newMessages);
        }).finally(function () {
          $scope.messagesLoadingProgress = false;
      });
    };

    $scope.loadMoreFeedbackMessages = function () {
      loadNextMessages();
    };

    function refresh() {
      loadNextMessages();

      if ($scope.nID && $scope.sID_Token) {
        FeedbackService.getFeedbackForService(ServiceService.oService.nID, $scope.nID, $scope.sID_Token)
        .then(function (response) {

          $scope.feedback.rating = response.data.nID_Rate;
          $scope.feedback.exist = !!response.data.oSubjectMessage;
          $scope.feedback.messageBody = response.data.oSubjectMessage ? response.data.oSubjectMessage.sBody : null;
          $scope.feedback.currentFeedback = angular.copy(response.data);

        }, function (error) {
          switch (error.message) {
            case "Security Error":
              pushError("Помилка безпеки!");
              break;
            case "Record Not Found":
              pushError("Запис не знайдено!");
              break;
            case "Already exist":
              pushError("Вiдгук вже залишено!");
              break;
            default :
              $scope.feedback.feedbackError = true;
              ErrorsFactory.logFail({sBody: "Невідома помилка!", sError: error.message});
              break;
          }
        }).finally(function () {
          $scope.loaded = true;
        });
      }
    }

    function rateFunction(rating) {
      $scope.feedback.rating = rating;
    }

    function postFeedback() {
      var sAuthorFIO = $scope.feedback.currentFeedback.sAuthorFIO
        , sMail = $scope.feedback.currentFeedback.sMail
        , sHead = $scope.feedback.currentFeedback.sHead
        , sPlace = $scope.feedback.currentFeedback.sPlace
        , sEmployeeFIO = $scope.feedback.currentFeedback.sEmployeeFIO;

      $scope.submitted = true;

      if (!((typeof $scope.feedback.messageBody) === 'string' ? !!$scope.feedback.messageBody.trim() : false)
        || !$scope.feedback.rating) {
        return;
      }

      var feedbackParams = {
        'sID_Token': $scope.sID_Token,
        'sBody': $scope.feedback.messageBody,
        'sID_Source': 'iGov',
        'nID': $scope.nID,
        'sAuthorFIO': sAuthorFIO,
        'sMail': sMail,
        'sHead': sHead,
        'nID_Rate': $scope.feedback.rating,
        'nID_Service': ServiceService.oService.nID,
        'sPlace': sPlace,
        'sEmployeeFIO': sEmployeeFIO
      };

      FeedbackService.postFeedbackForService(feedbackParams)
      .finally(function () {
        refresh();
        $state.go('index.service.feedback', {
          nID: null,
          sID_Token: null
        });
      });
    }

    function sendAnswer(data) {
      var sHead = '';

      var feedbackParams = {
        'sID_Token': $scope.sID_Token,
        'sBody': data.sAnswer.sText,
        'nID_SubjectMessageFeedback': data.nID,
        'sAuthorFIO': data.sAnswer.sAuthorFIO,
        'nID_Service': data.nID_Service,
        'nID_Subject': $state.nID_Subject
      };

      FeedbackService.postFeedbackAnswerForService(feedbackParams).finally(function () {
        refresh();
      });
      hideAnswer();
    }

    function answer(commentID) {
      $scope.feedback.commentToShowAnswer = commentID;
    }

    function hideAnswer() {
      $scope.feedback.commentToShowAnswer = -1;
    }

    function pushError(sErrorText) {
      $scope.messageError = true;

      //TODO Maybe it worth to use ErrorsFactory.push({type: "danger",text:  sErrorText}) here
      ErrorsFactory.logWarn({sBody: sErrorText});
    }

    function relativeTime(dateStr) {
      if (!dateStr) {
        return;
      }

      var result = ''
        , date = $.trim(dateStr)
        , parsedDate = new Date(date)
        , minutes = parsedDate.getMinutes()
        , time = parsedDate.getHours() + ':' + (minutes < 10 ? '0' + minutes : minutes)
        , today = moment().startOf('day')
        , releaseDate = moment(date)
        , diffDays = today.diff(releaseDate, 'days', true);

      if (diffDays < 0) {
        result = 'сьогодні ' + time;
      } else if (diffDays < 1) {
        result = ' вчора ' + time;
      } else if (Math.floor(diffDays) <= 4) {
        result = diffDays.toFixed(0) + ' дні назад ' + time;
      } else {
        result = diffDays.toFixed(0) + ' днів назад ' + time;
      }

      return result;
    }
  }

})();
