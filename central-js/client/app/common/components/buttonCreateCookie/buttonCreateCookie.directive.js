(function () {
  'use strict';

  angular.module('app')
    .directive('buttonCreateCookie', buttonCreateCookie);

  function buttonCreateCookie() {
    return {
      restrict: 'EA',
      scope: {},
      templateUrl: 'app/common/components/buttonCreateCookie/buttonCreateCookie.directive.html',
      controller: ButtonCreateCookieController,
      controllerAs: 'vm',
      bindToController: true
    };
  }

  function ButtonCreateCookieController($scope) {
    var vm = this;
    var cookieName = 'authMock';

    vm.onChange = onChange;

    vm.enable = typeof getCookie(cookieName) !== 'undefined';

    function onChange() {
      vm.enable = !vm.enable;
      if (vm.enable) {
        setCookie(cookieName, 'MockUser', {
          path: "/",
          expires: 180
        });
      } else if (!vm.enable) {
        deleteCookie(cookieName, 'MockUser');
      }
    }

    vm.isVisible = function () {
      if ($scope.$root.profile.isKyivCity) return false;
      return getCookie('bServerTest') === 'true';
    };


    function getCookie(name) {
      var matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
      ));
      return matches ? decodeURIComponent(matches[1]) : undefined;
    }


    function setCookie(name, value, options) {
      options = options || {};

      var expires = options.expires;

      if (typeof expires == "number" && expires) {
        var d = new Date();
        d.setTime(d.getTime() + expires * 1000);
        expires = options.expires = d;
      }
      if (expires && expires.toUTCString) {
        options.expires = expires.toUTCString();
      }

      value = encodeURIComponent(value);

      var updatedCookie = name + "=" + value;

      for (var propName in options) {
        updatedCookie += "; " + propName;
        var propValue = options[propName];
        if (propValue !== true) {
          updatedCookie += "=" + propValue;
        }
      }
      document.cookie = updatedCookie;
    }

    function deleteCookie(name, value) {
      setCookie(name, value, {
        expires: -1,
        path: "/"
      });
    }
  }
})();
