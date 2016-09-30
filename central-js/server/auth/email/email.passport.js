var emailService = require('./email.service');

exports.setup = function (config, authProviderRegistry) {
  authProviderRegistry.use('email', emailService);
};
