angular.module('app')
  .service('CatalogService', ['$http', '$q', function ($http, $q) {

  var servicesCache = {};

  this.getModeSpecificServices = function (asIDPlacesUA, sFind, bShowEmptyFolders, catalogTab, category, subcat, bRoot) {
    var asIDPlaceUA = asIDPlacesUA && asIDPlacesUA.length > 0 ? asIDPlacesUA.reduce(function (ids, current, index) {
      return ids + ',' + current;
    }) : null;
    var nID_Category = catalogTab || category || 1;
    var nID_ServiceTag = subcat;

    // TODO проверка на страницу категорий, заменить на возможность поиска как подкатегория
    if(!catalogTab
        && !category
        && !subcat
        || category
        && !subcat) {
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
    } else {
      // страница подкатегорий
        var data = {
          asIDPlaceUA: asIDPlaceUA,
          sFind: sFind || null,
          bShowEmptyFolders: bShowEmptyFolders,
          nID_Category: nID_Category,
          nID_ServiceTag: nID_ServiceTag,
          bRoot: bRoot
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

  this.getCatalogTreeTagService = function (category, serviceTag, bRoot) {
    var data = {
      nID_Category: category, // TODO fix it
      nID_ServiceTag: serviceTag,
      bRoot: bRoot
    };
    return $http.get('./api/catalog/getCatalogTreeTagService', {
      params: data,
      data: data
    }).then(function (response) {
      servicesCache = response.data;
      return response.data[0];
    });
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
    }else {
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

// this.getCatalogCounts = function(catalog) {
//   var catalogCounts = {'0': 0, '1': 0, '2': 0};
//   if (catalog === undefined) {
//     catalog = servicesCache;
//   }
//
//   angular.forEach(catalog.aService, function(category) {
//         if (typeof (catalogCounts[category.nStatus]) == 'undefined') {
//           catalogCounts[category.nStatus] = 0;
//         }
//         ++catalogCounts[category.nStatus];
//   });
//   return catalogCounts;
// };

// this.getCatalogCountsOld = function(catalog) {
//   var catalogCounts = {'0': 0, '1': 0, '2': 0};
//   if (catalog === undefined) {
//     catalog = servicesCache;
//   }
//
//   angular.forEach(catalog, function(category) {
//     angular.forEach(category.aSubcategory, function(subItem) {
//       angular.forEach(subItem.aService, function(aServiceItem) {
//         if (typeof (catalogCounts[aServiceItem.nStatus]) == 'undefined') {
//           catalogCounts[aServiceItem.nStatus] = 0;
//         }
//         ++catalogCounts[aServiceItem.nStatus];
//       });
//     });
//   });
//   return catalogCounts;
// };

// this.getOperatorsOld = function(catalog) {
//   var operators = [];
//   if (catalog === undefined) {
//     catalog = servicesCache;
//   }
//   angular.forEach(catalog, function(category) {
//     angular.forEach(category.aSubcategory, function(subCategory) {
//       angular.forEach(subCategory.aService, function(aServiceItem) {
//         var found = false;
//         for (var i = 0; i < operators.length; ++i) {
//           if (operators[i].sSubjectOperatorName === aServiceItem.sSubjectOperatorName) {
//             found = true;
//             break;
//           }
//         }
//         if (!found && aServiceItem.sSubjectOperatorName != "") {
//           operators.push(aServiceItem);
//         }
//       });
//     });
//   });
//   return operators;
// };
