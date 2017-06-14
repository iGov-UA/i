angular.module('dashboardJsApp').service('fieldsService', ['FieldMotionService', function (FieldMotionService) {
  return {
    isFieldVisible : function(item, taskForm) {
      var bVisible = item.id !== 'processName' && (FieldMotionService.FieldMentioned.inShow(item.id) ?
          FieldMotionService.isFieldVisible(item.id, taskForm) : true);
      if(item.options && item.options.hasOwnProperty('bVisible')){
        bVisible = bVisible && item.options['bVisible'];
      }
      return bVisible;
    },

    creationDateFormatted: function (date) {
      if (date){
        var unformatted = date.split(' ')[0];
        var splittedDate = unformatted.split('-');
        return splittedDate[2] + '.' + splittedDate[1] + '.' + splittedDate[0];
      }
    }
  }
}]);
