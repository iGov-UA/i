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

    $scope.sModalTitle = "";

    var getTheRule = function (a) {
      if (a != null && a != undefined){
        if(a.isCopied){
          for (var prop in exampleRule) if (exampleRule.hasOwnProperty(prop) && a.hasOwnProperty(prop)) {
            exampleRule[prop] = angular.copy(a[prop]);
          }
          $scope.sModalTitle = "Копіювання правила";
          return exampleRule;
        } else {
          $scope.sModalTitle = "Редагування правила";
          return angular.copy(a);
        }
      }
      $scope.sModalTitle = "Створення правила";
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
      bNow: false,
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

    $scope.changeDaysLimit = function(){
      var value = $scope.thisRule.oData.nDaysLimit;
      var rep = /[.,;":'a-zA-Zа-яА-Я\\=`ё/\*+!@#$%\^&_№?><]/;
      if (rep.test(value)) {
        value = value.replace(rep, '');
        $scope.thisRule.oData.nDaysLimit = value;
      }

      if(angular.isNumber($scope.thisRule.oData.nDaysLimit)){
        $scope.thisRule.oData.nDaysLimit.toFixed(0);
      }

      if($scope.thisRule.oData.nDaysLimit < -1){
        $scope.thisRule.oData.nDaysLimit = -1;
      }
      if ($scope.thisRule.oData.nDaysLimit > 365){
        $scope.thisRule.oData.nDaysLimit = 365;
      }

    };

    $scope.addContact = function(){
      $scope.thisRule.oData.aEmails.push({email:""});
    };

    $scope.removeContact = function(contact){
      for (var j = 0; j < $scope.thisRule.oData.aEmails.length; j++) {
        if (contact.email === $scope.thisRule.oData.aEmails[j].email) {
          $scope.thisRule.oData.aEmails.splice(j, 1);
        }
      }
    };

    $scope.initEscalationModalDialog = function () {
      $(".modal-dialog").addClass("escalation-modal-dialog");
      $scope.thisRule = angular.copy($scope.rule);

      $scope.thisRule.oData = parseStringToDate($scope.thisRule.soData);

      $scope.thisRule.oData.aEmails = []; // список для отображения в модальном окне
      if(angular.isArray($scope.thisRule.oData.asRecipientMail)){
        for(i = 0; i < $scope.thisRule.oData.asRecipientMail.length; i++){
          $scope.thisRule.oData.aEmails.push({email:$scope.thisRule.oData.asRecipientMail[i]});
        }
      }

      if (!$scope.thisRule.oData.nDaysLimit) {
        $scope.thisRule.oData.nDaysLimit = 0;
      }

      if ($scope.thisRule.oData.nDaysLimit < 0) {
        setStartConditionAsNow()
      } else {
        $scope.oRuleStart.bLater = true;
      }

      $scope.resolveBP();
    };

    $scope.save = function () {
      if($scope.thisRule.oData.nDaysLimit === undefined ||
        $scope.thisRule.oData.nDaysLimit === '' ||
        isNaN($scope.thisRule.oData.nDaysLimit)){
        $scope.thisRule.oData.nDaysLimit = 0;
      }

      // todo уточнить возможность применения стандартного JSON
      //$scope.thisRule.soData = JSON.stringify($scope.thisRule.oData);
      $scope.thisRule.soData = compileDataString($scope.thisRule.oData);

      $scope.thisRule.nID_EscalationRuleFunction = angular.copy(
        getEscalationFunctionByID($scope.thisRule.nID_EscalationRuleFunction.nID));

      for (var prop in $scope.rule) if ($scope.rule.hasOwnProperty(prop)) {
        $scope.rule[prop] = angular.copy($scope.thisRule[prop]);
      }

      $modalInstance.close($scope.rule);
    };

    // составление soData из oData
    function compileDataString(oData) {
      var result = "";
      result = result + '{' + 'nDaysLimit' + ':' + Number(oData.nDaysLimit).toFixed(0);
      if($scope.thisRule.nID_EscalationRuleFunction.nID == 1){
        result = result + ",asRecipientMail:['";
        for(i = 0; i < oData.aEmails.length; i++){
          result = result + oData.aEmails[i].email + "'";
          if(i == oData.aEmails.length - 1){
            result = result + "]";
          } else {
            result = result + ",'";
          }
        }
      }
      result = result + '}';
      return result;
    }

    // парсер для строки soData
    function parseStringToDate(str, type) {
      try {
        return JSON.parse(str);
      } catch (err) {
        return parseIncorrectJSON(str, type);
      }

      function parseIncorrectJSON(str, type) {
        var objDefinition = {
          obj: { // объект
            op: '{', // открывающий символ
            cl: '}', // закрывающий символ
            sl: ':', // разделитель между ключем и значением
            su: ',' // разделитель между членами многочлена
          },
          arr: { // массив
            op: '[',
            cl: ']',
            sl: '',
            su: ','
          },
          str1: { // строка1
            op: '"',
            cl: '"',
            sl: '',
            su: ''
          },
          str2: { // строка2
            op: "'",
            cl: "'",
            sl: '',
            su: ''
          }
        };
        var result; // возвращаемый результат
        var needFirstSymb = true; // нужен поиск открывающего символа

        // если тип передан и известен - создаем соответствующий тип результата
        if (type) {
          if (type.op === "{") {
            result = {};
          } else if (type.op === "[") {
            result = [];
          } else if (type.op === '"' || type.op === "'") {
            result = "";
          } else {
            result = 0;
          }
          needFirstSymb = false;
        }

        var ind = 0; // index

        if (needFirstSymb) {// нам нужно найти открывающий символ
          // определяем тип объекта по открывающемуся символу
          var thisType;
          for (var key in objDefinition) {
            if (str.charAt(0) === objDefinition[key].op) {
              needFirstSymb = false;
              thisType = angular.copy(objDefinition[key]);
              break;
            }
          }
          // если открывающего символа нет - значит это Число и передаем строку как результат
          if (needFirstSymb) {
            result = Number(str);
            return result
          }
          // находим закрывающий символ с конца строки
          var closeSymbIndex = 0;
          ind = str.length - 1;
          for (ind; ind >= 0; ind--) {
            if (str.charAt(ind) === thisType.cl) {
              closeSymbIndex = ind;
              break;
            }
          }
          if (closeSymbIndex == 0) {// если закрывающего символа не нашли - значит это была Строка и возвращаем ее как результат
            result = str;
            return result;
          }
          // извлекаем подстроку между открывающим и закрывающим символом
          var substring = str.substring(1, closeSymbIndex);
          // запускаем парсинг извлеченной подстроки (с передачей типа объекта)
          result = parseStringToDate($.trim(substring), thisType);
        } else {// не нужно искать открывющий символ
          // по типу переданной строки, определяем, требуется ли разделение на многочлены
          var needSeparate;
          if (type.su === '') {
            needSeparate = false;
          } else {
            needSeparate = true;
          }
          // если требуется разделение на члены
          if (needSeparate) {
            // выполняем деление массива на члены (объекта на подобъекты)
            var tempArray = [];//временный массив для хранения элементов объекта/массива, которые необходимо распарсить

            var startInd = 0; // индекс первого символа
            var endInd = 0; // индекс последнего символа
            var level = 0; // глубина вложенности
            var ignoreNextSymbol = false;
            var expClSymb = []; // ожидаемые закрывающие символы
            for (ind = 0; ind < str.length; ind++) {
              // если попадаем на экранирующий символ - следующий будем пропускать
              if (str.charAt(ind) == '\u005C' && ignoreNextSymbol == false) {
                ignoreNextSymbol = true;
              }

              if (ignoreNextSymbol == false) {
                // если натыкаемся на открывающийся или закрывающийся символ - меняем уровень вложенности
                for (var checkOp in objDefinition) { // пересмотр вероятных символов открытия
                  if (str.charAt(ind) == objDefinition[checkOp].op) {
                    level = level + 1;
                    expClSymb[level] = objDefinition[checkOp].cl;
                    break;
                  }
                }
                if (str.charAt(ind) == expClSymb[level]) {
                  expClSymb[level] = '';
                  level = level - 1;
                }
                // если натыкаемся на символ сепаратора в нулевом уровне - вычленяем подстроку и переводим курсор
                if (level == 0) {
                  if (str.charAt(ind) == type.su) {
                    tempArray.push($.trim(str.substring(startInd, ind)));
                    startInd = ind + 1;
                  } else if (ind == str.length - 1) {
                    tempArray.push($.trim(str.substring(startInd)));
                  }
                }
              }

              // снимаем экранирование
              if (ind > 0) {
                if (ignoreNextSymbol == true && str.charAt(ind - 1) == '\u005C') {
                  ignoreNextSymbol == false;
                }
              }

            }
            // поочередно каждый член (подобъект) передаем на парсинг
            for (var i = 0; i < tempArray.length; i++) {
              // если расчлененный был массивом - пушим в него результат
              if (type.op === "[") {
                result.push(parseStringToDate(tempArray[i]));
              }

              // если это был объект
              if (type.op === "{") {
                // находим позицию двоеточия
                var twicePos = tempArray[i].search(/:/);
                // вычленяем название параметра
                var propertyName = $.trim(tempArray[i].substring(0, twicePos));
                // вычленяем значение параметра, которое отправляем на парсинг
                // сохраняем полученные значения в результат
                result[propertyName] = parseStringToDate($.trim(tempArray[i].substring(twicePos + 1)));
              }
            }
          } else {// если нам не требуется разделение на члены
            // сохраняем подстроку в результат
            result = str;
          }
        }
        return result;
      }
    }

  }
);
