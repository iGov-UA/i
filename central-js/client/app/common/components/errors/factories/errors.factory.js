/*
  This factory is for displaying error messages
  to add new message to display you should use a push method
  the error object has two fields:
    type - defines a message style
    text - message displaying to user
*/

angular.module("app").factory("ErrorsFactory", function(SimpleErrorsFactory,$http) {
  /* existing error types */

  var oDataInfos = {}; //console only
  var oDataWarns = {};
  var oDataErrors = {};
  var oDataDefaultCommon = {};

  return {

    merge: function(oDataOld, oDataNew){
        if(!oDataNew){
            oDataNew={};
        }
        if(!oDataOld){
            oDataOld={};
        }
        console.log("[merge]:(START)oDataOld="+JSON.stringify(oDataOld)+",oDataNew="+JSON.stringify(oDataNew));
        if(oDataNew.asParam && oDataOld.asParam){
            angular.forEach(oDataOld.asParam, function (sParamOld) {
                console.log("[merge]:sParamOld="+sParamOld);
                if(oDataNew.asParam.indexOf(sParamOld)<0){
                    console.log("[merge]:(ADDED)sParamNew="+sParamOld);
                    oDataNew.asParam = oDataNew.asParam.concat([sParamOld]);
                }
            });
        }
        if(oDataNew.oResponse && oDataOld.oResponse){
            oDataNew.oResponse = $.extend(oDataOld.oResponse, oDataNew.oResponse);
        }
        oDataNew=$.extend(oDataOld,oDataNew);
        console.log("[merge]:(FINAL)oDataOld="+JSON.stringify(oDataOld)+",oDataNew="+JSON.stringify(oDataNew));
        return oDataNew;
    },

    init: function(oDataDefault,oDataNew){
        oDataDefaultCommon = oDataDefault ? oDataDefault : {};
        oDataDefaultCommon = this.merge(oDataDefaultCommon, oDataNew);
        oDataErrors = {};
        oDataWarns = {};
        oDataInfos = {};
    },

    reset: function(){
        this.init(oDataDefaultCommon);
    },

    addInfo: function(oDataNew, oDataDefault){
        oDataDefaultCommon = oDataDefault ? oDataDefault : oDataDefaultCommon;
        oDataInfos = this.merge(oDataInfos, oDataNew);
    },

    addWarn: function(oDataNew, oDataDefault){
        oDataDefaultCommon = oDataDefault ? oDataDefault : oDataDefaultCommon;
        oDataWarns = this.merge(oDataWarns, oDataNew);
    },

    addFail: function(oDataNew, oDataDefault){
        oDataDefaultCommon = oDataDefault ? oDataDefault : oDataDefaultCommon;
        oDataErrors = this.merge(oDataErrors, oDataNew);
    },

    add: function(oDataNew){
        if(oDataNew.sType==="warning"){
            this.addWarn(oDataNew);
        }else if(oDataNew.sType==="info" || oDataNew.sType==="debug" || oDataNew.sType==="success" || oDataNew.sType==="message"){
            this.addInfo(oDataNew);
        }else{
            this.addFail(oDataNew);
        }
    },

    bSuccess: function(oDataDefault,bSend,bHide){
        var bSuccess = true;
        if(!oDataDefault){
            oDataDefault=oDataDefaultCommon;
        }
        if(oDataErrors.sBody){
            this.addFail(oDataDefault);
            console.error("[bSuccess]:oDataErrors="+JSON.stringify(oDataErrors));
            var oData=oDataErrors;
            this.push({type: "danger", oData: oData});
            bSuccess  = false;
        }
        if(oDataWarns.sBody){
            this.addWarn(oDataDefault);
            console.warn("[bSuccess]:oDataWarns="+JSON.stringify(oDataWarns));
            var oData=oDataWarns;
            this.push({type: "warning", oData: oData});
            bSuccess  = false;
        }
        if(oDataInfos.sBody){
            this.addInfo(oDataDefault);
            console.info("[bSuccess]:oDataInfos="+JSON.stringify(oDataInfos));
            var oData=oDataInfos;
            if(bSend){
                this.push({type: "success", oData: oData, bSend: true, bHide: bHide});
            }
            bSuccess  = false;
        }
        this.init(oDataDefaultCommon);
        return bSuccess;
    },

    log: function(oDataDefault, bSend, bHide){
        this.bSuccess(oDataDefault,bSend, bHide);
    },

    logInfoSend: function(oDataNew, oDataDefault){
        this.addInfo(oDataNew, oDataDefault);
        this.log(null, true);
    },

    logInfoSendHide: function(oDataNew, oDataDefault){
        this.addInfo(oDataNew, oDataDefault);
        this.log(null, true, true);
    },

    logInfo: function(oDataNew, oDataDefault){
        this.addInfo(oDataNew, oDataDefault);
        this.log();
    },

    logWarn: function(oDataNew, oDataDefault){
        this.addWarn(oDataNew, oDataDefault);
        this.log();
    },

    logFail: function(oDataNew, oDataDefault){
        this.addFail(oDataNew, oDataDefault);
        this.log();
    },

    bSuccessResponse: function(oResponse, onCheckMessage, oDataDefault){
        if(!oDataDefault){
            oDataDefault=oDataDefaultCommon;
        }
        this.init(oDataDefaultCommon);
        try{
            if (!oResponse) {
                var oMergeDefault={sType: 'danger',sBody: 'Пуста відповідь на запит!'};
                if(onCheckMessage){
                    onCheckMessage(this,function(oThis, oMerge){
                        oMergeDefault = oThis.merge(oMergeDefault, oMerge) ;
                    });
                }
                this.add(oMergeDefault);
            }else if (typeof oResponse !== 'object') {
                var oMergeDefault={sType: 'danger',sBody: 'Повернено не об`єкт!'};
                if(onCheckMessage){
                    onCheckMessage(this,function(oThis, oMerge){
                        oMergeDefault = oThis.merge(oMergeDefault, oMerge) ;
                    }, null, null, oResponse);
                }
                oMergeDefault=$.extend(oMergeDefault,{oResponse:{sData: oResponse}});
              this.add(oMergeDefault);
            }else {
                var nError=0;
                if (oResponse.hasOwnProperty('message')) {

                  if(onCheckMessage){
                        onCheckMessage(this,function(oThis, oMerge){
                          oThis.add(oMerge);
                        }, oResponse.message);
                    }
                    this.add({oResponse:{sMessage: oResponse.message}});
                    oResponse.message=null;
                    nError++;
                }
                if (oResponse.hasOwnProperty('code')) {
                  if(onCheckMessage){
                        onCheckMessage(this,function(oThis, oMerge){
                          oThis.add(oMerge);
                        }, null, [oResponse.code]);
                    }
                    this.add({oResponse:{sCode: oResponse.code}});
                    oResponse.code=null;
                    nError++;
                }
                if(nError>0){
                    if(!oDataErrors.sBody&&!oDataWarns.sBody&&!oDataInfos.sBody){
                        if(nError!==2){
                            this.add({sBody: 'Помилка при запиті та повернено не стандартній об`єкт!'});
                        }else{
                            this.add({sBody: 'Помилка при запиті!'});
                        }
                    }
                    this.add({oResponse:{soData: JSON.stringify(oResponse)}});
                }
            }
        }catch(sError){
            this.addFail({sBody: 'Невідома помилка у обробці відповіді сервера!', sError: sError, oResponse: {soData: typeof oResponse !== 'object' ? oResponse : JSON.stringify(oResponse)}});
        }
        if(oDataErrors.sBody){
            this.logFail(oDataDefault);
            return false;
        }else if(oDataWarns.sBody){
            this.logWarn(oDataDefault);
            return false;
        }
        return true;
    },

    send: function(oMessage){
        try{
            var bProcessing = false;
            var sendData = function (oMessage) {//oData//oDataMessage
                var sFunction=oMessage.oData.sFunc;
                var oParams={sHead:oMessage.sHead,sBody:oMessage.sBody,sError:oMessage.oData.sError,sType:oMessage.sType};
                var oBody={oResponse:oMessage.oData.oResponse,asParam:oMessage.oData.asParam,sDate:oMessage.sDate};
                var oBodyData={oParams:oParams, oBody:oBody};
                var bSending = true;
                oMessage.bSending=bSending;
                $http.post('./api/order/setEventSystem/' + sFunction, oBodyData).success(function (nID) {
                    bProcessing = true;
                    console.log("[send]:oMessage.nID="+oMessage.nID);
                    bProcessing = false;
                    bSending=false;
                    oMessage.nID=nID;
                    oMessage.bSending=bSending;
                });
            };
            //oDataMessage
            sendData(oMessage);
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
        if(oMessage.sType==="warning"||oMessage.sType==="danger"){
            this.send(oMessage);
        }else if(oMessage.sType==="success"&&oMessage.bSend){
            this.send(oMessage);
        }else if(oMessage.sType==="info"&&oMessage.bSend){
            this.send(oMessage);
        }else if(oMessage.sType==="message"&&oMessage.bSend){
            this.send(oMessage);
        }
    }
  };

});
