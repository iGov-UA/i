var SignDialogInstanceCtrl = function ($scope, $modalInstance, signService, md5, $q, $base64) {
    function removeLastError() {
        $scope.lastError = undefined;
    }

    function catchLastError(error) {
        $scope.lastError = error;
    }

    var isInitialized = signService.init();

    $scope.isInitialized = isInitialized;
    $scope.isPluginActivated = false;

    $scope.signedContent = {};

    $scope.edsContext = {
        edsStorage: {
            name: "",
            file: "",
            password: ""
        },
        keyList: [],
        selectedKey: {
            key: undefined,
            password: undefined,
            needPassword: true
        },
        lastError: undefined
    };

    var pingCount = 0;
    var maxPings = 120;

    var ping = setInterval(avtivatePlugin, 1000);

    function avtivatePlugin() {
        if(pingCount > maxPings) {
            clearInterval(ping);
        } else {
            pingCount++;
            signService.activate().then(function () {
                clearInterval(ping);
                $scope.lastError = undefined;
                $scope.isPluginActivated = true;
                console.log('The sign plug-in was activated for ' + pingCount + ' request' + (pingCount < 2 ? '.' : 's.'));
            }).catch(catchLastError);
        }
    }

    $scope.isManuallySelectedFile = function () {
        return $scope._isManuallySelectedFile;
    };

    $scope.fileChanged = function (element) {
        $scope.$apply(function (scope) {
            var selectedFile = element.files[0];
            var reader = new FileReader();
            reader.onload = function (e) {
                var loadedContent = e.target.result.split("base64,")[1];
                var id = selectedFile.name;
                scope.contentData = {id: id, content: loadedContent, base64encoded: true};
            };
            reader.readAsDataURL(selectedFile);
        });
    };

    $scope.chooseEDSFile = function () {
        removeLastError();
        var edsContext = $scope.edsContext;
        return signService.selectFile().then(function (edsStorage) {
            edsContext.edsStorage.file = edsStorage;
            var filePath = edsStorage.filePath;
            edsContext.edsStorage.name = filePath.substr(filePath.lastIndexOf("/") + 1)
        }).catch(catchLastError);
    };

    var linksToCryptoplugin = {
        opera: {
            link: 'https://addons.opera.com/ru/extensions/details/bankid-cryptoplugin/',
            type: 'opera'
        },
        chrome: {
            link: 'https://chrome.google.com/webstore/detail/bankid-cryptoplugin/pgfbdgicjmhenccemcijooffohcdanic?utm_source=chrome-app-launcher-info-dialog',
            type: 'chrome'
        },
        mozilla: {
            link: '/IdentDigitalSignature/resources/plugin/cryptoplugin_ext_id@privatbank.ua.xpi',
            type: 'mozilla'
        },
        safari: {
            link : '',
            type: 'safari'
        },
        exe: {
            link: 'https://biprocessing.org.ua/IdentDigitalSignature/resources/plugin/cryptoplugin-1.2.2.exe'
        }
    };

    $scope.pluginsLink = { extension: '', exe: '', type: '' };

    (function () {
        if ((!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0) {
            $scope.pluginsLink.extension = linksToCryptoplugin.opera.link;
            $scope.pluginsLink.type = 'opera';
        } else if (typeof InstallTrigger !== 'undefined') {
            $scope.pluginsLink.extension = linksToCryptoplugin.mozilla.link;
            $scope.pluginsLink.type = 'mozilla';
        } else if (/constructor/i.test(window.HTMLElement) || (function (p) { return p.toString() === "[object SafariRemoteNotification]"; })(!window['safari'] || (typeof safari !== 'undefined' && safari.pushNotification))) {
            $scope.pluginsLink.extension = linksToCryptoplugin.safari.link;
            $scope.pluginsLink.type = 'safari';
        } else if (!!window.chrome && !!window.chrome.webstore) {
            $scope.pluginsLink.extension = linksToCryptoplugin.chrome.link;
            $scope.pluginsLink.type = 'chrome';
        } else {
            $scope.pluginsLink.extension = '';
        }

        $scope.pluginsLink.exe = linksToCryptoplugin.exe;
    })();

    $scope.getExtensionMozilla = function (link) {
        window.open(link,"_self");
    };

    $scope.findKeys = function () {
        removeLastError();
        var edsContext = $scope.edsContext;
        signService.openStore(edsContext.edsStorage.file.filePath, edsContext.edsStorage.password).then(function (keyList) {
            edsContext.keyList = keyList;
            if (keyList.length === 1) {
                edsContext.selectedKey.key = keyList[0];
                edsContext.selectedKey.needPassword = keyList[0].needPassword;
                if (!edsContext.selectedKey.needPassword) {
                    return signService.selectKey(edsContext.selectedKey.key, "");
                }
            }
        }).catch(catchLastError);
    };

    $scope.isNoChoice = function () {
        return $scope.edsContext.keyList.length < 2;
    };

    $scope.sign = function () {
        removeLastError();
        var edsContext = $scope.edsContext;
        if (edsContext.selectedKey.password || !edsContext.selectedKey.key.needPassword) {
            $q.when(edsContext.selectedKey.key.needPassword && edsContext.selectedKey.password ?
                signService.selectKey(edsContext.selectedKey.key, edsContext.selectedKey.password)
                : edsContext.keyList.length > 1 && !edsContext.selectedKey.key.needPassword ?
                    signService.selectKey(edsContext.selectedKey.key, "") :true).then(function () {

                return signService.signCMS($scope.contentData, !$scope.contentData.base64encoded)
                    .then(function (signResult) {
                        $modalInstance.close(signResult);
                    });
            }).catch(catchLastError);
        } else {
            catchLastError({msg: 'Потрібно ввести пароль до ключа'});
        }
    };

    $scope.cancel = function () {
        $modalInstance.dismiss();
    };
};

angular.module('signModule').controller('SignDialogInstanceCtrl', SignDialogInstanceCtrl);