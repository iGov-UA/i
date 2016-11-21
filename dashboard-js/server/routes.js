/**
 * Main application routes
 */

'use strict';

var errors = require('./components/errors');

module.exports = function(app) {

  // Insert routes below
  app.use('/api/processes', require('./api/process'));
  app.use('/api/tasks', require('./api/tasks'));
  app.use('/api/reports', require('./api/reports'));
  app.use('/api/schedule', require('./api/schedule'));
  app.use('/api/escalations', require('./api/escalations'));
  app.use('/api/deploy', require('./api/deploy'));
  app.use('/api/env', require('./api/env'));
  app.use('/api/markers', require('./api/markers'));
  app.use('/auth', require('./auth'));
  app.use('/api/profile', require('./api/profile'));
  app.use('/api/users', require('./api/user'));
  app.use('/api/countries', require('./api/countries'));
  app.use('/api/currencies', require('./api/currencies'));
  app.use('/api/object-customs', require('./api/object-customs'));
  app.use('/api/subject', require('./api/subject'));
  app.use('/api/object-earth-target', require('./api/object-earth-target'));
  app.use('/api/subject-action-kved', require('./api/subject-action-kved'));
  app.use('/api/object-place', require('./api/object-place'));
  app.use('/api/subject-role', require('./api/subject-role'));

  // All undefined asset or api routes should return a 404
  app.route('/:url(api|auth|components|app|bower_components|assets|public-js)/*')
   .get(errors[404]);

  // All other routes should redirect to the index.html
  app.route('/*')
    .get(function(req, res) {
      res.sendfile(app.get('appPath') + '/index.html');
    });
};
