angular.module('dashboardJsApp').service('envConfigService', ['$http', '$q', envConfig]);

function envConfig($http, $q) {
  this.loadConfig = function(success, error) {
    var deferred = $q.defer();
    $http.get('/api/env/get-env-config').then(function(response) {
      console.log('response:' + angular.toString(response.data));
      deferred.resolve(response.data);
    });
    deferred.promise.then(success, error);
  }
}
