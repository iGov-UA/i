angular.module('popUp').controller('PopUpController',
  ['$scope', '$modal', '$http', '$modalStack', '$window', '$log',
    function ($scope, $modal, $http, $modalStack, $log) {

      $scope.dropDown = 'sEnumExistentRequest_no';

      $scope.close = function () {
        $modalStack.dismissAll();
      };

      $scope.open = function () {
        var modalInstance = $modal.open({
          templateUrl: 'app/feedform/feedform.html',
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
            "bReferent": false}
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
