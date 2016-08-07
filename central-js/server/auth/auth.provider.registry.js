var registry = {};

var userServicesRegistry = {
  use: function (authType, service) {
    registry[authType] = service;
  },

  get: function (authType) {
    return registry[authType];
  }
};

module.exports.use = function (authType, service) {
  userServicesRegistry.use(authType, service);
};

module.exports.getUserService = function (authType) {
  return userServicesRegistry.get(authType);
};
