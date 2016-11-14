(function () {
  'use strict';

  angular.module('app')
    .controller('AnswerContentController', AnswerContentController);

  function AnswerContentController($http, $scope, $state, $location, UserService) {
    var vm = this;

    vm.isLoggedIn = false;
    vm.getRedirectURI = getRedirectURI;

    activate();

    function activate() {
      loggedInPending();

      var fileId = $state.params.signedFileID;
      var fileName = $state.params.fileName;

      if (fileId) {
        $http({
          method: 'GET',
          url: '/api/answer/DFS/decrypted?signedFileID=' + fileId + '&fileName=' + fileName
        }).then(function successCallback(response) {
          if (!response.data) {
            return;
          }

          var headers = response.headers();
          var filename = headers['x-filename'];
          var contentType = headers['content-type'];
          var linkElement = document.createElement('a');

          try {
            var blob = new Blob([response.data], {type: contentType});
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

      $scope.$on('event.logout', function () {
        vm.isLoggedIn = false;
      });

    }

    function loggedInPending() {
      UserService.isLoggedIn()
        .then(function () {
          vm.isLoggedIn = true;
        })
        .catch(function () {
          vm.isLoggedIn = false;
        });
    }

    function getRedirectURI() {
      var stateForRedirect = $state.href('index.journal.answer', {error: ''});
      return $location.protocol() +
        '://' + $location.host() + ':'
        + $location.port()
        + stateForRedirect;
    }
  }
})();
