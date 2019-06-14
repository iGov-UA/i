angular.module('app').factory('UserService', function ($http, $q, $rootScope, AdminService, ErrorsFactory, serviceLocationParser) {
  var bankIDLogin;
  var bankIDAccount;
  var oSubjectNew;

  return {
    isLoggedIn: function () {
      var oFuncNote = {sHead:"Перевірка авторизованості", sFunc:"isLoggedIn"};
      var deferred = $q.defer();

      $http.get('./auth/isAuthenticated').success(function (data, status) {
        if (location.href.indexOf('sID_Session=') > -1) {
          var reg = new RegExp("((&)*sID_Session=([^&]*))","g");
          var sNewLocaion = location.href.replace(reg, '');
          location = sNewLocaion;
        }

        deferred.resolve(true);
      }).error(function (data, status) {
        tryRestoreSession();

        bankIDLogin = undefined;
        bankIDAccount = undefined;
        $rootScope.$broadcast('event.logout.without.session');
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
            $rootScope.bankIDAccount = oResponse.data;
            return bankIDAccount = oResponse.data;
          } else {
            return $q.reject(oResponse.data);
          }
        }, function (err) {
          $rootScope.$broadcast('event.logout.without.session');
        }).catch(function (oResponse) {
          /*
           var err = oResponse.data ? oResponse.data.err || {} : {};
           ErrorsFactory.push({type: "danger", text: err.error});
           */

          bankIDLogin = undefined;
          bankIDAccount = undefined;
          ErrorsFactory.addFail({sBody:'Помилка сервіса!',asParam:['soResponse: '+JSON.stringify(oResponse)]});
          return  $q.reject(oResponse.data);
        }));
    },

    getCustomer: function() {
      try {
        return bankIDAccount ? convertCustomerObj(bankIDAccount.customer) : null;
      } catch (error) {
        return null;
      }
    }
  };

  function tryRestoreSession() {
    var oReqParams = serviceLocationParser.getParams();
    console.log(oReqParams);

    if (oReqParams && oReqParams.sID_Session) {
      location = '/auth/restoreSession?sID_Session='+oReqParams.sID_Session;
      return true;
    }
    return false
  }

  function convertCustomerObj(oCustomer) {
    var place = oCustomer.addresses.filter(function(addr) {
      return addr.type === 'factual';
    })[0];

    var oResCustomer = {  
      "sSubjectFullName": oCustomer.lastName +' '+ oCustomer.firstName +' '+ oCustomer.middleName,
      "sID_SubjectHuman":oCustomer.inn,
      "sKey_SubjectHumanGenderType":"",
      "sSubjectHumanLastName": oCustomer.lastName,
      "sSubjectHumanFirstName": oCustomer.firstName,
      "sSubjectHumanMiddleName": oCustomer.middleName,
      "sSubjectHumanDateBirth": oCustomer.birthDay,
      "sSubjectHumanDateDeath":"",
      "aSubjectHumanPhone":[  
        oCustomer.phone
      ],
      "aSubjectHumanEmail":[  
        oCustomer.email
      ],
      "aDocument":[],
      "oSubjectHumanPlace":{  
         "sPlaceKoatuu":"",
         "sPlaceRegion": place.state,
         "sPlace": place.city,
         "sPlaceType": null,
         "sPlaceArea": place.area,
         "sPlaceCityArea":"",
         "sPlaceBranchType":"",
         "sPlaceBranch": place.street,
         "sKey_PlaceBranchType":null,
         "sKey_PlaceBuildType":null,
         "sKey_PlaceBuildPartType":null,
         "sKey_PlaceBuildPartCellType":null,
         "sPlaceBuildPartCellType":null,
         "sPlaceBranchOld":null,
         "sPlaceBuildType": null,
         "sPlaceBuildNumber": place.houseNo,
         "sPlaceBuildLetter":null,
         "sPlaceBuildPart":null,
         "sPlaceBuildPartType":null,
         "sPlaceBuildPartCell":null,
         "sPlaceInfo":"",
         "fullAddress":""
      }
    };

    angular.forEach(oCustomer.documents, function(oDoc) {
      var oDocument = {  
        "sKey_DocumentType":"",
        "sDocumentType": oDoc.type,
        "sDocumentSeria": oDoc.series,
        "sDocumentNumber": oDoc.number,
        "sDocumentDateStart": oDoc.dateIssue,
        "sDocumentDateFinish": oDoc.dateExpiration,
        "sDocumentOrgan": oDoc.issue
      };

      oResCustomer.aDocument.push(oDocument);
    });

    oSubjectNew = oResCustomer;
    return oSubjectNew;
  }
});
