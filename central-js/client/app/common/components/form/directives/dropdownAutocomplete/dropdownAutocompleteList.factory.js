angular.module('app').controller('dropdownAutocompleteController', function ($scope, $http, $filter, TypeaheadFactory, DropdownFactory) {
  var self = this;
  $scope.typeahead = new TypeaheadFactory();
  $scope.dropdown = new DropdownFactory();
  $scope.$watch('dropdown.isOpen === true', this.load);

  this.initialize = function (list) {
    $scope.typeahead.initialize(list);
    $scope.dropdown.initialize(list);
  };

  this.select = function ($item, $model, $label) {
    $scope.typeahead.select($item, $model, $label);
    $scope.dropdown.select($item);
  };

  this.load = function (oServiceData, search, params) {
    if (!params)
      params = {};

    return $scope.typeahead.load($scope.autocompleteData.apiUrl, search, params).then(function (data) {
      if (search && search.length > 0 && search !== '[$empty$]') {
        var filter = {};
        filter[$scope.autocompleteData.titleProperty] = search;
        return $filter('filter')(data, filter);
      } else
        return data;
    }).then(function (regions) {
      $scope.typeahead.list = regions;
      $scope.dropdown.list = regions;
      return regions;
    });
  };

  this.reset = function () {
    $scope.typeahead.reset();
    $scope.dropdown.reset();
  };

});
