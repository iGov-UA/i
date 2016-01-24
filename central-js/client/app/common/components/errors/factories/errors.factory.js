/*
  This factory is for displaying error messages
  to add new message to display you should use a push method
  the error object has two fields:
    type - defines a message style
    text - message displaying to user
*/
angular.module("app").factory("ErrorsFactory", function() {
  /* existing error types */
  var errorTypes = ["warning", "danger", "success", "info"],
      errors = []; /*errors container for objects like {type: "...", text: "..."}*/


  var oDataErrors = {};
  var oDataDefaultCommon = {};
    
  return {
    
    logDebug: function(oDataErrorsNew, oDataDefault){
        if(!oDataDefault){
            oDataDefault=oDataDefaultCommon;
        }
        console.error("oDataErrorsNew="+JSON.stringify($.extend(oDataErrorsNew,oDataDefault)));
    },
    logFail: function(oDataErrorsNew, oDataDefault){
        addFail(oDataErrorsNew, oDataDefault);
        checkSuccess();
    },
    addFail: function(oDataErrorsNew, oDataDefault){
        oDataDefaultCommon = oDataDefault ? oDataDefault : oDataDefaultCommon;
        //oDataErrors=oDataErrors.concat(oDataErrorsNew);
        oDataErrors=$.extend(oDataErrors,oDataErrorsNew);
    },
    checkReset: function(oDataDefault){
        oDataDefaultCommon = oDataDefault ? oDataDefault : {};
        oDataErrors = {};
    },
    checkSuccess: function(oDataDefault){
        bCheckSuccess(oDataDefault);
    },
    bCheckSuccess: function(oDataDefault){
        if(oDataErrors>0){
            if(!oDataDefault){
                oDataDefault=oDataDefaultCommon;
            }
            console.error("oDataErrorsNew="+JSON.stringify(oDataErrors));
            var oData=oDataErrors;
            checkReset(oDataDefaultCommon);
            //ErrorsFactory.push({type: "danger", text: s});
            push({type: "danger", oData: oData});
        }else{
            return true;
        }
    },
    
    bCheckSuccessResponse: function(oData, onCheckMessage, oDataDefault){
        /*var oData = {"s":"asasas"};
        $.extend(oData,{sDTat:"dddddd"});
        var a=[];
        a=a.concat(["1"]);*/ //{"code":"SYSTEM_ERR","message":null}
        if(!oDataDefault){
            oDataDefault=oDataDefaultCommon;
        }
        //var asMessage = [];
        //oDataErrorsNew = [];
        checkReset(oDataDefaultCommon);
        try{
            if (!oData) {
                //oDataErrors=oDataErrors.concat(['Пуста відповідь на запит!']);
                if(onCheckMessage!==null){
                    var oDataErrorsResponseNew = onCheckMessage(null,null);
                    if(oDataErrorsResponseNew!==null){
                        addFail(oDataErrorsResponseNew);
                    }
                }
                if(!oDataErrors.sBody){
                    addFail({sBody: 'Пуста відповідь на запит!'});
                }
            }else{
                if (typeof oData !== 'object') {
                    var nError=0;
                    var oDataErrorsResponse={};
                    if (oData.hasOwnProperty('message')) {
                        if(onCheckMessage!==null){
                            var oDataErrorsResponseNew = onCheckMessage(oData.message);
                            if(oDataErrorsResponseNew!==null){
                                //oDataErrors=oDataErrors.concat(asMessageNew);
                                //addFail(asMessageNew);
    //                            oDataErrorsResponse=$.extend(oDataErrorsResponse,oDataErrorsResponseNew);
                                addFail(oDataErrorsResponseNew);
                            //}else{
                                //oDataErrors=oDataErrors.concat(['Message: '+oData.message]);
                                //addFail({sServerMessage: oData.message});
                            }
    //                        oDataErrorsResponse=$.extend(oDataErrorsResponse,{sMessage: oData.message});
    //                    }else{
                            //oDataErrors=oDataErrors.concat(['Message: '+oData.message]);
                            //addFail({sServerMessage: oData.message});
    //                        oDataErrorsResponse=$.extend(oDataErrorsResponse,{sMessage: oData.message});
                        }
                        oDataErrorsResponse=$.extend(oDataErrorsResponse,{sMessage: oData.message});
                        oData.message=null;
                        nError++;
                    }
                    if (oData.hasOwnProperty('code')) {
                        if(onCheckMessage!==null){
                            var oDataErrorsResponseNew = onCheckMessage(null,oData.code);
                            if(oDataErrorsResponseNew!==null){
                                addFail(oDataErrorsResponseNew);
                            }
                        }
                        //oDataErrors=oDataErrors.concat(['Code: '+oData.code]);
                        //addFail({sServerCode: oData.code});
                        oDataErrorsResponse=$.extend(oDataErrorsResponse,{sCode: oData.code});
                        oData.code=null;
                        nError++;
                    }
                    if(nError>0){
                        //oDataErrors=oDataErrors.concat(['oData: '+oData]);
                        //addFail(oData);
                        //addFail({oServerData: oData});
                        //oDataErrorsResponse=$.extend(oDataErrorsResponse,{oData: oData});
                        if(nError!==2 && !oDataErrors.sBody){
                            addFail({sBody:'Повернено не стандартній об`єкт!'});
                        }
                        oDataErrorsResponse=$.extend(oDataErrorsResponse,{soData: JSON.stringify(oData)});
                    }
                    addFail({oResponse:oDataErrorsResponse});
                }else{
                    //ErrorsFactory.addFail({sBody:'Помилка - повернено не об`єкт!', asParam:['sID_Order: '+sID_Order,'sToken: '+sToken,'oData: '+oData]});
                    if(onCheckMessage!==null){
                        var oDataErrorsResponseNew = onCheckMessage(null,null);
                        if(oDataErrorsResponseNew!==null){
                            addFail(oDataErrorsResponseNew);
                        }
                    }
                    oDataErrorsResponse=$.extend(oDataErrorsResponse,{sData: JSON.stringify(oData)});
                    addFail({oResponse:oDataErrorsResponse});
                    if(!oDataErrors.sBody){
                        addFail({sBody:'Повернено не об`єкт!'});
                    }
                }
            }
        }catch(sError){
            //oDataErrors=oDataErrors.concat(['Невідома помилка!','oData: '+oData,'sError: '+sError]);
            //addFail({sBody: 'Невідома помилка у обробці відповіді сервера!'});
            //addFail({oServerData: oData});
            //addFail({sServerUnknown: sError});
            /*oDataErrorsResponse=$.extend(oDataErrorsResponse,{sBody: 'Невідома помилка у обробці відповіді сервера!'});
            oDataErrorsResponse=$.extend(oDataErrorsResponse,{oResponse: {oData: oData}});
            oDataErrorsResponse=$.extend(oDataErrorsResponse,{sError: sError});*/
            //addFail({sBody: 'Невідома помилка у обробці відповіді сервера!', Error: sError, oResponse: {oData: oData}});
            addFail({sBody: 'Невідома помилка у обробці відповіді сервера!', Error: sError, oResponse: {soData: JSON.stringify(oData)}});
        }
        if(oDataErrors.length>0){
            //oDataErrorsNew=asMessageDefault.concat(oDataErrorsNew);
            addFail(oDataDefault);
            console.error('[asErrorMessages]:oDataErrors='+JSON.stringify(oDataErrors));
            return true;
        }
        //return oDataErrorsNew;
        //return oDataErrorsNew;
    },      
      
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
        if(oMessage.type){
            oMessage.sType = oMessage.type;
            oMessage.type=null;
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
                    oMessage.sHead = "Помила в операції: " + oValue + "'";
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
        errors.push(oMessage);
      //ErrorsFactory.push({type: "danger", text: s});
    }
  };
});
