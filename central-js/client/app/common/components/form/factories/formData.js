angular.module('app').factory('FormDataFactory', function (ParameterFactory, DatepickerFactory, SignFactory, FileFactory,
  ScanFactory, BankIDDocumentsFactory, BankIDAddressesFactory, CountryService, ActivitiService, $q, autocompletesDataFactory) {
  var FormDataFactory = function () {
    this.processDefinitionId = null;
    this.factories = [DatepickerFactory, SignFactory, FileFactory, ParameterFactory];
    this.fields = {};
    this.params = {};
  };

  FormDataFactory.prototype.initializeParamsOnly = function (ActivitiForm) {
    this.processDefinitionId = ActivitiForm.processDefinitionId;
    for (var key in ActivitiForm.formProperties) {
      if(ActivitiForm.formProperties.hasOwnProperty(key)){
        var property = ActivitiForm.formProperties[key];
        initializeWithFactory(this.params, this.factories, property);
        fillAutoCompletes(property);
      }
    }
    this.params.bReferent = new ParameterFactory;
    angular.extend(this.params.bReferent, {
      "id": "bReferent",
      "name": "Referent",
      "type": "invisible",
      "value": false,
      "readable": true,
      "writable": true,
      "required": false,
      "datePattern":null,
      "enumValues":[]
    });
  };

  FormDataFactory.prototype.initialize = function (ActivitiForm, BankIDAccount, oServiceData) {
    var self = this;
    self.initializeParamsOnly(ActivitiForm);
    setBankIDAccount(self, BankIDAccount);
    if(self.params.sID_Place_UA){
      self.params.sID_Place_UA.value = oServiceData.oPlace.sID_UA;
    }

    return $q.all([fillInCountryInformation(self),
      uploadScansFromBankID(self, oServiceData)])
      .then(function(results){
        return self;
      });
  };

  var initializeWithFactory = function (params, factories, property) {
    var result = factories.filter(function (factory) {
      return factory.prototype.isFit(property);
    });
    if (result.length > 0) {
      params[property.id] = result[0].prototype.createFactory();
      params[property.id].value = property.value;
      params[property.id].required = property.required;
      params[property.id].writable = property.hasOwnProperty('writable') ? property.writable : true;
    }
  };

  var fillInCountryInformation = function (formData){
    //TODO prepare all promises and use $q.all
    var field;
    if (formData.isResident()) {
      field = formData.getField('sID_Country');
      return $q.when(field ? CountryService.getCountries().then(function (list) {
        var searchResult = list.filter(function(country){return country.sNameShort_UA === field.value});
        if(searchResult.length > 0){
          return searchResult[0]
        } else {
          return null
        }
      }) : null).then(function(valueForField){
        if(valueForField){
          field.value = valueForField;
        }
      });
    } else {
      field = formData.getField('bankIdsID_Country');
      return $q.when(field ? CountryService.getCountryBy_sID_Two(field.value).then(function (response) {
        return response.data.sNameShort_UA;
      }) : null).then(function(valueForField){
        if(valueForField){
          field.value = valueForField;
        }
      });
    }
  };

  var fillAutoCompletes = function (property) {
    var match;
    if (((property.type == 'string' || property.type == 'select')
      && (match = property.id.match(/^s(Currency|ObjectCustoms|SubjectOrganJoinTax|ObjectEarthTarget|Country|ID_SubjectActionKVED|ID_ObjectPlace_UA)(_(\d+))?/)))
        ||(property.type == 'select' && (match = property.id.match(/^s(Country)(_(\d+))?/)))) {
      if (autocompletesDataFactory[match[1]]) {
        property.type = 'select';
        property.selectType = 'autocomplete';
        property.autocompleteName = match[1];
        if (match[2])
          property.autocompleteName += match[2];
        property.autocompleteData = autocompletesDataFactory[match[1]];
      }
    }
  };

  FormDataFactory.prototype.getSignField = function () {
    for (var key in this.params) {
      var param = this.params[key];
      if(param instanceof SignFactory){
        return param;
      }
    }
    return null;
  };

  function setBankIDAccount(formData, BankIDAccount) {
    var self = formData;
    return angular.forEach(BankIDAccount.customer, function (oValue, sKey) {
      switch (sKey) {
        case 'scans':
          var sFieldName;
          angular.forEach(oValue, function (scan) {
            sFieldName = ScanFactory.prototype.getName(scan.type);
            if (self.hasParam(sFieldName)) {
              self.params[sFieldName] = angular.extend(new ScanFactory(), self.params[sFieldName]);
              self.params[sFieldName].setScan(scan);
            }
          });
          break;
        case 'documents':
          var aDocument = new BankIDDocumentsFactory();
          aDocument.initialize(oValue);

          angular.forEach(aDocument.list, function (document) {
            var sFieldName = null;
            switch (document.type) {
              case 'passport':
                sFieldName = 'bankIdPassport';
            }
            if (sFieldName === null) {
              return;
            }
            if (self.hasParam(sFieldName)) {
              self.fields[sFieldName] = true;
              self.params[sFieldName].value = aDocument.getPassport();
            }
          }, this);
          break;

        case 'addresses':
          var aAddress = new BankIDAddressesFactory();
          aAddress.initialize(oValue);

          angular.forEach(aAddress.list, function (document) {
            var sFieldName = null;
            switch (document.type) {
              case 'factual':
                sFieldName = 'bankIdAddressFactual';
                if (self.hasParam(sFieldName)) {
                  self.fields[sFieldName] = true;
                  self.params[sFieldName].value = aAddress.getAddress();
                }
                sFieldName = 'bankIdsID_Country';
                if (self.hasParam(sFieldName)) {
                  self.fields[sFieldName] = true;
                  self.params[sFieldName].value = aAddress.getCountyCode();
                }


                sFieldName = 'bankIdAddressFactual_country';
                if (self.hasParam(sFieldName)) {
                  self.fields[sFieldName] = true;
                  self.params[sFieldName].value = aAddress.getCountry();
                }
                sFieldName = 'bankIdAddressFactual_state';
                if (self.hasParam(sFieldName)) {
                  self.fields[sFieldName] = true;
                  self.params[sFieldName].value = aAddress.getState();
                }
                sFieldName = 'bankIdAddressFactual_area';
                if (self.hasParam(sFieldName)) {
                  self.fields[sFieldName] = true;
                  self.params[sFieldName].value = aAddress.getArea();
                }
                sFieldName = 'bankIdAddressFactual_city';
                if (self.hasParam(sFieldName)) {
                  self.fields[sFieldName] = true;
                  self.params[sFieldName].value = aAddress.getCity();
                }
                sFieldName = 'bankIdAddressFactual_street';
                if (self.hasParam(sFieldName)) {
                  self.fields[sFieldName] = true;
                  self.params[sFieldName].value = aAddress.getStreet();
                }
                sFieldName = 'bankIdAddressFactual_houseNo';
                if (self.hasParam(sFieldName)) {
                  self.fields[sFieldName] = true;
                  self.params[sFieldName].value = aAddress.getHouseNo();
                }
                sFieldName = 'bankIdAddressFactual_flatNo';
                if (self.hasParam(sFieldName)) {
                  self.fields[sFieldName] = true;
                  self.params[sFieldName].value = aAddress.getFlatNo();
                }


                break;
            }
            if (sFieldName === null) {
              return;
            }

          }, this);
          break;

        default:
          var sFieldName = 'bankId' + sKey;

          if (self.hasParam(sFieldName)) {
            self.fields[sFieldName] = true;
            self.params[sFieldName].value = oValue;
          }
          break;
      }
    }, this);
  }

  function uploadScansFromBankID (formData, oServiceData) {
    var self = formData;
    var paramsForUpload = [];
    for (var key in self.params) {
      var param = self.params[key];
      if (param instanceof ScanFactory && !param.value) {
        paramsForUpload.push({key: key, scan: param.getScan()});
      }
    }

    var prepareForLoading = function (paramsForUpload) {
      paramsForUpload.forEach(function (paramForUpload) {
        self.params[paramForUpload.key].loading();
      });
    };

    var backToFileAll = function (paramsForUpload) {
      paramsForUpload.forEach(function (paramForUpload) {
        backToFile(paramForUpload.key);
      });
    };

    var backToFile = function (key) {
      var oldParam = self.params[key];
      self.params[key] = new FileFactory();
      self.params[key].writable = oldParam.writable;
      self.params[key].required = oldParam.required;
    };

    var populateWithValue = function (key, fileID) {
      self.params[key].loaded(fileID);
    };

    function uploadScans() {
      prepareForLoading(paramsForUpload);
      return ActivitiService.autoUploadScans(oServiceData, paramsForUpload)
        .then(function (uploadResults) {
          uploadResults.forEach(function (uploadResult) {
            if (!uploadResult.error) {
              populateWithValue(uploadResult.scanField.key, uploadResult.fileID);
            } else {
              backToFile(uploadResult.scanField.key);
            }
          });
          return {uploadScans: true};
        }).catch(function () {
          backToFileAll(paramsForUpload);
          return {uploadScans: false};
        });
    }

    return $q.when(paramsForUpload.length > 0 ? uploadScans() : {uploadScans: false});
  }

  FormDataFactory.prototype.getField = function(fieldID){
    var field = null;
    var self = this;
    for(var fieldKey in self){
      if(self.hasOwnProperty(fieldKey) && fieldKey === fieldID){
        field = self[fieldKey];
        break;
      }
    }
    return field;
  };

  FormDataFactory.prototype.isResident = function(){
    var self = this;
    var isResident = false;
    for(var key in self.params){
      if(self.params.hasOwnProperty(key)){
        if(self.params[key].id === 'resident') {
          isResident = params[key].value ? true : false;
        }
      }
    }
    return isResident;
  };

  FormDataFactory.prototype.hasParam = function (param) {
    return this.params.hasOwnProperty(param);
  };

  FormDataFactory.prototype.isSignNeeded = function () {
    return this.getSignField() !== null && !this.isAlreadySigned();
  };

  FormDataFactory.prototype.isSignNeededRequired = function () {//aFormProperties
    return this.getSignField() && this.getSignField() !== null && this.getSignField().required;
  };

  FormDataFactory.prototype.isAlreadySigned = function(){
    var field = this.getSignField();
    return field && field.value;
  };

  FormDataFactory.prototype.setFile = function (name, file) {
    var parameter = this.params[name];
    parameter.removeAll();
    parameter.addFiles([file]);
  };

  FormDataFactory.prototype.setFiles = function (name, files) {
    var parameter = this.params[name];
    parameter.removeAll();
    parameter.addFiles(files);
  };

  FormDataFactory.prototype.addFile = function (name, file) {
    var parameter = this.params[name];
    parameter.addFiles([file]);
  };

  FormDataFactory.prototype.addFiles = function (name, files) {
    var parameter = this.params[name];
    parameter.addFiles(files);
  };

  FormDataFactory.prototype.getRequestObject = function () {
    var data = {
      processDefinitionId: this.processDefinitionId,
      params: {}
    };
    for (var key in this.params) {
      var param = this.params[key];
      if (param.writable || param.required) {
        data.params[key] = param.get();
      }
    }
    return data;
  };

  return FormDataFactory;
});
