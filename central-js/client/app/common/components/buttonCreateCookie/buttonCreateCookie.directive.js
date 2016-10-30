/**
 * Created by Igor on 5/4/2016.
 */
'use strict';

angular.module('app')
  .directive('buttonCreateCookie', ['$location',
    function (location) {
      return {
        restrict: 'E',
        scope: {
          ngModel: '='
        },
        templateUrl: 'app/common/components/buttonCreateCookie/buttonCreateCookie.directive.html',
        link: function buttonEnableCoockie(scope) {
          var cookieName = 'authMock';

          scope.model = {
            enable: typeof getCookie(cookieName) !== 'undefined',
          };

          scope.onChange = function (arg) {
            if (arg) {
              setCookie(cookieName, 'MockUser', {
                path: "/",
                expires: 180
              });
            } else if (!arg) {
              deleteCookie(cookieName, 'MockUser');
            }
          };

          scope.isVisible = function () {
            if (this.$root.profile.isKyivCity) return false;
            //return location.host() === 'localhost' || location.host() === 'test.igov.org.ua';
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
      }
    }]
);
