angular.module('app').service('BanksResponses', ['$http', function ($http) {

  return {
    getErrorMessage: function (code, error) {
      var params = {
        code: code,
        error: error
      };

      return $http.get('./api/masterpass/getErrorMessage', {params: params}).then(function (res) {
        return res;
      })
    }
  }
}]);
