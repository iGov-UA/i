angular.module('app').service('LabelService', function () {
  return {

    isLabelHasClasses : function (field) {
      if(field && field.name) {
        var hasClass = field.name.split(';');
        return hasClass.length === 3 && hasClass[2].indexOf('labelType') > -1;
      }
    },

    labelStyle : function (field) {
      if(field && field.type === 'label' && field.name) {
        if(this.isLabelHasClasses(field)) {
          var split = field.name.split(';');
          if(split.length === 3) {
            return split[2].indexOf('labelType') !== -1 ? 'igov-' + split[2].split('labelType=')[1] + '-label' : 'igov-info-label';
          } else {
            return 'igov-info-label';
          }
        } else {
         return '';
        }
      }
    }
  }
});
