angular.module('app').factory('DropdownFactory', function($timeout) {
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

  dropdown.prototype.get = function (index, count, success) {
    if (this.isOpen) {
  		$timeout(function () {
  			var result = [];
  			for (var i = index; i <= index + count - 1; i++) {
  				result.push("item #" + i);
  			}
  			success(result);
  		}, 100);
    } else {
      success([]);
    }
  };

  return dropdown;
});
