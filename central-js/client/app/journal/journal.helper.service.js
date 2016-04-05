angular.module('journal').service('JournalHelperService', function (ErrorsFactory) {
  var sID_OrderRegex = /№(\d-)?(\d+)/;

  this.bExist = function (oValue) {
    return oValue && oValue !== null && oValue !== undefined && !!oValue;
  };

  this.bExistNotSpace = function (oValue) {
    return bExist(oValue) && oValue.trim() !== "";
  };

  this.getOrderID = function (event) {
    if (event.oHistoryEvent_Service) {
      return event.oHistoryEvent_Service.sID_Order;
    } else {
      var aID_Order = event.sMessage.match(sID_OrderRegex);
      if(angular.isArray(aID_Order)) {
        return (aID_Order[1] || '0-') + aID_Order[2];
      } else {
        return null;
      }
    }
  };

  this.getOrderStatusString = function (nID_StatusType) {
    switch (nID_StatusType) {
      case 3: return 'remark';
      case 6: return 'comment';
      case 8:
      case 9:
      case 10:
      case 11:
      case 12: return 'closed';
      default: return 'processing';
    }
  };

  this.parseAField = function (soData) {
    try {
      var aField = JSON.parse(soData.replace(/'/g, '"'));
      angular.forEach(aField, function (oField) {
        if (!bExist(oField.sID)) {
          oField.sID = oField.id;
          oField.sName = oField.id;
          oField.sType = oField.type;
          oField.sValue = oField.value;
          oField.sValueNew = oField.value;
          oField.sNotify = oField.value;
          oField.id = "";
          oField.type = "";
          oField.value = "";
        }
        if (oField.sType === "date") {
          oField.oFactory = DatepickerFactory.prototype.createFactory();
          oField.oFactory.value = oField.sValueNew;
          oField.oFactory.required = true;
        }
      });
      return aField;
    } catch (sError) {
      ErrorsFactory.addFail({
        sBody: 'Помилка десереалізації об`єкту з полями, у яких зауваження!',
        sError: sError,
        asParam: ['oData.soData: ' + oResponse.soData]
      });
    }
  };

  this.parseSData = function (aMessages) {
    angular.forEach(aMessages, function (oMessage) {
      try {
        var aData = JSON.parse(oMessage.sData.replace(/'/g, '"'));
        oMessage.osData = aData[0];
      } catch (sError) {
        ErrorsFactory.addFail({
          sBody: 'Помилка десереалізації об`єкту з полями повідомлення!',
          sError: sError,
          asParam: ['sData: ' + oMessage.sData]
        });
      }
    });
    return aMessages;
  };
});
