angular.module('iGovMarkers')
    .factory('iGovMarkers', function ($http, iGovMarkersDefaults, iGovMarkersSchema) {
        var markers = iGovMarkersDefaults;
        var definitions = iGovMarkersSchema;
        var fieldIDs = {
            isPresent: {},
            sFieldNotFaundMessages: "",
            isFieldNotFound: false,
            nFieldTurnOn: 0
        };
        return {
            getMarkers: function () {
                return markers;
            },
            validateMarkers: function (obj) {
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

                for (var i = 0; i < fieldIDs.inMarkers.length; i++){
                    var feldID = fieldIDs.inMarkers[i];
                    fieldIDs.isPresent[feldID] = false;
                }

                for (i in fieldIDs.isPresent) if (fieldIDs.isPresent.hasOwnProperty(i)){
                    if(fieldIDs.inForm.indexOf(i) >= 0){
                        fieldIDs.isPresent[i] = true;
                    }
                }

                for (i in fieldIDs.isPresent) if (fieldIDs.isPresent.hasOwnProperty(i)){
                    fieldIDs.nValidateFields++;
                    if(fieldIDs.isPresent[i] == false){
                        console.warn("Field " + i + " NOT found. Markers for this field is not working");
                        fieldIDs.sFieldNotFaundMessages = fieldIDs.sFieldNotFaundMessages + "Поле " + i + " - відсутнє на формі. ";
                        fieldIDs.isFieldNotFound = true;
                        fieldIDs.nFieldTurnOn++;
                    }
                }

                if(fieldIDs.nFieldTurnOn < fieldIDs.nValidateFields){
                    console.error("Markers connected to " +fieldIDs.nFieldTurnOn + " fields of the " + fieldIDs.nValidateFields + " announced..");
                } else {
                    console.info("Markers connected to ALL fields");
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
                            if (data.valid) {
                                alert(fieldIDs.sFieldNotFaundMessages);
                            } else {
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
                                alert(fieldIDs.sFieldNotFaundMessages + errMessages);
                            }
                        }
                    });
            },
            grepByPrefix: function (section, prefix) {
                return _.transform(_.pairs(markers[section]), function (result, value) {
                    if (value[0].indexOf(prefix) === 0) result.push(value[1]);
                });
            }
        }
    });
