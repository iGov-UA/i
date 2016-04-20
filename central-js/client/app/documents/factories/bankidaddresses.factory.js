angular.module('documents').factory('BankIDAddressesFactory', function(BankIDAddressesFactualFactory) {
  var addresses = function() {
    this.list = [];
  };

  addresses.prototype.initialize = function(list) {
    angular.forEach(list, function(value, key) {
      switch (value.type) {
        case 'factual':
          var element = new BankIDAddressesFactualFactory();
          element.initialize(value);

          this.list.push(element);
          break;
        default:
          break;
      }
    }, this);
  };

  addresses.prototype.getAddress = function() {
    for (var i = 0; i < this.list.length; i++) {
      var element = this.list[i];
      if (element instanceof BankIDAddressesFactualFactory) {
        return element.toString();
      }
    }
    return "";//null
  };
  addresses.prototype.getCountyCode = function() {
    for (var i = 0; i < this.list.length; i++) {
      var element = this.list[i];
      if (element instanceof BankIDAddressesFactualFactory) {
        return element.getCountyCodeTwo();
      }
    }
    return null;
  };

  addresses.prototype.getCountry = function() {
    for (var i = 0; i < this.list.length; i++) {
      var element = this.list[i];
      if (element instanceof BankIDAddressesFactualFactory) {
        return element.getCountry();
      }
    }
    return "";
  };


  addresses.prototype.getState = function() {
    for (var i = 0; i < this.list.length; i++) {
      var element = this.list[i];
      if (element instanceof BankIDAddressesFactualFactory) {
        return element.getState();
      }
    }
    return "";
  };

  addresses.prototype.getArea = function() {
    for (var i = 0; i < this.list.length; i++) {
      var element = this.list[i];
      if (element instanceof BankIDAddressesFactualFactory) {
        return element.getArea();
      }
    }
    return "";
  };

  addresses.prototype.getCity = function() {
    for (var i = 0; i < this.list.length; i++) {
      var element = this.list[i];
      if (element instanceof BankIDAddressesFactualFactory) {
        return element.getCity();
      }
    }
    return "";
  };

  addresses.prototype.getStreet = function() {
    for (var i = 0; i < this.list.length; i++) {
      var element = this.list[i];
      if (element instanceof BankIDAddressesFactualFactory) {
        return element.getStreet();
      }
    }
    return "";
  };

  addresses.prototype.getHouseNo = function() {
    for (var i = 0; i < this.list.length; i++) {
      var element = this.list[i];
      if (element instanceof BankIDAddressesFactualFactory) {
        return element.getHouseNo();
      }
    }
    return "";
  };

  addresses.prototype.getFlatNo = function() {
    for (var i = 0; i < this.list.length; i++) {
      var element = this.list[i];
      if (element instanceof BankIDAddressesFactualFactory) {
        return element.getFlatNo();
      }
    }
    return "";
  };

  return addresses;
});
