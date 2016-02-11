(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .factory('iGovNavbarHelper', iGovNavbarHelperFactory);

  iGovNavbarHelperFactory.$inject = ['Auth', 'tasks'];
  function iGovNavbarHelperFactory(Auth, tasks) {
    var service = {
      areInstrumentsVisible: false,
      isCollapsed: true,
      isLoggedIn: isLoggedIn,
      isTest: false,
      load: load,
      loadTaskCounters: loadTaskCounters,
      menus: [],
    };
    return service;

    function isLoggedIn() {
      return Auth.isLoggedIn();
    }

    function load() {
      service.menus = [{
        title: 'Необроблені',
        type: tasks.filterTypes.unassigned,
        count: 0
      }, {
        title: 'В роботі',
        type: tasks.filterTypes.selfAssigned,
        count: 0
      }, {
        title: 'Мій розклад',
        type: tasks.filterTypes.tickets,
        count: 0
      }, {
        title: 'Усі',
        type: tasks.filterTypes.all,
        count: 0
      }, {
        title: 'Історія',
        type: tasks.filterTypes.finished,
        count: 0
      }];

      service.loadTaskCounters();
    }

    function loadTaskCounters() {
      _.each(service.menus, function (menu) {
        tasks.list(menu.type)
          .then(function (result) {
            try {
              result = JSON.parse(result);
            } catch (e) {
              result = result;
            }
            menu.count = result.data.length;
          });
      });
    }
  }
})();
