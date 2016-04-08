angular.module('app').controller('dropdownAutocompleteCtrl', function ($scope, $http, $filter, $timeout, $q) {

  var hasNextChunk = true,
      queryString = '',
      skip = 0, count = 30;

  function getInfinityScrollChunk() {
    $scope.isRequestMoreItems = true;
    return $http.get($scope.autocompleteData.apiUrl + '=' + queryString,
            {params: {count: count, skip: skip}});
  }

  var getAdditionalPropertyName = function() {
    return ($scope.autocompleteData.additionalValueProperty ? $scope.autocompleteData.additionalValueProperty : $scope.autocompleteData.valueProperty) + '_' + $scope.autocompleteName;
  };

  $scope.requestMoreItems = function(query, collection) {
      if ($scope.isRequestMoreItems || !hasNextChunk) {
          return $q.reject();
      }

      if (!angular.isDefined(collection)) {
        collection = query;
        query = queryString;
      }

      skip = collection.length;

      return ($scope.autocompleteData ? getInfinityScrollChunk() : $timeout(getInfinityScrollChunk, 200))
          .then(function(response) {
            Array.prototype.push.apply(collection, response.data);
            if (response.data.lenght < count) {
              hasNextChunk = false;
            }
            return collection;
          }, function(err) {
            return $q.reject(err);
          })
          .finally(function() {
            $scope.isRequestMoreItems = false;
          });
  };

  $scope.refreshList = function(query) {
    if (queryString !== query || query === '') {
      queryString = query;
      skip = 0;
      $scope.isRequestMoreItems = false;
      $scope.requestMoreItems(query, []).then(function (items) {
        $timeout(function () {
          $scope.$select.items = items;
        }, 0, queryString === query);
      });
    }
  };

  $scope.onSelectDataList = function (item) {
    var additionalPropertyName = getAdditionalPropertyName();
    if ($scope.formData.params[additionalPropertyName]) {
      $scope.formData.params[additionalPropertyName].value = item[$scope.autocompleteData.valueProperty];
    }
  };
});
