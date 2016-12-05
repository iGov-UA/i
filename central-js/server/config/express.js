/**
 * Express configuration
 */
'use strict';

var express = require('express')
  , favicon = require('serve-favicon')
  , morgan = require('morgan')
  , compression = require('compression')
  , bodyParser = require('body-parser')
  , methodOverride = require('method-override')
  , cookieParser = require('cookie-parser')
  , errorHandler = require('errorhandler')
  , path = require('path')
  , config = require('./environment')
  , session = require('cookie-session')
  , uuid = require('node-uuid')
  , logger = require('../components/logger').createLogger(module);

module.exports = function (app) {
  function setupExpress() {
    app.set('env', config.env);
    var env = app.get('env');

    app.set('views', config.root + '/server/views');
    app.engine('html', require('ejs').renderFile);
    app.set('view engine', 'html');
    app.use(compression());
    app.use(bodyParser.urlencoded({extended: false}));
    app.use(bodyParser.json({limit: '5mb'}));
    app.use(session({
      secret: config.server.session.secret,
      keys: config.server.session.keys,
      secure: config.server.session.secure === "true",
      signed: true,
      maxAge: config.server.session.maxAge
    }));
    app.use(methodOverride());
    app.use(cookieParser());
    app.use(function (req, res, next) {
      req.reqid = uuid.v1({
        msecs: new Date().getTime()
      });
      res.set('reqid', req.reqid);
      next();
    });

    if (config.bCompile) {
      app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
      app.use(express.static(path.join(config.root, 'public')));
      app.set('appPath', config.root + '/public');
    }

    //if (!config.bCompile || 'development' === env || 'local' === env || 'test' === env) {
    if (!config.bCompile || 'development' === env || 'local' === env) {
      app.use(require('connect-livereload')());
      app.use(express.static(path.join(config.root, '.tmp')));
      app.use(express.static(path.join(config.root, 'client')));
      app.use('/public-js', express.static(path.resolve(config.root + '/../public-js')));
      app.set('appPath', path.join(config.root, 'client'));
      app.use(errorHandler()); // Error handler - has to be last
    }
  }

  logger.setup('setup express middleware', {}, setupExpress);
};
