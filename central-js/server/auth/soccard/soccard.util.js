var crypto = require('crypto');
var uuid = require('node-uuid');
var fs = require('fs');
var _ = require('lodash');
var url = require('url');

var getURL = function (config, pathname) {
  return url.format({
    protocol: config.soccard.socCardAPIProtocol,
    hostname: config.soccard.socCardAPIHostname,
    pathname: pathname
  });
};

module.exports.getInfoURL = function (config) {
  return getURL(config, '/api/info');
};

module.exports.getTokenURL = function (config) {
  return getURL(config, '/api/oauth/token');
};

module.exports.getAuthorizationURL = function (config) {
  return getURL(config, '/api/oauth');
};

module.exports.signData = function (config, socCardAPITransactionID, method, requestUrl, postBody, contentType) {
  var parsedURL = url.parse(requestUrl);

  var httpMethod = method; //HTTP Method У верхньому регістрі. Наприклад: POST, GET
  var httpRequestURI = parsedURL.path; //HTTP Request URI Шлях до ресурсу. Наприклад, /oauth/token
  var host = parsedURL.host; //Host Із заголовку запиту.Наприклад, test.kyivcard.com.ua
  var port = parsedURL.port ? parsedURL.port : (parsedURL.protocol.indexOf('https') === 0 ? 443 : 80); //Port Порт з’єднання. Якщо порт відсутній в Host, то встановлюється порт по замовчуванню («80» для http та «443» для https)
  var socCardAPIVersionID = config.soccard.socCardAPIVersion; //SocCard-API-Version Із заголовку запиту
  var socCardAPITransactionID = socCardAPITransactionID; //SocCard-API-Transaction-ID Із заголовку запиту
  var contentType = contentType; //Content-Type Із заголовку запиту
  var content = postBody; //Content Тіло запиту. Наприклад, grant_type=refresh_token &refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
  var separator = ';';

  var data = [httpMethod, httpRequestURI, host,
    port, socCardAPIVersionID, socCardAPITransactionID, contentType,
    content].reduce(function (buffer, current, index, arr) {
      buffer = index === 1 ? buffer + separator : buffer;
      current = index < arr.length ? current += separator : current;
      return buffer + current;
    });
  var prk = fs.readFileSync(config.soccard.socCardAPIPrivateKey);
  var key = prk.toString('ascii');

  var sign = crypto.createSign('RSA-SHA256');
  sign.update(data);
  var sig = sign.sign({key: key, passphrase: '' + config.soccard.socCardAPIPrivateKeyPassphrase}, 'base64');

  return sig;
};

module.exports.addAPIVersionTokenHeader = function (headers, config) {
  return _.merge(headers, {'SocCard-API-Version': config.soccard.socCardAPIVersion});
};

module.exports.addAccessTokenHeader = function (headers, accessToken) {
  return _.merge(headers, {'SocCard-API-Access-Token': accessToken});
};

module.exports.addTransactionHeader = function (headers) {
  return _.merge(headers, {'SocCard-API-Transaction-ID': uuid.v1()});
};

module.exports.addSignHeader = function (headers, config, socCardAPITransactionID, method,
                                         requestUrl, postBody, contentType) {
  return _.merge(headers, {
    'SocCard-API-Signature': module.exports.signData(config, socCardAPITransactionID, method,
      requestUrl, postBody, contentType)
  });
};
