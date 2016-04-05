angular.module('feedback').controller('FeedbackController',function($state,$scope,$location, FeedbackService, ErrorsFactory) {

  $scope.commentExist = false;
  $scope.commentBody = "";

  //var nID = $location.search().nID;
  var nID = $location.search().sID_Order;
  
  var sSecret = $location.search().sSecret;

  if (!nID || !sSecret){
    window.location = "/";
  }

  $scope.postComment = function(){
    FeedbackService.postFeedback(nID, sSecret, $scope.commentBody);
    window.location="/";
  };

  FeedbackService.getFeedback(nID,sSecret).then(function(oResponse){
    var oFuncNote = {sHead:"Завантаженя фідбеку", sFunc:"getFeedback"};
    ErrorsFactory.init(oFuncNote, {asParam:['nID: '+nID,'sSecret: '+sSecret]});

    if(ErrorsFactory.bSuccessResponse(oResponse)){
        if(!!oResponse.sDate){
            $scope.commentExist = true;
            $scope.commentDate = oResponse.sDate;
        }
        /*if (!!oResponse.sDate){
          $scope.commentExist = true;
          $scope.commentDate = oResponse.sDate;
        }*/
        $scope.commentHead = !!oResponse.sHead ? oResponse.sHead : "";
    }

  }, function (oError){

    switch (oError.message){
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
        $scope.messageError = true;
        ErrorsFactory.logFail({sBody:"Невідома помилка!",sError:oError.message});
        //ErrorsFactory.logWarn({sBody:sErrorText});
        //pushError(error.message);
        break;
    }
  });

  function pushError(sErrorText){
    $scope.messageError = true;
    ErrorsFactory.logWarn({sBody:sErrorText});
    /*ErrorsFactory.push({
      type: "danger",
      text:  sErrorText
    });*/
  }
});
