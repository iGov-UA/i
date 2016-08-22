angular.module('app').controller('dropdownAutocompleteCtrl', function ($scope, $http, $filter, $timeout, $q) {

  var hasNextChunk = true,
      queryParams = {params: {}},
      tempCollection, count = 30;

  function getInfinityScrollChunk() {
    $scope.isRequestMoreItems = true;
    return $http.get($scope.autocompleteData.apiUrl, queryParams).then(function (res) {
      if(angular.isDefined(res.config.params.sFind) && angular.isArray(res.data)){
        angular.forEach(res.data, function (el) {
          if(angular.isDefined(el.sID) && angular.isDefined(el.sNote)){
            el.sFind = el.sID + " " + el.sNote;
          } else if (angular.isDefined(el.sID) && angular.isDefined(el.sName_UA)) {
            el.sFind = el.sID + " " + el.sName_UA;
          }
        });
      }
      return res;
    });
  }

  var getAdditionalPropertyName = function() {
    return ($scope.autocompleteData.additionalValueProperty ? $scope.autocompleteData.additionalValueProperty : $scope.autocompleteData.valueProperty) + '_' + $scope.autocompleteName;
  };

  $scope.requestMoreItems = function(collection) {
      if ($scope.isRequestMoreItems || !hasNextChunk) {
          return $q.reject();
      }

      return ($scope.autocompleteData ? getInfinityScrollChunk() : $timeout(getInfinityScrollChunk, 200))
          .then(function(response) {
            Array.prototype.push.apply(collection, $filter('orderBy')(response.data, $scope.autocompleteData.orderBy));
            if (!$scope.autocompleteData.hasPaging || response.data.lenght < count) {
              hasNextChunk = false;
            }
            if ($scope.autocompleteData.hasPaging) {
              queryParams.params.skip = collection.length;
            }
            return collection;
          }, function(err) {
            return $q.reject(err);
          })
          .finally(function() {
            $scope.isRequestMoreItems = false;
          });
  };

  $scope.refreshList = function(queryKey, queryValue) {
    if (!angular.isDefined(queryParams.params[queryKey])) {
      hasNextChunk = true;
    }
    if (!angular.equals(queryParams.params[queryKey], queryValue)) {
      if ($scope.autocompleteData.hasPaging || !angular.isDefined(queryParams.params[queryKey])) {
        if ($scope.autocompleteData.hasPaging) {
          queryParams.params.count = count;
          queryParams.params.skip = 0;
        }
        $scope.isRequestMoreItems = false;
        hasNextChunk = true;
        queryParams.params[queryKey] = queryValue;
        $scope.requestMoreItems([]).then(function (items) {
          $timeout(function () {
            $scope.$select.items = items;
          }, 0, !angular.equals(queryParams.params[queryKey], queryValue));
        });
      } else {
        tempCollection = tempCollection || $scope.$select.items;
        $scope.$select.items = $filter('filter')(tempCollection, queryValue);
      }
    }
  };

  $scope.onSelectDataList = function (item) {
    var additionalPropertyName = getAdditionalPropertyName();
    if ($scope.formData.params[additionalPropertyName]) {
      $scope.formData.params[additionalPropertyName].value = item[$scope.autocompleteData.valueProperty];
    }
  };
});
