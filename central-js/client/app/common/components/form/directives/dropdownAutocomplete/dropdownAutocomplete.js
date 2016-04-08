angular.module('app').directive('dropdownAutocomplete', function () {
  return {
    restrict: 'A',
    controller: 'dropdownAutocompleteCtrl',
    controllerAs: 'uiScroll',
    link: function (scope, element, attrs) {
      attrs.$observe('autocompleteData', function (attr) {
        scope.$eval('autocompleteData = ' + attr);
      });
      attrs.$observe('autocompleteName', function (attr) {
        scope.$eval('autocompleteName = ' + attr);
      });
      attrs.$observe('formData', function (attr) {
        scope.$eval('formData = ' + attr);
      });
    }
  };
});
