define('state/journal/controller', ['angularAMD'], function (angularAMD) {
	angularAMD.controller('JournalController', ['$rootScope', function ($rootScope) {
		console.log('$rootScope');
    }]);
});
define('journal', ['angularAMD'], function (angularAMD) {
    var app = angular.module('journal', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('journal', {
                url: '/journal',
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/journal/index.html');
						}],
						controller: 'JournalController',
                        controllerUrl: 'state/journal/controller'
                    })
                }
            })
    }]);
    return app;
});

