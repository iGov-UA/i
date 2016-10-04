angular.module('app').controller('ServiceFormController', function ($scope, service, regions, AdminService, ServiceService, TitleChangeService, CatalogService, $anchorScroll) {
  $scope.spinner = true;
  $scope.service = service;
  $scope.regions = regions;
  $scope.bAdmin = AdminService.isAdmin();
  var sServiceName = $scope.service.sName;
  var data = CatalogService.getServiceTags(sServiceName).then(function (res) {
    if (res.length !== 0) {
      var tag = res[0].oServiceTag_Root.sName_UA;
      var situation = res[0].aServiceTag_Child[0].sName_UA;
      TitleChangeService.setTitle(sServiceName + ' / ' + situation + ' / ' + tag);
      $scope.spinner = false;
    } else {
      CatalogService.getServiceBusiness(sServiceName).then(function (res) {
        var scat = res[0].aSubcategory[0].sName;
        TitleChangeService.setTitle(sServiceName + ' / ' + scat + ' / Бізнес');
        $scope.spinner = false;
      })
    }
  });
  $anchorScroll();
});

angular.module('app').controller('NewIndexController', function ($scope, AdminService, catalogContent, messageBusService, $rootScope, $anchorScroll, TitleChangeService) {
  var subscriptions = [];
  messageBusService.subscribe('catalog:update', function (data) {
    $scope.mainSpinner = false;
    $rootScope.fullCatalog = data;
    $scope.catalog = data;
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

angular.module('app').controller('SituationController', function ($scope, AdminService, ServiceService, chosenCategory, messageBusService, $rootScope, $sce, $anchorScroll, TitleChangeService, $location) {
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
    return $sce.trustAsHtml(string);
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
  var situation = $scope.category.aServiceTag_Child[0].sName_UA;
  var tag = $scope.category.oServiceTag_Root.sName_UA;
  var title = situation + ' / ' + tag;
  TitleChangeService.setTitle(title);

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

angular.module('app').controller('ServiceFeedbackController', function (SimpleErrorsFactory, $state, $stateParams, $scope, service, ServiceService, FeedbackService, ErrorsFactory, $q, AdminService, UserService) {

  $scope.nID = null;
  $scope.sID_Token = null;
  $scope.feedback = {
    messageBody: '',
    messageList: [],
    allowLeaveFeedback: false,
    feedbackError: false,
    postFeedback: postFeedback,
    rateFunction: rateFunction,
    sendAnswer: sendAnswer,
    answer: answer,
    hideAnswer: hideAnswer,
    rating: 3,
    exist: false,
    readonly: true,
    isAdmin: false,
    showAnswer: false,
    relativeTime: relativeTime
  };

  activate();

  function activate() {

    if(!ServiceService.oService.nID){
      SimpleErrorsFactory.push({
        type: "denger",
        oData: {sHead:'Послуга не існує!',
          sBody:'Виберіть, будьласка, існуючу послугу.'}});
      return;
    }

    UserService.isLoggedIn().then(function (result) {
      if (result) {
        UserService.fio().then(function (res) {
          $scope.feedback.isAdmin = AdminService.isAdmin();
        });
      }
    });

    $scope.$on('logoutEvent', function (event, data) {
      $scope.feedback.isAdmin = data.isLogged;
    });

    $scope.nID = $stateParams.nID;
    $scope.sID_Token = $stateParams.sID_Token;
    $scope.feedback.sSubjectOperatorName = service.sSubjectOperatorName;

    if ($scope.nID && $scope.sID_Token) {
      $scope.feedback.allowLeaveFeedback = true;
    }
    refreshList();
  }

  function refreshList() {
    FeedbackService.getFeedbackForService(ServiceService.oService.nID, $scope.nID, $scope.sID_Token)
      .then(function (response) {
        var funcDesc = {sHead: "Завантаженя фідбеку для послуг", sFunc: "getFeedbackForService"};
        ErrorsFactory.init(funcDesc, {asParam: ['nID: ' + ServiceService.oService.nID]});
        if (ErrorsFactory.bSuccessResponse(response)) {
        }

        $scope.feedback.messageList = _.sortBy(service.aFeedbacks, function (o) {
          return o.hasOwnProperty('oSubjectMessage') ? -Date.parse(o.oSubjectMessage.sDate) : -o.nID;
        });

        $scope.feedback.rating = response.data.nID_Rate || 5;
        $scope.feedback.exist = !!response.data.oSubjectMessage;
        $scope.feedback.messageBody = response.data.oSubjectMessage ? response.data.oSubjectMessage.sBody : null;

        $scope.feedback.messageList = _.filter($scope.feedback.messageList, function (o) {
          return (typeof o.sBody) === 'string' ? !!o.sBody.trim() : false;
        });

        $scope.feedback.currentFeedback = angular.copy(response.data);

      }, function (error) {

        switch (error.message) {
          case "Security Error":
            pushError("Помилка безпеки!");
            break;
          case "Record Not Found":
            pushError("Запис не знайдено!");
            break;
          case "Already exist":
            pushError("Вiдгук вже залишено!");
            break;
          default :
            $scope.feedback.feedbackError = true;
            ErrorsFactory.logFail({sBody: "Невідома помилка!", sError: error.message});
            break;
        }
      }).finally(function () {
        $scope.loaded = true;
      });
  }

  function rateFunction(rating) {
    $scope.feedback.rating = rating;
  }

  function postFeedback() {
    var sAuthorFIO = $scope.feedback.currentFeedback.sAuthorFIO,
      sMail = $scope.feedback.currentFeedback.sMail,
      sHead = $scope.feedback.currentFeedback.sHead;

    if (!((typeof $scope.feedback.messageBody) === 'string' ? !!$scope.feedback.messageBody.trim() : false)) {
      return;
    }

    var feedbackParams = {
      'sToken': $scope.sID_Token,
      'sBody': $scope.feedback.messageBody,
      'sID_Source': 'iGov',
      'nID': $scope.nID,
      'sAuthorFIO': sAuthorFIO,
      'sMail': sMail,
      'sHead': sHead,
      'nID_Rate': $scope.feedback.rating,
      'nID_Service': ServiceService.oService.nID
    };

    FeedbackService.postFeedbackForService(feedbackParams).finally(function () {
      refreshList();
    });

    $state.go('index.service.feedback', {
      nID: null,
      sID_Token: null
    });
  }

  function sendAnswer(data) {
    var sHead = '';

    var feedbackParams = {
      'sID_Token': $scope.sID_Token,
      'sBody': data.sAnswer.sText,
      'nID_SubjectMessageFeedback': data.nID,
      'sAuthorFIO': data.sAnswer.sAuthorFIO,
      'nID_Service': data.nID_Service,
      'nID_Subject': $state.nID_Subject
    };

    FeedbackService.postFeedbackAnswerForService(feedbackParams).then(function () {
      refreshList();
    });
    hideAnswer();
  }

  function answer(commentID) {
    $scope.feedback.commentToShowAnswer = commentID;
  }

  function hideAnswer() {
    $scope.feedback.commentToShowAnswer = -1;
  }

  function pushError(sErrorText){
    $scope.messageError = true;
    ErrorsFactory.logWarn({sBody:sErrorText});
    /*ErrorsFactory.push({
     type: "danger",
     text:  sErrorText
     });*/
  }

  function relativeTime(dateStr) {
    if (!dateStr) {
      return;
    }

    var result = '',
        date = $.trim(dateStr),
        parsedDate = new Date(date),
        time = parsedDate.getHours()+ ':' + parsedDate.getMinutes(),
        today = moment().startOf('day'),
        releaseDate = moment(date),
        diffDays = today.diff(releaseDate, 'days', true);

    if(diffDays < 0){
      result = 'сьогодні ' + time;
    } else if(diffDays < 1) {
      result = ' вчора ' + time;
    } else if(Math.floor(diffDays) <= 4){
      result = Math.floor(diffDays) + ' дні назад ' + time;
    } else {
      result = Math.floor(diffDays) + ' днів назад ' + time;
    }

    return result;
  }
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
