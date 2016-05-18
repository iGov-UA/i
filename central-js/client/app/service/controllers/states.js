angular.module('app').controller('ServiceFormController', function($scope, service, regions, AdminService, ServiceService) {
  $scope.service = service;
  $scope.regions = regions;
  $scope.bAdmin = AdminService.isAdmin();
});

angular.module('app').controller('ServiceGeneralController', function($state, $scope, ServiceService, PlacesService) {
  PlacesService.resetPlaceData();

  return $state.go('index.service.general.place', {
    id: ServiceService.oService.nID
  }, {
    location: false
  });
});

angular.module('app').controller('ServiceLegislationController', function($state, $rootScope, $scope) {});

angular.module('app').controller('ServiceQuestionsController', function($state, $rootScope, $scope) {});

angular.module('app').controller('ServiceDiscussionController', function($state, $rootScope, $scope) {
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

angular.module('app').controller('ServiceStatisticsController', function($scope, ServiceService) {
  $scope.loaded = false;
  $scope.arrow = '\u2191';
  $scope.reverse = false;

  $scope.changeSort = function() {
    $scope.reverse = !$scope.reverse;
    $scope.arrow = $scope.reverse ? '\u2191' : '\u2193';
  };

  ServiceService.getStatisticsForService(ServiceService.oService.nID).then(function(response) {
    $scope.stats = response.data;
    $scope.nRate = 0;
    var nRate=0;
    angular.forEach(response.data, function (entry) {
      if (entry.nRate !== null && entry.nRate > 0) {
          //nRate=nRate+(entry.nRate/20);
          nRate=nRate+entry.nRate;
          //nRate=nRate/20;
      }
      //1 - однина, якщо складений (>=20) і закінч на 1 - то однина
      //>=5 && <=20 - родовий множина
      //якщо закінч на - то 2,3,4 називний інакше родовий множина
      function getWord(num, odnina, rodovii_plural, nazivnii_plural) {
        if (num == 1 || (num > 20 && num % 10 == 1) )
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
      if (days > 0) entry.timing =  days + ' ' + daysw;
      if (hours > 0) entry.timing += (entry.timing ? ', ' : '') + hours + ' ' + hoursw;
      if (minutes > 0) entry.timing += (entry.timing ? ', ' : '') + minutes + ' ' + minutesw;
      if (!entry.timing) entry.timing = '0 годин'
    });
    $scope.nRate = nRate;


    }, function(response) {
      console.log(response.status + ' ' + response.statusText + '\n' + response.data);
    })
    .finally(function() {
      $scope.loaded = true;
    });
});


// контроллер для загрузки статистики во вкладке "О портале" https://github.com/e-government-ua/i/issues/1230
angular.module('app').controller('ServiceHistoryReportController', ['$scope', 'ServiceService', 'AdminService', function($scope, ServiceService, AdminService) {

  $scope.bAdmin = AdminService.isAdmin();

  $scope.predicate = 'nID_ServiceData';
  $scope.reverse = true;
  $scope.order = function(predicate) {
    $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
    $scope.predicate = predicate;
  };

  var result;
  var dateFrom;
  var dateTo;
  var exclude;

  $scope.getTimeInterval = function (date) {
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    var chosenDate = date.toString();
    var dateSplited = chosenDate.split(' ');
    var selectedTime = dateSplited[4];
    var selectedDay = dateSplited[2];
    var selectedYear = dateSplited[3];
    var selectedMonth = '';

    if(dateSplited[1].indexOf(months)){
      selectedMonth = months.indexOf(dateSplited[1])+1;
      if(selectedMonth < 10){
        selectedMonth = '0' + selectedMonth;
      }
    }
    result = selectedYear + '-' + selectedMonth + '-' + selectedDay + ' ' + selectedTime;
    return result

  };

  $scope.downloadStatistic = function() {
    var prot = location.protocol;
    if($scope.statistics !== undefined) {
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

  $scope.getDates = function () {

    dateFrom = $scope.getTimeInterval($scope.begin);
    dateTo = $scope.getTimeInterval($scope.end);
    exclude = $scope.sanIDServiceExclude;

    ServiceService.getServiceHistoryReport(dateFrom, dateTo, exclude).then(function (res) {
      var resp = res.data;
      var responseSplited = resp.split(';');
      var correct = responseSplited[12].split('\n');
      responseSplited.splice(0, 13, correct[1]);

      $scope.statistics = [];
      var statistic = {};

      for(i=0; i<responseSplited.length; i++){
        var n = 12*i;
        if(n + 2 > responseSplited.length){
          break
        }
        statistic.sID_Order = responseSplited[n];
        statistic.nID_Server = Number(responseSplited[1 + n]);
        statistic.nID_Service = Number(responseSplited[2 + n]);
        statistic.sID_Place = Number(responseSplited[3 + n]);
        statistic.nID_Subject = Number(responseSplited[4 + n]);
        statistic.nRate = Number(responseSplited[5 + n]);
        statistic.sTextFeedback = responseSplited[6 + n];
        statistic.sUserTaskName = responseSplited[7 + n];
        statistic.sHead = responseSplited[8 + n];
        statistic.sBody = responseSplited[9 + n];
        statistic.nTimeMinutes = Number(responseSplited[10 + n]);
        statistic.sPhone = responseSplited[11 + n];
        statistic.nID_ServiceData = '';

        $scope.statistics.push(statistic);
        statistic = {};
      }

    });
  }

}]);
