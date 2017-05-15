'use strict';

//angular.module('dashboardJsApp').factory('PrintTemplateProcessor', ['$sce', 'Auth', '$filter', 'FieldMotionService', '$lunaService', function ($sce, Auth, $filter, FieldMotionService, lunaService) {
angular.module('dashboardJsApp').factory('PrintTemplateProcessor', ['$sce', 'Auth', '$filter', 'FieldMotionService', function ($sce, Auth, $filter, FieldMotionService) {
  function processMotion(printTemplate, form, fieldGetter) {
    var formData = form.reduce(function(prev, curr) {
      prev[curr.id] = curr;
      return prev;
    }, {});
    var template = $('<div/>').append(printTemplate);
    FieldMotionService.getElementIds().forEach(function(id) {
      var el = template.find('#' + id);
      if (el.length > 0 && !FieldMotionService.isElementVisible(id, formData))
        el.remove();
    });
    var splittingRules = FieldMotionService.getSplittingRules();
    var replacingRules = FieldMotionService.getReplacingRules();
    form.forEach(function(e) {
      var val = fieldGetter(e);
      if (val && _.has(splittingRules, e.id)) {
        var rule = splittingRules[e.id];
        var a = val.split(rule.splitter);
        template.find('#' + rule.el_id1).html(a[0]);
        a.shift();
        template.find('#' + rule.el_id2).html(a.join(rule.splitter));
      }
      if (val && _.has(replacingRules, e.id)) {
        rule = replacingRules[e.id];
        //a = val.slice(0, val.length - rule.nSymbols) + rule.sValueNew;
        // a = val.replace(rule.sFrom, rule.sTo);
        // template.find('#' + rule.sID_Element_sValue).html(a);
        var a = val.split(' ');
        var b = a[0].split('');
        b.splice(b.length - rule.symbols, rule.symbols, rule.valueNew);
        var c = b.join("");
        a.splice(0, 1, c);
        var result = a.join(' ');
        template.find('.' + rule.el_id2).html(result);
      }
    });
    return template.html();
  }

  return {
    processPrintTemplate: function (task, form, printTemplate, reg, fieldGetter) {
      var _printTemplate = printTemplate;
      var templates = [], ids = [], found;
      while (found = reg.exec(_printTemplate)) {
        templates.push(found[1]);
        ids.push(found[2]);
      }
      if (templates.length > 0 && ids.length > 0) {
        templates.forEach(function (templateID, i) {
          var id = ids[i];
          if (id) {
            var item = form.filter(function (item) {
              return item.id === id;
            })[0];
            if (item) {
              var sValue = fieldGetter(item);
              if (sValue === null){
                sValue = "";
              }
              _printTemplate = _printTemplate.replace(templateID, sValue);//fieldGetter(item)
            }
          }
        });
      }
      return _printTemplate;
    },
    //наполнение принтформы данными из таблицы. поиск по ид таблицы маркера, при необходмости клонирование, сразу же наполняем.
    fillPrintTable: function (task, form, printTemplate, reg) {
      var self = this;
      var _printTemplate = printTemplate;
      var templates = [], ids = [], found, idArray = [];
      while (found = reg.exec(_printTemplate)) {
        templates.push(found[1]);
        ids.push(found[2]);
      }
      var matchesIds = [];
      // ищем маркеры что определяют в принтформе таблицы
      angular.forEach(templates, function (template) {
        var comment = template.match(/<!--[\s\S]*?-->/g);
        if(Array.isArray(comment)) {
          for(var i=0; i<comment.length; i++) {
            comment[i] = comment[i].match(/\w+/)[0];
          }
        }
        if(comment) matchesIds.push(comment);
      });

      // формируем массив из ид маркеров таблиц что встретились в принтформе
      angular.forEach(matchesIds, function (ids) {
        var arr = ids.filter(function(item, pos, self) {
          return self.indexOf(item) == pos;
        });
        idArray.push(arr);
      });

      // когда ид маркера принтформы совпало с ид таблицы - заменяем теги на поля таблицы. проверяем к-во строк таблицы
      // если больше одной - клонируем до нужного к-ва, после наполняем таблицей.
      angular.forEach(idArray, function(id) {
        angular.forEach(form.taskData.aTable, function(item) {
          if(item.id === id[0]) {
            angular.forEach(templates, function (template) {
              var commentedField = template.match(/<!--.*?-->/)[0];
              var uncommentedField = commentedField.split('--')[1];
              var result = uncommentedField.slice(1);
              if(result === id[0]){
                var withAddedRowsTemplate = template.repeat(item.aRow.length);
                angular.forEach(item.aRow, function (row) {
                  angular.forEach(row.aField, function (field) {
                    var fieldId = function () {
                      if(field.type === 'enum' && field.value) {
                        for (var j = 0; j < field.a.length; j++) {
                          if (field.a[j].name === field.value) {
                            return field.a[j].name ? field.a[j].name : ' ';
                          }
                        }
                      } else if(field.type === 'enum' && !field.value) {
                        return ' ';
                      } else if(field.type === 'date' && !field.value) {
                        return field.props && field.props.value ? field.props.value.split('T')[0] : ' ';
                      } else if (field.value) return field.value;
                        else if (field.default) return field.default;
                        else if (field.props) return field.props.value;
                        else return ' ';
                    };
                    withAddedRowsTemplate = self.populateSystemTag(withAddedRowsTemplate, '['+ field.id +']', fieldId, true);
                  })
                });
                _printTemplate = _printTemplate.replace(template, withAddedRowsTemplate);
              }
            })
          }
        });
      });
      return _printTemplate
    },
    populateSystemTag: function (printTemplate, tag, replaceWith, table) {
      var replacement;
      if (replaceWith instanceof Function) {
        replacement = replaceWith();
      } else {
        replacement = replaceWith;
      }
      if(table) {
        return printTemplate.replace(new RegExp(this.escapeRegExp(tag)), replacement);
      } else {
        return printTemplate.replace(new RegExp(this.escapeRegExp(tag), 'g'), replacement);
      }
    },

    /**
     * function populateTableField (printTemplate, printFormTableObject)
     *  Searches printTemplate for [oTableName.sTableField] and replaces
     *   with data of specified row of printFormTableObject.nRowIndex
     *
     * @returns original template with replaced values
     * @author Sysprog
     */
    populateTableField: function( printTemplate, printFormTableObject ) {

      var replacement;
    	var tag;
      var templateString = "";

      if( printTemplate.length ) {
         templateString = printTemplate;
      }
      else {
         templateString = $sce.getTrustedHtml( printTemplate );
      }

      if(printFormTableObject.oRow) {

        for ( var fieldIndex in printFormTableObject.oRow.aField ) {

          var field = printFormTableObject.oRow.aField[fieldIndex];

          if( field.type === "enum") {
             var enumItem = FieldMotionService.getEnumItemById( field, field.value );
             if( enumItem != null) {
               replacement = enumItem.name;
             }
          }
          else {
            replacement = field.value;
          }

    		  tag = "["+ printFormTableObject.sTableName + "." + field.id + "]";

      	  templateString = templateString.replace(new RegExp(this.escapeRegExp(tag)), replacement);

        }
      }

      if( printTemplate.length ) {
         return templateString;
      }
      else {
         return $sce.trustAsHtml( templateString );
      }
    },
    escapeRegExp: function (str) {
      return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
    },
    getPrintTemplate: function (task, form, originalPrintTemplate) {
      // helper function for getting field value for different types of fields
      function fieldGetter(item) {
        if (item.type === 'enum') {
          var enumID = item.value;
          var enumItem = item.enumValues.filter(function (enumObj) {
            return enumObj.id === enumID;
          })[0];
          if (enumItem && enumItem.name) {
            var enumItemName = enumItem.name;
            var enumItemNameArray = enumItemName.split('|');
            if (!_.isEmpty(enumItemNameArray[1])) {
              return enumItemNameArray[1];
            }
            else {
              return enumItemNameArray[0];
            }
          } else
            return '';
        } else if(item.type === 'fileHTML') {
          if(item.valueVisible) {
            return item.valueVisible;
          }
        } else {
          return item.value;
        }
      }


      function getLunaValue(id) {

        // Number 2187501 must give CRC=3
        // Check: http://planetcalc.ru/2464/
        if(id===null || id === 0){
          return null;
        }
        var n = parseInt(id);
        var nFactor = 1;
        var nCRC = 0;
        var nAddend;

        while (n !== 0) {
          nAddend = Math.round(nFactor * (n % 10));
          nFactor = (nFactor === 2) ? 1 : 2;
          nAddend = nAddend > 9 ? nAddend - 9 : nAddend;
          nCRC += nAddend;
          n = parseInt(n / 10);
        }

        nCRC = nCRC % 10;
        return nCRC;
      }

      var printTemplate = this.processPrintTemplate(task, form, originalPrintTemplate, /(\[(\w+)])/g, fieldGetter);
      // What is this for? // Sergey P
      printTemplate = this.processPrintTemplate(task, form, printTemplate, /(\[label=(\w+)])/g, function (item) {
        return item.name;
      });
      printTemplate = this.fillPrintTable(task, form, printTemplate, /(?=<!--\[)([\s\S]*?]-->)/g);
      printTemplate = this.populateSystemTag(printTemplate, "[sUserInfo]", function () {
        var user = Auth.getCurrentUser();
        return user.lastName + ' ' + user.firstName ;
      });
      printTemplate = this.populateSystemTag(printTemplate, "[sCurrentDateTime]", $filter('date')(new Date(), 'yyyy-MM-dd HH:mm'));
      printTemplate = this.populateSystemTag(printTemplate, "[sDateCreate]", $filter('date')(task.createTime.replace(' ', 'T'), 'yyyy-MM-dd HH:mm'));

      //№{{task.processInstanceId}}{{lunaService.getLunaValue(task.processInstanceId)}}
      //$scope.lunaService = lunaService;
      //lunaService.getLunaValue(
      printTemplate = this.populateSystemTag(printTemplate, "[sID_Order]", task.processInstanceId+getLunaValue(task.processInstanceId)+"");

      // #998 реализовать поддержку системного тэга [sDateTimeCreateProcess], [sDateCreateProcess] и [sTimeCreateProcess]
      // в принтформе, вместо которого будет подставляться Дата создания процесса
      // (в формате "YYYY-MM-DD hh:mm", "YYYY-MM-DD" и "hh:mm")
      try {
        if (angular.isDefined(form.taskData) && angular.isDefined(form.taskData.oProcess)) {
          printTemplate = this.populateSystemTag(printTemplate, "[sDateTimeCreateProcess]", function () {
            return $filter('date')(form.taskData.oProcess.sDateCreate.replace(' ', 'T'), 'yyyy-MM-dd HH:mm');
          });
          printTemplate = this.populateSystemTag(printTemplate, "[sDateCreateProcess]", function () {
            return $filter('date')(form.taskData.oProcess.sDateCreate.replace(' ', 'T'), 'yyyy-MM-dd');
          });
          printTemplate = this.populateSystemTag(printTemplate, "[sTimeCreateProcess]", function () {
            return $filter('date')(form.taskData.oProcess.sDateCreate.replace(' ', 'T'), 'HH:mm');
          });
        }
      } catch (e) {
        Modal.inform.error()(form.taskData.message)
      }
      return $sce.trustAsHtml(processMotion(printTemplate, form, fieldGetter));
    }
  }
}]);
