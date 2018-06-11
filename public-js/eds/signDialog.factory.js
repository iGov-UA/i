angular.module('signModule', [])
    .factory('signDialog', function ($rootScope, $modal, $q, $base64) {
        function openModal(modalScope, modalClass) {
            modalScope = modalScope ? modalScope : $rootScope.$new();
            modalClass = modalClass || 'modal-info';

            return $modal.open({
                template: template,
                windowClass: modalClass,
                scope: modalScope,
                controller: 'SignDialogInstanceCtrl'
            });
        }

        function signContent(contentDataOrLoader, resultCallback, dismissCallback, errorCallback, modalClass) {
            $q.when(contentDataOrLoader).then(function (contentData) {
                var modalScope = $rootScope.$new();
                modalScope.contentData = contentData;
                var signModal = openModal(modalScope, modalClass);
                signModal.result.then(function (signedContent) {
                    if(signedContent.auth_token) {
                        resultCallback({
                            passport: signedContent
                        });
                    }
                    else if(contentData === null){
                        resultCallback({
                            certificateInfo: signedContent
                        });
                    } else if(contentData.content) {
                        resultCallback({
                            id: contentData.id,
                            content: contentData.content,
                            certificate: signedContent.certBase64,
                            sign: signedContent.sign,
                            subject: signedContent.subject,
                            issuer: signedContent.issuer
                        });
                    } else if(contentData.sHash) {
                        resultCallback({
                            id: contentData.nID,
                            content: contentData.sHash,
                            certificate: signedContent.certificate,
                            sign: signedContent.sign,
                            subject: signedContent.subject,
                            issuer: signedContent.issuer
                        });
                    }
                }, function () {
                    dismissCallback();
                });
            }).catch(errorCallback);
        }

        function signContentsArray(contentDataOrLoader, resultCallback, dismissCallback, errorCallback, modalClass) {
            $q.when(contentDataOrLoader).then(function (contentData) {
                var modalScope = $rootScope.$new();
                modalScope.contentData = contentData;
                var signModal = openModal(modalScope, modalClass);
                signModal.result.then(function (signedContent) {
                    angular.forEach(signedContent, function (el, key) {
                        el.id = contentData[key].id;
                        el.content = contentData[key].content;
                        if(contentData[key].hasOwnProperty('oParam')){
                            el.oParam = contentData[key].oParam;
                            if(contentData[key].hasOwnProperty('oAttachment')){
                                el.sType = 'TasksAttachment'
                            } else if (contentData[key].hasOwnProperty('oDocument')){
                                el.sType = 'DocumentImage'
                            }
                        }
                    });

                    resultCallback(signedContent);

                }, function () {
                    dismissCallback();
                });
            }).catch(errorCallback);
        }

        function signManuallySelectedFile(resultCallback, dismissCallback, errorCallback) {
            var modalScope = $rootScope.$new();
            modalScope._isManuallySelectedFile = true;
            var signModal = openModal(modalScope);

            signModal.result.then(function (signedContent) {
                var byteCharacters = $base64.decode(signedContent.sign);
                var byteNumbers = new Array(byteCharacters.length);
                for (var i = 0; i < byteCharacters.length; i++) {
                    byteNumbers[i] = byteCharacters.charCodeAt(i);
                }
                var byteArray = new Uint8Array(byteNumbers);
                var blob = new Blob([byteArray], {type: 'application/pdf'});
                var url = (window.URL || window.webkitURL).createObjectURL(blob);
                var link = document.createElement("a");
                link.download = "document.pdf";
                link.href = url;
                link.click();
                //window.open(url, '_blank');
                resultCallback(signedContent);
            }, function () {
                dismissCallback();
            }).catch(errorCallback);
        }

        var template = "<div class=\"modal-header eds-header-text\" style=\"background: #3694BA;\">\n" +
            "  <button type=\"button\" ng-click=\"$dismiss()\" class=\"close\">&times;</button>\n" +
            "  <h4 class=\"modal-title\">Підписати за допомогою електронного ключа</h4>\n" +
            "</div>\n" +
            "<div class=\"modal-body\" ng-if=\"!isInitialized\">\n" +
            "  <p>Треба встановити розширення до браузера</p>\n" +
            "</div>\n" +
            "<div class=\"modal-body\" ng-if=\"isInitialized\">\n" +
            "  <form name=\"form\" novalidate style=\"margin-top: 15px\">\n" +
            "    <div ng-if=\"isManuallySelectedFile()\" class=\"col-sm-12 form-field\">\n" +
            "      <div class=\"col-sm-4 task-form-title\">\n" +
            "        <label>Файл на підпис<span style=\"color: red\">*</span></label>\n" +
            "      </div>\n" +
            "      <div class=\"col-sm-4\" style=\"padding-right: 0\">\n" +
            "        <input ng-model=\"edsContext.edsStorage.file\"\n" +
            "               onchange=\"angular.element(this).scope().fileChanged(this)\"\n" +
            "               type=\"file\" accept=\"application/pdf\" required readonly/>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    <div class=\"col-sm-12 form-field\" ng-disabled=\"isPluginActivated\">\n" +
            "      <div class=\"col-sm-4 task-form-title\">\n" +
            "        <label>Файл сховища ЕЦП <span style=\"color: red\">*</span></label>\n" +
            "      </div>\n" +
            "      <div class=\"col-sm-4\" style=\"padding-right: 0\">\n" +
            "        <input class=\"form-control\" name=\"eds\" ng-model=\"edsContext.edsStorage.name\" required readonly>\n" +
            "      </div>\n" +
            "      <div class=\"col-sm-3 task-form-title\">\n" +
            "        <button type=\"button\"\n" +
            "                class=\"btn btn-link\"\n" +
            "                style=\"border: 1px solid #3694BA; outline: 0; border-radius: 3px; text-decoration: none;\"\n" +
            "                ng-click=\"chooseEDSFile()\">Обрати файл\n" +
            "        </button>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    <div class=\"col-sm-12 form-field sign-last-element\" ng-disabled=\"isPluginActivated\">\n" +
            "      <div class=\"col-sm-4 task-form-title\">\n" +
            "        <label>Пароль до сховища <span style=\"color: red\">*</span></label>\n" +
            "      </div>\n" +
            "      <div class=\"col-sm-4\" style=\"padding-right: 0\">\n" +
            "        <input class=\"form-control\" name=\"eds-password\" type=\"password\" ng-model=\"edsContext.edsStorage.password\" required>\n" +
            "      </div>\n" +
            "      <div class=\"col-sm-3 task-form-title\">\n" +
            "        <button type=\"button\"\n" +
            "                class=\"btn btn-link\"\n" +
            "                style=\"border: 1px solid #3694BA; outline: 0; border-radius: 3px; text-decoration: none;\"\n" +
            "                ng-click=\"findKeys()\">Отримати ключі\n" +
            "        </button>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    <div class=\"col-sm-12 form-field\" ng-disabled=\"isPluginActivated\" ng-hide=\"isNoChoice()\">\n" +
            "      <div class=\"col-sm-4 task-form-title\">\n" +
            "        <label>Ключ для підпису <span style=\"color: red\">*</span></label>\n" +
            "      </div>\n" +
            "      <div class=\"col-sm-4\" style=\"padding-right: 0\">\n" +
            "        <select name=\"eds-key\"\n" +
            "                style=\"width:100%;\"\n" +
            "                class=\"form-control\"\n" +
            "                ng-options=\"key.alias for key in edsContext.keyList\"\n" +
            "                ng-model=\"edsContext.selectedKey.key\" required></select>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    <div ng-if=\"edsContext.selectedKey.key && edsContext.selectedKey.key.needPassword\" ng-disabled=\"isPluginActivated\" class=\"col-sm-12 form-field\">\n" +
            "      <div class=\"col-sm-4 task-form-title\">\n" +
            "        <label>Пароль до ключа <span style=\"color: red\">*</span></label>\n" +
            "      </div>\n" +
            "      <div class=\"col-sm-4\" style=\"padding-right: 0\">\n" +
            "        <input class=\"form-control\" name=\"eds-key-password\" type=\"password\" ng-model=\"edsContext.selectedKey.password\" required>\n" +
            "      </div>\n" +
            "    </div>\n" +
            // "    <div class=\"col-sm-12 form-field\">\n" +
            // "      <div class=\"col-sm-4 task-form-title\">\n" +
            // "        <label>Пароль до пристрою <span style=\"color: red\"></span></label>\n" +
            // "      </div>\n" +
            // "      <div class=\"col-sm-4\" style=\"padding-right: 0\">\n" +
            // "        <input class=\"form-control\" name=\"token-password\" type=\"password\" ng-model=\"deviceInfo.password\">\n" +
            // "      </div>\n" +
            // "      <div class=\"col-sm-3 task-form-title\">\n" +
            // "        <button type=\"button\"\n" +
            // "                class=\"btn btn-link\"\n" +
            // "                style=\"border: 1px solid #3694BA; outline: 0; border-radius: 3px; text-decoration: none;\"\n" +
            // "                ng-click=\"signInByToken()\">Токен\n" +
            // "        </button>\n" +
            // "      </div>\n" +
            // "    </div>\n" +
            // "    <div class=\"col-sm-12 form-field\" style=\"margin-top: 6px; margin-bottom: 15px; margin-left: 8px;\">\n" +
            // "      <div class=\"col-sm-4\" style=\"padding-right: 0\">\n" +
            // "        <input class=\"form-control\" name=\"token-password\" type=\"password\" ng-model=\"deviceInfo.password\">\n" +
            // "      </div>\n" +
            // "       <button type=\"button\" class=\"col-md-4 col-md-offset-4 btn btn-link\" style=\"border: 1px solid #3694BA; outline: 0; border-radius: 3px; text-decoration: none;\" ng-click=\"signInByToken()\">Токен</button>\n" +
            // "    </div>\n" +
            "    <div class=\"col-sm-12 form-field\" style=\"margin-top: 6px; margin-bottom: 15px;\">\n" +
            "      <hr class=\"hr-divider\" style=\"margin: 0\">\n" +
            "    </div>\n" +
            "  </form>\n" +
            "  <span style=\"color: red\">{{lastError.msg}}</span>\n" +
            "  <div style='color: grey' ng-if=\"['noExtensionInstalled', 'noFileSelected'].indexOf(lastError.code) > -1\">" +
            "   (<a style=\"font-size: 14px;\"\n" +
            "       ng-href=\"{{pluginsLink.extension}}\"\n" +
            "       target=\"_blank\"\n" +
            "       >Встановити розширення для браузера</a>\n" +
            "   <span style=\"font-size: 14px\" ng-if=\"pluginsLink.type !== 'safari'\"'> та </span>\n" +
            "   <a style=\"font-size: 14px;\" ng-if=\"pluginsLink.type !== 'safari'\" ng-href=\"{{pluginsLink.exe.link}}\" target=\"_blank\">Встановити додаток</a>\n" +
            "   <a style=\"font-size: 14px;\" ng-if=\"pluginsLink.type === 'safari'\" ng-href=\"{{pluginsLink.safari.link}}\" target=\"_blank\">Встановити додаток</a>)\n" +
            "  </div>" +
            "</div>\n" +
            "<div class=\"modal-footer\" style=\"border: none\">\n" +
            "  <button type=\"button\"\n" +
            "          ng-if=\"edsContext.selectedKey.key\"\n" +
            "          class=\"btn btn-info\"\n" +
            "          style=\"background-color: #3694BA; outline: none; border: 1px solid #3694BA;\"\n" +
            "          ng-click=\"sign()\">Підписати\n" +
            "  </button>\n" +
            "  <button type=\"button\"\n" +
            "          class=\"btn btn-link\"\n" +
            "          style=\"border: 1px solid #3694BA; outline: 0; border-radius: 3px; text-decoration: none;\"\n" +
            "          ng-click=\"cancel()\">Відмінити\n" +
            "  </button>\n" +
            "</div>\n";

        return {
            /**
             * pass contentData = { id : "id of data", content : "real data content", base64encoded: "true/false"}
             * or pass promise that will return contentData object
             *
             * resultCallback will return :
             * {
       *    id : contentData.id,
       *    content: contentData.content,
       *    certificate: certificate in base64
       *    sign : sign in base64 (CMS sign result that can be saved as pdf)
       *  }
             */
            signContent: signContent,
            signContentsArray: signContentsArray,
            signManuallySelectedFile: signManuallySelectedFile
        }
    });