angular.module('datepickerService', []).factory('DatepickerFactory', function($filter) {
    var datepicker = function() {
        this.value = null;
        this.format = 'dd/MM/yyyy';
        this.opened = false;
    };

    datepicker.prototype.today = function() {
        this.value = new Date();
    };

    datepicker.prototype.clear = function() {
        this.value = null;
    };

    datepicker.prototype.open = function($event) {
        $event.preventDefault();
        $event.stopPropagation();

        this.opened = true;
    };

    datepicker.prototype.getForTable = function() {
        var convDate = this.value.getFullYear() + '/' + (this.value.getMonth() + 1) + '/' + this.value.getDate() + ' ' + '12:00';
        this.value = new Date(convDate);
    };

    datepicker.prototype.get = function() {
        return $filter('date')(this.value, this.format);
    };

    datepicker.prototype.isFit = function(property){
        return property.type === 'date';
    };

    datepicker.prototype.createFactory = function(){
        return new datepicker();
    };

    return datepicker;
});
