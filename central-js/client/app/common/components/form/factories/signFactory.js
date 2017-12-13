angular.module('app').factory('SignFactory', function () {

  var Sign = function Sign() {
    this.value = null;
  };

  Sign.prototype.isFit = function (property) {
    return property.type === 'file' && (/form_signed_[\d]+/.test(property.id) || /form_signed_all_[\d]+/.test(property.id));
  };

  Sign.prototype.get = function() {
    return this.value;
  };

  Sign.prototype.createFactory = function(){
    return new Sign();
  };

  return Sign;
});
