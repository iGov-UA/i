angular.module('app').directive('slotPicker', function($http, dialogs) {
  return {
    restrict: 'EA',
    templateUrl: 'app/common/components/form/directives/slotPicker.html',
    scope: {
      serviceData: "=",
      service: "=",
      ngModel: "=",
      formData: "=",
      property: "="
    },
    link: function(scope) {

      scope.selected = {
        date: null,
        slot: null
      };

      scope.$watch('selected.date', function() {
        scope.selected.slot = null;
      });

      var resetData = function()
      {
        scope.slotsData = {};
        scope.selected.date = null;
        scope.selected.slot = null;
        scope.ngModel = null;
      };

      var nDiffDaysProperty = 'nDiffDays_' + scope.property.id;
      var nDiffDaysParam = scope.formData.params[nDiffDaysProperty];

      var departmentProperty = 'nID_Department_' + scope.property.id;
      var departmentParam = scope.formData.params[departmentProperty];

      scope.$watch('selected.slot', function(newValue) {
        if (newValue) {
          //$http.post('/api/service/flow/set/' + newValue.nID + '?sURL=' + scope.serviceData.sURL).then(function(response) {
          $http.post('/api/service/flow/set/' + newValue.nID + '?nID_Server=' + scope.serviceData.nID_Server).then(function(response) {
            scope.ngModel = JSON.stringify({
              nID_FlowSlotTicket: response.data.nID_Ticket,
              sDate: scope.selected.date.sDate + ' ' + scope.selected.slot.sTime + ':00.00'
            });
          }, function() {
            scope.selected.date.aSlot.splice(scope.selected.date.aSlot.indexOf(scope.selected.slot), 1);
            scope.selected.slot = null;
            dialogs.error('Помилка', 'Неможливо вибрати час. Спробуйте обрати інший або пізніше, будь ласка');
          });
        }
      });

      scope.slotsData = {};
      scope.slotsLoading = true;

      scope.loadList = function(){
        var data = {
          //sURL: scope.serviceData.sURL,
          nID_Server: scope.serviceData.nID_Server,
          nID_Service: (scope && scope.service && scope.service!==null ? scope.service.nID : null)
        };

        if (departmentParam) {
          if (parseInt(departmentParam.value) > 0)
            data.nID_SubjectOrganDepartment = departmentParam.value;
          else return;
        }

        if (nDiffDaysParam && parseInt(nDiffDaysParam.value) > 0) {
          data.nDiffDays = nDiffDaysParam.value;
        }

        scope.slotsLoading = true;

        return $http.get('/api/service/flow/' + scope.serviceData.nID, {params:data}).then(function(response) {
          scope.slotsData = response.data;
          scope.slotsLoading = false;
        });
      };

      if (angular.isDefined(departmentParam)) {
        scope.$watch('formData.params.' + departmentProperty + '.value', function (newValue, oldValue) {
          resetData();
          if (parseInt(newValue) > 0) {
            scope.loadList();
          }
        });
      } else {
        scope.loadList();
      }

      if (angular.isDefined(nDiffDaysParam)) {
        scope.$watch('formData.params.' + nDiffDaysProperty + '.value', function (newValue, oldValue) {
          if (newValue == oldValue)
            return;
          resetData();
          scope.loadList();
        });
      }
    }
  };
});
