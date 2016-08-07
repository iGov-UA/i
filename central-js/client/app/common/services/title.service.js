angular.module('app').service('TitleChangeService', function () {
  var title = 'iGov – Портал державних послуг';
  return {
    title: function() {
      return title;
    },
    setTitle: function(newTitle) {
      $('title').html(newTitle + ' / ' + title)
    },
    defaultTitle: function () {
      $('title').html(title)
    }
  };
});
