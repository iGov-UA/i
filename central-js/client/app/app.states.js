angular.module('app').config(function($stateProvider, statesRepositoryProvider) {
  statesRepositoryProvider.init(window.location.host);
  $stateProvider
    .state('index', statesRepositoryProvider.index())
    .state('index.service', {
      abstract: true,
      url: 'service/{id:int}',
      resolve: {
        // TODO make sure it works stable
        service: function($stateParams, ServiceService) {
          return ServiceService.get($stateParams.id);
        },
        regions: function(PlacesService, service) {
          return PlacesService.getRegionsForService(service);
        },
        title: function (TitleChangeService) {
          TitleChangeService.defaultTitle();
        },
        //TODO should be removed after refactoring for single controller for app/service/index.html
        feedback: function($q, $stateParams, FeedbackService){
          var deferred = $q.defer();

          FeedbackService.getFeedbackListForService($stateParams.id)
          .then(function (response) {
            var messageList = _.filter(response.data, function (o) {
              var filters = o.sAuthorFIO.trim().match(/null/gi);

              return ((o.oSubjectMessage && (typeof o.oSubjectMessage.sBody) === 'string') ? !!o.oSubjectMessage.sBody.trim() : false)
                && !(Array.isArray(filters) && filters[0] ? filters[0].trim() === 'null' : false);
            });
            deferred.resolve({visible: messageList.length});
          });

          return deferred.promise;
        }
      },
      views: {
        'main@': {
          templateUrl: 'app/service/index.html',
          controller: 'ServiceFormController'
        }
      }
    })
    .state('index.situation', {
      url: 'subcategory/:catID/:scatID/situation/:sitID',
      resolve: {
        chosenCategory: function(CatalogService, $stateParams) {
          return CatalogService.getCatalogTreeTagService($stateParams.catID, $stateParams.scatID, $stateParams.sitID);
        }
      },
      views: {
        'contentIn': {
          templateUrl: 'app/service/new.situation.html',
          controller: 'SituationController'
        }
      }
    })
    .state('index.newsubcategory', {
      url: 'subcategory/:catID/:scatID',
      resolve: {
        chosenCategory: function(CatalogService, $stateParams) {
          return CatalogService.getCatalogTreeTagService($stateParams.catID, $stateParams.scatID);
        }
      },
      views: {
        'contentIn': {
          templateUrl: 'app/service/subcategory/new.subcategory.html',
          controller: 'NewSubcategoryController'
        }
      }
    })
    .state('index.service.general', {
      url: '/general',
      views: {
        'content': {
          controller: 'ServiceGeneralController'
        }
      }
    })
    .state('index.service.instruction', {
      url: '/instruction',
      views: {
        'content': {
          templateUrl: 'app/service/instruction/instruction.html',
          controller: 'ServiceInstructionController'
        }
      }
    })
    .state('index.service.feedback', {
      url: '/feedback?:nID&:sID_Token',
      views: {
        'content': {
          templateUrl: 'app/service/feedback.html',
          controller: 'ServiceFeedbackController'
        }
      }
    })
    .state('index.service.legislation', {
      url: '/legislation',
      views: {
        'content': {
          templateUrl: 'app/service/legislation.html',
          controller: 'ServiceLegislationController'
        }
      }
    })
    .state('index.service.questions', {
      url: '/questions',
      views: {
        'content': {
          templateUrl: 'app/service/questions.html',
          controller: 'ServiceQuestionsController'
        }
      }
    })
    .state('index.service.discussion', {
      url: '/discussion',
      views: {
        'content': {
          templateUrl: 'app/service/discussion.html',
          controller: 'ServiceDiscussionController'
        }
      }
    })
    .state('index.service.statistics', {
      url: '/statistics',
      views: {
        'content': {
          templateUrl: 'app/service/statistics.html',
          controller: 'ServiceStatisticsController'
        }
      }
    })
    .state('index.catalog', {
      url: ':catID',
      resolve : {
        catalogContent : function (CatalogService, $stateParams) {
          return CatalogService.getCatalogTreeTag($stateParams.catID)
        },
        title: function (TitleChangeService) {
          TitleChangeService.defaultTitle();
        }
      },
        views: {
          'contentIn' : {
            templateUrl: 'app/service/template.services.html',
            controller: 'NewIndexController'
          }
        }
    })
    // !! для старого "бизнеса"
    .state('index.oldbusiness', {
      url: 'business/1',
      resolve: {
        businessContent: function (CatalogService) {
          return CatalogService.getServices()
        },
        title: function (TitleChangeService) {
          TitleChangeService.defaultTitle();
        }
      },
      views: {
        'contentIn' : {
          templateUrl: 'app/service/index/oldbusiness.html',
          controller: 'OldBusinessController'
        }
      }
    })
    .state('index.subcategory', {
      url: 'business/subcategory/:catID/:scatID',
      resolve: {
        catalog: function (CatalogService) {
          return CatalogService.getServices()
        }
      },
      views: {
        'contentIn': {
          templateUrl: 'app/service/subcategory/oldbusiness.subcategory.html',
          controller: 'SubcategoryController'
        }
      }
    })
});
