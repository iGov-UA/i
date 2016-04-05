angular.module('dashboardJsApp').factory('MarkersFactory', function() {
  var markers = {
    motion: {

    }
  };

  return {
    getMarkers: function () {
      return markers;
    },
    grepByPrefix: function (section, prefix) {
      return _.transform(_.pairs(markers[section]), function (result, value) {
        if (value[0].indexOf(prefix) === 0) result.push(value[1]);
      });
    }
  }
});
