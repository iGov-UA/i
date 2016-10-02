(function () {
  'use strict';

  angular.module('app')
      .factory('taxTemplateFileHandler', taxTemplateFileHandler);

  function taxTemplateFileHandler($http, taxTemplateFileHandlerConfig) {

    // var OFILE_XML_SWINED = 'oFile_XML_SWinEd',
    //     SID_FIELD_SWINED = 'sID_Field_SWinEd',
    //     TYPE = 'file';

    /**
     * Parse template into JSON
     * @param params
     * @param callback
     */
    function postJSON(params, callback) {
      var json,
          parameters = params;

      callback = callback || function () {
          };

      var patternCode = getField({
        id: taxTemplateFileHandlerConfig.oFile_XML_SWinEd,
        type: taxTemplateFileHandlerConfig.file,
        formProperties: parameters.formProperties
      });

      if (patternCode) {
        json = createJSON(parameters);

        $http.post('/api/service/getPatternFilled', {
          nID_Server: parameters.oServiceData.nID_Server,
          sID_Pattern: patternCode.value,
          oData: json
        }).success(function (data, status, headers, config) {

          if(data.code !== 'SYSTEM_ERR'){
            _.merge(data, {fileFieldName: taxTemplateFileHandlerConfig.oFile_XML_SWinEd});
            callback(data, null);
          }else if(data.code === 'SYSTEM_ERR'){
            callback(null, {text:'Помилка відповіді веб-сервісу: ', value: data});
          }

        }).error(function (data, status, headers, config) {
          callback(null, error);
        });
      }else{
        callback(null, null);
      }

      /**
       * Get field from form object(parameters) with specified id and type
       * @param params
       * @returns {*}
       */
      function getField(params) {
        var formProperties = params.formProperties || [],
            id = params.id || '',
            type = params.type || '',
            filtered;

        filtered = formProperties.filter(function (item) {
          return item.id === id && item.type === type;
        });

        return filtered ? filtered[0] : null;
      }


      function createJSON(args) {
        var properites = args.formProperties || [],
            formData = args.formData,
            result = {};

        angular.forEach(properites, function (prop) {
          var obj = getObjectFromName((prop.name || '').split(';')[2]);

          if (obj) {
            result[obj[taxTemplateFileHandlerConfig.sID_Field_SWinEd]] = formData.params[prop.id].value;
          }
        });

        return result;

        /**
         * Parse name property and create object from string with specified name
         * @param param
         * @returns {*}
         */
        function getObjectFromName(param) {
          if (!param || !typeof param === 'string') return null;

          var input = param.trim(),
              finalArray,
              result = {};

          var elementExp = input.split(',').filter(function (item) {
            return (item && typeof item === 'string' ? item.trim() : '')
                    .split('=')[0]
                    .trim() === taxTemplateFileHandlerConfig.sID_Field_SWinEd;
          })[0];

          if (!elementExp) return null;

          finalArray = elementExp.split('=');

          if (!finalArray || !finalArray[1]) return null;

          var indexes = finalArray[1].trim().match(/\d+/ig),
              index;

          if (Array.isArray(indexes)) {
            index = isNaN(+indexes[0]) || +indexes[0];
          }

          result[finalArray[0].trim()] = index !== undefined
          && index !== null
          || index === 0 ? index : finalArray[1].trim();

          return result;
        }
      }
    }

    return {
      postJSON: postJSON
    }
  }
})();