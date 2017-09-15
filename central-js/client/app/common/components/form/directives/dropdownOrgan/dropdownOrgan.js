angular.module('app').directive('dropdownOrgan', function (OrganListFactory, $http, $timeout) {
  return {
    restrict: 'EA',
    templateUrl: 'app/common/components/form/directives/dropdownOrgan/dropdownOrgan.html',
    scope: {
      ngModel: "=",
      serviceData: "=",
      ngRequired: "=",
      ngDisabled: "=",
      formDataProperty: "=",
      activitiForm: "=",
      formData: "=",
      name: "="
    },
    link: function (scope) {
      // init organ list for organ select
      scope.organList = new OrganListFactory(scope.serviceData);
      scope.loadOrganList = function (search) {
        return scope.organList.load(scope.serviceData, search);
      };
      scope.onSelectOrganList = function (organ) {
        scope.ngModel = organ.sID_Public;
        scope.formDataProperty.nID = organ.nID;
        scope.organList.typeahead.model = organ.sNameUa;
      };
      scope.organList.reset();
      scope.organList.initialize();
      scope.organList.load(scope.serviceData, null).then(function (regions) {
        scope.organList.initialize(regions);
      });

      var getAttributesDataObject = function () {
        var result = {};
        angular.forEach(scope.formData.params, function (param, key) {
          result[key] = param.value;
        });
        return result;
      };

      var getSelectedRegion = function() {
        if(scope.organList.dropdown.list){
          for (var i=0;i<scope.organList.dropdown.list.length;i++) {
            var region = scope.organList.dropdown.list[i];
            if (region.sID_Public == scope.ngModel)
              return region;
          }
        }else{
          return {};
        }

      };

      var attributesApplying = false;

      var loadAttributesData = function (currentKey) {
        var selectedRegion = getSelectedRegion();
        if(selectedRegion.nID) {

          $http.post('./api/subject/organs/attributes/' + scope.serviceData.oSubject_Operator.nID + '/' + selectedRegion.nID,
            getAttributesDataObject()).success(function (attributes) {
            attributesApplying = true;
            angular.forEach(attributes, function (attr) {
              if (attr.sValue && attr.sValue !== null && attr.sValue.substr(0, 1) === "[") {
                var n = 0;
                if (scope.activitiForm && scope.activitiForm !== null) {
                  if (scope.activitiForm.formProperties && scope.activitiForm.formProperties !== null) {
                    angular.forEach(scope.activitiForm.formProperties, function (oProperty) {
                      if (oProperty.id === attr.sName && oProperty.type === "enum") {
                        var sa = attr.sValue;
                        sa = sa.substr(1);
                        sa = sa.substr(0, sa.length - 1);
                        var as = sa.split(",");
                        var a = [];
                        var nItem = 0;
                        angular.forEach(as, function (s) {
                          if (s.substr(0, 1) === "\"") {
                            s = s.substr(1);
                          }
                          if (s.substr(s.length - 1, 1) === "\"") {
                            s = s.substr(0, s.length - 1);
                          }
                          var o = {id: nItem + "", name: s};
                          a = a.concat([o]);
                          /*enumValues: [{id: "attr1_post", name: "через національного оператора поштового зв'язку"},…]
                           0: {id: "attr1_post", name: "через національного оператора поштового зв'язку"}
                           id: "attr1_post"
                           name: "через національного оператора поштового зв'язку"
                           1: {id: "attr2_bank", name: "на рахунок у банку"}
                           id: "attr2_bank"
                           name: "на рахунок у банку"*/
                          nItem++;
                        });
                        if (oProperty.enumValues) {
                          oProperty.bVariable = true;
                          oProperty.enumValues = a;
                        } else {
                          oProperty.enumValues = [];
                        }
                      }
                      n++;
                    });
                  }
                }
              } else if (angular.isDefined(scope.formData.params[attr.sName]) && currentKey != attr.sName) {
                scope.formData.params[attr.sName].value = attr.sValue || "";
              }
            });
            $timeout(function () {
              attributesApplying = false;
            })
          });
        }
      };

      angular.forEach(Object.keys(scope.formData.params), function (key) {
        scope.$watch('formData.params.' + key + '.value', function () {
          if (scope.ngModel !== null && scope.ngModel !== undefined && scope.ngModel !== '0' && scope.ngModel.length > 0 && !attributesApplying){
              loadAttributesData(key);
          }
        });
      });
    }
  };
});
