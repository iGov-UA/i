angular.module('dashboardJsApp').config(function($provide) {
  $provide.decorator('taOptions', ['taRegisterTool', '$delegate', function (taRegisterTool, taOptions) {

    taRegisterTool('backgroundColor', {
      display: "<spectrum-colorpicker class='custom-button-edit' ng-model='color' on-change='!!color && action(color)' format='\"hex\"' options='options'></spectrum-colorpicker>",
      action: function(color){
        if (this.$editor().wrapSelection) {
          return this.$editor().wrapSelection('backColor', color);
        }
      },
      activeState: function(){
        return this.$editor().queryFormatBlockState('backColor');
      },
      options: {
        replacerClassName: 'fa fa-paint-brush', showButtons: false
      },
      color: "#fff"
    });

    taRegisterTool('fontColor', {
      display: "<spectrum-colorpicker class='custom-button-edit' trigger-id='{{trigger}}' ng-model='color' on-change='!!color && action(color)' format='\"hex\"' options='options'></spectrum-colorpicker>",
      action: function(color){
        if (this.$editor().wrapSelection) {
          return this.$editor().wrapSelection('foreColor', color);
        }
      },
      activeState: function(){
        return this.$editor().queryFormatBlockState('foreColor');
      },
      options: {
        replacerClassName: 'fa fa-font', showButtons: false
      },
      color: "#000"
    });

    taOptions.toolbar[1].push('backgroundColor', 'fontColor');
    return taOptions;
  }])
});
