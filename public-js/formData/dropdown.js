angular.module('autocompleteService').factory('DropdownFactory', function($timeout) {
  var dropdown = function() {
    this.model = null;
    this.list = null;

    this.isOpen = false;
  };

  dropdown.prototype.initialize = function(list) {
    this.list = list;
  };

  dropdown.prototype.select = function($item) {
    this.model = $item;
  };

  dropdown.prototype.reset = function() {
    this.model = null;
    this.list = null;
  };

  return dropdown;
});
