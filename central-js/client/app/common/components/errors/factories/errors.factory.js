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

        /*var oData = {"s":"asasas"};
        $.extend(oData,{sDTat:"dddddd"});
        var a=[];
        a=a.concat(["1"]);*/ //{"code":"SYSTEM_ERR","message":null}


  var oDataInfos = {}; //console only
  var oDataWarns = {};
  var oDataErrors = {};
  var oDataDefaultCommon = {};
    
  return {
    init: function(oDataDefault){
        oDataDefaultCommon = oDataDefault ? oDataDefault : {};
        oDataErrors = {};
        oDataWarns = {};
        oDataInfos = {};        
    },
    addInfo: function(oDataInfosNew, oDataDefault){
        oDataDefaultCommon = oDataDefault ? oDataDefault : oDataDefaultCommon;
        oDataInfos=$.extend(oDataInfos,oDataInfosNew);
    },
    addWarn: function(oDataWarnsNew, oDataDefault){
        oDataDefaultCommon = oDataDefault ? oDataDefault : oDataDefaultCommon;
        oDataWarns=$.extend(oDataWarns,oDataWarnsNew);
    },
    addFail: function(oDataErrorsNew, oDataDefault){
        oDataDefaultCommon = oDataDefault ? oDataDefault : oDataDefaultCommon;
        oDataErrors=$.extend(oDataErrors,oDataErrorsNew);
    },
    add: function(oDataNew){
        if(oDataNew.sType==="warning"){
            this.addWarn(oDataNew);
        }else if(oDataNew.sType==="debug"){
            this.addInfo(oDataNew);
        }else{
            this.addFail(oDataNew);
        }
    },

    bSuccess: function(oDataDefault){
        var bSuccess = true;
        if(!oDataDefault){
            oDataDefault=oDataDefaultCommon;
        }
        if(oDataErrors.sBody){
            this.addFail(oDataDefault);
            console.error("oDataErrorsNew="+JSON.stringify(oDataErrors));
            var oData=oDataErrors;
            //ErrorsFactory.push({type: "danger", text: s});
            this.push({type: "danger", oData: oData});
            bSuccess  = false;
        }
        if(oDataWarns.sBody){
            this.addWarns(oDataDefault);
            console.warn("oDataWarnsNew="+JSON.stringify(oDataWarns));
            var oData=oDataWarns;
            this.push({type: "warning", oData: oData});
            bSuccess  = false;
        }
        if(oDataInfos.sBody){
            this.addInfo(oDataDefault);
            console.info("oDataInfosNew="+JSON.stringify(oDataInfos));
        }
        this.init(oDataDefaultCommon);
        return bSuccess ;
    },        
    log: function(oDataDefault){
        this.bSuccess(oDataDefault);
    },
    
    logInfo: function(oDataInfosNew, oDataDefault){
        this.addInfo(oDataInfosNew, oDataDefault);
        log();
    },
    logWarn: function(oDataWarnsNew, oDataDefault){
        this.addWarn(oDataWarnsNew, oDataDefault);
        log();
    },
    logFail: function(oDataErrorsNew, oDataDefault){
        this.addFail(oDataErrorsNew, oDataDefault);
        log();
    },

    bSuccessResponse: function(oData, onCheckMessage, oDataDefault){
        if(!oDataDefault){
            oDataDefault=oDataDefaultCommon;
        }
        this.init(oDataDefaultCommon);
        try{
            if (!oData) {
                if(onCheckMessage){
                    var oDataMessageResponseNew = onCheckMessage(null,null);
                    if(oDataMessageResponseNew){
                        this.add(oDataMessageResponseNew);
                    }
                }
                if(!onCheckMessage || oDataErrors.sType){
                    if(!oDataErrors.sBody){
                        this.addFail({sBody: 'Пуста відповідь на запит!'});
                    }
                }
            }else{
                if (typeof oData !== 'object') {
                    var nError=0;
                    var oDataErrorsResponse={};
                    if (oData.hasOwnProperty('message')) {
                        if(onCheckMessage){
                            var oDataMessageResponseNew = onCheckMessage(oData.message);
                            if(oDataMessageResponseNew){
                                this.add(oDataMessageResponseNew);
                            }
                        }
                        oDataErrorsResponse=$.extend(oDataErrorsResponse,{sMessage: oData.message});
                        oData.message=null;
                        nError++;
                    }
                    if (oData.hasOwnProperty('code')) {
                        if(onCheckMessage){
                            var oDataMessageResponseNew = onCheckMessage(null,oData.code);
                            if(oDataMessageResponseNew){
                                this.add(oDataMessageResponseNew);
                            }
                        }
                        oDataErrorsResponse=$.extend(oDataErrorsResponse,{sCode: oData.code});
                        oData.code=null;
                        nError++;
                    }
                    if(nError>0){
                        if(!onCheckMessage || oDataErrors.sType){
                            if(nError!==2 && !oDataErrors.sBody){
                                this.addFail({sBody:'Повернено не стандартній об`єкт!'});
                            }
                        }
                        oDataErrorsResponse=$.extend(oDataErrorsResponse,{soData: JSON.stringify(oData)});
                    }
                    if(!onCheckMessage || oDataErrors.sType){
                        this.addFail({oResponse:oDataErrorsResponse});
                    }
                }else{
                    if(onCheckMessage){
                        var oDataMessageResponseNew = onCheckMessage(null,null);
                        if(oDataMessageResponseNew!==null){
                            this.add(oDataMessageResponseNew);
                        }
                    }
                    oDataErrorsResponse=$.extend(oDataErrorsResponse,{sData: JSON.stringify(oData)});
                    if(!onCheckMessage || oDataErrors.sType){
                        this.addFail({oResponse:oDataErrorsResponse});
                        if(!oDataErrors.sBody){
                            this.addFail({sBody:'Повернено не об`єкт!'});
                        }
                    }
                }
            }
        }catch(sError){
            this.addFail({sBody: 'Невідома помилка у обробці відповіді сервера!', Error: sError, oResponse: {soData: JSON.stringify(oData)}});
        }
        if(oDataErrors.sBody){
            this.addFail(oDataDefault);
            return false;
        }
        return true;
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
