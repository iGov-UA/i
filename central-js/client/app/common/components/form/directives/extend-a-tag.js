angular.module('app').directive('a', ['$rootScope', '$http', function($rootScope, $http) {
	'use strict';
	return {
		restrict: 'E',
		link: function link(scope, elem, attrs, ngModel) {
			var element = elem[0];
			var sCurrHost = location.hostname;
			var sLinkHref = element.href;
			var sLinkHost = element.hostname;

			if (sCurrHost !== sLinkHost && sLinkHref.indexOf('igov') > -1) {
                element.href = '/api/service/setAuthForURL?sURL=' + sLinkHref;
                
                element.addEventListener('click', function(event) {
					location = element.href;
				});
			}
		}
	};
}]); 