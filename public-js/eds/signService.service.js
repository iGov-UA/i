'use strict';

angular.module('signModule').service('signService', function ($q, $base64, cryptoPluginFactory, $timeout, $rootScope) {
    var tspURL = $rootScope.config && !$rootScope.config.bIncludeTimeStampInEDS ? null : "http://acsk.privatbank.ua/services/tsp/";//presume we have online mode and pass url to plugin
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

    function doSingleSign(data, isForcedBase64Encoding, isCertificateSavedInEDS) {
        return executeIfPluginCreated(function () {
            var d = $q.defer();
            if(data.content) {
                var dataBase64 = isForcedBase64Encoding ? $base64.encode(data.content) : data.content;
            } else if(data.sHash) {
                var dataBase64 = data.sHash;
            }
            plugin.getCertificate(function (data) {
                if(data) {
                    var certBase64 = data.certificate;
                }
                plugin.getDataFromCMS(dataBase64, function (success) {

                    plugin.CMSSign(success.data,
                        "",
                        certBase64,
                        tspURL,
                        false,
                        isCertificateSavedInEDS,
                        function (data) {

                            plugin.
                            CMSJoin([dataBase64, data.sign], function (signed) {
                                var certInfo;
                                var issuer;
                                plugin.getCertificateInfo(certBase64, function (certData) {
                                    var currentDate = new Date().getTime() / 1000;
                                    if(currentDate <= certData.notValidAfter && currentDate >= certData.notValidBefore) {
                                        certInfo = certData.subject;
                                        issuer = certData.issuer;
                                        d.resolve({sign: signed.CMS, certificate: certBase64, subject: certInfo, issuer: issuer});
                                    } else {
                                        var notValidAfter = new Date(certData.notValidAfter * 1000).toLocaleString();
                                        d.reject({msg: 'Сертифікат дійсний до'.concat(notValidAfter)
                                            .concat('. Тому він не може бути використаний для накладання підпису')});
                                    }
                                }, function (error) {
                                    console.log(error);
                                });
                            }, function (error) {
                                d.reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
                            })

                        }, function () {
                            d.reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
                        });
                }, function (error) {
                    plugin.CMSSign(dataBase64,
                        "",
                        certBase64,
                        tspURL,
                        true,
                        isCertificateSavedInEDS,
                        function (data) {
                            var certInfo;
                            var issuer;
                            plugin.getCertificateInfo(certBase64, function (certData) {
                                var currentDate = new Date().getTime() / 1000;
                                if(currentDate <= certData.notValidAfter && currentDate >= certData.notValidBefore) {
                                    issuer = certData.issuer;
                                    certInfo = certData.subject;
                                    d.resolve({sign: data.sign, certificate: certBase64, subject: certInfo, issuer: issuer});
                                } else {
                                    var notValidAfter = new Date(certData.notValidAfter * 1000).toLocaleString();
                                    d.reject({msg: 'Сертифікат дійсний до '.concat(notValidAfter)
                                        .concat('. Тому він не може бути використаний для накладання підпису')});
                                }
                            }, function (error) {
                                d.reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
                                console.log(error);
                            });
                        }, function () {
                            d.reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
                        });
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
    }

    function doMultiSign(data, isForcedBase64Encoding, isCertificateSavedInEDS) {
        return executeIfPluginCreated(function () {
            var d = $q.defer();
            var dataBase64s = [];// isForcedBase64Encoding ? $base64.encode(data.content) : data.content;
            angular.forEach(data, function (fileContent) {
                if(isForcedBase64Encoding){
                    dataBase64s.push($base64.encode(fileContent.content ? fileContent.content : fileContent))
                } else {
                    dataBase64s.push(fileContent.content ? fileContent.content : fileContent);
                }
            });
            plugin.getCertificate(function (data) {
                var certBase64 = data.certificate;
                var signedContents = [];

                angular.forEach(dataBase64s, function (file) {
                    signedContents.push($q.resolve(file));
                });

                $q.all(signedContents).then(function (contents) {

                    var readyPromises = [],
                        content = [],
                        signPromises = [],
                        signDefer = [],
                        counter = 0;

                    angular.forEach(contents, function (dataBase64, key) {
                        readyPromises.push($timeout(function(){
                            signDefer[key] = $q.defer();
                            content[key] = dataBase64;
                            signPromises[key] = signDefer[key].promise;
                        }))
                    });


                    var asyncSign = function(i, contentForSign, defs) {
                        if (i < contentForSign.length) {
                            var dataBase64 = contentForSign[i];

                            return plugin.getDataFromCMS(dataBase64, function (success) {
                                plugin.CMSSign(success.data,
                                    "",
                                    certBase64,
                                    tspURL,
                                    false,
                                    isCertificateSavedInEDS,
                                    function (data) {

                                        plugin.CMSJoin([dataBase64, data.sign], function (signed) {
                                            defs[i].resolve({sign: signed.CMS, certificate: certBase64});
                                            return asyncSign(i + 1, contentForSign, defs);
                                        }, function (error) {
                                            defs[i].reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
                                        })

                                    }, function () {
                                        defs[i].reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
                                    });
                            }, function (error) {
                                plugin.CMSSign(dataBase64,
                                    "",
                                    certBase64,
                                    tspURL,
                                    true,
                                    isCertificateSavedInEDS,
                                    function (data) {
                                        defs[i].resolve({sign: data.sign, certificate: certBase64});
                                        return asyncSign(i + 1, contentForSign, defs);
                                    }, function () {
                                        defs[i].reject({code: errorCodes.undefinedError, msg: "Неочікувана помилка"});
                                    });
                            });

                        }
                    };

                    var first = $q.all(readyPromises).then(function () {
                        return asyncSign(counter, content, signDefer);
                    });

                    $q.all([first, signPromises]).then(function (signedDefers) {
                        $q.all(signedDefers[1]).then(function (resp) {
                            d.resolve(resp);
                        })
                    });
                })

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
    }

    this.signCMS = function (data, isForcedBase64Encoding) {
        var isCertificateSavedInEDS = true;
        if(angular.isArray(data)){
            return doMultiSign(data, isForcedBase64Encoding, isCertificateSavedInEDS);
        } else if (data.content || data.sHash){
            return doSingleSign(data, isForcedBase64Encoding, isCertificateSavedInEDS);
        }
    };

    this.getCertificateInfo = function() {
        var deferred = $q.defer();
        plugin.getCertificate(function (data) {
            var certBase64 = data.certificate;
            plugin.getCertificateInfo(certBase64, function (certData) {
                certData.certificate = certBase64;
                deferred.resolve(certData);
            });
        }, function(error) {
            console.log(error);
            deferred.reject({code: error, msg: "Неочікувана помилка"});
        });
        return deferred.promise;
    };

    this.signInByToken = function(password) {
        var deferred = $q.defer();
        plugin.getDeviceList(function (data) {
            console.log(data);
            if(data.length > 0) {
                plugin.selectDevice(data[0].deviceId, password, function (success) {
                    plugin.readIdPassportInfo(function (passportInfo) {
                        console.log(passportInfo);
                        deferred.resolve({passport: passportInfo, auth_token: true});
                    }, function (err) {
                        console.log(err);
                        deferred.reject({code: err, msg:'Ошибка чтения данных ID Passport (DG1)', auth_token: true});
                    })
                }, function (err) {
                    console.log(err);
                    deferred.reject({code: err, msg: "Невірний пароль", auth_token: true});
                });
            } else {
                deferred.resolve({msg: 'Підтримувані токени в системі не виявлено\n' +
                'Підключить токен \n' +
                '\n' +
                'Ми підтримуємо токени Autor SecureToken 337, BIFIT iToken, IIT Кристал-1\n' +
                'Якщо система не знаходить ваш токен, можливо ви не встановили драйвер', auth_token: true})
            }
        }, function (err) {
            console.log(err);
            deferred.reject({code: err, msg: "Неочікувана помилка", auth_token: true});
        });
        return deferred.promise;
    };

    this.errorCodes = errorCodes;
});