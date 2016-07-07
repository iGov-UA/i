angular.module('iGovMarkers')
    .factory('iGovMarkers', function ($http, iGovMarkersDefaults, iGovMarkersSchema) {
        var markers = iGovMarkersDefaults;
        var definitions = iGovMarkersSchema;
        return {
            getMarkers: function () {
                return markers;
            },
            validateMarkers: function () {
                $http.post('/api/markers/validate', {
                    markers: markers,
                    definitions: definitions
                })
                    .then(function (response) {
                        var data = response.data;
                        if (data.valid) {
                            console.info('markers are valid');
                        } else {
                            console.error('markers validation failed', data.errors);
                            var errMessages = "Виникла помилка під час валідації маркерів: ";
                            for (var ind = 0; ind < data.errors.length; ind++) {
                                if (ind > 0) {
                                    errMessages = errMessages + "; ";
                                }
                                errMessages = errMessages + data.errors[ind].message;
                            }
                            alert(errMessages);

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
