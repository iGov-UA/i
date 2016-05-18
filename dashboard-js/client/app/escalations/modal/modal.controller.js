'use strict';

angular.module('dashboardJsApp')
  .controller('RuleEditorModalCtrl', function ($scope, $modalInstance, ruleToEdit, processesList, escalationsService) {

    var i = 0; // счетчик итераций

    $scope.aConditions = [
      {
        sFormula: 'nElapsedDays >= nDaysLimit',
        sShortName: '>=',
        sFullName: 'більше або дорівнює'
      },
      {
        sFormula: 'nElapsedDays == nDaysLimit',
        sShortName: '=',
        sFullName: 'дорівнює'
      },
      {
        sFormula: 'nElapsedDays > nDaysLimit',
        sShortName: '>',
        sFullName: 'більше'
      }
    ];

    $scope.fileEscalationTemplate = {
      isDefault: true,
      sDefaultFilePath: 'escalation/escalation_template.html'
    };

    var getTheProcesses = function (a) {
      return a;
    };

    $scope.processes = getTheProcesses(processesList);

    var exampleRule = {
      sID_BP: $scope.processes[0].sID,
      sID_UserTask: '*',
      sCondition: $scope.aConditions[0].sFormula,
      soData: '{nDaysLimit:3,asRecipientMail:[test@email.com]}',
      sPatternFile: $scope.fileEscalationTemplate.sDefaultFilePath,
      nID_EscalationRuleFunction: {nID: 2}
    };

    var getTheRule = function (a) {
      if (a != null && a != undefined)
        return angular.copy(a);
      return exampleRule;
    };

    $scope.ruleFunctions = null;
    escalationsService.getAllEscalationFunctions()
      .then(function (data) {
        $scope.ruleFunctions = data;
      });

    function getEscalationFunctionByID(nID){
      for(i = 0; i < $scope.ruleFunctions.length; i++){
        if(nID == $scope.ruleFunctions[i].nID){
          return $scope.ruleFunctions[i];
        }
      }
      return {};
    }

    $scope.rule = getTheRule(ruleToEdit);

    function getProcessNameByID(sID) {
      var aProcesses = getTheProcesses(processesList);
      for (var i = 0; i < aProcesses.length; i++) {
        if (aProcesses[i].sID === sID) {
          return aProcesses[i].sName;
        }
      }
      return "";
    }

    $scope.rule.bp = {
      sID: $scope.rule.sID_BP,
      sName: getProcessNameByID($scope.rule.sID_BP)
    };

    // объект правила, которое будет редактироваться в модальном окне
    // и, в последствии, сохранено при нажатии кнопки "Сохранить"
    $scope.thisRule = {};

    function trimString(sSourceString, nMaxLength) {
      if ($.trim(sSourceString.toString()).length + 3 > nMaxLength) {
        return sSourceString.substring(0, (nMaxLength - 3)) + "...";
      }
      return $.trim(sSourceString.toString());
    }

    $scope.tableRowsByPage = 5; // кількість рядків на сторінку в таблиці відображення списку бізнес-процесів
    $scope.tableDataProcesses = []; // массив для відображення списку бізнес-процесів в таблиці
    var nMaxStringSymbols = 50; // максимальна довжина строки

    for (i = 0; i < processesList.length; i++) {
      $scope.tableDataProcesses.push(angular.copy(processesList[i]));
      $scope.tableDataProcesses[i].sNameWirhID = processesList[i].sName + " (" + processesList[i].sID + ")";
      $scope.tableDataProcesses[i].sTrimedName = trimString(processesList[i].sName, nMaxStringSymbols);
      if ($scope.tableDataProcesses[i].sID === $scope.rule.sID_BP) {
        $scope.tableDataProcesses[i].isSelected = true;
      }
    }

    $scope.ruleBpIsIncorrect = false;

    $scope.resolveBP = function () {
      if ($scope.processes != '' && $scope.processes.length > 0) {
        $scope.ruleBpIsIncorrect = $scope.processes.every(function (process) {
          if ($scope.rule.bp.sID == process.sID) {
            $scope.rule.bp = process;
            return false;
          }
          return true;
        });
      }
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };

    if ($scope.thisRule.sPatternFile === $scope.fileEscalationTemplate.sDefaultFilePath) {
      $scope.fileEscalationTemplate.isDefault = true;
    }

    $scope.isDefaultEscalationTemplateChanged = function () {
      if ($scope.fileEscalationTemplate.isDefault == true) {
        $scope.thisRule.sPatternFile = $scope.fileEscalationTemplate.sDefaultFilePath;
      } else {
        $scope.thisRule.sPatternFile = '';
      }
    };

    var isFirstClickOnTable = true;

    $scope.selectThisProcess = function (item) {
      if (isFirstClickOnTable) {
        for (i = 0; i < $scope.tableDataProcesses.length; i++) {
          if ($scope.tableDataProcesses[i].isSelected) {
            $scope.tableDataProcesses[i].isSelected = false;
            item.isSelected = true;
            break;
          }
        }
        isFirstClickOnTable = false;
      }
      $scope.thisRule.sID_BP = item.sID;
      $scope.thisRule.bp.sID = item.sID;
      $scope.thisRule.bp.sName = item.sName;
    };

    $scope.oRuleStart = {
      bNow: true,
      bLater: false
    };

    function setStartConditionAsNow() {
      $scope.oRuleStart.bNow = true;
      $scope.oRuleStart.bLater = false;
      $scope.thisRule.sCondition = $scope.aConditions[0].sFormula;
      $scope.thisRule.oData.nDaysLimit = -1;
    }

    $scope.setRuleStarterNow = function () {
      if ($scope.oRuleStart.bNow) {
        setStartConditionAsNow();
      } else {
        $scope.oRuleStart.bLater = true;
      }
    };

    $scope.setRuleStarterLater = function () {
      if ($scope.oRuleStart.bLater) {
        $scope.oRuleStart.bNow = false;
      } else {
        setStartConditionAsNow();
      }
    };


    var saEmails = []; // список для сохранения в объект правила

    $scope.addContact = function(){
      $scope.thisRule.oData.aEmails.push({email:""});
    };

    $scope.removeContact = function(contact){
      for (var j = 0; j < $scope.thisRule.oData.aEmails.length; j++) {
        if (contact === $scope.thisRule.oData.aEmails[j]) {
          $scope.thisRule.oData.aEmails.splice(j, 1);
        }
      }
    };

    $scope.initEscalationModalDialog = function () {
      $(".modal-dialog").addClass("escalation-modal-dialog");
      $scope.thisRule = angular.copy($scope.rule);

      try {
        $scope.thisRule.oData = JSON.parse($scope.thisRule.soData);
      } catch (err) {
        console.error(err);
        $scope.thisRule.oData = {nDaysLimit: 0};
      }

      if (!$scope.thisRule.oData.nDaysLimit) {
        $scope.thisRule.oData.nDaysLimit = 0;
      }
      if ($scope.thisRule.oData.nDaysLimit < 0) {
        $scope.setRuleStarterNow();
      } else {
        $scope.setRuleStarterLater();
      }
      $scope.thisRule.oData.aEmails = []; // список для отображения в модальном окне

      $scope.resolveBP();
    };

    $scope.save = function () {
      saEmails = [];
      for(i = 0; i < $scope.thisRule.oData.aEmails.length; i++){
        saEmails.push($scope.thisRule.oData.aEmails[i].email);
      }
      //$scope.thisRule.soData = JSON.stringify($scope.thisRule.oData);
      //$scope.thisRule.soData = angular.toJson($scope.thisRule.oData);
      $scope.thisRule.soData = compileDataString($scope.thisRule.oData);
      $scope.thisRule.nID_EscalationRuleFunction = angular.copy(
        getEscalationFunctionByID($scope.thisRule.nID_EscalationRuleFunction.nID));
      for (var prop in $scope.rule) if ($scope.rule.hasOwnProperty(prop)) {
        $scope.rule[prop] = angular.copy($scope.thisRule[prop]);
      }
      debugger;
      $modalInstance.close($scope.rule);
    };

    var objectsDefinition = [
      {keys: ["nDaysLimit"], type: "numeric", startSymbol: '', endSymbol: '', separatorSymbol: ''},
      {keys: ["asRecipientMail"], type: "array", startSymbol: '[', endSymbol: ']', separatorSymbol: ','},
      {keys: [], type: "object", startSymbol: '{', endSymbol: '}', separatorSymbol: ','},
      {keys: [], type: "string", startSymbol: "'", endSymbol: "'", separatorSymbol: ","}
    ];

    // парсинг soData и запись результата в раздельные объекты oData
    function parseDataString(soData) {
      var result = {};
      var findTwicePoint = true; // поиск двоеточия, как разделителя, между именем и значением свойства
      var findElementSeparator = false; // поиск разделителя между элементами
      var startInd = 1; // индекс начального символа
      var endInd = 1; // индекс последнего индекса
      i = 0; // счетчик итератора
      var paramKey, paramValue;

      for (i = 1; i < soData.length; i++) {
        if (findTwicePoint && soData.charAt(i) === ':') {
          // мы искали двоеточие и нашли его
          endInd = i;
          paramKey = $.trim(soData.substring(startInd, endInd)); //сохранили имя ключа в переменную
          startInd = startInd + 1; // перевели курсор
          findTwicePoint = false;
          findElementSeparator = true;
          //todo

        }
        if (findElementSeparator) {
          // искали сепаратор...
          if (soData.charAt(i) === ',') {
            // ...и нашли его
            //todo

          }
          if (soData.charAt(i) === '}' && i == soData.length - 1) {
            // ...и дошли до окончания строки
            //todo

          }
        }
      }

      i = 0; // сброс счетчика
      return result;
    }

    // составление soData из oData
    function compileDataString(oData) {
      var result = "";
      result = result + '{' + 'nDaysLimit' + ':' + Number($scope.thisRule.oData.nDaysLimit).toFixed(0);
      if($scope.thisRule.nID_EscalationRuleFunction.nID == 1){
        result = result + ",asRecipientMail:['";
        for(i = 0; i < saEmails.length; i++){
          result = result + saEmails[i] + "'";
          if(i == saEmails.length - 1){
            result = result + "]";
          } else {
            result = result + ",";
          }
        }
      }
      result = result + '}';
      return result;
    }

  }
);
