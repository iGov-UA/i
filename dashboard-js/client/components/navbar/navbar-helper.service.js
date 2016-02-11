(function () {
  'use strict';

  angular
    .module('dashboardJsApp')
    .factory('iGovNavbarHelper', iGovNavbarHelperFactory);

  iGovNavbarHelperFactory.$inject = ['Auth', 'tasks'];
  function iGovNavbarHelperFactory(Auth, tasks) {
    var service = {
      areInstrumentsVisible: false,
      auth: Auth,
      isCollapsed: true,
      isTest: false,
      load: load,
      loadTaskCounters: loadTaskCounters,
      menus: [],
    };
    return service;

    function load() {
      service.menus = [{
        title: 'Необроблені',
        type: tasks.filterTypes.unassigned,
        count: 0,
        tab: 'unassigned'
      }, {
        title: 'В роботі',
        type: tasks.filterTypes.selfAssigned,
        count: 0,
        tab: 'selfAssigned'
      }, {
        title: 'Мій розклад',
        type: tasks.filterTypes.tickets,
        count: 0,
        tab: 'tickets'
      }, {
        title: 'Усі',
        type: tasks.filterTypes.all,
        count: 0,
        tab: 'all'
      }, {
        title: 'Історія',
        type: tasks.filterTypes.finished,
        count: 0,
        tab: 'finished'
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
