angular.module('feedback').controller('FeedbackController',function($state,$scope,$location, FeedbackService, ErrorsFactory) {

  $scope.commentExist = false;
  $scope.commentBody = "";

  var nID = $location.search().nID;
  var sSecret = $location.search().sSecret;

  if (!nID || !sSecret){
    window.location = "/";
  }

  $scope.postComment = function(){
    FeedbackService.postFeedback(nID, sSecret, $scope.commentBody);
    window.location="/";
  };

  FeedbackService.getFeedback(nID,sSecret).then(function(data){
    if (!!data.sDate){
      $scope.commentExist = true;
      $scope.commentDate = data.sDate;
    }

    $scope.commentHead = !!data.sHead ? data.sHead : "";

  }, function (error){

    switch (error.message){
      case "Security Error":
        pushError("Помилка безпеки!");
        break;
      case "Record Not Found":
        pushError("Запис не знайдено!");
        break;
      case "Already exist":
        pushError("Вiдгук вже залишен!");
        break;
      default :
        pushError(error.message);
        break;
    }
  });

  function pushError(errorText){
    $scope.messageError = true;
    ErrorsFactory.push({
      type: "danger",
      text:  errorText
    });
  }
});
