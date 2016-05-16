'use strict';

angular.module('dashboardJsApp')
  .controller('RuleEditorModalCtrl', function ($scope, $modalInstance, ruleToEdit, processesList, $filter) {

    $scope.fileEscalationTemplate = {
      isDefault: true,
      sDefaultFilePath: 'escalation/escalation_template.html'
    };

    var i = 0;

    var exampleRule = {
      sID_BP: 'dnepr_spravka_o_doxodax',
      sID_UserTask: '*',
      sCondition: 'nElapsedDays==nDaysLimit',
      soData: 'nDaysLimit:3,asRecipientMail:[test@email.com]',
      sPatternFile: $scope.fileEscalationTemplate.sDefaultFilePath,
      nID_EscalationRuleFunction: 'EscalationHandler_SendMailAlert'
    };

    var getTheRule = function (a) {
      if (a != null && a != undefined)
        return angular.copy(a);
      return exampleRule;
    };

    var getTheProcesses = function (a) {
      return a;
    };

    $scope.rule = getTheRule(ruleToEdit);
    $scope.processes = getTheProcesses(processesList);

    function getProcessNameByID(sID){
      var aProcesses = getTheProcesses(processesList);
      for(var i = 0; i < aProcesses.length; i++){
        console.log("i = " + i + " aProcesses[i].sID = " + aProcesses[i].sID);
        if(aProcesses[i].sID === sID){
          //debugger;
          return aProcesses[i].sName;
        }
      }
      //debugger;
      return "";
    }

    $scope.rule.bp = {
      sID: $scope.rule.sID_BP,
      sName: getProcessNameByID($scope.rule.sID_BP)
    };

    // объект правила, которое будет редактироваться в модальном окне
    // и, в последствии, сохранено при нажатии кнопки "Сохранить"
    $scope.thisRule = {};
    function initEditableRule(){
      $scope.thisRule = angular.copy($scope.rule);
    }

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
      if($scope.tableDataProcesses[i].sID === $scope.rule.sID_BP){
        $scope.tableDataProcesses[i].isSelected = true;
      }
    }

    $scope.ruleBpIsIncorrect = false;

    $scope.resolveBP = function () {
      initEditableRule();
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

    $scope.save = function () {
      $scope.rule = angular.copy($scope.thisRule);
      $modalInstance.close($scope.rule);
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
      if(isFirstClickOnTable){
        for(i = 0; i < $scope.tableDataProcesses.length; i++){
          if($scope.tableDataProcesses[i].isSelected){
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



  }
);

