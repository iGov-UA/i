(function () {
  'use strict';

  angular.module('dashboardJsApp')
    .controller('ScannerModalCtrl', ['$scope', '$modalInstance', 'Modal', 'ScannerService', function ($scope, $modalInstance, Modal, ScannerService) {

      var GlobalScanSettingsName = "GlobalScanSettings";
      var sURL = ScannerService.getTwainServerUrl();
      var downloadToLocalMachine = false;


      var changeMode = function (enableID) {
        var switches = $('#scan-form .mode-switch-container .mode-switch');
        for (var i = 0; i < switches.length; i++) {
          if ($(switches[i]).attr('id') == enableID) {
            $(switches[i]).removeClass('inactive').addClass('active');

            //если выбран ADF
            var scanFeed = $("#scan-form").find("#Form_ScanFeed");
            if (scanFeed.val() != 0 && scanFeed.is(':visible')) {
              $('#scan-form .package-setting').addClass("disp-no");
              eval("$('#scan-form .package-feeder-setting')." + $(switches[i]).attr('changeAction'));
            } else {
              eval("$('#scan-form .package-setting')." + $(switches[i]).attr('changeAction'));
            }
          } else {
            $(switches[i]).removeClass('active').addClass('inactive');
          }
        }
      };

      $scope.switchMode = function (sMode) {
        if ($scope.sScanMode !== sMode) {
          changeMode(sMode);
          $scope.sScanMode = sMode;
        }
      };

      function pad(num, size) {
        var s = num + "";
        while (s.length < size) s = "0" + s;
        return s;
      }

      var counterModify = function () {
        var fileCounter = $("#Form_FileCounter").val();
        fileCounter = pad(fileCounter * 1 + 1, fileCounter.length);
        UpdateGlobalSetting(GetGlobalSettings(), {"FileName": $('#Form_FileName').val(), "CountScans": fileCounter});
        $("#Form_FileCounter").val(fileCounter);
      };

      var downloadFile = function (downloadFiles, save_as) {
        //если несколько файлов необходимо сохранить как картинки - загружаем их последовательно
        if ((save_as == undefined || save_as == 0) && downloadFiles.length > 1) {
          for (var i = 0; i < downloadFiles.length; i++) {
            downloadFile([downloadFiles[i]]);
          }

        } else {
          save_as = save_as == undefined ? "" : "saveAs=" + save_as;
          var query = "?";
          for (var i = 0; i < downloadFiles.length; i++) {
            query += 'fileId' + i + '=' + encodeURIComponent(downloadFiles[i].temp) + '&fileName' + i + '=' + encodeURIComponent(downloadFiles[i].file) + "&";
          }
          query += save_as;

          if (downloadToLocalMachine) {
            downloadFileFromFakeLink(encodeURI('download' + query));
          } else {
            var downloadUrl = sURL + encodeURI('download' + query);
            sendScannedFiles(downloadFiles, save_as, downloadUrl);
          }

          counterModify();
        }
      };

      function downloadFileFromFakeLink(url) {
        var link = document.createElement('a');

        link.setAttribute('download', null);
        link.style.display = 'none';

        document.body.appendChild(link);

        link.setAttribute('href', sURL + url);
        link.click();

        document.body.removeChild(link);
        $modalInstance.dismiss('cancel');
      }

      function sendScannedFiles(files, save_as, url) {
        $modalInstance.close({downloadFiles: files, saveAs: save_as, downloadUrl: url});
      }

      var setSetting = function (setting, defaultSetting) {
        return (setting == undefined) ? defaultSetting : setting;
      };
      var ModeSwitch = function (modeSwitch) {
        var self = this;
        self.id = setSetting(modeSwitch.id, 'conventional-scan');
        self.countScans = setSetting(modeSwitch.countScans, '1');
        self.timeout = setSetting(modeSwitch.timeout, '5');
        self.saveAs = setSetting(modeSwitch.saveAs, '0');
      };
      var NameSetting = function (nameSetting) {
        var self = this;
        self.FileName = nameSetting.FileName;
        self.CountScans = nameSetting.CountScans;
      };
      var GlobalScanSettings = function (settings) {
        var self = this;
        self.NamesSettings = [];
        if (settings != undefined) {
          for (var key in settings) {
            self.NamesSettings.push(new NameSetting(settings[key]));
          }
        }
      };

      var ApplyGlobalSettings = function (globalSettings, fileName) {
        var key = SearchGlobalSetting(globalSettings, fileName);
        if (key != null) {
          $('#Form_FileCounter').val(globalSettings.NamesSettings[key].CountScans);
          return true;
        }
        return false;
      };

      var SearchGlobalSetting = function (globalSettings, fileName) {
        for (var key in globalSettings.NamesSettings) {
          if (globalSettings.NamesSettings[key].FileName == fileName) {
            return key;
          }
        }
        return null;
      };

      var UpdateGlobalSetting = function (globalSettings, nameSetting) {
        var key = SearchGlobalSetting(globalSettings, nameSetting.FileName);
        if (key == null)
          globalSettings.NamesSettings.push(new NameSetting(nameSetting));
        else
          globalSettings.NamesSettings[key].CountScans = nameSetting.CountScans;
        localStorage.setItem(GlobalScanSettingsName, JSON.stringify(globalSettings.NamesSettings));
      };

      var ScanSettings = function (settings) {
        var self = this;
        self.source = setSetting(settings.source, "0");
        self.fileName = setSetting(settings.fileName, "Скан");
        self.dpi = setSetting(settings.dpi, "150");
        self.scanFeed = setSetting(settings.scanFeed, null);
        self.pixelType = setSetting(settings.pixelType, "2");
        self.compressionFormat = setSetting(settings.compressionFormat, "100*Jpeg");
        self.format = setSetting(settings.format, "8,5*11,7");
        self.Counter = setSetting(settings.Counter, '001');
        self.modeSwitch = new ModeSwitch(setSetting(settings.modeSwitch, {}));
      };

      var canScan = false;
      var localSettingsName = "ScanSettings";
      var GetGlobalSettings = function () {
        var globalSettings = localStorage.getItem(GlobalScanSettingsName);
        return globalSettings != null ? new GlobalScanSettings(JSON.parse(globalSettings)) : new GlobalScanSettings();
      };
      var GetLocalSettings = function () {
        var localSettings = localStorage.getItem(localSettingsName);
        return localSettings != null ? new ScanSettings(JSON.parse(localSettings)) : new ScanSettings({});
      };
      var SetLocalSettings = function (source, fileName, dpi, scanFeed, pixelType, compressionFormat, format, counter, modeSwitch) {
        var settings = new ScanSettings({
          "source": source,
          "fileName": fileName,
          "dpi": dpi,
          "scanFeed": scanFeed,
          "pixelType": pixelType,
          "compressionFormat": compressionFormat,
          "format": format,
          "modeSwitch": modeSwitch
        });
        localStorage.setItem(localSettingsName, JSON.stringify(settings));
        return settings;
      };
      var ApplyLocalSettings = function (settings, defaultScanSettings) {
        settings = new ScanSettings(settings);

        var dpiSelect = $('#scan-form #Form_Dpi');
        var minDpi = dpiSelect.val(dpiSelect.find('option').first().val());
        var maxDpi = dpiSelect.val(dpiSelect.find('option').last().val());

        function isEmpty(str) {
          return (!str || 0 === str.length);
        }

        var selectedDpi;
        if (minDpi != undefined && !isEmpty(minDpi) && minDpi * 1 > settings.dpi * 1)
          selectedDpi = minDpi;
        else if (maxDpi != undefined && !isEmpty(maxDpi) && maxDpi * 1 < settings.dpi * 1)
          selectedDpi = maxDpi;
        else
          selectedDpi = settings.dpi;


        $('#scan-form').find('#Form_Dpi').val(selectedDpi);
        $('#scan-form').find('#Form_CompressionFormat').val(settings.compressionFormat);
        $('#scan-form').find('#Form_FileName').val(settings.fileName);
        if (!ApplyGlobalSettings(GetGlobalSettings(), settings.fileName))
          $('#scan-form').find('#Form_FileCounter').val(settings.Counter);
        $('#scan-form').find("#Form_ColorMode").val(settings.pixelType);
        $('#scan-form').find("#Form_ScanFeed").val(settings.scanFeed);
        $('#scan-form').find("#Form_Format option:contains(" + settings.format + ")").attr('selected', 'selected');
        $('#scan-form').find('#Form_CountScans').val(settings.modeSwitch.countScans);
        $('#scan-form').find('#Form_ScanInterval').val(settings.modeSwitch.timeout);
        $('#scan-form').find('#Form_SaveAs').val(settings.modeSwitch.saveAs);
        changeMode(settings.modeSwitch.id);
      };
      var ActivateScanForm = function () {
        $('#scan-form select, #scan-form input').removeAttr("disabled");
        canScan = true;
      };
      var DisactivateScanForm = function () {
        $('#scan-form select, #scan-form input').attr("disabled", "disabled");

        canScan = false;
      };

      var DisactivateScanParameters = function () {
        $('#scan-form select:not(#Form_Source), #scan-form input').attr("disabled", "disabled");

        canScan = false;
      };

      $scope.oStatusMessage = {
        sText: '',
        bShow: false
      };

      function showOverlay(msg) {
        /*
        $(".overlay-message").text(msg);
        $(".overlay, .overlay-message").show();
        */
        $scope.oStatusMessage = {
          sText: msg,
          bShow: true
        };
      }

      function hideOverlay() {
        /*
        $(".overlay-message").text("");
        $(".overlay, .overlay-message").hide();
        */
        $scope.oStatusMessage = {
          sText: '',
          bShow: false
        };
      }

      var GetScanFormData = function () {
        var scanParameters = $('#scan-form [name]');
        var values = "";
        for (var i = 0; i < scanParameters.length; i++) {
          values += $(scanParameters[i]).attr("name") + "=" + $(scanParameters[i]).val();
          values += i < (scanParameters.length - 1) ? "&" : "";
        }
        return values;
      };
      var sendAjax = function (method, doneFunction, data, async, failFunction, completeFunction, statusMsg, useOverlay) {
        if (method != undefined) {
          doneFunction = doneFunction == undefined ? function () {
          } : doneFunction;
          failFunction = failFunction == undefined ? function () {
          } : failFunction;
          completeFunction = completeFunction == undefined ? function () {
          } : completeFunction;
          data = data == undefined ? "" : "&" + data;
          if (useOverlay) {
            showOverlay(statusMsg);
            DisactivateScanForm();
          }

          $.ajax({
            async: async,
            url: /*window.location.pathname + */sURL + "ajax",
            crossDomain: true,
            type: 'POST',
            dataType: 'json',
            data: "method=" + method + data,
            success: function (responce) {
              ActivateScanForm();
              doneFunction(responce);
            },
            error: function (xhr, msdfsg) {
              var msg = xhr.responseText;
              if (msg == undefined || msg == "") {
                msg = "Виникла невідома помилка";
              }
              failFunction(msg);
            },
            complete: function (resp) {
              completeFunction();
              if (useOverlay) {
                hideOverlay();
              }
            }
          });
        }
      };

      var GetScanParameters = function (newSourceIndex) {
        var localSettings = GetLocalSettings();
        var fillSelect = function (element, list, selectedValue) {
          element.empty();
          for (var index in list) {
            var isSelected = selectedValue == list[index].key ? "selected=selected" : "";
            element.append("<option " + isSelected + " value='" + list[index].key + "'>" + list[index].value + "</option>");
          }
        };
        var clearSelect = function (element) {
          element.empty();
        };

        var sourceIndex = "sourceIndex=";
        if (newSourceIndex != undefined) {
          sourceIndex += newSourceIndex;
        } else {
          sourceIndex += localSettings.source;
        }
        var doneFunction = function (responce) {
          if (responce.sources != undefined && responce.sources.sourcesList != undefined && responce.sources.selectedSource != undefined && responce.sources.sourcesList.length > 0) {
            var form = $('#scan-form');
            var selectSources = form.find("#Form_Source");
            var selectedSources = responce.sources.selectedSource;
            fillSelect(selectSources, responce.sources.sourcesList, selectedSources);

            var selectTypePixel = $('#scan-form').find("#Form_ColorMode");
            if (responce.pixelTypes != undefined) {
              fillSelect(selectTypePixel, responce.pixelTypes);
            } else {
              clearSelect(selectTypePixel);
            }

            var selectDpi = $('#scan-form').find("#Form_Dpi");
            if (responce.flatbedResolutions != undefined) {
              fillSelect(selectDpi, responce.flatbedResolutions);
            } else {
              clearSelect(selectDpi);
            }

            var selectDpiHidden = $('#scan-form').find("#Form_Dpi_Hidden");
            if (responce.feederResolutions != undefined) {
              fillSelect(selectDpiHidden, responce.feederResolutions);
            } else {
              clearSelect(selectDpiHidden);
            }
            selectDpiHidden.removeClass('flatbedResolutions');
            selectDpiHidden.addClass('feederResolutions');

            var selectScanFeed = $('#scan-form').find("#Form_ScanFeed");
            if (responce.scanFeeds != undefined) {
              fillSelect(selectScanFeed, responce.scanFeeds);
              selectScanFeed.parent().show();
            } else {
              clearSelect(selectScanFeed);
              selectScanFeed.parent().hide();
            }

            var selectAllowedFormats = $('#scan-form').find("#Form_Format");
            if (responce.allowedFormats != undefined) {
              fillSelect(selectAllowedFormats, responce.allowedFormats);
            } else {
              clearSelect(selectAllowedFormats);
            }
            ApplyLocalSettings(localSettings);

            scanFeedOnChange();

            if (responce.allowedFormats == undefined || responce.pixelTypes == undefined || (responce.flatbedResolutions == undefined && responce.feederResolutions == undefined)) {
              DisactivateScanParameters();
              Modal.inform.warning()("Не вдалося отримати параметри сканера.")

            }
          } else {
            Modal.inform.error()("Не знайдено жодного сумісного пристрою. Перевірте підключення чи спробуйте змінити тип драйвера в конфігурації TWAIN@WEB.");
          }
        };
        sendAjax("GetScannerParameters", doneFunction, sourceIndex, true, function (msg) {
          Modal.inform.warning()(msg)
        }, null, "Завантаження параметрів сканера...", true);
      };

      $scope.changeFileName = function () {
        if (!ApplyGlobalSettings(GetGlobalSettings(), $("#scan-form").find("#Form_FileName").val())){
          $("#scan-form").find("#Form_FileCounter").val(new ScanSettings({}).Counter);
        }
      };

      var parseScanResponse = function (resp) {

        var downloadFiles = [];
        if (resp.file != undefined) {
          downloadFiles.push(new FileForDownload(resp));
        } else if (resp.files != undefined) {

          resp.files.forEach(function (file) {
            downloadFiles.push(new FileForDownload(file));

          });
        }
        return downloadFiles;
      };

      $scope.submitScanForm = function () {
        try {
          DisactivateScanForm();
          var save_as = $('#scan-form #Form_SaveAs').val();
          var scanFeed = $('#scan-form #Form_ScanFeed').val();
          SetLocalSettings($(this).find('#Form_Source').val(), $(this).find('#Form_FileName').val(), $(this).find('#Form_DPI').val(), $(this).find('#Form_ScanFeed').val(), $(this).find('#Form_ColorMode').val(), $(this).find('#Form_CompressionFormat').val(), $.trim($(this).find('#Form_Format option:selected').text()),
            $(this).find('#Form_FileCounter').val(),
            {
              "id": $('#scan-form .mode-switch.active').attr('id'),
              "countScans": $(this).find('#Form_CountScans').val(),
              "timeout": $(this).find('#Form_ScanInterval').val(),
              "saveAs": save_as
            });

          if ($('#scan-form #package-scan').is('.active') && (scanFeed == undefined || scanFeed == 0)) {
            var countScans = $('#scan-form #Form_CountScans').val();
            var timeout = $('#scan-form #Form_ScanInterval').val();
            showOverlay("Сканирование...");
            if ((countScans * 1) != undefined && (timeout * 1) != undefined) {
              var i = 1;
              var DownloadFiles = [];
              setTimeout(function scan() {
                sendAjax("Scan",
                  function (responce) {
                    DownloadFiles.push(new FileForDownload(responce));
                    if (save_as == '0' || i == countScans * 1) {
                      downloadFile(DownloadFiles, save_as);
                      DownloadFiles = [];
                    }

                  },
                  GetScanFormData() + '&isPackage=true', true, function (msg) {
                    Modal.inform.warning()(msg)
                  },
                  function () {
                    if (i < countScans) {
                      setTimeout(scan, (timeout * 1000));
                    }
                    else {
                      ActivateScanForm();
                      hideOverlay();
                    }
                    ++i;
                  });

              }, (timeout * 1000));
            }
          } else {
            if (!$('#scan-form #package-scan').is('.active')) save_as = null;
            sendAjax("Scan", function (responce) {
              downloadFile(parseScanResponse(responce), save_as);
            }, GetScanFormData(), true, function (msg) {
              Modal.inform.warning()(msg)
            }, ActivateScanForm, "Сканування...", true);
          }
        } catch (ex) {
          console && console.log(ex);
        }

        return false;
      };

      $("#Form_FileName").focus();
      GetScanParameters();

      function scanFeedOnChange() {

        function swapSelects(first, second) {

          var selectedValue = first.val();

          var firstOptions = first.html();
          var secondOptions = second.html();

          second.html(firstOptions);
          first.html(secondOptions);

          first.val(selectedValue);

        }

        function replaceSelectFromHidden(activeSelect, hiddenSelect, activeClass, hiddenClass) {
          if (hiddenSelect.hasClass(hiddenClass)) {

            swapSelects(activeSelect, hiddenSelect);

            hiddenSelect.removeClass(hiddenClass);
            hiddenSelect.addClass(activeClass);
          }

        }

        var selectDpi = $('#scan-form').find("#Form_Dpi");
        var selectDpiHidden = $('#scan-form').find("#Form_Dpi_Hidden");

        var scanFeed = $("#scan-form").find("#Form_ScanFeed");
        if (scanFeed.val() != 0 && scanFeed.is(':visible')) {
          var scanMode = $("#scan-form .mode-switch.active").attr('id');

          localStorage.setItem("previousScanMode", scanMode);
          changeMode("package-scan");
          $("#scan-form").find("#conventional-scan").attr('disabled', 'disabled');

          replaceSelectFromHidden(selectDpi, selectDpiHidden, 'flatbedResolutions', 'feederResolutions');

        } else {
          $("#scan-form").find("#conventional-scan").removeAttr('disabled');
          var previousScanMode = localStorage.getItem("previousScanMode");
          changeMode(previousScanMode);

          replaceSelectFromHidden(selectDpi, selectDpiHidden, 'feederResolutions', 'flatbedResolutions');

        }
      };

      $scope.changeSource = function () {
        GetScanParameters($("#scan-form").find("#Form_Source").val());
        scanFeedOnChange();
      };

      $scope.changeScanFeed = function () {
        scanFeedOnChange();
      };

      var FileForDownload = function (resp) {
        var self = this;
        self.temp = resp.temp;
        self.file = resp.file;
        self.base64 = resp.base64;
      };


      scanFeedOnChange();
      var refreshPage = function () {
        $.ajax({
          url: "",
          context: document.body,
          success: function (xhr) {
            Modal.inform.success(function () {
            })("Застосунок успішно перезапущено");
            location.reload();

          },
          error: function () {
            setTimeout(refreshPage, 500);
          }
        });
      };

      $scope.restartApp = function () {
        $scope.oStatusMessage = {
          sText: 'Перезапуск застосунку...',
          bShow: true
        };

        sendAjax("Restart", function () {
          setTimeout(refreshPage, 2000);
        }, null, true, function () {
          setTimeout(refreshPage, 2000);
        }, null, "Перезапуск застосунку...", false);
      };

      $scope.restartWia = function () {
        sendAjax("RestartWia", function () {
          Modal.inform.success(function () {
          })('Служба WIA успішно перезапущена')
        }, null, true, function () {
          Modal.inform.warning()("Не вдалося перезапустити службу WIA");
        }, null, "Перезапуск WIA...", true);
      };

      $(function () {
        var trueAjax = (function () {
          var result = true;
          if (navigator.appName == 'Microsoft Internet Explorer') {
            var ua = navigator.userAgent;
            var re = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
            if (re.exec(ua) != null) {
              var rv = parseFloat(RegExp.$1);
              if (rv * 1 <= 9)
                result = false;
            }
          }
          return result;
        })();

      });

    }])
})();
