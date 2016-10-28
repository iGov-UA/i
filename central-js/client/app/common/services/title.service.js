angular.module('app').service('TitleChangeService', function (statesRepository, $rootScope) {

  var title = '';
  if(statesRepository.isKyivCity){
    title = 'Портал державних послуг';
  } else {
    title = 'iGov – Портал державних послуг';
  }
  $rootScope.oMainPageParams = {
    sTitle : title
  };

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
