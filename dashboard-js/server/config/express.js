/**
 * Express configuration
 */

'use strict';

var express = require('express');
var favicon = require('serve-favicon');
var morgan = require('morgan');
var compression = require('compression');
var bodyParser = require('body-parser');
var methodOverride = require('method-override');
var cookieParser = require('cookie-parser');
var errorHandler = require('errorhandler');
var path = require('path');
var config = require('./environment');
var session = require('cookie-session');

module.exports = function(app) {
  app.set('env', config.env);
  var env = app.get('env');

  app.set('views', config.root + '/server/views');
  app.engine('html', require('ejs').renderFile);
  app.set('view engine', 'html');
  app.use(compression());
  app.use(bodyParser.urlencoded({
    extended: false
  }));
  app.use(bodyParser.json());
  app.use(methodOverride());
  app.use(cookieParser());
  app.use(session(config.server.session));

  app.use(function(req, res, next) {
    res.setHeader("Access-Control-Allow-Origin", "*");
    return next();
  });

/*
  if ('production' === env) {
    app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
    app.use(express.static(path.join(config.root, 'public')));
    app.set('appPath', config.root + '/public');
    app.use(morgan(':method :url :status :response-time ms - :res[content-length]'));
  }
    if ('prod-backup' === env) {
    app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
    app.use(express.static(path.join(config.root, 'public')));
    app.set('appPath', config.root + '/public');
    app.use(morgan(':method :url :status :response-time ms - :res[content-length]'));
  }
  if ('test-alpha' === env) {
    app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
    app.use(express.static(path.join(config.root, 'public')));
    app.set('appPath', config.root + '/public');
    app.use(morgan(':method :url :status :response-time ms - :res[content-length]'));
  }
  if ('test-beta' === env) {
    app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
    app.use(express.static(path.join(config.root, 'public')));
    app.set('appPath', config.root + '/public');
    app.use(morgan(':method :url :status :response-time ms - :res[content-length]'));
  }
  if ('test-omega' === env) {
    app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
    app.use(express.static(path.join(config.root, 'public')));
    app.set('appPath', config.root + '/public');
    app.use(morgan(':method :url :status :response-time ms - :res[content-length]'));
  }
  if ('test-delta' === env) {
    app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
    app.use(express.static(path.join(config.root, 'public')));
    app.set('appPath', config.root + '/public');
    app.use(morgan(':method :url :status :response-time ms - :res[content-length]'));
  }

  if ('test-delta' === env) {
    app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
    app.use(express.static(path.join(config.root, 'public')));
    app.set('appPath', config.root + '/public');
    app.use(morgan(':method :url :status :response-time ms - :res[content-length]'));
  }
  if ('test-omega' === env) {
    app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
    app.use(express.static(path.join(config.root, 'public')));
    app.set('appPath', config.root + '/public');
    app.use(morgan(':method :url :status :response-time ms - :res[content-length]'));
  }
*/

  //if (config.bCompile || 'PROD' === env || 'prod-backup' === env || 'test-alpha' === env || 'test-beta' === env || 'test-delta' === env || 'test-omega' === env) {
  if (config.bCompile) {
    app.use(favicon(path.join(config.root, 'public', 'favicon.ico')));
    app.use(express.static(path.join(config.root, 'public')));
    app.set('appPath', config.root + '/public');
    app.use(morgan(':method :url :status :response-time ms - :res[content-length]'));
  }

  //if (!config.bCompile || 'local' === env || 'test' === env) {
  //if ('development' === env || 'test' === env) {
  //if (!config.bCompile || 'development' === env || 'local' === env || 'test' === env) {
  if (!config.bCompile || 'development' === env || 'local' === env) {
    app.use(require('connect-livereload')({port: 1337}));
    app.use(express.static(path.join(config.root, '.tmp')));
    app.use(express.static(path.join(config.root, 'client')));
    app.use('/public-js', express.static(path.resolve(config.root + '/../public-js')));
    app.set('appPath', 'client');
    app.use(morgan('dev'));
    app.use(errorHandler()); // Error handler - has to be last
  }
};
