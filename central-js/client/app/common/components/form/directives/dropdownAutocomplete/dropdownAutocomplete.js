angular.module('app').directive('dropdownAutocomplete', function () {
  return {
    restrict: 'A',
    controller: 'dropdownAutocompleteCtrl',
    controllerAs: 'uiScroll',
    link: function (scope, element, attrs) {
      scope.$eval('autocompleteData = ' + attrs.autocompleteData);
      scope.$eval('autocompleteName = ' + attrs.autocompleteName);
      scope.$eval('formData = ' + attrs.formData);
      if (angular.isFunction(scope.autocompleteData.init)) {
        scope.$eval(scope.autocompleteData.init);
      }
    }
  };
});
