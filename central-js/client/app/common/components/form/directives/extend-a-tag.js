angular.module('linkdir', []).directive('a', [function() {
	'use strict';
	return {
		restrict: 'E',
		link: function link(scope, elem, attrs, ngModel) {
			var element = elem[0];
			var sCurrHost = location.hostname;

			var sLinkHref = attrs.ngHref ? attrs.ngHref : (attrs.href ? attrs.href : element.href);

			if (sLinkHref.indexOf(sCurrHost) === -1  && sLinkHref.indexOf('igov') > -1) {
				element.href = '/api/service/setAuthForURL?sURL=' + encodeURIComponent(sLinkHref);

				element.addEventListener('click', function(event) {
					event.preventDefault();
					location = element.href;
				});
			}
		}
	};
}]);