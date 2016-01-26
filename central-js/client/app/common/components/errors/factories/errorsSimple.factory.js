/*
  This factory is for displaying error messages
  to add new message to display you should use a push method
  the error object has two fields:
    type - defines a message style
    text - message displaying to user
*/
//angular.module('app').directive('dropdownOrgan', function (OrganListFactory, $http, $timeout) {
//angular.module("app").factory("ErrorsFactory", function() {
angular.module("app").factory("SimpleErrorsFactory", function() {
  /* existing error types */
  var errorTypes = ["warning", "danger", "success", "info"],
      errors = []; /*errors container for objects like {type: "...", text: "..."}*/

        /*var oData = {"s":"asasas"};
        $.extend(oData,{sDTat:"dddddd"});
        var a=[];
        a=a.concat(["1"]);*/ //{"code":"SYSTEM_ERR","message":null}


  return {
 
      
    /*
      returns all existing errors
    */
    getErrors: function() {
      return errors;
    },
    /*
      adding a new error message to errors collection
      @example ... ErrorsFactory.push({type:"warning", text: "Critical Error"});
    */
    push: function(oMessage) {
      if(!oMessage) return;
        //message.type = errorTypes.indexOf(message.type) >= 0 ? message.type : "danger";
        //message.warn = message.type === "danger" ? "Помилка" : "";
        if(oMessage.type && !oMessage.sType){
            oMessage.sType = oMessage.type;
            oMessage.type=null;
        }
        if(oMessage.oData && oMessage.oData.sType){
            oMessage.sType = oMessage.oData.sType;
        }
        
        oMessage.sType = errorTypes.indexOf(oMessage.sType) >= 0 ? oMessage.sType : "danger";
        oMessage.sHead = oMessage.sType === "danger" ? "Помилка" : "";
        if(oMessage.text){
            oMessage.sBody = oMessage.text;
            oMessage.text=null;
        }
        oMessage.sDate = new Date();
        /*      
        <div class="modal-body">
          <p class="text-{{error.type}}">
            {{error.text}}
          </p>
          <div ng-if="error.aText" class="text-{{error.type}}">
            <label ng-repeat="oText in error.aText" class="text-{{error.type}}">
              {{oParam.oText}}
            </label>
          </div>
          <p ng-if="error.sFunc" class="text-{{error.type}}">
            Функція: {{error.sFunc}}
          </p>
          <p ng-if="error.sDate" class="text-{{error.type}}">
            Час: {{error.sDate}}
          </p>
          <div ng-if="error.mParam" class="text-{{error.type}}">
            Значення параметрів:
            <label ng-repeat="oParam in error.mParam" class="text-{{error.type}}">
              {{oParam.sName}}: {{oParam.sValue}}
            </label>
          </div>
        </div>
        */
        if(oMessage.oData){
            //angular.forEach(message.aData, function(oData){
            angular.forEach(oMessage.oData, function (oValue, sKey) {
                if(sKey==="sHead"){
                    oMessage.sHead = (oMessage.sType === "danger" ? "Помилка" : oMessage.sType === "warning" ? "Попередження" : "Інформація") + " по операції: '" + oValue + "'";
                } else if(sKey==="sBody"){
                    if(oMessage.sBody){
                        oMessage.sBody=oMessage.sBody+"<br>"
                    }else{
                        oMessage.sBody="";
                    }
                    oMessage.sBody = oMessage.sBody + oValue;
                }
                /*if(sKey==="asParam"){
                angular.forEach(oValue, function(oParam){
                    //{{oParam.sName}}: {{oParam.sValue}}
                });
                    message.sFunc = oData.sFunc;
                }else if(oData.sHead){
                    message.sHead = oData.sHead;
                }else if(oData.sFunc){                    
                }*/
            });
            //aText
       }else{
            oMessage.oData={};
       }
       if(!oMessage.bHide){
            errors.push(oMessage);
       }
        //ErrorsFactory.push({type: "danger", text: s});
        //this.send(oMessage);
///        LogSend.send(oMessage);
        
    }
  };

});
