/**
 * Main application routes
 */

'use strict';

var uuid = require('node-uuid')
  , errors = require('./components/errors')
  , region = require('./components/region')
  , errorHandler = require('./components/errors/errorhander')
  , auth = require('./auth/auth.service.js')
  , logger = require('./components/logger').createLogger(module)
  , path = require('path');

module.exports = function (app) {
  function setupRoutes() {
    app.all('/:url(api|auth)/*', function (req, res, next) {
      logger.request(req);
      next();
    });
    app.use('/auth', require('./auth'));
    // check api call for nID_Server and get region host
    app.all('/api/*', region.searchForHost());

    //userMock
    app.use('/api/user', require('./api/user-mock'));

    // routes
    app.use('/api/user', require('./api/user'));
    app.use('/api/documents', require('./api/documents'));
    app.use('/api/journal', require('./api/journal'));
    app.use('/api/order', require('./api/order'));
    app.use('/api/places', require('./api/places/index'));
    app.use('/api/process-definitions', require('./api/process-definitions/index'));
    app.use('/api/process-form', auth.isAuthenticated(), require('./api/process-form'));
    app.use('/api/service', require('./api/service/index'));
    app.use('/api/service/flow', require('./api/service/flow'));
    app.use('/api/messages', require('./api/messages/index'));
    app.use('/api/catalog', require('./api/catalog'));
    app.use('/api/uploadfile', auth.isAuthenticated(), require('./api/uploadfile'));
    app.use('/api/countries', require('./api/countries'));
    app.use('/api/currencies', require('./api/currencies'));
    app.use('/api/object-customs', require('./api/object-customs'));
    app.use('/api/subject', require('./api/subject'));
    app.use('/api/payment-liqpay', require('./api/payment-liqpay'));
    app.use('/api/object-earth-target', require('./api/object-earth-target'));
    app.use('/api/subject-action-kved', require('./api/subject-action-kved'));
    app.use('/api/object-place', require('./api/object-place'));
    app.use('/api/markers', require('./api/markers'));
    app.use('/api/sign-content', require('./api/sign-content'));
    app.use('/api/answer', require('./api/answer'));
    app.use('/api/organization-info', require('./api/organization-info'));
    app.use('/api/masterpass', require('./api/masterpass'));
    app.use('/api/process-feedform', require('./api/process-feedform'));
    // All undefined asset or api routes should return a 404
    app.route('/:url(api|auth|components|app|bower_components|assets|public-js)/*')
      .get(errors[404]);

    // All other routes should redirect to the index.html
    var indexHtml = app.get('appPath') + '/index.html';
    app.route('/*')
      .get(function (req, res) {
        // req.session.id = uuid.v1();
        res.sendFile(indexHtml);
      });

    app.use(errorHandler.logErrorHandler);
    app.use(errorHandler.errorHandler);
  }
  // Insert routes below
  logger.setup('setup routes', {}, setupRoutes);
};
