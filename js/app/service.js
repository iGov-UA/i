define('service/link/controller', ['angularAMD'], function (angularAMD) {
	angularAMD.controller('ServiceLinkController', ['$rootScope', '$scope', function ($rootScope, $scope) {
		$scope.data = {
			region: null,
			city: null
		};
    }]);
});
define('service.link', ['angularAMD'], function (angularAMD) {
    var app = angular.module('service.link', []);
    return app;
});


define('service/built-in/controller', ['angularAMD'], function (angularAMD) {
	angularAMD.controller('ServiceBuiltInController', ['$rootScope', '$scope', function ($rootScope, $scope) {
		$scope.data = {
			region: null,
			city: null
		};
    }]);
});
define('service.built-in', ['angularAMD'], function (angularAMD) {
    var app = angular.module('service.built-in', []);
    return app;
});


define('service.country.built-in', ['angularAMD'], function (angularAMD) {
    var app = angular.module('service.country.built-in', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service.country.built-in', {
                url: '/built-in',
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/country/built-in/index.html');
						}],
						controller: 'ServiceBuiltInController',
                        controllerUrl: 'service/built-in/controller'
                    })
                }
            })
    }]);
    return app;
});


define('state/service/country/controller', ['angularAMD'], function (angularAMD) {
	angularAMD.controller('ServiceCountryController', ['$state', '$rootScope', '$scope', 'service', function ($state, $rootScope, $scope, service) {
		$scope.service = service;
		
		switch(service.serviceType.id) {
			case 1:
				return $state.go('service.country.link', {id: service.id}, { location: true });
			case 4:
				return $state.go('service.country.built-in', {id: service.id}, { location: true });
		}
    }]);
});
define('service.country.link', ['angularAMD', 'service.link'], function (angularAMD) {
    var app = angular.module('service.country.link', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service.country.link', {
                url: '/link',
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/country/link/index.html');
						}],
						controller: 'ServiceLinkController',
                        controllerUrl: 'service/link/controller'
                    })
                }
            })
    }]);
    return app;
});


define('service.country', ['angularAMD', 'service.country.link', 'service.country.built-in'], function (angularAMD) {
    var app = angular.module('service.country', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service.country', {
                url: '/country',
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/country/index.html');
						}],
						controller: 'ServiceCountryController',
                        controllerUrl: 'state/service/country/controller'
                    })
                }
            })
    }]);
    return app;
});
define('service.region.built-in', ['angularAMD'], function (angularAMD) {
    var app = angular.module('service.region.built-in', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service.region.built-in', {
                url: '/built-in',
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/region/built-in/index.html');
						}],
						controller: 'ServiceBuiltInController',
                        controllerUrl: 'service/built-in/controller'
                    })
                }
            })
    }]);
    return app;
});


define('state/service/region/controller', ['angularAMD'], function (angularAMD) {
	angularAMD.controller('ServiceRegionController', ['$state', '$rootScope', '$scope', 'service', 'places',
		function ($state, $rootScope, $scope, service, places) {
			$scope.service = service;
			$scope.places = places;
			
			switch(service.serviceType.id) {
				case 1:
					return $state.go('service.region.link', {id: service.id}, { location: true });
				case 4:
					return $state.go('service.region.built-in', {id: service.id}, { location: true });
			}
		}
	]);
});
define('service.region.link', ['angularAMD'], function (angularAMD) {
    var app = angular.module('service.region.link', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service.region.link', {
                url: '/link',
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/region/link/index.html');
						}],
						controller: 'ServiceLinkController',
                        controllerUrl: 'service/link/controller'
                    })
                }
            })
    }]);
    return app;
});


define('service.region', ['angularAMD', 'service.region.link', 'service.region.built-in'], function (angularAMD) {
    var app = angular.module('service.region', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service.region', {
                url: '/region',
				resolve: {
					places: ['$stateParams', 'ServiceService', function($stateParams, ServiceService) {
						return ServiceService.getPlaces();
					}]
				},
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/region/index.html');
						}],
						controller: 'ServiceRegionController',
                        controllerUrl: 'state/service/region/controller'
                    })
                }
            })
    }]);
    return app;
});


define('service.city.built-in', ['angularAMD'], function (angularAMD) {
    var app = angular.module('service.city.built-in', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service.city.built-in', {
                url: '/built-in',
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/city/built-in/index.html');
						}],
						controller: 'ServiceBuiltInController',
                        controllerUrl: 'service/built-in/controller'
                    })
                }
            })
    }]);
    return app;
});


define('state/service/city/controller', ['angularAMD'], function (angularAMD) {
	angularAMD.controller('ServiceCityController', ['$state', '$rootScope', '$scope', 'service', 'places',
		function ($state, $rootScope, $scope, service, places) {
			$scope.service = service;
			$scope.places = places;
			
			switch(service.serviceType.id) {
				case 1:
					return $state.go('service.city.link', {id: service.id}, { location: true });
				case 4:
					return $state.go('service.city.built-in', {id: service.id}, { location: true });
			}
		}
	]);
});
define('service.city.link', ['angularAMD'], function (angularAMD) {
    var app = angular.module('service.city.link', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service.city.link', {
                url: '/link',
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/city/link/index.html');
						}],
						controller: 'ServiceLinkController',
                        controllerUrl: 'service/link/controller'
                    })
                }
            })
    }]);
    return app;
});


define('service.city', ['angularAMD', 'service.city.link', 'service.city.built-in'], function (angularAMD) {
    var app = angular.module('service.city', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service.city', {
                url: '/city',
				resolve: {
					places: ['$stateParams', 'ServiceService', function($stateParams, ServiceService) {
						return ServiceService.getPlaces();
					}]
				},
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/city/index.html');
						}],
						controller: 'ServiceCityController',
                        controllerUrl: 'state/service/city/controller'
                    })
                }
            })
    }]);
    return app;
});
define('state/service/controller', ['angularAMD'], function (angularAMD) {
	angularAMD.controller('ServiceController', ['$state', '$rootScope', '$scope', 'service', function ($state, $rootScope, $scope, service) {
		$scope.service = service;
	}]);
});

define('state/service/general/controller', ['angularAMD'], function (angularAMD) {
	angularAMD.controller('ServiceGeneralController', ['$state', '$rootScope', '$scope', 'service', function ($state, $rootScope, $scope, service) {
		$scope.service = service;
		
		var places = service.places;
		if(places.regions.length == 0) {
			return $state.go('service.country', {id: service.id}, { location: true });
		}
		
		if(places.cities.length == 0) {
			return $state.go('service.region', {id: service.id}, { location: true });
		}
		
		return $state.go('service.city', {id: service.id}, { location: true });
    }]);
});
define('service/service', ['angularAMD'], function (angularAMD) {
	angularAMD.service('ServiceService', ['$http', function($http) {
		this.get = function(id) {
			var data = {
				'id': id
			};
			return $http.get('./api/service', {
				params: data,
				data: data
			}).then(function(response) {
				return response.data;
			});
		};
		this.getPlaces = function() {
			return $http.get('./api/places').then(function(response) {
				return response.data;
			});
		};
	}]);
});
define('service', ['angularAMD', 'service.country', 'service.region', 'service.city', 'service/service'], function (angularAMD) {
    var app = angular.module('service', []);

    app.config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('service', {
                url: '/service/{id:int}',
				resolve: {
					service: ['$stateParams', 'ServiceService', function($stateParams, ServiceService) {
						return ServiceService.get($stateParams.id);
					}]
				},
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/index.html');
						}],
						controller: 'ServiceController',
                        controllerUrl: 'state/service/controller'
                    })
                }
            })
			.state('service.general', {
				url: '/general',
                views: {
                    '': angularAMD.route({
                        templateProvider: ['$templateCache', function($templateCache) {
							return $templateCache.get('html/service/general.html');
						}],
						controller: 'ServiceGeneralController',
                        controllerUrl: 'state/service/general/controller'
                    })
                }
			})
			.state('service.instruction', {
				url: '/instruction'
			})
			.state('service.legislation', {
				url: '/legislation'
			})
			.state('service.questions', {
				url: '/questions'
			})
			.state('service.discussion', {
				url: '/discussion'
			})
    }]);
    return app;
});

