angular.module('app').factory('UserService', function ($http, $q, $rootScope, AdminService, ErrorsFactory) {
  var bankIDLogin;
  var bankIDAccount;

  return {
    isLoggedIn: function () {
      var oFuncNote = {sHead:"Перевірка авторизованості", sFunc:"isLoggedIn"};
      var deferred = $q.defer();

      $http.get('./auth/isAuthenticated').success(function (data, status) {
        deferred.resolve(true);
      }).error(function (data, status) {
        bankIDLogin = undefined;
        bankIDAccount = undefined;
        deferred.reject(true);
        ErrorsFactory.init(oFuncNote,{asParam:['bankIDLogin: '+bankIDLogin, 'bankIDAccount: '+bankIDAccount]});
        ErrorsFactory.addFail({sBody:'Помилка сервіса!',asParam:['data: '+data,'status: '+status]});
      });

      return deferred.promise;
    },

    login: function (code, redirect_uri) {
      var oFuncNote = {sHead:"Завантаження логіна по авторизації", sFunc:"login"};
      ErrorsFactory.init(oFuncNote,{asParam:['code: '+code, 'redirect_uri: '+redirect_uri, 'bankIDLogin: '+bankIDLogin, 'bankIDAccount: '+bankIDAccount]});
      var data = {
        'code': code,
        'redirect_uri': redirect_uri
      };
      if (bankIDLogin) {
        var deferred = $q.defer();
        deferred.resolve(bankIDLogin);
        return deferred.promise;
      } else {
        return $http.get('./api/bankid/login', {
          params: data,
          data: data
        }).then(function (oResponse) {
          if(ErrorsFactory.bSuccessResponse(oResponse.data)){
            return bankIDLogin = oResponse.data;
          }
        });
      }
    },

    logout: function (){
      $http.post('./auth/logout').then(function(){
        $rootScope.$broadcast('event.logout');
      });
    },

    fio: function(){
      var oFuncNote = {sHead:"Завантаження ПІБ по авторизації", sFunc:"fio"};
      return $http.get('./api/user/fio').then(function(oResponse){
        if(ErrorsFactory.bSuccessResponse(oResponse.data)){
          return oResponse.data;
        }
      }).catch(function (oResponse) {
        /*
         var err = oResponse.data ? oResponse.data.err || {} : {};
         ErrorsFactory.push({type: "danger", text: err.error});
         */
        bankIDLogin = undefined;
        bankIDAccount = undefined;
        ErrorsFactory.init(oFuncNote,{asParam:['bankIDLogin: '+bankIDLogin, 'bankIDAccount: '+bankIDAccount]});
        ErrorsFactory.addFail({sBody:'Помилка сервіса!',asParam:['soResponse: '+JSON.stringify(oResponse)]});
        return bankIDAccount;
      });
    },

    account: function () {
      var oFuncNote = {sHead:"Завантаження акаунту по авторизації", sFunc:"account"};
      ErrorsFactory.init(oFuncNote,{asParam:['bankIDLogin: '+bankIDLogin, 'bankIDAccount: '+bankIDAccount]});
      var data = {};
      return $q.when(bankIDAccount ? bankIDAccount :
        $http.get('./api/user', {
          params: data,
          data: data
        }).then(function (oResponse) {
          AdminService.processAccountResponse(oResponse);
          if(ErrorsFactory.bSuccessResponse(oResponse.data)){
            return bankIDAccount = oResponse.data;
          }
        }).catch(function (oResponse) {
          /*
           var err = oResponse.data ? oResponse.data.err || {} : {};
           ErrorsFactory.push({type: "danger", text: err.error});
           */
          bankIDLogin = undefined;
          bankIDAccount = undefined;
          ErrorsFactory.addFail({sBody:'Помилка сервіса!',asParam:['soResponse: '+JSON.stringify(oResponse)]});
          return oResponse.data;
        }));
    }
  };
});
