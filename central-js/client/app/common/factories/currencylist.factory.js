
angular.module('app').factory('CurrencyListFactory', function($http, $filter, TypeaheadFactory, DropdownFactory) {
  var currencyList = function() {
    this.typeahead = new TypeaheadFactory();
    this.dropdown = new DropdownFactory();
  };

  currencyList.prototype.initialize = function(list) {
    this.typeahead.initialize(list);
    this.dropdown.initialize(list);
  };

  currencyList.prototype.select = function($item, $model, $label) {
    this.typeahead.select($item, $model, $label);
    this.dropdown.select($item);
  };

  currencyList.prototype.load = function(oServiceData, search) {
    var self = this;

    var data = {};
    //  resident: oServiceData.resident
    //};
    return this.typeahead.load('./api/currencies/', search, data).then(function(currencyList) {
      console.log(currencyList);
      if (search && search.length > 0 && search !== '[$empty$]')
        return $filter('filter')(currencyList, {sName_UA:search});
      else
        return currencyList;
    }).then(function(currencyList) {
      console.log(currencyList);
      self.typeahead.list = currencyList;
      self.dropdown.list = currencyList;
      return currencyList;
    });
  };

  currencyList.prototype.reset = function() {
    this.typeahead.reset();
    this.dropdown.reset();
  };

  return currencyList;
});
