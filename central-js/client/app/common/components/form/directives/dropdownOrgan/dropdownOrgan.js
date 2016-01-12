angular.module('app').directive('dropdownOrgan', function (OrganListFactory, $http, $timeout) {
  return {
    restrict: 'EA',
    templateUrl: 'app/common/components/form/directives/dropdownOrgan/dropdownOrgan.html',
    scope: {
      ngModel: "=",
      serviceData: "=",
      ngRequired: "=",
      formDataProperty: "=",
      formData: "="
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
        for (var i=0;i<scope.organList.dropdown.list.length;i++) {
          var region = scope.organList.dropdown.list[i];
          if (region.sID_Public == scope.ngModel)
            return region;
        }
      };

      var attributesApplying = false;

      var loadAttributesData = function (currentKey) {
        $http.post('./api/organs/attributes/' + scope.serviceData.oSubject_Operator.nID + '/' + getSelectedRegion().nID,
          getAttributesDataObject()).success(function (attributes) {
          attributesApplying = true;
          angular.forEach(attributes, function(attr){
            if (angular.isDefined(scope.formData.params[attr.sName]) && currentKey != attr.sName)
              scope.formData.params[attr.sName].value = attr.sValue || "";
          });
          $timeout(function(){
            attributesApplying = false;
          })
        });
      };

      angular.forEach(Object.keys(scope.formData.params), function (key) {
        scope.$watch('formData.params.' + key + '.value', function () {
          if (scope.ngModel !== '0' && scope.ngModel.length > 0 && !attributesApplying)
            loadAttributesData(key);
        })
      });
    }
  };
});
