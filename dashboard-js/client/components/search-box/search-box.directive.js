'use strict';

angular.module('dashboardJsApp').directive('searchBox', ['$window', function ($window) {
  return {
    restrict: 'EA',
    templateUrl: 'components/search-box/search-box.template.html',
    link: function () {
      $('.idoc-search-dropdown').click(function(e) {
        e.stopPropagation();
      });
      $('.close-search-box').on('click', function (e) {
        $('.idoc-search-dropdown').parent().removeClass('open');
      })
    }
  };
}]);
