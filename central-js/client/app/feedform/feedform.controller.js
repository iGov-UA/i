angular.module('popUp').controller('PopUpController',
  ['$scope', '$modal', '$http', '$modalStack', '$window', '$location',
    function ($scope, $modal, $http, $modalStack) {

      $scope.saveFAQDataToLocal = function () {
        localStorage.setItem("sNameCitizen", $scope.sNameCitizen);
        localStorage.setItem("email", $scope.email);
        localStorage.setItem("phone", $scope.phone);
        localStorage.setItem("sProblemDescription", $scope.sProblemDescription);
        localStorage.setItem("pageReloadAfterAuth", "afterReload");
      };

      $scope.getRedirectUrlOnFAQ = function () {
        var URL = document.URL;
        return URL;
      };

      $scope.dropDown = 'sEnumExistentRequest_yes';
      var isOrder = false;
      var oTaskData;
      var properties = [];
      var firstName;
      var lastName;
      var middleName;

      $scope.isAuthenticated = function () {
        $http.get('auth/isAuthenticated').success(function (data) {
          $http.get('api/user/fio').success(function (data) {
            lastName = data.lastName;
            firstName = data.firstName;
            middleName = data.middleName;
            $scope.sNameCitizen = firstName + " " + middleName + " " + lastName;
          })
          $scope.isAuth = true;
        })
          .error(function (data, status) {
            $scope.isAuth = false;
          });

        var URL = document.URL;
        var orderIdExpr = new RegExp(/(\d{1}-\d{9})/g);
        var sID_Order;
        var orderExpr = new RegExp(/sID_Order=(\d{1}-\d{9})/g);
        isOrder = orderExpr.test(URL);

        if (isOrder) {
          sID_Order = URL.match(orderIdExpr);
          sID_Order = sID_Order[0].toString();
          $scope.sRequestNumber = sID_Order;
          $scope.getOrderData(sID_Order);
        }
      };


      $scope.open = function () {
        var modalInstance = $modal.open({
          scope: $scope,
          templateUrl: 'app/feedform/feedform.html'
        });
        if (localStorage.getItem("email") !== null && localStorage.getItem("email") !== "undefined"){
          $scope.email = localStorage.getItem("email");
        }
        if (localStorage.getItem("phone") !== null && localStorage.getItem("phone") !== "undefined"){
          $scope.phone = localStorage.getItem("phone");
        }
        if (localStorage.getItem("sNameCitizen") !== null && localStorage.getItem("sNameCitizen") !== "undefined"){
          $scope.sNameCitizen = localStorage.getItem("sNameCitizen");
        }
        if(localStorage.getItem("sProblemDescription") !== null && localStorage.getItem("sProblemDescription") !== "undefined"){
          $scope.sProblemDescription = localStorage.getItem("sProblemDescription");
        }
      };

      $scope.close = function () {
        $modalStack.dismissAll();
      };


      $scope.getOrderData = function (sID_Order) {
        $scope.gotOrder = true;
        var body = {
          "nID_Server": "5",
          sID_Order: sID_Order,
          bIncludeStartForm: true
        };

        $http.post('/api/process-feedform/getTaskData', body).then(function successCallback(response) {
          oTaskData = {
            sDateCreate: response.data.oProcess.sDateCreate,
            sDateClose: response.data.oProcess.sDateClose,
            sNameOrgan: response.data.aFieldStartForm.sNameOrgan,
            sLoginAssigned: response.data.sLoginAssigned,
            sName: response.data.oProcess.sName,
            sStatusName: response.data.sStatusName,
            sCancelInfo: response.data.aFieldStartForm.sCancelInfo,
            bReferent: response.data.aFieldStartForm.bReferent,
            sMailClerk: response.data.aFieldStartForm.sMailClerk,
            sPhoneOrgan: response.data.aFieldStartForm.sPhoneOrgan,
            sID_Public_SubjectOrganJoin: response.data.aFieldStartForm.sID_Public_SubjectOrganJoin
          };

          var oRename = {
            sDateCreate: "Дата створення",
            sDateClose: "Дата закриття",
            sNameOrgan: "Назва органу",
            sLoginAssigned: "Логін",
            sName: "Назва послуги",
            sStatusName: "Назва таску",
            sCancelInfo: "Відміна",
            sMailClerk: "E-mail співробітника",
            sPhoneOrgan: "Телефони органу"
          }
          var renamed = {};


          for (var id in oTaskData) {
            if (oTaskData.hasOwnProperty(id)) {
              var value = oTaskData[id];
              if (!value) {
                delete oTaskData[id];
              }
            }
          }

          for (id in oTaskData) {
            if (oTaskData.hasOwnProperty(id)) {
              for (id in oRename) {
                if (oRename.hasOwnProperty(id) && oTaskData[id]) {
                  renamed[oRename[id]] = oTaskData[id];
                }
              }
            }
          }

          $scope.oTask = renamed;
          $scope.email = response.data.aFieldStartForm.email || response.data.aField[4].sValue;
          $scope.phone = response.data.aFieldStartForm.phone || response.data.aField[5].sValue;
          $scope.sServiceName = response.data.oProcess.sName;
        }, function errorCallback(response) {
        });
      };


      $scope.sendData = function () {
        var body = JSON.stringify({
          "nID_Server": "5",
          "params": {
            "sNameCitizen": $scope.sNameCitizen ? $scope.sNameCitizen : null,
            "email": $scope.email ? $scope.email : null,
            "phone": $scope.phone ? $scope.phone : null,
            "asExistentRequest": $scope.asExistentRequest ? $scope.asExistentRequest : null,
            "sRequestNumber": $scope.sRequestNumber ? $scope.sRequestNumber : null,
            "asRegionName": $scope.asRegionName ? $scope.asRegionName : null,
            "sCityName": $scope.sCityName ? $scope.sCityName : null,
            "sServiceName": $scope.sServiceName ? $scope.sServiceName : null,
            "sProblemDescription": $scope.sProblemDescription ? $scope.sProblemDescription : null,
            "Screen": $scope.Screen ? $scope.Screen : null,
            "markers1": "{\r\n  \"motion\": {\r\n    \"ShowFieldsOnCondition_1\": {\r\n      \"aField_ID\": [\r\n        \"sRequestNumber\", \"asRegionName\",\"sCityName\",\"sServiceName\", \"sProblemDescription\"\r\n      ],\r\n      \"asID_Field\": {\r\n        \"sCondit\": \"asExistentRequest\"\r\n      },\r\n      \"sCondition\": \"[sCondit]== 'sEnumExistentRequest_yes'\"\r\n    },\r\n    \"ShowFieldsOnCondition_2\": {\r\n      \"aField_ID\": [\r\n        \"asRegionName\",\"sCityName\",\"sServiceName\", \"sProblemDescription\"\r\n      ],\r\n      \"asID_Field\": {\r\n        \"sCondit\": \"asExistentRequest\"\r\n      },\r\n      \"sCondition\": \"[sCondit]=='sEnumExistentRequest_no'\"\r\n    \t}\r\n     \r\n\t}\r\n}",
            "saField": JSON.stringify(oTaskData),
            "bankIdlastName": lastName,
            "bankIdfirstName": firstName,
            "bankIdmiddleName": middleName
          }
        });

        $http({
          method: 'POST',
          url: 'api/process-feedform',
          data: body,
        }).success(function (data, status, headers, config) {
          $modal.open({
            templateUrl: 'app/feedform/feedsuccess.html'
          });
        }).error(function (data, status, headers, config) {
          $modal.open({
            templateUrl: 'app/feedform/feederror.html'
          });
        });
        localStorage.clear();
      };

      function checkOnPageReload() {
        if (localStorage.getItem("pageReloadAfterAuth") === "afterReload"){
          $scope.open();
          localStorage.setItem("pageReloadAfterAuth", "notafterReload");
        }
      }
      checkOnPageReload();
    }
  ]
);
