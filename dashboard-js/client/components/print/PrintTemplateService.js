'use strict';

//angular.module('dashboardJsApp').service('PrintTemplateService', ['tasks', 'PrintTemplateProcessor', '$q', '$templateRequest', '$lunaService', function(tasks, PrintTemplateProcessor, $q, $templateRequest, lunaService) {
angular.module('dashboardJsApp').service('PrintTemplateService', ['tasks', 'FieldMotionService', 'PrintTemplateProcessor', '$q', '$templateRequest', function(tasks, FieldMotionService, PrintTemplateProcessor, $q, $templateRequest) {
  // TODO: move code from PrintTemplateProcessor here
  // helper function to get path to a print template based on it's ID
  function findPrintTemplate (form, sCustomFieldID) {
    var s = ((sCustomFieldID!==null && sCustomFieldID !== undefined && sCustomFieldID!=='-') ? sCustomFieldID : 'sBody');
    var printTemplateResult = form.filter(function (item) {
      //var bPrintform = item.id === s || (item.name && item.name.indexOf('bPrintform=true') >= 0);
      var bPrintform = item.id === s;
      return bPrintform;
      //return item.id === s;
    });
    var retval = printTemplateResult.length !== 0 ? printTemplateResult[0].name.replace(/\[pattern(.+)\].*/, '$1') : "";
    return retval;
  };
  // object for caching loaded templates
  var loadedTemplates = {};
  var service = {
    // method to get list of available print templates based on task form.
    getTemplates: function(form) {
      if (!form) {
        return [];
      }
       var templates = [];
      var topItems = [];

      var markerExists = false;

      for(var i = 0; i < form.length; i++) {
        if (form[i].id && form[i].id.indexOf('marker') >= 0 && form[i].value.indexOf('ShowFieldsOn') >= 0){
          markerExists = true;
          break;
        }
      }

      try {

        for(var i = 0; i < form.length; i++) { 

           if( form[i].type === 'table' && form[i].aRow && typeof form[i].aRow[0] !== 'number') { 

			    	  var prints = FieldMotionService.getPrintForms(); // form[i].id 

			    	  angular.forEach ( prints, function(printsItem, printsKey, printsObj ) { 

                 if( _.contains(printsItem.aField_ID, form[i].id) ) { 

		                  angular.forEach( form[i].aRow, function( item, key, obj ) { 

                        if( FieldMotionService.isPrintFormVisible(printsItem, form[i], form, item) ) { 
                        
                            var itemObject = {

                              oPrintForm: printsItem,
                              sPrintFormKey: printsKey,
                              sPatternPath: printsItem.sPatternPath,
                              sTableName: form[i].id,
                              nRowIndex: key,
                              oRow: item,
                              oField: null,
                              sLabel: "",

                            };


                            if( printsItem.sTitleField ) { 
                              angular.forEach( item.aField, function( field, fieldKey ) { 

                                if( field.id === printsItem.sTitleField )  {

                                  itemObject.oField = field;
                                  itemObject.sLabel = field.value;

                                  var enumItem = FieldMotionService.getEnumItemById( itemObject.oField, itemObject.oField.value ); 
                                  if( enumItem != null ) { 
                                    itemObject.sLabel = enumItem.name; 
                                  } 

                                  return;
                                }

                              } );
                            }

                            if( itemObject.sLabel === "" && item.aField[0] !== null) { 

                              itemObject.oField = item.aField[0];
                              itemObject.sLabel = item.aField[0].value;

                              var enumItem = FieldMotionService.getEnumItemById( itemObject.oField, itemObject.oField.value ); 
                              if( enumItem != null ) { 
                                 itemObject.sLabel = enumItem.name;  
                              } 

                              //console.log( " #1438 '" + form[i].id + "'=" + itemObject.sLabel );

                            }

                            if( itemObject.sLabel ) {
                              var item = {

                                id: form[i].id,
                                displayTemplate: printsItem.sName + ' (' + itemObject.sLabel + ')',
                                type: "prints",
                                value: itemObject,

                              };

                              topItems.unshift( item );

                              console.log( "Top item added " + printsItem.sName + " count:" + topItems.length);
                            }
                        } 

		                } );
                 }
            } );

          }
        }

      }
      catch(e) {
        console.log( "Mistake " + e );
      }

      if (markerExists){
          templates = form.filter(function (item) {
          var result = false;
          //if (item.id && item.id.indexOf('sBody') >= 0
          if (((item.id && item.id.indexOf('sBody') >= 0) || (item.name && item.name.indexOf('bPrintform=true') >= 0)) 
            && (!FieldMotionService.FieldMentioned.inShow(item.id)
            || (FieldMotionService.FieldMentioned.inShow(item.id)
            && FieldMotionService.isFieldVisible(item.id, form)))) {
            result = true;
            // На дашборде при вытягивани для формы печати пути к патерну, из значения поля -
            // брать название для каждого элемента комбобокса #792
            // https://github.com/e-government-ua/i/issues/792
            if (item.value && item.value.trim().length > 0 && item.value.length <= 100){
              item.displayTemplate = item.value;
            } else {
              item.displayTemplate = item.name;
            }
          }
          return result;
        });
      } else {
          templates = form.filter(function (item) {
          var result = false;
          //if (item.id && item.id.indexOf('sBody') >= 0) {
          if ((item.id && item.id.indexOf('sBody') >= 0) || (item.name && item.name.indexOf('bPrintform=true') >= 0)) {
            result = true;
            // На дашборде при вытягивани для формы печати пути к патерну, из значения поля -
            // брать название для каждого элемента комбобокса #792
            // https://github.com/e-government-ua/i/issues/792
            if (item.value && item.value.trim().length > 0 && item.value.length <= 100){
              item.displayTemplate = item.value;
            } else {
              item.displayTemplate = item.name;
            }
          }
          return result;
        });
      }

      if(topItems.length > 0) {
        angular.forEach( topItems, function( item ) { templates.unshift(item); } );
      }

      //templates.unshift({ id: "Id1438", displayTemplate: "Testing", type: "markers", value: "Test 1438" });

      return templates;
    },

    /**
     * function getPrintTemplateByObject
     *  Returns template for PrintForm object combined with tables value
     *
     * @returns loaded template
     * @author Sysprog
     */

    getPrintTemplateByObject: function( task, form, printTemplateObject ) {
      var deferred = $q.defer();
      if(!printTemplateObject.sPatternPath) {
        deferred.reject('Неможливо завантажити форму: "' + printTemplateObject.sPatternPath + '"');
        return deferred.promise;
      }

      var parsedForm;
      if(!angular.isDefined(loadedTemplates[printTemplateObject.sPatternPath])) {
         tasks.getPatternFile(printTemplateObject.sPatternPath).then(function(originalTemplate) {

           loadedTemplates[printTemplateObject.sPatternPath] = originalTemplate;
           parsedForm = PrintTemplateProcessor.getPrintTemplate(task, form, originalTemplate);
           parsedForm = PrintTemplateProcessor.populateTableField( parsedForm , printTemplateObject );
           deferred.resolve(parsedForm);

         }, function() {
           deferred.reject('Помилка завантаження форми "' + printTemplateObject.sPatternPath + '"');
         });
      }
      else {
        parsedForm = PrintTemplateProcessor.getPrintTemplate(task, form, loadedTemplates[printTemplateObject.sPatternPath]);
        parsedForm = PrintTemplateProcessor.populateTableField( parsedForm, printTemplateObject );
        deferred.resolve(parsedForm);
      }
      return deferred.promise;
    },

    // method to get parsed template
    getPrintTemplate: function(task, form, printTemplateName) {
      var deferred = $q.defer();
      if (!printTemplateName) {
        deferred.reject('Неможливо завантажити форму: немає назви');
        return deferred.promise;
      }
      // normal flow: load raw template and then process it
      var parsedForm;
      if (!angular.isDefined(loadedTemplates[printTemplatePath])) {
        var printTemplatePath = findPrintTemplate(form, printTemplateName);
        tasks.getPatternFile(printTemplatePath).then(function(originalTemplate){
          // cache template
          loadedTemplates[printTemplatePath] = originalTemplate;
          parsedForm = PrintTemplateProcessor.getPrintTemplate(task, form, originalTemplate);
          deferred.resolve(parsedForm);
        }, function() {
          deferred.reject('Помилка завантаження форми');
        });
      } else {
        // resolve deferred in case the form was cached
        parsedForm = PrintTemplateProcessor.getPrintTemplate(task, form, loadedTemplates[printTemplatePath]);
        deferred.resolve(parsedForm);
      }
      // return promise
      return deferred.promise;
    }
  };
  return service;
}]);
