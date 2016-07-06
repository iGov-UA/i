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
                        if (!data.valid){
                            console.error('markers validation failed', data.errors);
                            debugger;
                        } else {
                            console.info('markers are valid');
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
