angular.module('app').controller('dropdownAutocompleteCtrl', function ($scope, $http, $filter, $timeout, $q) {

  var hasNextChunk = true,
      queryString = '',
      skip = 0, count = 30,
      tempCollection;

  function getInfinityScrollChunk() {
    $scope.isRequestMoreItems = true;
    var params = $scope.autocompleteData.hasPaging ? {count: count, skip: skip} : {};
    hasNextChunk = $scope.autocompleteData.hasPaging ? hasNextChunk : false;
    params[$scope.autocompleteData.titleProperty] = queryString;
    return $http.get($scope.autocompleteData.apiUrl, {params: params});
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
            Array.prototype.push.apply(collection, $scope.autocompleteData.hasPaging ? response.data :
              $filter('orderBy')(response.data, $scope.autocompleteData.titleProperty));
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
    if (!$scope.autocompleteData.hasPaging && !hasNextChunk) {
      tempCollection = tempCollection || $scope.$select.items;
      if (query.length) {
        var params = {};
        params[$scope.autocompleteData.titleProperty] = query;
        $scope.$select.items = $filter('filter')(tempCollection, params);
      } else {
        $scope.$select.items = tempCollection;
      }
      return;
    }
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
