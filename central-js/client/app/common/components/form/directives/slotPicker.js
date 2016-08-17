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

      var nSlotsKey = 'nSlots_' + scope.property.id;
      var nSlotsParam = scope.formData.params[nSlotsKey];
      scope.$watch('selected.slot', function(newValue) {
        if (scope.property.id.indexOf('_DMS') > 0) {
          if(newValue){
            var data = {
              nID_Server: scope.serviceData.nID_Server,
              nID_Service_Private: scope.formData.params.nID_Service_Private.value,
              sDateTime: scope.selected.date.sDate + " " + newValue.sTime,
              sSubjectFamily: scope.formData.params.bankIdlastName.value,
              sSubjectName: scope.formData.params.bankIdfirstName.value,
              sSubjectSurname: scope.formData.params.bankIdmiddleName.value || '',
              sSubjectPassport: getPasportLastFourNumbers(scope.formData.params.bankIdPassport.value),
              sSubjectPhone: scope.formData.params.phone.value || ''
            };
            $http.post('/api/service/flow/DMS/setSlotHold', data).
            success(function(data, status, headers, config) {
              scope.ngModel = JSON.stringify({
                reserved_to: data.reserved_to,
                reserve_id: data.reserve_id,
                interval: data.interval
              });
              console.info('Reserved slot: ' + angular.toJson(data));
            }).
            error(function(data, status, headers, config) {
              console.error('Error reserved slot ' + angular.toJson(data));
            });
          }
        } else {
          if (newValue) {
            var setFlowUrl = '/api/service/flow/set/' + newValue.nID + '?nID_Server=' + scope.serviceData.nID_Server;
            if (nSlotsParam) {
              var nSlots = parseInt(nSlotsParam.value) || 0;
              if (nSlots > 1)
                setFlowUrl += '&nSlots=' + nSlots;
            }
            //$http.post('/api/service/flow/set/' + newValue.nID + '?sURL=' + scope.serviceData.sURL).then(function(response) {
            $http.post(setFlowUrl).then(function (response) {
              scope.ngModel = JSON.stringify({
                nID_FlowSlotTicket: response.data.nID_Ticket,
                sDate: scope.selected.date.sDate + ' ' + scope.selected.slot.sTime + ':00.00'
              });
            }, function () {
              scope.selected.date.aSlot.splice(scope.selected.date.aSlot.indexOf(scope.selected.slot), 1);
              scope.selected.slot = null;
              dialogs.error('Помилка', 'Неможливо вибрати час. Спробуйте обрати інший або пізніше, будь ласка');
            });
          }
        }

      });

      function getPasportLastFourNumbers(str) {
        if(!str || str === "") return "";
        return str.replace(new RegExp(/\s+/g), ' ').match(new RegExp(/\S{2} {0,1}\d{6}/gi))[0].match(new RegExp(/\d{4,4}$/))[0];
      }

      scope.unreadyRequestDMS = function () {
        if (this.property.id.indexOf('_DMS') > 0){
          return this.$parent.$parent.$parent.$parent.$parent.form.phone.$invalid ||
            (!scope.formData.params.bankIdlastName || scope.formData.params.bankIdlastName.value === '') ||
            (!scope.formData.params.bankIdfirstName || scope.formData.params.bankIdfirstName.value === '') ||
            (getPasportLastFourNumbers(scope.formData.params.bankIdPassport.value).length != 4);
        } else {
          return false;
        }
      };

      scope.slotsData = {};
      scope.slotsLoading = true;

      var departmentProperty = 'nID_Department_' + scope.property.id;
      var departmentParam = scope.formData.params[departmentProperty];

      scope.loadList = function(){

        scope.slotsLoading = true;
        var data = {};
        var sURL = '';

        if (this.property.id.indexOf('_DMS') > 0){

          data = {
            nID_Server: scope.serviceData.nID_Server,
            nID_Service_Private: this.formData.params.nID_Service_Private.value
          };
          sURL = '/api/service/flow/DMS/getSlots';

        } else {

          data = {
            nID_Server: scope.serviceData.nID_Server,
            nID_Service: (scope && scope.service && scope.service!==null ? scope.service.nID : null)
          };
          if (departmentParam) {
            if (!departmentParam.value) {
              return false;
            } else {
              data.nID_SubjectOrganDepartment = departmentParam.value;
            }
          }
          if (nSlotsParam && parseInt(nSlotsParam.value) > 1) {
            data.nSlots = nSlotsParam.value;
          }
          sURL = '/api/service/flow/' + scope.serviceData.nID;

        }

        return $http.get(sURL, {params:data}).then(function(response) {
          if (scope.property.id.indexOf('_DMS') > 0){
            scope.slotsData = convertSlotsDataDMS(response.data);
          } else {
            scope.slotsData = response.data;
          }
          scope.slotsLoading = false;
        });
      };

      function convertSlotsDataDMS(data) {
        var result = {
          aDay: []
        };
        var nSlotID = 1;
        for (var sDate in data) if (data.hasOwnProperty(sDate)) {
          result.aDay.push({
            aSlot: [],
            //bHasFree : true,
            sDate: sDate
          });
          angular.forEach(data[sDate], function (slot) {
            result.aDay[result.aDay.length - 1].aSlot.push({
              bFree: true,
              nID: nSlotID,
              nMinutes: slot.t_length,
              sTime: slot.time
            });
            nSlotID++;
          });
          result.aDay[result.aDay.length - 1].bHasFree = result.aDay[result.aDay.length - 1].aSlot.length > 0;
        }
        return result;
      }

      scope.$watch('formData.params.' + departmentProperty + '.value', function (newValue) {
        resetData();
        scope.loadList();
      });

      scope.$watch('formData.params.' + nSlotsKey + '.value', function (newValue) {
        resetData();
        scope.loadList();
      });

      scope.loadList();
    }
  }
});
