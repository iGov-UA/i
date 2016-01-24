/*
  This factory is for displaying error messages
  to add new message to display you should use a push method
  the error object has two fields:
    type - defines a message style
    text - message displaying to user
*/
//angular.module('app').directive('dropdownOrgan', function (OrganListFactory, $http, $timeout) {
//angular.module("app").factory("ErrorsFactory", function() {
angular.module("app").factory("ErrorsFactory", function(SimpleErrorsFactory) {
  /* existing error types */
    
  return {

    send: function(oMessage){//oDataMessage
  
/*
 /api/order/setEventSystem/:sFunction
    @RequestMapping(value = "/action/event/setEventSystem", method = {RequestMethod.GET, RequestMethod.POST})
    public
    @ResponseBody
    Long setEventSystem(
            @ApiParam(value = "", required = false) @RequestParam(value = "sType", required = false) String sType,
            @ApiParam(value = "Номер-ИД субьекта", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "Номер-ИД сервера", required = false) @RequestParam(value = "nID_Server", required = false) Long nID_Server,
            @ApiParam(value = "", required = false) @RequestParam(value = "sFunction", required = false) String sFunction,
            @ApiParam(value = "", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "", required = false) @RequestParam(value = "sError", required = false) String sError,
            @ApiParam(value = "Карта других параметров", required = false) @RequestBody String smData
*/

    /*
      <p ng-bind-html="error.sBody" class="text-{{error.sType}}">
        <!--{{error.sBody}}-->
      </p>
      <p ng-if="error.sDate" class="text-{{error.sType}}">
        Час: {{error.sDate}}
      </p>
      <p ng-if="error.oData.sFunc" class="text-{{error.sType}}">
        Функція: {{error.oData.sFunc}}
      </p>
      <p ng-if="error.oData.sError" class="text-{{error.sType}}">
        Війняток: {{error.oData.sError}}
      </p>
      <div ng-if="error.oData.oResponse" class="text-{{error.sType}}">
        <b>Відповідь сервера:</b>
        <label ng-if="error.oData.oResponse.sMessage" class="text-{{error.sType}}">
            Повідомленя: {{error.oData.oResponse.sMessage}}
        </label>
        <label ng-if="error.oData.oResponse.sCode" class="text-{{error.sType}}">
            Код: {{error.oData.oResponse.sCode}}
        </label>
        <label ng-if="error.oData.oResponse.soData" class="text-{{error.sType}}">
            Інші дані (обь'єкт): {{error.oData.oResponse.soData}}
        </label>
        <label ng-if="error.oData.oResponse.sData" class="text-{{error.sType}}">
            Інші дані (строка): {{error.oData.oResponse.sData}}
        </label>
      </div>
      <div ng-if="error.oData.asParam" class="text-{{error.sType}}">
        <b>Значення параметрів:</b>
        <label ng-repeat="sParam in error.oData.asParam" class="text-{{error.sType}}">
          {{sParam}}
        </label><br>
      </div>
    */
        try{
            var bProcessing = false;
            var sendData = function (oMessage) {//oData//oDataMessage
                var sFunction=oMessage.oData.sFunc;
                var oParams={sHead:oMessage.sHead,sBody:oMessage.sBody,sError:oMessage.oData.sError,sType:oMessage.sType,sDate:oMessage.sDate};
                var oBody={oResponse:oMessage.oData.oResponse,asParam:oMessage.oData.asParam};
                var oBodyData={oParams:oParams, oBody:oBody};
                //oMessage.sHead
                /*    
                oParams = _.extend(oParams, req.body.oParams);
                var apiReq = activiti.buildRequest(req, '/action/event/setEventSystem', oParams);
                apiReq.body = req.body.oBody;
                */
                var bSending = true;
                oMessage.bSending=bSending;
                $http.post('./api/order/setEventSystem/' + sFunction, oBodyData).success(function (nID) {
                    bProcessing = true;
                    //angular.forEach(attributes, function(attr){
                      //console.log("attr.sName="+attr.sName+",currentKey="+currentKey);
                    //});
                    console.log("[send]:oMessage.nID="+oMessage.nID);
                    //$timeout(function(){
                      bProcessing = false;
                      bSending=false;
                    //});
                    oMessage.nID=nID;
                    oMessage.bSending=bSending;
                });
            };  
            //oDataMessage
            sendData(oMessage);
            /*angular.forEach(Object.keys(scope.formData.params), function (key) {
              scope.$watch('formData.params.' + key + '.value', function () {
                if (scope.ngModel !== null && scope.ngModel !== '0' && scope.ngModel.length > 0 && !bSent)
                  sendData(key);
              })
            });*/            
        }catch(sError){
            console.log("[send]:sError="+sError);
        }
    },
    
    /*
      returns all existing errors
    */
    getErrors: function() {
      return SimpleErrorsFactory.getErrors();
    },
    /*
      adding a new error message to errors collection
      @example ... ErrorsFactory.push({type:"warning", text: "Critical Error"});
    */
    push: function(oMessage) {
        SimpleErrorsFactory.push(oMessage);
        //ErrorsFactory.push({type: "danger", text: s});
        //this.send(oMessage);
        this.send(oMessage);
    }
    
    
  };

});
