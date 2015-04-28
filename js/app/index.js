define('catalog/service', ['angularAMD'], function (angularAMD) {
	angularAMD.service('CatalogService', ['$http', function($http) {
		this.getServices = function() {
			return $http.get('./api/services').then(function(response) {
				return response.data;
			});
		};
	}]);
});
define('state/index/controller', ['angularAMD'], function (angularAMD) {
	angularAMD.controller('IndexController', ['$scope', 'catalog', function ($scope, catalog) {
		$scope.catalog = catalog;
    }]);
});
define('index', ['angularAMD', 'catalog/service'], function (angularAMD) {
    var app = angular.module('index', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('index', {
                url: '/index',
				resolve: {
					catalog: ['CatalogService', function(CatalogService) {
						return CatalogService.getServices();
					}]
				},
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/catalog/services.html');
						}],
						controller: 'IndexController',
                        controllerUrl: 'state/index/controller'
                    })
                }
            })
    }]);
    return app;
});

