angular.module('app').factory('dropdownAutocompleteListFactory', function ($http, $filter, TypeaheadFactory, DropdownFactory) {
  var dropdownAutocompleteListFactory = function (autocompleteData) {
    this.autocompleteData = autocompleteData;
    this.typeahead = new TypeaheadFactory();
    this.dropdown = new DropdownFactory();
  };

  dropdownAutocompleteListFactory.prototype.initialize = function (list) {
    this.typeahead.initialize(list);
    this.dropdown.initialize(list);
  };

  dropdownAutocompleteListFactory.prototype.select = function ($item, $model, $label) {
    this.typeahead.select($item, $model, $label);
    this.dropdown.select($item);
  };

  dropdownAutocompleteListFactory.prototype.load = function (oServiceData, search, params) {
    var self = this;
    if (!params)
      params = {};

    return this.typeahead.load(self.autocompleteData.apiUrl, search, params).then(function (data) {
      if (search && search.length > 0 && search !== '[$empty$]') {
        var filter = {};
        filter[self.autocompleteData.titleProperty] = search;
        return $filter('filter')(data, filter);
      } else
        return data;
    }).then(function (regions) {
      self.typeahead.list = regions;
      self.dropdown.list = regions;
      return regions;
    });
  };

  dropdownAutocompleteListFactory.prototype.reset = function () {
    this.typeahead.reset();
    this.dropdown.reset();
  };

  return dropdownAutocompleteListFactory;
});
