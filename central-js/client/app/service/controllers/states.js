angular.module('app')
  .controller('ServiceFormController', function ($scope, service, regions, AdminService,
                                                 ServiceService, TitleChangeService, CatalogService,
                                                 $anchorScroll, $rootScope, feedback, statesRepository) {
    $scope.spinner = true;
    $scope.service = service;
    $scope.regions = regions;
    $scope.bAdmin = AdminService.isAdmin();

    if(statesRepository.isKyivCity()){
      $scope.bHideTab = true;
    } else {
      $scope.bHideTab = false;
    }

    //TODO should be refactored after refactoring for single controller for app/service/index.html
    $scope.feedback = feedback;

  var sServiceName = $scope.service.sName;
  var data = CatalogService.getServiceTags(sServiceName).then(function (res) {
    if (res.length !== 0) {
      var tag = res[0].oServiceTag_Root.sName_UA;
      var situation = res[0].aServiceTag_Child[0].sName_UA;
      TitleChangeService.setTitle(sServiceName + ' / ' + situation + ' / ' + tag);
      $scope.spinner = false;
    } else {
      CatalogService.getServiceBusiness(sServiceName).then(function (res) {
        if(res.length !==0 && res[0].aSubcategory) {
          var scat = res[0].aSubcategory[0].sName;
          TitleChangeService.setTitle(sServiceName + ' / ' + scat + ' / Бізнес');
        } else {
          TitleChangeService.setTitle(sServiceName + ' / Бізнес');
        }
        $scope.spinner = false;
      })
    }
  });
  $anchorScroll();
});

angular.module('app').controller('NewIndexController', function ($scope, AdminService, catalogContent, messageBusService, $rootScope, $anchorScroll, statesRepository, TitleChangeService) {
  var subscriptions = [];
  messageBusService.subscribe('catalog:update', function (data) {
    $scope.mainSpinner = false;
    $rootScope.fullCatalog = data;
    $scope.catalog = data;
    $scope.spinner = false;
    $rootScope.rand = (Math.random() * 10).toFixed(2);
  }, false);

  $scope.isKyivCity = !!statesRepository.isKyivCity();

  $scope.$on('$destroy', function () {
    subscriptions.forEach(function (item) {
      messageBusService.unsubscribe(item);
    });
  });

  $scope.$on('$stateChangeStart', function (event, toState) {
    if (toState.resolve) {
      $scope.spinner = true;
    }
  });
  $scope.$on('$stateChangeError', function (event, toState) {
    if (toState.resolve) {
      $scope.spinner = false;
    }
  });
  $anchorScroll();
});

angular.module('app').controller('OldBusinessController', function ($scope, AdminService, businessContent, messageBusService, $rootScope, $anchorScroll, TitleChangeService) {
  $scope.spinner = true;
  var subscriptions = [];
  messageBusService.subscribe('catalog:update', function (data) {
    $scope.mainSpinner = false;
    $rootScope.fullCatalog = data;
    $scope.catalog = data;
    $rootScope.busSpinner = false;
    $scope.spinner = false;
    $rootScope.rand = (Math.random() * 10).toFixed(2);
  }, false);

  $scope.$on('$destroy', function () {
    subscriptions.forEach(function (item) {
      messageBusService.unsubscribe(item);
    });
  });

  $scope.$on('$stateChangeStart', function (event, toState) {
    if (toState.resolve) {
      $scope.spinner = true;
    }
  });

  $scope.$on('$stateChangeError', function (event, toState) {
    if (toState.resolve) {
      $scope.spinner = false;
    }
  });
  $rootScope.$watch('catalog', function () {
    if ($scope.catalog.length !== 0) $scope.spinner = false;
  });
  $anchorScroll();
});

angular.module('app').controller('SituationController', function ($scope, AdminService, ServiceService, chosenCategory, messageBusService, statesRepository, $rootScope, $sce, $anchorScroll, TitleChangeService, $location) {
  $scope.category = chosenCategory;
  $scope.bAdmin = AdminService.isAdmin();

  messageBusService.subscribe('catalog:update', function (data) {
    console.log('catalog updated, will update items');
    $scope.catalog = data;
    if ($scope.catalog) {
      $scope.category = data;
    } else {
      $scope.category = null;
    }
    $scope.spinner = false;
    $rootScope.rand = (Math.random() * 10).toFixed(2);
  }, false);

  if ($scope.catalog
    && $scope.catalog.aServiceTag_Child
    && chosenCategory.aServiceTag_Child[0].nID === $scope.catalog.aServiceTag_Child[0].nID
    || $rootScope.wasSearched) {
    $scope.category = $scope.catalog;
    $rootScope.wasSearched = false;
  }
  if (!$scope.catalog) {
    $scope.category = $scope.catalog;
  }
  $scope.trustAsHtml = function (string) {
    if(statesRepository.isKyivCity()){
      return $sce.trustAsHtml(string.replace(new RegExp('igov.org.ua', 'g'), 'es.kievcity.gov.ua').replace(new RegExp('iGov.org.ua', 'g'), 'es.KievCity.gov.ua').replace(new RegExp('iGOV.org.ua', 'g'), 'es.KievCity.gov.ua').replace(/\b[Ii][Gg][Oo][Vv]\b/g, 'KievCity'));
    } else {
      return $sce.trustAsHtml(string);
    }
  };
  $scope.$on('$stateChangeStart', function (event, toState) {
    if (toState.resolve) {
      $scope.spinner = true;
    }
  });
  // $scope.$on('$stateChangeError', function(event, toState) {
  //   if (toState.resolve) {
  //     $scope.spinner = false;
  //   }
  // });

  // hypercomments
  var HC_LOAD_INIT = false;
  window._hcwp = window._hcwp || [];
  window._hcwp.push({
    widget: 'Stream',
    widget_id: 60115
  });
  if ('HC_LOAD_INIT' in window) {
    return;
  }
  HC_LOAD_INIT = true;
  var lang = (navigator.language || navigator.systemLanguage || navigator.userLanguage || 'en').substr(0, 2).toLowerCase();
  var hcc = document.createElement('script');
  hcc.type = 'text/javascript';
  hcc.async = true;
  hcc.src = ('https:' === document.location.protocol ? 'https' : 'http') + '://w.hypercomments.com/widget/hc/60115/' + lang + '/widget.js';

  $scope.runComments = function () {
    angular.element(document.querySelector('#hypercomments_widget')).append(hcc);
  };
  try {
    var situation = $scope.category.aServiceTag_Child[0].sName_UA;
    var tag = $scope.category.oServiceTag_Root.sName_UA;
    var title = situation + ' / ' + tag;
    TitleChangeService.setTitle(title);
  } catch (e){

  }
  // якорь для содержания "жизненной ситуации"
  $scope.gotoAnchor = function (x) {
    var newHash = 'anchor' + x;
    if ($location.hash() !== newHash) {
      $location.hash('anchor' + x);
    } else {
      $anchorScroll();
    }
  };

  $anchorScroll();
});

// данная директива нужна для работы контроллера в data-ng-bind-html
angular.module('app').directive('compileTemplate', function ($compile, $parse) {
  return {
    link: function (scope, element, attr) {
      var parsed = $parse(attr.ngBindHtml);

      function getStringValue() {
        return (parsed(scope) || '').toString();
      }

      //Recompile if the template changes
      scope.$watch(getStringValue, function () {
        $compile(element, null, -9999)(scope);  //The -9999 makes it skip directives so that we do not recompile ourselves
      });
    }
  }
});

angular.module('app').controller('ServiceGeneralController', function ($state, $scope, ServiceService, PlacesService) {
  PlacesService.resetPlaceData();

  return $state.go('index.service.general.place', {
    id: ServiceService.oService.nID
  }, {
    location: false
  });
});

angular.module('app').controller('ServiceLegislationController', function ($state, $rootScope, $scope) {
});

angular.module('app').controller('ServiceQuestionsController', function ($state, $rootScope, $scope) {
});

angular.module('app').controller('ServiceDiscussionController', function ($state, $rootScope, $scope) {
  var HC_LOAD_INIT = false;
  window._hcwp = window._hcwp || [];
  window._hcwp.push({
    widget: 'Stream',
    widget_id: 60115
  });
  if ('HC_LOAD_INIT' in window) {
    return;
  }
  HC_LOAD_INIT = true;
  var lang = (navigator.language || navigator.systemLanguage || navigator.userLanguage || 'en').substr(0, 2).toLowerCase();
  var hcc = document.createElement('script');
  hcc.type = 'text/javascript';
  hcc.async = true;
  hcc.src = ('https:' === document.location.protocol ? 'https' : 'http') + '://w.hypercomments.com/widget/hc/60115/' + lang + '/widget.js';
  angular.element(document.querySelector('#hypercomments_widget')).append(hcc);
});

angular.module('app').controller('ServiceStatisticsController', function ($scope, ServiceService) {
  $scope.loaded = false;
  $scope.arrow = '\u2191';
  $scope.reverse = false;

  $scope.changeSort = function () {
    $scope.reverse = !$scope.reverse;
    $scope.arrow = $scope.reverse ? '\u2191' : '\u2193';
  };

  ServiceService.getStatisticsForService(ServiceService.oService.nID).then(function (response) {
    $scope.stats = response.data;
    $scope.nRate = 0;
    var nRate = 0;
    angular.forEach(response.data, function (entry) {
      if (entry.nRate !== null && entry.nRate > 0) {
        //nRate=nRate+(entry.nRate/20);
        nRate = nRate + entry.nRate;
        //nRate=nRate/20;
      }
      //1 - однина, якщо складений (>=20) і закінч на 1 - то однина
      //>=5 && <=20 - родовий множина
      //якщо закінч на - то 2,3,4 називний інакше родовий множина
      function getWord(num, odnina, rodovii_plural, nazivnii_plural) {
        if (num == 1 || (num > 20 && num % 10 == 1))
          return odnina;
        else if ((num < 5 || num > 20) && _.contains([2, 3, 4], num % 10))
          return nazivnii_plural;
        else
          return rodovii_plural;
      }

      entry['timing'] = '';
      var days = Math.floor(entry.nTimeMinutes / 60 / 24), hours = Math.floor(entry.nTimeMinutes / 60) % 24,
        minutes = entry.nTimeMinutes % 60;
      var daysw = getWord(days, 'день', 'днів', 'дні'),
        hoursw = getWord(hours, 'година', 'годин', 'години'),
        minutesw = getWord(minutes, 'хвилина', 'хвилин', 'хвилини');
      if (days > 0) entry.timing = days + ' ' + daysw;
      if (hours > 0) entry.timing += (entry.timing ? ', ' : '') + hours + ' ' + hoursw;
      if (minutes > 0) entry.timing += (entry.timing ? ', ' : '') + minutes + ' ' + minutesw;
      if (!entry.timing) entry.timing = '0 годин'
    });
    $scope.nRate = nRate;


  }, function (response) {
    console.log(response.status + ' ' + response.statusText + '\n' + response.data);
  })
    .finally(function () {
      $scope.loaded = true;
    });
});


// контроллер для загрузки статистики во вкладке "О портале" https://github.com/e-government-ua/i/issues/1230
angular.module('app').controller('ServiceHistoryReportController', ['$scope', 'ServiceService', 'AdminService', function ($scope, ServiceService, AdminService) {

  // поскольку статистика видна только админу, делаем проверку.
  $scope.bAdmin = AdminService.isAdmin();

  $scope.statisticDateBegin = {
    value: new Date(2016, 0, 1, 0, 0)
  };
  $scope.statisticDateEnd = {
    value: new Date(2016, 0, 1, 0, 0)
  };


  // сортировка по клику на заголовок в шапке
  $scope.predicate = 'sID_Order';
  $scope.reverse = true;
  $scope.order = function (predicate) {
    $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
    $scope.predicate = predicate;
  };

  //проверка процесса загрузки таблицы. в процессе загрузки true, загружен - false
  $scope.isStatisticLoading = {
    bState: false
  };

  $scope.switchStatisticLoadingStatus = function () {
    $scope.isStatisticLoading.bState = !$scope.isStatisticLoading.bState
  };

  var result;
  var dateFrom;
  var dateTo;
  var exclude;

  // конвертируем дату и время с datepicker'а в нужный для запроса формат YYYY-MM-DD hh:mm:ss
  $scope.getTimeInterval = function (date) {
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    var chosenDate = date.value.toString();
    var dateSplited = chosenDate.split(' ');
    var selectedTime = dateSplited[4];
    var selectedDay = dateSplited[2];
    var selectedYear = dateSplited[3];
    var selectedMonth = '';

    // меняем буквенное обозначение мес на числовое
    if (dateSplited[1].indexOf(months)) {
      selectedMonth = months.indexOf(dateSplited[1]) + 1;
      if (selectedMonth < 10) {
        selectedMonth = '0' + selectedMonth;
      }
    }
    result = selectedYear + '-' + selectedMonth + '-' + selectedDay + ' ' + selectedTime;
    return result

  };

  // загрузка статистики в формате .csv . проверка на корректность фильтра (если он есть),
  // а также если он используеться - исключение отфильтрованных тасок с файла
  $scope.downloadStatistic = function () {
    var prot = location.protocol;
    if ($scope.statistics !== undefined) {
      if ($scope.sanIDServiceExclude !== undefined) {
        if ($scope.sanIDServiceExclude.match(/^(\d+,)*\d+$/)) {
          window.open(prot + "/wf/service/action/event/getServiceHistoryReport?sDateAt=" + dateFrom + "&sDateTo=" + dateTo + '&sanID_Service_Exclude=' + exclude)
          return
        } else {
          return false
        }
      }
      window.open(prot + "/wf/service/action/event/getServiceHistoryReport?sDateAt=" + dateFrom + "&sDateTo=" + dateTo)
    }
  };

  // загрузка и формирование таблицы
  $scope.getStatisticTable = function () {
    //блокируем возможность повторно нажатия "Завантажити" пока предыдущий запрос находиться в работе
    $scope.switchStatisticLoadingStatus();

    dateFrom = $scope.getTimeInterval($scope.statisticDateBegin);
    dateTo = $scope.getTimeInterval($scope.statisticDateEnd);
    exclude = $scope.sanIDServiceExclude;

    ServiceService.getServiceHistoryReport(dateFrom, dateTo, exclude).then(function (res) {
      var resp = res.data;
      var responseSplited = resp.split(';');
      var correct = responseSplited[12].split('\n');
      responseSplited.splice(0, 13, correct[1]);

      $scope.statistics = [];
      var statistic = {};

      for (var i = 0; i < responseSplited.length; i++) {
        var n = 12 * i;
        if (n + 2 > responseSplited.length) {
          break
        }
        statistic = {
          sID_Order: responseSplited[n],
          nID_Server: Number(responseSplited[1 + n]),
          nID_Service: Number(responseSplited[2 + n]),
          sID_Place: Number(responseSplited[3 + n]),
          nID_Subject: Number(responseSplited[4 + n]),
          nRate: Number(responseSplited[5 + n]),
          sTextFeedback: responseSplited[6 + n],
          sUserTaskName: responseSplited[7 + n],
          sHead: responseSplited[8 + n],
          sBody: responseSplited[9 + n],
          nTimeMinutes: Number(responseSplited[10 + n]),
          sPhone: responseSplited[11 + n],
          nID_ServiceData: ''
        };

        $scope.statistics.push(statistic);
        statistic = {};
      }
      $scope.switchStatisticLoadingStatus();
    });
  }

}]);

angular.module('app').controller('TitleChange', function ($scope, TitleChangeService) {
  $scope.$on('$stateChangeSuccess', function (event, toState) {
    if (!$state.is('index.situation') || !$state.is('index.newsubcategory') || !$state.is('index.service') || !$state.is('index.oldbusiness') || !$state.is('index.subcategory')) {
      TitleChangeService.defaultTitle();
    }
  })
});
