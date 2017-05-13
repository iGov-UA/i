'use strict';

angular.module('dashboardJsApp').service('signService', function ($q, $base64, cryptoPluginFactory) {
  var tspURL = "http://acsk.privatbank.ua/services/tsp/";//presume we have online mode and pass url to plugin
  var sessionTimeout = 36000;
  var pluginVersion = '1.0.3';
  var plugin;

  var errorCodes = {
    noPluginFound: 'noPluginFound',
    incorrectVersionCode: 'incorrectVersionCode',
    cantCreateHashFromData: 'cantCreateHashFromData',
    noFileSelected: 'noFileSelected',
    noInstalledPlugin: 'noInstalledPlugin',
    noExtensionInstalled: 'noExtensionInstalled',
    noSessionCreated: 'noSessionCreated',
    errorOnKeysLoading: 'errorOnKeysLoading',
    isNotAnEDSStorage: 'isNotAnEDSStorage',
    wrongPassword: 'wrongPassword',
    wrongPasswordForKey: 'wrongPasswordForKey',
    undefinedError: 'undefinedError',
    noCertificateFromKey: 'noCertificateFromKey'
  };

  function isActualVersion(plugin, currentVersion) {
    if (plugin.version !== currentVersion) {
      var verExpected = parseInt(currentVersion.replace(/\./g, ""));
      var verReal = parseInt(plugin.version.replace(/\./g, ""));
      return verExpected <= verReal;
    } else {
      return true;
    }
  }

  function executeIfPluginCreated(async) {
    return $q.when(plugin ? async() : noPluginFoundError());
  }

  function noPluginFoundError() {
    return $q.reject({code: errorCodes.noPluginFound, msg: 'Спочатку треба проініціалізувати плагін'});
  }

  function pluginError(defer) {
    return function (error) {
      if (error.code == 0) {
        defer.reject({
          code: errorCodes.noFileSelected,
          msg: 'Для використання ЕЦП, Вам необхідно встановити плагін для браузеру'
        });
      } else if (error.code == -1) {
        defer.reject({
          code: errorCodes.noExtensionInstalled,
          msg: 'Для роботи з крипто плагіном необхідно встановити розширення для браузера'
        });
      } else {
        defer.reject({
          code: errorCodes.noSessionCreated,
          msg: 'Не вдалося вiдкрити сесію (Помилка ' + error.code + ')'
        });
      }
    }
  }

  this.init = function () {
    return cryptoPluginFactory.initialize();
  };

  this.activate = function () {
    var d = $q.defer();

    function activate(plug) {
      plugin = plug;
      plugin.getCrashLog(true, function (crashLog) {
        if (typeof crashLog !== "undefined") {
          // Catch crash log.
          console.error("crashLog", "crashLog");
        }
        if (isActualVersion(plugin, pluginVersion)) {
          plugin.openSession(sessionTimeout, function () {
            d.resolve();
          }, pluginError(d));
        } else {
          d.reject({code: errorCodes.incorrectVersionCode});
        }
      });
    }

    CryptoPlugin.connect(activate, pluginError(d));

    return d.promise;
  };

  this.selectFile = function () {
    return executeIfPluginCreated(function () {
      var d = $q.defer();
      plugin.selectFile("", "*", "Выберите файл с ключами ЭЦП", function (file) {
        if (file) {
          d.resolve(file);
        } else {
          d.reject({code: errorCodes.noFileSelected, msg: 'Треба обрати файл'});
        }
      }, function () {
        d.reject({code: errorCodes.noFileSelected, msg: 'Треба обрати файл'});
      });
      return d.promise;
    });
  };

  this.openStore = function (filePath, password) {
    return executeIfPluginCreated(function () {
      var d = $q.defer();
      plugin.selectFileStorage(filePath, password, function () {
        plugin.getKeysList(function (list) {
          d.resolve(list);
        }, function () {
          d.reject({code: errorCodes.errorOnKeysLoading, msg: "Помилка при отриманні ключів"});
        });
      }, function (result) {
        if (result.code == 106 && result.source == "selectFileStorage") {
          d.reject({code: errorCodes.wrongPassword, msg: "Неправильний пароль до сховища"});
        } else if (result.code == 109 && result.source == "selectFileStorage") {
          d.reject({code: errorCodes.isNotAnEDSStorage, msg: "Обраний файл не є файлом з ключами ЕЦП"});
        } else {
          d.reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
        }
      });
      return d.promise;
    });
  };

  this.selectKey = function (selectedKey, passwordForKey) {
    return executeIfPluginCreated(function () {
      var d = $q.defer();
      plugin.selectKey(selectedKey.alias, passwordForKey, function () {
        d.resolve();
      }, function (result) {
        if (result.code == 105 && result.source == "selectKey") {
          d.reject({code: errorCodes.wrongPasswordForKey, msg: "Неправильний пароль до ключа"});
        } else {
          d.reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
        }
      });
      return d.promise;
    })
  };

  this.signCMS = function (data, isForcedBase64Encoding) {
    var isDataSavedInEDS = true;
    var isCertificateSavedInEDS = true;

    return executeIfPluginCreated(function () {
      var d = $q.defer();
      var dataBase64 = isForcedBase64Encoding ? $base64.encode(data) : data;
      plugin.getCertificate(function (data) {
        var certBase64 = data.certificate;
        plugin.CMSSign(dataBase64,
          "",
          certBase64,
          tspURL,
          isDataSavedInEDS,
          isCertificateSavedInEDS,
          function (data) {
            d.resolve({sign: data.sign, certificate: certBase64});
          }, function () {
            d.reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
          });
      }, function (result) {
        if (result.code == 107 && result.source == "getCertificate") {
          d.reject({code: errorCodes.noCertificateFromKey, msg: "Ключ немає сертифікату"});
          //TODO implement here manual certificate search in 2 iteration
        } else {
          d.reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
        }
      });

      return d.promise;
    });
  };

  this.errorCodes = errorCodes;
});
