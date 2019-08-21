angular.module('dashboardJsApp').service('ParsedTagFunction', [function() {
    var tagFunctions = [
      {
        regExp: /\[.*?\]\.getDateByFormat\('.*?'\)/g,
        function: getDateByFormat
      }
    ];

    function getDateByFormat(sFieldValue, sFormat) {
        try {
            return moment(new Date(sFieldValue)).format(sFormat);
        } catch (error) {
            return moment(new Date()).format(sFormat);
        }
    }
  
    return {
      getParsed: function(template, formData, processFunction) {
        var bWasProcessed = false;
  
        if (typeof template !== 'string') {
          template = template.toString();
        }
  
        var _template = template.replace(/&#39;/g, "'");
        var asSplitTemplate = [];
  
        if (_template.length > 10000) {
          asSplitTemplate = _template.match(/[\s\S]{1,10000}/g);
        } else {
          asSplitTemplate = [_template];
        }
  
        angular.forEach(tagFunctions, function(item) {
  
          angular.forEach(asSplitTemplate, function(sTemplatePart, i) {
            if (item.regExp.test(sTemplatePart)) {
              bWasProcessed = true;
              sTemplatePart = sTemplatePart.replace(item.regExp, function() {
                var aRegExpGroup=arguments;
                var aParamValue = Array.prototype.slice.call(aRegExpGroup, 1);
                var sFunctionAll = arguments[0];
                  if (sFunctionAll.indexOf('&gt;') > -1 && aParamValue[0].indexOf('&gt;') > -1) {
                      sFunctionAll = sFunctionAll.replace(/&gt;/g, ">");
                      aParamValue[0] = aParamValue[0].replace(/&gt;/g, ">");
                  }
                  else if (sFunctionAll.indexOf('&lt;') > -1 && aParamValue[0].indexOf('&lt;') > -1) {
                      sFunctionAll = sFunctionAll.replace(/&lt;/g, "<");
                      aParamValue[0] = aParamValue[0].replace(/&lt;/g, ">");
                  }
                console.log('sFunctionAll='+sFunctionAll);
                try {
                  angular.forEach(aParamValue, function(sParamValue, nIndex, oParamValue) {
                    console.log('nIndex='+nIndex+',sParamValue='+sParamValue);
                    if (sParamValue && typeof sParamValue === 'string' && sParamValue.indexOf('[') > -1) {
                      var sKey = sParamValue.replace('[', '').replace(']', '');
                      console.log('nIndex='+nIndex+',sKey='+sKey+',sParamValue='+sParamValue);
                      if (formData[sKey]) {
                        var sParamValueParsed=formData[sKey].value;
                        console.log('nIndex='+nIndex+',sParamValueParsed='+sParamValueParsed);
                        oParamValue[nIndex] = sParamValueParsed;
                      }
                    }
                  });
                  
                  console.log('sFunctionAll='+sFunctionAll+',aParamValue='+aParamValue);
                  return processFunction ? processFunction.apply(null, aParamValue) : item.function.apply(null, aParamValue);
                } catch (e) {
                  console.log('ERROR: ' + e.name + '\n' + e.message + '\n' + 'sAll=:' + arguments[0]);
                }
                  if (arguments[0].indexOf('&gt;') > -1) {
                      arguments[0] = arguments[0].replace(/&gt;/g, ">");
                  }
                  else if (arguments[0].indexOf('&lt;') > -1) {
                      arguments[0] = arguments[0].replace(/&lt;/g, "<");
                  }
                return arguments[0];
              });
            }
  
            asSplitTemplate[i] = sTemplatePart;
          });
        });
  
        if (!bWasProcessed)
          return template;
        else 
          return asSplitTemplate.join('');
      }
    };
  }]);
  