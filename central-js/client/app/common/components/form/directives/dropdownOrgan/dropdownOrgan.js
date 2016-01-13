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
            console.log("attr.sName="+attr.sName+",currentKey="+currentKey);
            if (angular.isDefined(scope.formData.params[attr.sName]) && currentKey != attr.sName){
              console.log("isDefined,attr.sValue="+attr.sValue+",scope.formData.params[attr.sName].type="+scope.formData.params[attr.sName].type );
              if(scope.formData.params[attr.sName].type === "enum" && attr.sValue.substr(0,1)==="["){
                  var sa=attr.sValue;
                  sa=sa.substr(1);
                  sa=sa.substr(0,sa.length-1);
                  console.log("sa="+sa);
                  var as=sa.split(",");
                  var a=[];
                  var n=0;
                  angular.forEach(as, function(s){
                      var o={id: n+"", name: s+""};
                      a=a.concat([o]);
                      /*enumValues: [{id: "attr1_post", name: "через національного оператора поштового зв'язку"},…]
                            0: {id: "attr1_post", name: "через національного оператора поштового зв'язку"}
                            id: "attr1_post"
                            name: "через національного оператора поштового зв'язку"
                            1: {id: "attr2_bank", name: "на рахунок у банку"}
                            id: "attr2_bank"
                            name: "на рахунок у банку"*/
                      n++;
                  });
                  console.log("a="+a);
                  console.log("scope.formData.params[attr.sName].enumValues="+scope.formData.params[attr.sName].enumValues);
                  if(scope.formData.params[attr.sName].enumValues!==a){
                      console.log("<>");
                    scope.formData.params[attr.sName].enumValues = a;
                  }
                  
                  //as
              }else{
                scope.formData.params[attr.sName].value = attr.sValue || "";
              }
          }
          });
          $timeout(function(){
            attributesApplying = false;
          })
        });
      };

      angular.forEach(Object.keys(scope.formData.params), function (key) {
        scope.$watch('formData.params.' + key + '.value', function () {
          if (scope.ngModel !== null && scope.ngModel !== '0' && scope.ngModel.length > 0 && !attributesApplying)
            loadAttributesData(key);
        })
      });
    }
  };
});
