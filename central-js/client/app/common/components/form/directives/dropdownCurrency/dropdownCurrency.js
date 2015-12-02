angular.module('app').directive('dropdownCurrency', function (CurrencyListFactory) {
  return {
    restrict: 'EA',
    templateUrl: 'app/common/components/form/directives/dropdownCurrency/dropdownCurrency.html',
    scope: {
      ngModel: "=",
      serviceData: "=",
      ngRequired: "="
    },
    link: function (scope) {
      // init currency list for currency select
      scope.currencyList = new CurrencyListFactory();
      scope.loadCurrencyList = function (search) {
        return scope.currencyList.load(scope.serviceData, search);
      };
      scope.onSelectCurrencyList = function (currency) {
        scope.ngModel = currency.sName_UA;
        scope.currencyList.typeahead.model = currency.sName_UA;
      };
      scope.currencyList.reset();
      scope.currencyList.initialize();
      scope.currencyList.load(scope.serviceData, null).then(function (currencyList) {
        scope.currencyList.initialize(currencyList);
        scope.currencyList.typeahead.model = scope.ngModel;
      });
    }
  };
});
