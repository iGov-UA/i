angular.module('app')
  .service('CatalogService', ['$http', '$q', function ($http, $q) {

  var servicesCache = {};

  this.getModeSpecificServices = function (asIDPlacesUA, sFind, bShowEmptyFolders, category, subcat, situation, filter) {
    var asIDPlaceUA = asIDPlacesUA && asIDPlacesUA.length > 0 ? asIDPlacesUA.reduce(function (ids, current, index) {
      return ids + ',' + current;
    }) : null;
    var nID_Category = category || 1;
    var nID_ServiceTag_Root = subcat;
    var nID_ServiceTag_Child = situation;
    if(sFind.length < 3){
      sFind = null;
    }

    if(!category
        && !subcat
        || category
        && !subcat && category !== 'business') {
      // пока есть параметр bNew ввожу доп проверку, после нужно будет убрать
      // пока не реализованы теги нового бизнеса, вернул в проверку старый.
      if(sFind || filter) {
        var data = {
          asIDPlaceUA: asIDPlaceUA,
          sFind: sFind || null,
          bShowEmptyFolders: bShowEmptyFolders,
          nID_Category: nID_Category,
          bNew: true
        };
        return $http.get('./api/catalog/getCatalogTree', {
          params: data,
          data: data
        }).then(function (response) {
          servicesCache = response.data;
          return response.data;
        });
      } else {
        var data = {
          asIDPlaceUA: asIDPlaceUA,
          sFind: sFind || null,
          bShowEmptyFolders: bShowEmptyFolders,
          nID_Category: nID_Category
        };
        return $http.get('./api/catalog/getCatalogTree', {
          params: data,
          data: data
        }).then(function (response) {
          servicesCache = response.data;
          return response.data;
        });
      }
    } else if(nID_Category === 'business'){
      var data = {
        asIDPlaceUA: asIDPlaceUA,
        sFind: sFind || null,
        bShowEmptyFolders: bShowEmptyFolders
      };
      return $http.get('./api/catalog', {
        params: data,
        data: data
      }).then(function (response) {
        servicesCache = response.data;
        return response.data;
      });
    } else {
      // страница подкатегорий
        var data = {
          asIDPlaceUA: asIDPlaceUA,
          sFind: sFind || null,
          bShowEmptyFolders: bShowEmptyFolders,
          nID_Category: nID_Category,
          nID_ServiceTag_Root: nID_ServiceTag_Root,
          nID_ServiceTag_Child: nID_ServiceTag_Child
        };
        return $http.get('./api/catalog/getCatalogTreeTagService', {
          params: data,
          data: data
        }).then(function (response) {
          servicesCache = response.data;
          return response.data;
        });
    }
  };

  this.getCatalogCounts = function(catalog) {
    var catalogCounts = {'0': 0, '1': 0, '2': 0};
    if (catalog === undefined) {
      catalog = servicesCache;
    }

    if(catalog.aService) {
      angular.forEach(catalog.aService, function (aServiceItem) {
        if (typeof (catalogCounts[aServiceItem.nStatus]) == 'undefined') {
          catalogCounts[aServiceItem.nStatus] = 0;
        }
        ++catalogCounts[aServiceItem.nStatus];
      });
    }else if(catalog[0].aService){
      angular.forEach(catalog, function (service) {
        angular.forEach(service.aService, function (aServiceItem) {
          if (typeof (catalogCounts[aServiceItem.nStatus]) == 'undefined') {
            catalogCounts[aServiceItem.nStatus] = 0;
          }
          ++catalogCounts[aServiceItem.nStatus];
        });
      })
    } else {
      angular.forEach(catalog, function(category) {
        angular.forEach(category.aSubcategory, function(subItem) {
          angular.forEach(subItem.aService, function(aServiceItem) {
            if (typeof (catalogCounts[aServiceItem.nStatus]) == 'undefined') {
              catalogCounts[aServiceItem.nStatus] = 0;
            }
            ++catalogCounts[aServiceItem.nStatus];
          });
        });
      });
    }
    return catalogCounts;
  };

  this.getServiceTags = function (sFind) {
    var data = {
      sFind: sFind,
      nID_Category: 1
    };
    return $http.get('./api/catalog/getCatalogTree', {
      params: data,
      data: data
    }).then(function (response) {
        return response.data;
    });
  };

  this.getServiceBusiness = function (sFind) {
    var data = {
      sFind: sFind
    };
    return $http.get('./api/catalog', {
      params: data,
      data: data
    }).then(function (res) {
      return res.data;
    })
  };

  this.getCatalogTreeTag = function (nID_Category, sFind) {
    var data = {
      nID_Category: nID_Category,
      sFind: sFind || null
    };
    return $http.get('./api/catalog/getCatalogTree', {
      params: data,
      data: data
    }).then(function (response) {
      servicesCache = response.data;
      return response.data;
    });
  };

  this.getCatalogTreeTagService = function (category, serviceTag, situation) {
    var data = {
      nID_Category: category,
      nID_ServiceTag_Root: serviceTag,
      nID_ServiceTag_Child: situation || null
    };
    return $http.get('./api/catalog/getCatalogTreeTagService', {
      params: data,
      data: data
    }).then(function (response) {
      servicesCache = response.data;
      return response.data[0];
    })
  };

  this.getOperators = function(catalog) {
    var operators = [];
    if (catalog === undefined) {
      catalog = servicesCache;
    }
    if(catalog.aService) {
      angular.forEach(catalog.aService, function(category) {
        var found = false;
        for (var i = 0; i < operators.length; ++i) {
          if (operators[i].sSubjectOperatorName === category.sSubjectOperatorName) {
            found = true;
            break;
          }
        }
        if (!found && category.sSubjectOperatorName != "") {
          operators.push(category);
        }
      });
    }else if(!catalog.aService && !catalog.aServiceTag_Child) {
      angular.forEach(catalog, function (category) {
        if(category.aSubcategory) {
          angular.forEach(category.aSubcategory, function (subcategory) {
            angular.forEach(subcategory.aService, function (service) {
              var found = false;
              for (var i = 0; i < operators.length; ++i) {
                if (operators[i].sSubjectOperatorName === service.sSubjectOperatorName) {
                  found = true;
                  break;
                }
              }
              if (!found && service.sSubjectOperatorName != "") {
                operators.push(service);
              }
            })
          })
        }
      })
    } else {
      angular.forEach(catalog[0], function(category) {
        var found = false;
        for (var i = 0; i < operators.length; ++i) {
          if (operators[i].sSubjectOperatorName === category.sSubjectOperatorName) {
            found = true;
            break;
          }
        }
        if (!found && category.sSubjectOperatorName != "") {
          operators.push(category);
        }
      });
    }

    return operators;
  };

  function simpleHttpPromise(req, callback) {
    var cb = callback || angular.noop;
    var deferred = $q.defer();

    $http(req).then(
      function(response) {
        deferred.resolve(response.data);
        return cb();
      },
      function(response) {
        deferred.reject(response);
        return cb(response);
      }.bind(this));
    return deferred.promise;
  }

  // используем старый поиск для бизнеса, пока теги не реализированы, после - удалить.
  this.getOperatorsOld = function(catalog) {
    var operators = [];
    if (catalog === undefined) {
      catalog = servicesCache;
    }
      angular.forEach(catalog, function(category) {
        angular.forEach(category.aSubcategory, function(subCategory) {
          angular.forEach(subCategory.aService, function(aServiceItem) {
            var found = false;
            for (var i = 0; i < operators.length; ++i) {
              if (operators[i].sSubjectOperatorName === aServiceItem.sSubjectOperatorName) {
                found = true;
                break;
              }
            }
            if (!found && aServiceItem.sSubjectOperatorName != "") {
              operators.push(aServiceItem);
            }
          });
        });
      });
      return operators;
  };

  // пока не реализованы теги для бизнеса - используем старый сервис, после реализации - удалить.
  this.getServices = function (sFind) {
    var data = {
      sFind: sFind || null
    };
    return $http.get('./api/catalog', {
      params: data,
      data: data
    }).then(function (response) {
      servicesCache = response.data;
      // нам нужен только бизнес
      return response.data;
    });
  };

  this.setServicesTree = function(data, callback){
    var request = {
      method: 'POST',
      url: '/api/catalog',
      data: data
    };

    return simpleHttpPromise(request, callback);
  };

  var del = function(path, nID, bRecursive, callback){
    var request = {
      method: 'DELETE',
      url: path,
      params: {
        nID: nID,
        bRecursive: bRecursive
      }
    };

    return simpleHttpPromise(request, callback);
  };

  this.removeCategory = function(nID, bRecursive, callback){
    return del('/api/catalog/category', nID, bRecursive, callback);
  };

  this.removeSubcategory = function(nID, bRecursive, callback){
    return del('/api/catalog/subcategory', nID, bRecursive, callback);
  };

  this.removeService = function(nID, bRecursive, callback){
    return del('/api/catalog/service', nID, bRecursive, callback);
  };

  this.removeServicesTree = function(nID_Subject, callback){
    return del('/api/catalog/servicesTree', undefined, undefined, nID_Subject, callback);
  };
}]);

// сервисы до редизайна, в будущем можно будет удалить.

// this.getModeSpecificServicesOld = function (asIDPlacesUA, sFind, bShowEmptyFolders) {
//   var asIDPlaceUA = asIDPlacesUA && asIDPlacesUA.length > 0 ? asIDPlacesUA.reduce(function (ids, current, index) {
//     return ids + ',' + current;
//   }) : null;
//
//   var data = {
//     asIDPlaceUA: asIDPlaceUA,
//     sFind: sFind || null,
//     bShowEmptyFolders: bShowEmptyFolders
//   };
//   return $http.get('./api/catalog', {
//     params: data,
//     data: data
//   }).then(function (response) {
//     servicesCache = response.data;
//     return response.data;
//   });
// };

// this.getServices = function (sFind) {
//   var data = {
//     sFind: sFind || null
//   };
//   return $http.get('./api/catalog', {
//     params: data,
//     data: data
//   }).then(function (response) {
//     servicesCache = response.data;
//     return response.data;
//   });
// };


