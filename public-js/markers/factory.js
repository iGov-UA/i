angular.module('iGovMarkers')
    .factory('iGovMarkers', function ($http, iGovMarkersDefaults, iGovMarkersSchema) {
        var markers, definitions, fieldIDs;
        return {
            reset: function () {
              markers = null;
              definitions = null;
              fieldIDs = null;
            },
            init: function () {
              markers = iGovMarkersDefaults.getDefaultMarkers();
              definitions = iGovMarkersSchema;
              fieldIDs = {
                isPresent: {},
                sFieldNotFaundMessages: "",
                isFieldNotFound: false,
                nValidateFields: 0,
                nFieldTurnOn: 0
              };
            },
            getMarkers: function () {
                return markers;
            },
            validateMarkers: function (obj, bTest) {
                if (!bTest){
                    bTest = false;
                }
                if (!obj) {
                    fieldIDs.inForm = [];
                    fieldIDs.inMarkers = [];
                } else {
                    if(!obj.inForm) obj.inForm = [];
                    if(!obj.inMarkers) obj.inMarkers = [];
                    fieldIDs = obj;
                }

                fieldIDs.isPresent = {};
                fieldIDs.sFieldNotFaundMessages =  "";
                fieldIDs.isFieldNotFound =  false;
                fieldIDs.nFieldTurnOn =  0;
                fieldIDs.nValidateFields = 0;

                for (var i = 0; i < fieldIDs.inMarkers.length; i++){
                    var feldID = fieldIDs.inMarkers[i];
                    fieldIDs.isPresent[feldID] = false;
                }

                for (i in fieldIDs.isPresent) if (fieldIDs.isPresent.hasOwnProperty(i)){
                    if(fieldIDs.inForm.indexOf(i) >= 0){
                        fieldIDs.isPresent[i] = true;
                    }
                }

                var aUndefinedFormFields = [];

                for (i in fieldIDs.isPresent) if (fieldIDs.isPresent.hasOwnProperty(i)){
                    fieldIDs.nValidateFields++;
                    if(fieldIDs.isPresent[i] == false){
                        console.warn("Field " + i + " NOT found. Markers for this field is not working");
                        aUndefinedFormFields.push(i);
                        fieldIDs.isFieldNotFound = true;
                    } else {
                        fieldIDs.nFieldTurnOn++;
                    }
                }

                if(fieldIDs.isFieldNotFound){
                    if(aUndefinedFormFields.length > 1){
                        fieldIDs.sFieldNotFaundMessages = "На формі не виявлені поля з ідентифікаторами ";
                        aUndefinedFormFields.forEach(function (field) {
                            fieldIDs.sFieldNotFaundMessages = fieldIDs.sFieldNotFaundMessages + field + ", ";
                        });
                        fieldIDs.sFieldNotFaundMessages = fieldIDs.sFieldNotFaundMessages + "тому маркери для них не працюють!";
                    } else {
                        fieldIDs.sFieldNotFaundMessages = "Поле " + aUndefinedFormFields[0] + " не виявлено на формі, тому маркери для цього поля не працюють!";
                    }
                }

                if(fieldIDs.nFieldTurnOn < fieldIDs.nValidateFields){
                    console.error("Markers connected to " +fieldIDs.nFieldTurnOn + " fields of the " + fieldIDs.nValidateFields + " announced..");
                } else {
                    if (fieldIDs.nValidateFields > 0) console.info("Markers connected to ALL fields");
                }

                $http.post('/api/markers/validate', {
                    markers: markers,
                    definitions: definitions
                })
                    .then(function (response) {
                        var data = response.data;
                        if (data.valid && fieldIDs.isFieldNotFound == false) {
                            console.info('markers are valid');
                        } else {
                            if (!data.valid) {
                                console.error('markers validation failed', data.errors);
                                var errMessages = "Виникла помилка під час валідації маркерів: ";
                                for (var ind = 0; ind < data.errors.length; ind++) {
                                    if (ind > 0) {
                                        errMessages = errMessages + "; ";
                                    }
                                    errMessages = errMessages + data.errors[ind].dataPath + " " + data.errors[ind].message;
                                    if (data.errors[ind].params) {
                                        for (var key1 in data.errors[ind].params) if (data.errors[ind].params.hasOwnProperty(key1)) {
                                            errMessages = errMessages + " " + data.errors[ind].params[key1];
                                        }
                                    }
                                }
                                fieldIDs.sFieldNotFaundMessages = fieldIDs.sFieldNotFaundMessages + errMessages;
                            }
                        }
                        
                        if(bTest){
                            alert(fieldIDs.sFieldNotFaundMessages);
                        }
                    });
            },
            grepByPrefix: function (section, prefix) {
                return _.transform(_.pairs(markers[section]), function (result, value) {
                    if (value[0].indexOf(prefix) === 0) result.push(value[1]);
                });
            },

          /**
           * Интерполяция строки с подстановкой значения полей формы
           * @param strPattern - исходная строка с паттерном
           * @param objData - сам объект, из которого будут подставляться значения
           * @param startSymbol - (опциональный) открывающийся символ для обозначения места вставки
           * @param endSymbol - (опциональный) закрывающий символ для обозначения места подстановки
           * @param arrKeys - (опциональный) массив наименований параметров объекта, откуда будут браться значения для подстановки
           * @param arrFormProperties - Activiti Form Properties
           * @returns {{value: string, differentTriggered: boolean}}
           */
            interpolateString: function(strPattern, objData, startSymbol, endSymbol, arrKeys, arrFormProperties) {
                var result = {
                    value: '',
                    differentTriggered: false
                };
                if (!strPattern || strPattern === null || strPattern === "") {
                    return result;
                }
                if (!arrKeys || arrKeys === null) {
                    arrKeys = [];
                    for (var sKey in objData) if (objData.hasOwnProperty(sKey)){
                        arrKeys.push(sKey);
                    }
                }
                if (!startSymbol || startSymbol === null || startSymbol === '') {
                    startSymbol = "{{";
                }
                if (!endSymbol || endSymbol === null || endSymbol === '') {
                    endSymbol = "}}";
                }
                //if (!arrFormProperties || arrFormProperties === null) {
                //    arrFormProperties = [];
                //}
                //objData.aActivitiFormProperties = arrFormProperties || [];

                var aSubstrings = [];

                var findOpenSymbol = true;
                var startIndexChar = 0;
                for (var strCharInd = 0; strCharInd < strPattern.length; strCharInd++) {
                    if (findOpenSymbol && strPattern.charAt(strCharInd) === startSymbol) {
                        addSubstring(strCharInd);
                        continue;
                    }
                    if (!findOpenSymbol && strPattern.charAt(strCharInd) === endSymbol) {
                        addSubstring(strCharInd);
                        continue;
                    }
                    if (strCharInd == strPattern.length - 1) {
                        addSubstring(strCharInd + 1);
                    }
                }
                function addSubstring(index) {
                    aSubstrings.push(strPattern.substring(startIndexChar, index));
                    startIndexChar = index + 1;
                    findOpenSymbol = !findOpenSymbol;
                }
                startIndexChar = 0;
                var needAddFromArray = true;
                for (var arrInd = 0; arrInd < aSubstrings.length; arrInd++) {
                    angular.forEach(arrKeys, function (objDataKey) {
                        if (objDataKey === aSubstrings[arrInd]) {
                            if(objData[objDataKey].value && angular.isString(objData[objDataKey].value)){
                                var bIsSetEnumValue = false;
                                if(arrFormProperties){
                                    angular.forEach(arrFormProperties, function (oProperty) {
                                        if(oProperty.type && oProperty.type === 'enum'){
                                            angular.forEach(oProperty.enumValues, function (oEnumValue) {
                                                if(objData[objDataKey].value === oEnumValue.id){
                                                    result.value = result.value + oEnumValue.name;
                                                    bIsSetEnumValue = true;
                                                }
                                            })
                                        }
                                    })
                                }
                                if(!bIsSetEnumValue){
                                    result.value = result.value + objData[objDataKey].value;
                                }
                                result.differentTriggered = true;
                            } else if (angular.isString(objData[objDataKey])) {
                                result.value = result.value + objData[objDataKey];
                                result.differentTriggered = true;
                            }
                            needAddFromArray = false;
                        }
                    });
                    if (!needAddFromArray) {
                        needAddFromArray = true;
                        continue;
                    }
                    result.value = result.value + aSubstrings[arrInd];
                }
                return result;
            }
        }
    });
