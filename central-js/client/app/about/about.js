angular.module('about').config(function ($stateProvider, statesRepositoryProvider) {
  statesRepositoryProvider.init(window.location.host);
//  if (statesRepositoryProvider.isCentral()) {
    $stateProvider
      .state('index.about', {
        url: 'about',
        resolve: {
          title: function (TitleChangeService) {
            TitleChangeService.defaultTitle();
          }
        },
        views: {
          'main@': {
            templateUrl: 'app/about/about.html',
            controller: 'ServiceHistoryReportController'
          }
        }
      })
      .state('index.test', {
        url: 'test',
        views: {
          'main@': {
            templateUrl: 'app/about/test.html',
            controller: 'TestController'
          }
        }
      });
//  }
});

angular.module('about').controller('aboutController', function ($scope, $http) {

  $http.get('volunteers.json').then(function (res) {
    var volunteers = res.data.aVolunteer;
    var top = [];
    var region = [];
    var sortedVol = [];
    var city = [];
    var volList = [];
    angular.forEach(volunteers, function (volunteer) {
      if(volunteer.sGroup.sID === "main") {
        top.push(volunteer);
      } else if (volunteer.sGroup.sID === "region") {
        region.push(volunteer);
      }

    });
    angular.forEach(region, function (volunteer) {
      var found = false;
      for(i = 0; i < sortedVol.length; ++i) {
        if (sortedVol[i][0].regionName === volunteer.sGroup.type[0].region[0].sName) {
          sortedVol[i].push(volunteer);
          found = true;
          break;
        }
      }
      if (!found && volunteer.sGroup.type[0].region[0].sName != "") {
        sortedVol.push([{regionName : volunteer.sGroup.type[0].region[0].sName}, volunteer]);
      }
    });

    angular.forEach(sortedVol, function (volunteers) {
      volunteers.cities = [];
        angular.forEach(volunteers, function (volunteer) {
        var found = false;
        for(i = 0; i < city.length; ++i) {
          if (city[i][0].cityName === volunteer.sGroup.type[0].region[0].city[0].sName) {
            city[i].push(volunteer);
            found = true;
            break;
          }
        }

        if (!found && volunteer.sGroup && volunteer.sGroup.type[0].region[0].city[0].sID != "") {
          city.push([{cityName : volunteer.sGroup.type[0].region[0].city[0].sName}, volunteer]);
        } else if(!found && volunteer.regionName) {
            city.push([{regionName : volunteer.regionName}])
        }
      });
      volList.push(city);
      city = [];
    });
    $scope.topVolunteers = top;
    $scope.regionVolunteers = volList;

  });

  $scope.checkForEmpty = function () {
    var el = document.querySelectorAll('.content-ul');
    for(i=0; i<el.length; i++) {
      if(el[i].childElementCount === 0) {
        el[i].parentNode.style.display = 'none'
      } else {
        el[i].parentNode.style.display = 'block'
      }
    }
  };
});
