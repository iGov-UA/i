angular.module('popUp').controller('PopUpController',
  ['$scope', '$modal', '$http', '$modalStack', '$window', '$location',
    function ($scope, $modal, $http, $modalStack) {

      $scope.dropDown = 'sEnumExistentRequest_yes';
      var isOrder = false;
      var oTaskData;
      var properties = [];

      $scope.isAuthenticated = function () {
        $http.get('auth/isAuthenticated').success(function (data) {
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
      };

      $scope.close = function () {
        $modalStack.dismissAll();
      };


      $scope.getOrderData = function (sID_Order) {
        var body = {
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

          for (var id in oTaskData) {
            if (oTaskData.hasOwnProperty(id)) {
              var value = oTaskData[id];
              if (!value) {
                delete oTaskData[id];
              }
            }
          }

          $scope.oTask = oTaskData;

          //console.log($scope.oTask);

          $scope.email = response.data.aFieldStartForm.email;
          $scope.phone = response.data.aFieldStartForm.phone;
          $scope.sNameCitizen = response.data.aFieldStartForm.bankIdfirstName + " " + response.data.aFieldStartForm.bankIdlastName;

        }, function errorCallback(response) {
          console.log('false');
        });
      };


      $scope.sendData = function () {
        var body = JSON.stringify({
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
            "saField": oTaskData ? oTaskData : null
          }
        });

        $http.post('api/process-feedform', body).success(function (data, status, headers, config) {
          console.log(data);
          $modal.open({
            templateUrl: 'app/feedform/feedsuccess.html'
          });
        }).error(function (data, status, headers, config) {
          console.log(data);
          $modal.open({
            templateUrl: 'app/feedform/feederror.html'
          });
        });
      };
    }
  ]
);
