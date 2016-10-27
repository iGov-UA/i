'use strict';

angular.module('appBoilerPlate').provider('statesRepository', function StatesRepositoryProvider() {
  var selfProvider = this;
  var findModeRegexp = /(\w*).(\w*).(\w*)(\w*|.*)*/;

  var modeModel = {
    "ternopil": {
      "header": "ternopil.header.html",
      "footer": "ternopil.footer.html",
      "placesID": ['6110100000', '6100000000']
      ,"asOrgan": []
      ,"bSearch": true
      ,"anID_CatalogCategoryShowAll": []
    },
    "kyiv": {
      "header": "kyiv.header.html",
      "footer": "kyiv.footer.html",
      "placesID": ['8000000000', '8000000000']
      ,"asOrgan": []
      ,"bSearch": true
      ,"anID_CatalogCategoryShowAll": []
    },
    "kharkiv": {
      "header": "kharkiv.header.html",
      "footer": "kharkiv.footer.html",
      "placesID": ['6310100000', '6300000000']
      ,"asOrgan": []
      ,"bSearch": true
      ,"anID_CatalogCategoryShowAll": []
    },
    "mvd": {
      "header": "mvd.header.html",
      "footer": "mvd.footer.html",
      "placesID": []
      ,"asOrgan": []
      ,"bSearch": true
      ,"anID_CatalogCategoryShowAll": []
    },
    "alpha_mvd": {
      "header": "mvd.header.html",
      "footer": "mvd.footer.html",
      "placesID": []
      ,"asOrgan": []
      ,"bSearch": true
      ,"anID_CatalogCategoryShowAll": []
    },
    "dfs": {
      "header": "dfs.header.html",
      "footer": "dfs.footer.html",
      "placesID": []
      ,"asOrgan": ['Державна фіскальна служба']
      ,"bSearch": false
      ,"anID_CatalogCategoryShowAll": [1]
    }
  };

  var modes = {
    "ternopil": modeModel.ternopil
    ,"ternopol": modeModel.ternopil
    ,"kyiv": modeModel.kyiv
    ,"kiev": modeModel.kyiv
    ,"kharkiv": modeModel.kharkiv
    ,"kharkov": modeModel.kharkiv
    ,"mvd": modeModel.mvd
    ,"alpha_mvd": modeModel.alpha_mvd
    ,"dfs": modeModel.dfs
  };
  this.init = function (domen) {
    //test.kiev.igov.org.ua

    this.domain = domen;


    if (domen.split(':')[0] !== 'localhost') {
      if (domen.indexOf('kievcity') >= 0) {
        //https://es.kievcity.gov.ua
        this.mode = 'kyiv';
        //this.mode = modes.kyiv;
      }else if (domen.indexOf('alpha-mvd') >= 0) {
        //alpha-mvd.test.igov.org.ua
        this.mode = 'alpha_mvd';
        //this.mode = modes.kyiv;
      }else if (domen.indexOf('mvd') >= 0 || domen.indexOf('mvs') >= 0) {
        //https://es.kievcity.gov.ua
        this.mode = 'mvd';
        //this.mode = modes.kyiv;
      } else if (domen.indexOf('ternopil') >= 0) {
        //ternopil.igov.org.ua
        this.mode = 'ternopil';
      } else if (domen.indexOf('sfs') >= 0) {
        //dfs.igov.org.ua
        this.mode = 'dfs';
      } else {
        var matches = findModeRegexp.exec(domen);
        // проверка на старые домены test & test-version устарела, со временем их нужно будет убрать. добавлена проверка на новые домена.
        if (matches[1] === 'test') {// || matches[1] === 'test-version'
          if (matches[2] === 'version') {
            this.mode = matches[3];
          } else {
            this.mode = matches[2];
          }
        } else if (matches[1] === 'delta'
          || matches[1] === 'alpha'
          || matches[1] === 'beta'
          || matches[1] === 'omega') {
          this.mode = matches[3];
        } else if ( (matches[1] === 'alpha' && matches[2] === 'old')
          || (matches[1] === 'beta' && matches[2] === 'old')) {
          this.mode = matches[4];
        } else {
          this.mode = matches[1];
        }
      }

    } else {
      //this.mode = 'local';
      this.mode = 'kyiv';
    }

  };

  this.isCentral = function () {
    return this.mode === 'local' || this.mode === 'igov';
  };

  this.isKyivCity = function () {
    return this.mode === 'kyiv';
  };

  var getHeader = function (mode) {
    var hdr;
    if (!!modes[mode]) {
      hdr = modes[mode].header;
    } else {
      hdr = 'new.header.html';
    }
    return 'app/header/' + hdr;
  };

  var getFooter = function (mode) {
    var footer;
    if (!!modes[mode]) {
      footer = modes[mode].footer;
    } else {
      footer = 'footer.html';
    }
    return 'app/footer/' + footer;
  };


  this.index = function () {
    return {
      url: '/',
      views: {
        header: {
          templateUrl: getHeader(this.mode),
          controller: 'IndexController'
        },
        'main@': {
          templateUrl: 'app/service/index/new.services.html',
          controller: 'ServiceController'
        },
        footer: {
          templateUrl: getFooter(this.mode),
          controller: 'FooterController'
        }
      }
    };
  };

  var StatesRepository = function (mode, domain) {
    this.mode = mode;
    this.domain = domain;
  };

  StatesRepository.prototype.getIDPlaces = function () {
    if (!!modes[this.mode]) {
      return modes[this.mode].placesID;
    }
    return [];
  };

  StatesRepository.prototype.getOrgan = function () {
    if (!!modes[this.mode]) {
        if(modes[this.mode].asOrgan.length>0){
            return modes[this.mode].asOrgan[0];
        }
    }
    return "";
  };

  StatesRepository.prototype.getOrgans = function () {
    if (!!modes[this.mode]) {
      return modes[this.mode].asOrgan;
    }
    return [];
  };

  StatesRepository.prototype.isSearch = function () {
    if (!!modes[this.mode]) {
      return modes[this.mode].bSearch;
    }
    return true;
  };

  StatesRepository.prototype.isCatalogCategoryShowAll = function (nID) {
    var bAll=false;
    if (!!modes[this.mode] && nID) {
      angular.forEach(modes[this.mode].anID_CatalogCategoryShowAll, function (nID_CatalogCategoryShowAll) {
        if (nID_CatalogCategoryShowAll === nID) {
            bAll=true;
        }
        //return bAll;
      });
      //return modes[this.mode].asCatalogCategoryShowAll;
    }
    return bAll;
  };


  StatesRepository.prototype.getRegion = function (regions) {
    var result = null;
    var placesID = this.getIDPlaces();
    if (placesID) {
      angular.forEach(regions, function (region) {
        if (region.sID_UA == placesID[0]) {
          result = region;
        }
      });
    }
    return result;
  };

  StatesRepository.prototype.getCity = function (cities) {
    var result = null;
    var placesID = this.getIDPlaces();
    if (placesID) {
      angular.forEach(cities, function (city) {
        if (city.sID_UA == placesID[1]) {
          result = city;
        }
      });
    }
    return result;
  };

  StatesRepository.prototype.isCentral = function () {
    return selfProvider.isCentral();
  };

  StatesRepository.prototype.isKyivCity = function () {
    return selfProvider.isKyivCity();
  };

  this.$get = [function StatesRepositoryFactory() {
    return new StatesRepository(this.mode, this.domain);
  }];
});
