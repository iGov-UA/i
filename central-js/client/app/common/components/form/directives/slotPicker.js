angular.module('app').directive('slotPicker', function($http, dialogs, ErrorsFactory) {

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

      scope.getSelectorsBpAndFieldId = function (field) {
        return this.serviceData.oData.processDefinitionId.split(':')[0] + "_--_" + field.property.id + "_--_"
      };

      var resetData = function()
      {
        scope.slotsData = {};
        scope.selected.date = null;
        scope.selected.slot = null;
        scope.ngModel = null;
        scope.formData.params[scope.property.id].value = null;
        var formObj = scope.$parent.form;
        if(formObj[scope.property.id]){
          formObj[scope.property.id].$viewValue = null;
          formObj[scope.property.id].$modelValue = null;
        }
      };

      var sID_Type_ID = 'sID_Type_' + scope.property.id;
      var nID_ServiceCustomPrivate_ID = 'nID_ServiceCustomPrivate_' + scope.property.id;
      var isQueueDataType = {
        iGov: !scope.formData.params[sID_Type_ID] || (scope.formData.params[sID_Type_ID] && (scope.formData.params[sID_Type_ID].value === 'iGov' || scope.formData.params[sID_Type_ID].value === '')),
        DMS: scope.formData.params[sID_Type_ID] && scope.formData.params[sID_Type_ID].value === 'DMS'
      };

      function isInvalidServiceCustomPrivate() {
        if (!scope.formData.params[nID_ServiceCustomPrivate_ID] ||
          scope.formData.params[nID_ServiceCustomPrivate_ID].value === null ||
          scope.formData.params[nID_ServiceCustomPrivate_ID].value === ''){
          console.warn('Field ' + nID_ServiceCustomPrivate_ID + ' is EMPTY');
          return true;
        }
        return false;
      }

      var nSlotsKey = 'nSlots_' + scope.property.id;
      var nSlotsParam = scope.formData.params[nSlotsKey];
      var nDiffDaysProperty = 'nDiffDays_' + scope.property.id;
      var nDiffDaysParam = scope.formData.params[nDiffDaysProperty];
      var nDiffDaysForStartDateProperty = 'nDiffDaysForStartDate_' + scope.property.id;
      var nDiffDaysForStartDateParam = scope.formData.params[nDiffDaysForStartDateProperty];
      function selectedSlot(newValue) {
        if (isQueueDataType.DMS) {
          if(newValue){
            if (isInvalidServiceCustomPrivate()) return;
            var data = {
              nID_Server: scope.serviceData.nID_Server,
              nID_Service_Private: scope.formData.params[nID_ServiceCustomPrivate_ID].value,
              sDateTime: scope.selected.date.sDate + " " + newValue.sTime,
              sSubjectFamily: scope.formData.params.bankIdlastName.value,
              sSubjectName: scope.formData.params.bankIdfirstName.value,
              sSubjectSurname: scope.formData.params.bankIdmiddleName.value || '',
              sSubjectPassport: scope.formData.params.bankIdPassport.value ? getPasportLastFourNumbers(scope.formData.params.bankIdPassport.value) : '',
              sSubjectPhone: scope.formData.params.phone.value || ''
            };
            $http.post('/api/service/flow/DMS/setSlotHold', data).
            success(function(data, status, headers, config) {
              scope.ngModel = JSON.stringify({
                reserved_to: data.reserved_to,
                reserve_id: data.reserve_id,
                interval: data.interval
              });
              if(scope.$parent.slotsCache.showConfirm){
                dialogs.notify('Зарезервовано', 'Талон електронної черги зарезервовано');
              };
              console.info('Reserved slot: ' + angular.toJson(data));
            }).
            error(function(data, status, headers, config) {
              console.error('Error reserved slot ' + angular.toJson(data));
              var err = data.message.split(": response=");
              if(data.message.indexOf('api.cherg.net') >= 0 && err[1]){
                var needReload = false;
                if(data.message.indexOf('Время уже занято') >= 0 || data.message.indexOf('Обраний Вами час вже заброньовано') >= 0){
                  needReload = true;
                  dialogs.error('Помилка', 'Обраний Вами час вже недоступний. Повторіть, будь ласка, спробу пізніше або оберіть інший час та дату.')
                } else {
                  needReload = true;
                  dialogs.error('Помилка', err[1])
                }
                scope.selected.slot = null;
                if(needReload){
                  scope.loadList();
                }
              } else {
                dialogs.error('Помилка', data.message);
              }
            });
          }
        } else if (isQueueDataType.iGov) {
          if (newValue) {
            var baseURL = '/api/service/flow/set/';
            var setFlowUrl = baseURL + newValue.nID + '?nID_Server=' + scope.serviceData.nID_Server;
            if (nSlotsParam) {
              var nSlots = parseInt(nSlotsParam.value) || 0;
              if (nSlots > 1)
                setFlowUrl += '&nSlots=' + nSlots;
            }
            if(checkParamsOfSlot(newValue.nID)){

            } else {
              resetData();
              scope.loadList();
              scope.$parent.slotsCache.showConfirm = true;
              dialogs.error('Помилка', 'Під час реєстрації талону електронної черги, були змінені реєстраційні дані. Будь ласка, повторіть свій вибір дати та часу і дочекайтесь підтвердження успішної реєстрації талону.');
              return;
            }
            $http.post(setFlowUrl).then(function (response) {
              scope.ngModel = JSON.stringify({
                sID_Type: "iGov",
                nID_FlowSlotTicket: response.data.nID_Ticket,
                sDate: scope.selected.date.sDate + ' ' + scope.selected.slot.sTime + ':00.00'
              });
              var slotId = response.config.url.substring(baseURL.length, response.config.url.indexOf('?'));
              scope.$parent.slotsCache.iGov[response.data.nID_Ticket] = scope.$parent.slotsCache.loadedList[slotId];
              if(scope.$parent.slotsCache.showConfirm){
                dialogs.notify('Зареєстровано', 'Талон електронної черги зареєстровано');
              }
            }, function () {
              scope.selected.date.aSlot.splice(scope.selected.date.aSlot.indexOf(scope.selected.slot), 1);
              scope.selected.slot = null;
              scope.$parent.slotsCache.showConfirm = true;
              dialogs.error('Помилка', 'Неможливо вибрати час. Спробуйте обрати інший або пізніше, будь ласка');
            });
          }
        }
      }
      function checkParamsOfSlot(slotId){
        var bValid = true;
        if(!scope.$parent.slotsCache.loadedList[slotId]){
          return false;
        }
        bValid = bValid && scope.$parent.slotsCache.loadedList[slotId].nID_Server === scope.serviceData.nID_Server;
        if(bValid && scope.service && scope.service.nID && scope.$parent.slotsCache.loadedList[slotId].nID_Service){
          bValid = bValid && (""+scope.service.nID) === (""+scope.$parent.slotsCache.loadedList[slotId].nID_Service);
        }
        if(bValid && scope.formData.params.sID_Public_SubjectOrganJoin && scope.formData.params.sID_Public_SubjectOrganJoin.nID && scope.$parent.slotsCache.loadedList[slotId].sID_Public_SubjectOrganJoin){
          bValid = bValid && (""+scope.formData.params.sID_Public_SubjectOrganJoin.nID) === (""+scope.$parent.slotsCache.loadedList[slotId].sID_Public_SubjectOrganJoin);
        }
        if(bValid && scope.formData.params.nID_SubjectOrganDepartment && scope.formData.params.nID_SubjectOrganDepartment.value && scope.$parent.slotsCache.loadedList[slotId].nID_SubjectOrganDepartment){
          bValid = bValid && (""+scope.formData.params.nID_SubjectOrganDepartment.value) === (""+scope.$parent.slotsCache.loadedList[slotId].nID_SubjectOrganDepartment);
        }
        if(bValid && nSlotsParam && nSlotsParam.value && scope.$parent.slotsCache.loadedList[slotId].nSlots){
          bValid = bValid && (""+nSlotsParam.value) === (""+scope.$parent.slotsCache.loadedList[slotId].nSlots);
        }
        if(bValid && nDiffDaysParam && nDiffDaysParam.value && scope.$parent.slotsCache.loadedList[slotId].nDiffDays){
          bValid = bValid && (""+nDiffDaysParam.value) === (""+scope.$parent.slotsCache.loadedList[slotId].nDiffDays);
        }
        if(bValid && nDiffDaysForStartDateParam && nDiffDaysForStartDateParam.value && scope.$parent.slotsCache.loadedList[slotId].nDiffDaysForStartDate){
          bValid = bValid && (""+nDiffDaysForStartDateParam.value) === (""+scope.$parent.slotsCache.loadedList[slotId].nDiffDaysForStartDate);
        }
        return bValid;
      }

      function updateReservedSlot(newValue) {
        if (newValue && isQueueDataType.DMS){
          selectedSlot(scope.selected.slot);
        }
      }

      scope.$watch('selected.slot', function(newValue) {
        selectedSlot(newValue);
      });

      function getPasportLastFourNumbers(str) {
        if(!str || str === "") return "";
        return str.replace(new RegExp(/\s+/g), ' ').match(new RegExp(/\S{2} {0,1}\d{6}/gi))[0].match(new RegExp(/\d{4,4}$/))[0];
      }

      scope.unreadyRequestDMS = function () {
        if (isQueueDataType.DMS){
          return this.$parent.$parent.$parent.$parent.$parent.form.phone.$invalid ||
            (!scope.formData.params.bankIdlastName || scope.formData.params.bankIdlastName.value === '') ||
            (!scope.formData.params.bankIdfirstName || scope.formData.params.bankIdfirstName.value === ''); //||
            //(getPasportLastFourNumbers(scope.formData.params.bankIdPassport.value).length != 4);
        } else {
          return false;
        }
      };

      scope.slotsData = {};
      scope.slotsLoading = true;

      var departmentProperty = 'nID_Department_' + scope.property.id;
      var departmentParam = scope.formData.params[departmentProperty];

      scope.loadList = function(){
        var data = {};
        var sURL = '';

        if (isQueueDataType.DMS){

          if (isInvalidServiceCustomPrivate()) {
            return;
          }

          data = {
            nID_Server: scope.serviceData.nID_Server,
            nID_Service_Private: this.formData.params[nID_ServiceCustomPrivate_ID].value
          };
          sURL = '/api/service/flow/DMS/getSlots';

        } else if (isQueueDataType.iGov) {
          data = {
            nID_Server: scope.serviceData.nID_Server,
            nID_Service: (scope && scope.service && scope.service!==null ? scope.service.nID : null)
          };

          if (parseInt(departmentParam.value) > 0 && scope.formData.params.sID_Public_SubjectOrganJoin && scope.formData.params.sID_Public_SubjectOrganJoin.nID){
            console.log('data.nID_SubjectOrganDepartment = ' + parseInt(departmentParam.value) + '; sID_Public_SubjectOrganJoin = ' + scope.formData.params.sID_Public_SubjectOrganJoin.nID);
            var hKey = '' + parseInt(departmentParam.value);
            if(scope.$root.queue.previousOrganJoin[scope.formData.params.sID_Public_SubjectOrganJoin.nID]){
              if(scope.$root.queue.previousOrganJoin[scope.formData.params.sID_Public_SubjectOrganJoin.nID] === departmentParam.value){
                //return;
              }
            } else {
              for(var checkKey in scope.$root.queue.previousOrganJoin) if (scope.$root.queue.previousOrganJoin.hasOwnProperty(checkKey)){
                if (scope.$root.queue.previousOrganJoin[checkKey] === departmentParam.value){
                  return;
                }
              }
              scope.$root.queue.previousOrganJoin[scope.formData.params.sID_Public_SubjectOrganJoin.nID] = departmentParam.value;
            }
            data.sID_Public_SubjectOrganJoin = scope.formData.params.sID_Public_SubjectOrganJoin.nID;
          }

          if (departmentParam) {
            if (parseInt(departmentParam.value) > 0)
              data.nID_SubjectOrganDepartment = departmentParam.value;
            else {
              return;
            }
          }

          if (nSlotsParam && parseInt(nSlotsParam.value) > 1) {
            data.nSlots = nSlotsParam.value;
          }

          if (nDiffDaysParam && parseInt(nDiffDaysParam.value) > 0) {
            data.nDiffDays = nDiffDaysParam.value;
          }
          if (nDiffDaysForStartDateParam && parseInt(nDiffDaysForStartDateParam.value) > 0) {
            data.nDiffDaysForStartDate = nDiffDaysForStartDateParam.value;
          }



          sURL = '/api/service/flow/' + scope.serviceData.nID;
        } else {
          scope.slotsLoading = false;
          ErrorsFactory.push({
            type: 'danger',
            text: 'В полі ' + sID_Type_ID + ' прописаний непідтримуваний тип для поля queueData: ' + scope.formData.params[sID_Type_ID].value
          });
          console.error('slotsData for field id [' + this.property.id + '] not loading');
          return;
        }

        scope.slotsLoading = true;

        return $http.get(sURL, {params:data}).then(function(response) {
          if (isQueueDataType.DMS){
            scope.slotsData = convertSlotsDataDMS(response.data);
          } else if (isQueueDataType.iGov) {
            scope.slotsData = response.data;
            if(response.data.aDay){
              angular.forEach(response.data.aDay, function (day) {
                angular.forEach(day.aSlot, function (slot) {
                  if(slot.nID && !scope.$parent.slotsCache.loadedList[slot.nID]){
                    scope.$parent.slotsCache.loadedList[slot.nID] = {
                      nID_Server: response.config.params.nID_Server,
                      nID_Service: response.config.params.nID_Service,
                      nID_SubjectOrganDepartment: response.config.params.nID_SubjectOrganDepartment,
                      sID_Public_SubjectOrganJoin: response.config.params.sID_Public_SubjectOrganJoin,
                      nSlots: response.config.params.nSlots,
                      nDiffDays: response.config.params.nDiffDays,
                      nDiffDaysForStartDate: response.config.params.nDiffDaysForStartDate,
                      sDepartmentField: departmentProperty
                    }
                  }
                })
              })
            }
          }
          scope.slotsLoading = false;
        });
      };

      function convertSlotsDataDMS(data) {
        var aDay = [];
        var nSlotID = 1;
        for (var sDate in data) if (data.hasOwnProperty(sDate)) {
          aDay.push({
            aSlot: [],
            sDate: sDate
          });
          angular.forEach(data[sDate], function (slot) {
            aDay[aDay.length - 1].aSlot.push({
              bFree: true,
              nID: nSlotID,
              nMinutes: slot.t_length,
              sTime: slot.time
            });
            nSlotID++;
          });
          aDay[aDay.length - 1].bHasFree = aDay[aDay.length - 1].aSlot.length > 0;
        }
        var result = {
          aDay: []
        };
        angular.forEach(aDay, function (day) {
          if(day.aSlot.length > 0){
            result.aDay.push(day);
          }
        });
        result.aDay.sort(function (a, b) {
          return Date.parse(a.sDate) - Date.parse(b.sDate);
        });
        return result;
      }

      if(angular.isDefined(scope.formData.params.sID_Public_SubjectOrganJoin && angular.isDefined(departmentParam))){
        scope.$watch('formData.params.sID_Public_SubjectOrganJoin.value', function (newValue, oldValue) {
          if (newValue == oldValue)
            return;
          resetData();
        });
      }

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

      if (angular.isDefined(nSlotsParam)) {
        scope.$watch('formData.params.' + nSlotsKey + '.value', function (newValue, oldValue) {
          if (newValue == oldValue)
            return;
          resetData();
          scope.loadList();
        });
      }

      if (angular.isDefined(nDiffDaysParam)) {
        scope.$watch('formData.params.' + nDiffDaysProperty + '.value', function (newValue, oldValue) {
          if (newValue == oldValue)
            return;
          resetData();
          scope.loadList();
        });
      }
      if (angular.isDefined(nDiffDaysForStartDateParam)) {
        scope.$watch('formData.params.' + nDiffDaysForStartDateProperty + '.value', function (newValue, oldValue) {
          if (newValue == oldValue)
            return;
          resetData();
          scope.loadList();
        });
      }

      scope.$watch('formData.params.' + nID_ServiceCustomPrivate_ID + '.value', function (newValue, oldValue) {
        if (newValue == oldValue)
          return;
        if (newValue){
          resetData();
          scope.loadList();
        }
      });

      scope.$watch('formData.params.bankIdlastName.value', function (newValue, oldValue) {
        if (newValue == oldValue)
          return;
        updateReservedSlot(newValue);
      });

      scope.$watch('formData.params.bankIdfirstName.value', function (newValue, oldValue) {
        if (newValue == oldValue)
          return;
        updateReservedSlot(newValue);
      });

      scope.$watch('formData.params.bankIdmiddleName.value', function (newValue, oldValue) {
        if (newValue == oldValue)
          return;
        updateReservedSlot(newValue);
      });

      scope.$watch('formData.params.bankIdPassport.value', function (newValue, oldValue) {
        if (newValue == oldValue)
          return;
        updateReservedSlot(newValue);
      });

      scope.$watch('formData.params.phone.value', function (newValue, oldValue) {
        if (newValue == oldValue)
          return;
        updateReservedSlot(newValue);
      });

      scope.$watch('formData.params.' + nDiffDaysProperty + '.value', function (newValue, oldValue) {
        if (newValue == oldValue)
          return;
        resetData();
        scope.loadList();
      });

      scope.$on('reset-slot-picker', function () {
        resetData();
        scope.loadList();
      });

      scope.loadList();
    }
  }
});
