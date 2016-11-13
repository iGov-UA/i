(function () {
  'use strict';

  angular.module('app')
    .controller('AnswerContentController', AnswerContentController);

  function AnswerContentController($http, $state, BankIDLogin) {
    var vm = this;

    activate();

    function activate() {
      if(!BankIDLogin){
        $state.go('index.journal');
      }

      var fileId = $state.params.signedFileID;

      if (fileId) {
        $http({
          method: 'GET',
          url: '/api/answer/DFS/decrypted?signedFileID=' + fileId
        }).then(function successCallback(response) {
          if(!response.data){
            return;
          }

          var headers = response.headers();
          var filename = headers['x-filename'];
          var contentType = headers['content-type'];
          var linkElement = document.createElement('a');

          try {
              var blob = new Blob([response.data], { type: contentType });
              var url = window.URL.createObjectURL(blob);

              linkElement.setAttribute('href', url);
              linkElement.setAttribute("download", filename);

              var clickEvent = new MouseEvent("click", {
                "view": window,
                "bubbles": true,
                "cancelable": false
              });
              linkElement.dispatchEvent(clickEvent);
          } catch (ex) {
            console.log(ex);
          }

        }, function errorCallback(response) {
          console.log(response);
        });
      }
    }
  }
})();
