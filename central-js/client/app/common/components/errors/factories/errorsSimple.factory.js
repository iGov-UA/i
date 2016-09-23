/*
  This factory is for displaying error messages
  to add new message to display you should use a push method
  the error object has two fields:
    type - defines a message style
    text - message displaying to user
*/
angular.module("app").factory("SimpleErrorsFactory", function() {
  /* existing error types */
      var errorTypes = ["warning", "danger", "success", "info", "message"];
      var windowSizes = ["lg", "md", "sm"];
      errors = []; /*errors container for objects like {type: "...", text: "..."}*/


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
        if(oMessage.size){
          oMessage.sSize = windowSizes.indexOf(oMessage.size) >= 0 ? oMessage.size : "md";
          oMessage.size = null;
        }

        oMessage.sDate = new Date();

        if(oMessage.oData){
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
            });
       }else{
            oMessage.oData={};
       }
       if(!oMessage.bHide){
            errors.push(oMessage);
       }
    }
  };

});
