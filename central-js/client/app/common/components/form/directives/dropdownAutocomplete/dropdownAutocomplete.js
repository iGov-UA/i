angular.module('app').directive('dropdownAutocomplete', function ($controller) {
  return {
    restrict: 'EA',
    templateUrl: 'app/common/components/form/directives/dropdownAutocomplete/dropdownAutocomplete.html',
    scope: {
      ngModel: "=",
      serviceData: "=",
      ngRequired: "=",
      autocompleteData: "=",
      autocompleteName: '=',
      formData: "="
    },
    controller: 'dropdownAutocompleteController',
    link: function (scope, element, attrs, controller, transcludeFn) {
      console.log(controller);
      scope.loadDataList = function (search) {
        return controller.load(scope.serviceData, search);
      };
      var getAdditionalPropertyName = function() {
        return (scope.autocompleteData.additionalValueProperty ? scope.autocompleteData.additionalValueProperty : scope.autocompleteData.valueProperty) + '_' + scope.autocompleteName;
      };
      scope.onSelectDataList = function (item) {
        scope.ngModel = item[scope.autocompleteData.titleProperty];
        var additionalPropertyName = getAdditionalPropertyName();
        if (scope.formData.params[additionalPropertyName])
          scope.formData.params[additionalPropertyName].value = item[scope.autocompleteData.valueProperty];
        controller.typeahead.model = item[scope.autocompleteData.titleProperty];
      };
      controller.reset();
      controller.initialize();
      if (scope.autocompleteData.link)
        scope.autocompleteData.link(scope);
      else {
        controller.load(scope.serviceData, null).then(function (regions) {
          controller.initialize(regions);
        });
      }
      scope.resetAutoComplete = function() {
        controller.reset();
        var additionalPropertyName = getAdditionalPropertyName();
        if (scope.formData.params[additionalPropertyName])
          scope.formData.params[additionalPropertyName].value = null;
      };
    }
  };
});
