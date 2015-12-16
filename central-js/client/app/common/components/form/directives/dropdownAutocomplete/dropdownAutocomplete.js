angular.module('app').directive('dropdownAutocomplete', function (dropdownAutocompleteListFactory) {
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
    link: function (scope) {
      scope.dataList = new dropdownAutocompleteListFactory(scope.autocompleteData);
      scope.loadDataList = function (search) {
        return scope.dataList.load(scope.serviceData, search);
      };
      var getAdditionalPropertyName = function() {
        return (scope.autocompleteData.additionalValueProperty ? scope.autocompleteData.additionalValueProperty : scope.autocompleteData.valueProperty) + '_' + scope.autocompleteName;
      };
      scope.onSelectDataList = function (item) {
        scope.ngModel = item[scope.autocompleteData.titleProperty];
        var additionalPropertyName = getAdditionalPropertyName();
        if (scope.formData.params[additionalPropertyName])
          scope.formData.params[additionalPropertyName].value = item[scope.autocompleteData.valueProperty];
        scope.dataList.typeahead.model = item[scope.autocompleteData.titleProperty];
      };
      scope.dataList.reset();
      scope.dataList.initialize();
      if (scope.autocompleteData.link)
        scope.autocompleteData.link(scope);
      else {
        scope.dataList.load(scope.serviceData, null).then(function (regions) {
          scope.dataList.initialize(regions);
        });
      }
      scope.resetAutoComplete = function() {
        scope.dataList.reset();
        var additionalPropertyName = getAdditionalPropertyName();
        if (scope.formData.params[additionalPropertyName])
          scope.formData.params[additionalPropertyName].value = null;
      };
    }
  };
});
