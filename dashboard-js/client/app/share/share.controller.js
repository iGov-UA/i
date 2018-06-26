(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .controller('ShareCtrl', shareCtrl);


  shareCtrl.$inject = ['$scope', '$http', 'tasks', 'Auth', '$rootScope', '$stateParams', 'ExecAndCtrlService', '$timeout', 'DocumentsService', 'CurrentServer', 'cryptoPluginFactory', 'Modal', 'signDialog', 'signService', 'Share', '$base64'];
  function shareCtrl($scope, $http, tasks, Auth, $rootScope, $stateParams, ExecAndCtrlService, $timeout, DocumentsService, CurrentServer, cryptoPluginFactory, Modal, signDialog, signService, Share, $base64) {
    $scope.signsPeople = [];

    $scope.isPrintPressed = false;

    $scope.signedByPeople = '';

    $scope.intermediateData = {
      name: 'fas',
      email: '',
      tel: ''
    };

    cryptoPluginFactory.initialize();

    var plugin = null;
    CryptoPlugin.connect(startWork, error);

    function error(details){
      console.log(details);
      // Обработка ошибки
      // details содержит:
      // code	   Код ошибки
      // message Человекопонятное описание ошибки
      // source  Имя метода, во время выполнения которого произошла ошибка
    }

    function startWork(instance){
      plugin = instance;
      // Открыть сессию на 900с (15 мин) и после вызвать функцию открытия ключа
      plugin.openSession(900, function(data) {
      }, error);
    }

    $scope.getDevicesList = function() {
      plugin.getDeviceList(function (data) {
        console.log(data);
        if(data && data.length > 0) {
          plugin.selectDevice(data[0].deviceId, function (success) {
            console.log(success);
          }, function (err) {
            console.log(err);
          });
        }
      }, function (err) {
        console.log(err);
      });

    };

    $scope.certificateData = '';
    $scope.certificateSurname = '';

    $scope.bName = false;
    $scope.bEmail = false;
    $scope.bNameAndEmail = function () {
      if ($scope.bName && !$scope.bEmail) {
        return true;
      }
    };
    $scope.bTel = false;

    $scope.tagTransform = function (newTag) {
      var item = {
        name: newTag,
        date: newTag + 'date',
        time: newTag + 'time',
        email: newTag + '@email.com',
        isChecked: false
      };
      return item;
    };

    $scope.proceedData = function (e) {
      if (e.keyCode === 13) {
        console.log('Done');
        var data = $scope.intermediateData;
        if (!$scope.bName) {
          $scope.bName = true;
        } else if (!$scope.bEmail) {
          $scope.bEmail = true;
        } else if (!$scope.bTel) {
          $scope.bTel = true;
        }
      }
    };

    $scope.document = null;

    // $scope.nID = 2000767;
    // 2006220
    // 2006297
    // 2006539
    // 2009319
    // 2017449
    $scope.nID = Number($stateParams.nID.substr($stateParams.nID.indexOf('=') + 1, $stateParams.nID.length));
    // $scope.sSecret = '33579127-3a23-41b0-9d5c-792d7596bfd7';
    // 9984ac2c-445c-41e0-9ec6-ef018a9c8454
    // 1a722b96-7da2-46a1-b93f-81ac1cd9e12e
    // 19454b19-6b40-4bf9-878c-8ff6aa2e8421
    // 55909e0d-dd75-4232-85a4-3e7eb56ab20f
    // 4abc2dcf-3e57-4139-a9ed-c5cbb424a162
    $scope.sSecret = $stateParams.sSecret.substr($stateParams.sSecret.indexOf('=') + 1, $stateParams.sSecret.length);

    var document = {
      params: {
        nID: $scope.nID,
        sSecret: $scope.sSecret
      },
      responseType: 'arraybuffer'
    };

    Share.getDocumentPDF(document).then(function (dataPdf, res, req) {
      $scope.data = dataPdf;

      var docParams = {params: {nID: $scope.nID, sSecret: $scope.sSecret}};

      Share.getDocumentImageFileVO(docParams).then(function (data) {
        setIntoSignedPeople(data);
        localStorage.setItem('doc-content', JSON.stringify(data));
      });
    })
      .catch(function (err) {
        if (err.message.indexOf('Entity with id') > -1) {
          Modal.inform.error()('Такого документу не існує');
        } else if (err.message.indexOf('Access denied, reason: wrong secret') > -1) {
          Modal.inform.error()('Відмовлено в доступі. Невірний секретний пароль');
        } else {
          Modal.info.error('Виникла помилка під час завантаження документа. Спробуйте, будь ласка, ще раз');
        }
        console.log(err);
      });


    function setIntoSignedPeople(document) {
      $scope.signsPeople = [];
      if (document.aDocumentImageFileSign && document.aDocumentImageFileSign.length > 0) {
        angular.forEach(document.aDocumentImageFileSign, function (subject) {
          try {
            var jsonObj = JSON.parse(subject.sSignData_JSON);
            var surname = jsonObj.SRN[0] + jsonObj.SRN.substr(1, jsonObj.SRN.length).toLowerCase();
            var firstName = jsonObj.GN.split(' ')[0];
            firstName = firstName[0] + firstName.substr(1, firstName.length).toLowerCase();
            if(jsonObj.GN.split(' ')[1]) {
              var fatherName = jsonObj.GN.split(' ')[1];
              fatherName = fatherName[0] + fatherName.substr(1, fatherName.length).toLowerCase();
              firstName = firstName + ' ' + fatherName;
            }
            var fullName = surname + ' ' + firstName;
            var email = jsonObj.E;
            var date = jsonObj.date;
            var time = jsonObj.time;

            var person = {name: fullName, email: email, date: date, time: time, isChecked: false};
            $scope.signsPeople.push(person);
          } catch (err) {
            console.log(err);
          }
        });
      }
    }

    function applyEcpSigns() {
      var bIsAlreadySigned = false;
      var content = JSON.parse(localStorage.getItem('doc-content'));
      signDialog.signContent(content,
        function (signedContent) {
          var sSign = signedContent.sign;
          var signData = JSON.stringify(signedContent.subject);
          angular.forEach(content.aDocumentImageFileSign, function (sign) {
            if (!bIsAlreadySigned) {
              if (sign.sSignData_JSON === signData) {
                bIsAlreadySigned = true;
                Modal.inform.warning()('Ви вже підписали даний документ!');
              }
            }
          });
          if (!bIsAlreadySigned) {
            var month;
            var day;
            var currentDate = new Date();
            month = (currentDate.getMonth() + 1) < 10 ? '0' + (currentDate.getMonth() + 1)
              : (currentDate.getMonth() + 1);
            day = currentDate.getDate() < 10 ? '0' + currentDate.getDate() : currentDate.getDate();
            var date = day + '.' + month + '.' + currentDate.getFullYear();
            var time = currentDate.getHours() + ':' + currentDate.getMinutes();
            var notValidBefore = signedContent.notValidBefore;
            var notValidAfter = signedContent.notValidAfter;
            signedContent.subject.date = date;
            signedContent.subject.time = time;
            signedContent.subject.notValidBefore = notValidBefore;
            signedContent.subject.notValidAfter = notValidAfter;
            signedContent.subject.certificate = signedContent.certificate;
            signedContent.subject.issuer = signedContent.issuer;
            var setDocParams = {
              sSign: sSign,
              sID_SignType: signedContent.subject.O,
              sSignData_JSON: JSON.stringify(signedContent.subject),
              nID_DocumentImageFile: $scope.nID,
              sSecret_DocumentImageFile: $scope.sSecret
            };
            Share.setDocumentImageFileSign(setDocParams).then(function (oDocumentImageFileVO) {
              console.log(oDocumentImageFileVO);
              var getDocParams = {params: {nID: oDocumentImageFileVO.nID, sSecret: oDocumentImageFileVO.sSecret}};
              Share.getDocumentImageFileVO(getDocParams).then(function (data) {
                setIntoSignedPeople(data);
                localStorage.setItem('doc-content', JSON.stringify(data));
              });
            })
              .catch(function (err) {
                Modal.inform.error()(angular.toJson(err));
                console.log(err);
              });
          }
        },
        function () {
          console.log('Sign Dismissed');
        },
        function (error) {
          Modal.inform.error()(angular.toJson(error));
        })
    }

    $scope.bCertificateInfo = false;

    $scope.isModalByButtonIsOpened = false;

    $scope.execCtrlModals = {
      bShareDoc: false
    };

    $scope.shareParams = {
      bIsCheckedValue1: false,
      bIsCheckedValue2: false
    };

    $scope.openModalWindow = function (action) {
      for (var modal in $scope.execCtrlModals) {
        if ($scope.execCtrlModals.hasOwnProperty(modal) && modal === action) {
          angular.forEach($scope.execCtrlModals, function (value, key, obj) {
            obj[key] = false;
          });
          $scope.execCtrlModals[modal] = $scope.isModalByButtonIsOpened = true;
        }
      }
    };

    $scope.isEmpty = function () {
      return angular.isUndefined(isSelected) || isSelected === null || isSelected === '';
    };

    $scope.closeModalByButton = function () {
      $scope.isModalByButtonIsOpened = false;
    };

    $scope.closeCertificateWindow = function() {
      $scope.bCertificateInfo = false;
    };

    $scope.getCertificateInfo = function (index) {
      try {
        var content = JSON.parse(localStorage.getItem('doc-content'));
        var certificateInfo = JSON.parse(content.aDocumentImageFileSign[index].sSignData_JSON);
        var surname = certificateInfo.SRN;
        var firstName = certificateInfo.GN;
        var fullName = surname + ' ' + firstName;
        var signDate = certificateInfo.date;
        var signTime = certificateInfo.time;
        var date = signTime + ' ' + signDate;
        var inn = certificateInfo.INN;
        var edrpou = certificateInfo.EDRPOU;
        var organization = certificateInfo.O;
        var position = certificateInfo.T;
        var city = certificateInfo.L;
        var serialNumber = certificateInfo.SN;
        var issuerInfo = certificateInfo.issuer;
        var acsk = issuerInfo.CN;
        var issuerOrg = issuerInfo.O;
        var issuerCity = issuerInfo.L;
        var issuerSn = issuerInfo.SN;
        var notValidBefore = moment(certificateInfo.notValidBefore * 1000).format('HH:mm DD.MM.YYYY');
        var notValidAfter = moment(certificateInfo.notValidAfter * 1000).format('HH:mm DD.MM.YYYY');
        // var notValidBefore = new Date(certificateInfo.notValidBefore * 1000).toLocaleString();
        // var notValidAfter = new Date(certificateInfo.notValidAfter * 1000).toLocaleString();
        $scope.certificateInfo = {
          name: fullName, date: date, inn: inn,
          edrpou: edrpou, org: organization,
          pos: position, city: city, sn: serialNumber,
          acsk: acsk, issuerOrg: issuerOrg,
          issuerCity: issuerCity, issuerSn: issuerSn,
          notValidBefore: notValidBefore,
          notValidAfter: notValidAfter
        };
        var img = new Image();
        img.onload = function(){
          $scope.bCertificateInfo = true;
        };
        img.src = './assets/images/certificate.png';
        $scope.certificateData = certificateInfo.certificate;
        $scope.certificateSurname = surname;

      } catch(error) {
        console.log(error);
      }
    };

    $scope.downloadCertificate = function() {
      var fileName = $scope.certificateSurname;
      var a = angular.element('#dynam-a')[0];
      console.log('hey');
      plugin.saveCertificateToFile($scope.certificateData,
        '/assets/', function (data) {
          console.log(data);
        }, function (st) {
          console.log(st);
        });

      // plugin.getCertificateInfo($scope.certificateData, function (data) {
      //   console.log(data);
      //   var file = new Blob([data], {type: 'text/plain'});
      //   var fileURL = window.URL.createObjectURL(file);
      //   console.log(fileURL);
      //   a.href = fileURL;
      //   a.download = fileName;
      //   a.click();
      // });
    };

    $scope.showPrintText = function () {
      $scope.isPrintPressed = !$scope.isPrintPressed;
      if ($scope.isPrintPressed) {
        var signedByPeople = '';
        angular.forEach($scope.signsPeople, function (person) {
          var infoString = person.name.toUpperCase() + ' (' + person.time
            + ' ' + person.date + '); ';
          signedByPeople = signedByPeople.concat(infoString);
        });
        $scope.signedByPeople = signedByPeople;
      }
    };

      $scope.addSign = function () {

      };

      $scope.removeSignString = function (attr, $index) {
        $scope.signsPeople.splice($index, 1);

      };

      $scope.subscribe = function () {
        applyEcpSigns();
      };

      $scope.sendPdf = function () {
        var file = angular.element('#uploadFile')[0].files[0];
        if (file) {
          var reader = new FileReader();
          reader.readAsDataURL(file);
          reader.onload = function (e) {
            var arrayBuffer = e.target.result;
            var base64File = arrayBuffer.substr(arrayBuffer.indexOf(',') + 1,
                              arrayBuffer.length);
            var pdf = new Blob([arrayBuffer]);
            console.log(base64File);

            //getPdf(pdf);

            // var pdfParams = {sID_Token: '123456', file: pdf, sHash: base64File};
            // console.log(pdfParams.sHash);
            // Share.setDocumentImageFile(pdfParams).then(function (data) {
            //   console.log(data);
            // });
          };
        }
      };

      $scope.downloadDoc = function () {
        var content = JSON.parse(localStorage.getItem('doc-content'));
        var fileName = content.nID;
        var a = angular.element('#dynam-a')[0];

        Share.getDocumentImageFileSigned(document).then(function (data) {
          if(data) {
            var file = new Blob([data], {type: 'application/pdf'});
            var fileURL = window.URL.createObjectURL(file);
            a.href = fileURL;
            a.download = fileName;
            a.click();
          }
        });
      };

  }
})();
