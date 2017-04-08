angular.module('autocompleteService')
    .controller('dropdownAutocompleteCtrl', function ($scope, $http, $filter, $timeout, $q) {

    $scope.selectedValue = function (field, select) {
      var val = field.name.split(';');
      if(val.length === 3) {
          return field.value;
      } else {
          return select;
      }
    };

    var hasNextChunk = true,
        queryParams = {params: {}},
        tempCollection, count = 30;

    function getInfinityScrollChunk() {
        $scope.isRequestMoreItems = true;
        return $http.get($scope.autocompleteData.apiUrl, queryParams).then(function (res) {
            if(angular.isDefined(res.config.params.sFind) && angular.isArray(res.data)){
                angular.forEach(res.data, function (el) {
                    if(angular.isDefined(el.sID) && angular.isDefined(el.sNote)){
                        el.sFind = el.sID + " " + el.sNote;
                    } else if (angular.isDefined(el.sID) && angular.isDefined(el.sName_UA)) {
                        el.sFind = el.sName_UA + " " + el.sID;
                    } else if (angular.isDefined(el.sID_UA) && angular.isDefined(el.sName_UA)) {
                        el.sFind = el.sName_UA + " " + el.sID_UA;
                    }
                });
            } else if(typeof res.data === 'object' && res.config.params.sID_SubjectRole) {
                var response;
                if(res.config.params.sID_SubjectRole === 'Executor') {
                    response = subjectUserFilter(res.data.aSubjectGroupTree);
                    angular.forEach(response, function (user) {
                        user.sName = user.sFirstName + " " + user.sLastName;
                    })
                } else if(res.config.params.sID_SubjectRole === 'ExecutorDepart') {
                    response = departmentFilter(res.data.aSubjectGroupTree);
                }
            }
            return res;
        });
    }

    // filter for resp from getSubjectGroupsTree, with tree list
    var subjectUserFilter = function (arr) {
        var allUsers = [], filteredUsers = [], logins = [];

        (function loop(arr) {
            angular.forEach(arr, function(item) {
                if(item.aUser) {
                    angular.forEach(item.aUser, function(user) {
                        allUsers.push(user);
                    })
                }
                if(item.aSubjectGroupChilds && item.aSubjectGroupChilds.length > 0){
                    loop(item.aSubjectGroupChilds)
                }
            })
        })(arr);

        for(var i=0; i<allUsers.length; i++) {
            if(logins.indexOf(allUsers[i].sLogin) === -1) {
                filteredUsers.push(allUsers[i]);
                logins.push(allUsers[i].sLogin);
            }
        }
        return filteredUsers;
    };

    //filter with departments from resp
    var departmentFilter = function (arr) {
        var allDeps = [], filteredDeps = [], logins = [];

        (function loop(arr) {
            angular.forEach(arr, function(item) {
                allDeps.push({sID_Group_Activiti:item.sID_Group_Activiti, sName:item.sName});
                if(item.aSubjectGroupChilds && item.aSubjectGroupChilds.length > 0){
                    loop(item.aSubjectGroupChilds)
                }
            })
        })(arr);

        for(var i=0; i<allDeps.length; i++) {
            if(logins.indexOf(allDeps[i].sID_Group_Activiti) === -1) {
                filteredDeps.push(allDeps[i]);
                logins.push(allDeps[i].sID_Group_Activiti);
            }
        }
        return filteredDeps;
    };

    var getAdditionalPropertyName = function() {
        return ($scope.autocompleteData.additionalValueProperty ? $scope.autocompleteData.additionalValueProperty : $scope.autocompleteData.prefixAssociatedField) + '_' + $scope.autocompleteName;
    };

    var getNameWithPostFix = function (field) {
      return field && field.id ? field.id.split('_') : null;
    };

    $scope.requestMoreItems = function(collection) {
        if ($scope.isRequestMoreItems || !hasNextChunk) {
            return $q.reject();
        }

        return ($scope.autocompleteData ? getInfinityScrollChunk() : $timeout(getInfinityScrollChunk, 200))
            .then(function(response) {
                var resp = response.data.aSubjectGroupTree ? response.data.aSubjectGroupTree : response.data;
                if(response.config.params.sID_SubjectRole === 'Executor') {
                    resp = subjectUserFilter(response.data.aSubjectGroupTree);
                } else if(response.config.params.sID_SubjectRole === 'ExecutorDepart') {
                    resp = departmentFilter(response.data.aSubjectGroupTree);
                }
                Array.prototype.push.apply(collection, $filter('orderBy')(resp, $scope.autocompleteData.orderBy));
                if (!$scope.autocompleteData.hasPaging || response.data.length < count) {
                    hasNextChunk = false;
                }
                if ($scope.autocompleteData.hasPaging) {
                    queryParams.params.skip = collection.length;
                }
                return collection;
            }, function(err) {
                return $q.reject(err);
            })
            .finally(function() {
                $scope.isRequestMoreItems = false;
            });
    };

    $scope.refreshList = function(queryKey, queryValue, params) {
        if (!angular.isDefined(queryParams.params[queryKey])) {
            hasNextChunk = true;
        }
        if (!angular.equals(queryParams.params[queryKey], queryValue)) {
            // if ($scope.autocompleteData.hasPaging || !angular.isDefined(queryParams.params[queryKey])) {
            if ($scope.autocompleteData.hasPaging) {
                queryParams.params.count = count;
                queryParams.params.skip = 0;
            }
            $scope.isRequestMoreItems = false;
            hasNextChunk = true;
            var ps = params ? params.split(';')[2] : null;
            if(ps && ps.indexOf('sID_SubjectRole') > -1) {
                var param = ps.split(',');
                angular.forEach(param, function (p) {

                    if(p.indexOf('sID_SubjectRole') > -1) {
                        var role = p.split('=');
                        queryParams.params[role[0]] = role[1];
                    }

                    angular.forEach($scope.taskForm, function (field, key) {
                        if('aRow' in field) {
                            angular.forEach(field.aRow, function (row, rkey) {
                                angular.forEach(row.aField, function (f, fkey) {
                                    if(p.split('=')[1] === f.id) {
                                        setParams(f, key, rkey, fkey);
                                    }
                                })
                            })
                        }
                        if(p.split('=')[1] === field.id) {
                            setParams(field, key);
                        }
                    })
                });
                function setParams(field, key, rowKey, fieldKey) {
                    if (field.id.indexOf('sID_Group_Activiti') === 0) {
                        queryParams.params['sID_Group_Activiti'] = field.value;
                    } else if (field.id.indexOf('nDeepLevel') === 0) {
                        queryParams.params['nDeepLevel'] = field.value;
                    } else {
                        queryParams.params[field.id] = field.value;
                    }
                    if(key && rowKey && fieldKey) {
                        $scope.$watch('taskForm['+ key +'].aRow['+ rowKey + '].aField[' + fieldKey + '].value', function (newValue) {
                            $scope.refreshList(field.id, newValue);
                        })
                    } else {
                        $scope.$watch('taskForm['+ key +'].value', function (newValue) {
                            $scope.refreshList($scope.taskForm[key].id, newValue);
                        })
                    }
                }
                if(queryValue) {
                    queryParams.params.sFind = queryValue;
                }
            } else {
                queryParams.params[queryKey] = queryValue
            }
            $scope.requestMoreItems([]).then(function (items) {
                // $timeout(function () {
                //   $scope.$select.items = items;
                // }, 0, !angular.equals(queryParams.params[queryKey], queryValue));
                var filtered = null;
                if(queryValue && isNaN(queryValue)){
                   filtered = items.filter(function(i){
                        var name = i.sName_UA ? i.sName_UA : (i.sNameShort_UA ? i.sNameShort_UA : (i.sName ? i.sName : i.sNote));
                        return name.toLowerCase().indexOf(queryValue.toLowerCase()) !== -1;
                    });  
                }
                $scope.$select.items = filtered ? filtered : items;
                !angular.equals(queryParams.params[queryKey], queryValue);
            });
        } else {
            tempCollection = tempCollection || $scope.$select.items;
            $scope.$select.items = $filter('filter')(tempCollection, queryValue);
        }
        // }
    };
    $scope.onSelectDataList = function (item, tableName, rowIndex, field) {
        var additionalPropertyName = getAdditionalPropertyName();
        var nameWithPostFix = getNameWithPostFix(field);
        var selectPostfix = nameWithPostFix[nameWithPostFix.length - 1];

        if (rowIndex !== null && (rowIndex || rowIndex >= 0)) {
            var form = $scope.activitiForm ? $scope.activitiForm.formProperties : $scope.taskForm;
            angular.forEach(form, function (property) {
                if (property.id === tableName) {
                    angular.forEach(property.aRow[rowIndex].aField, function (field, key, obj) {
                        if (field.id === additionalPropertyName) {
                            if(obj[key].hasOwnProperty('default')) {
                                obj[key].default = item[$scope.autocompleteData.prefixAssociatedField];
                            } else {
                                obj[key].value = item[$scope.autocompleteData.prefixAssociatedField];
                            }
                        }
                        if(nameWithPostFix && nameWithPostFix[1]) {
                            var splited = field.id.split(/_/),
                                postfix = splited.pop(),
                                anotherPart =  splited.join('_');
                            if(isNaN(parseInt(nameWithPostFix[1])) && nameWithPostFix[1] === postfix) {
                                obj[key].value = item[anotherPart];
                            }
                        }
                    });
                }
            });
        } else {
            if ($scope.formData && $scope.formData.params[additionalPropertyName]) {
                $scope.formData.params[additionalPropertyName].value = item[$scope.autocompleteData.prefixAssociatedField];
            } else if($scope.taskForm) {
                angular.forEach($scope.taskForm, function (f, key, obj) {
                    if(f.id === additionalPropertyName) {
                        obj[key].value = item[$scope.autocompleteData.prefixAssociatedField];
                    } else if(nameWithPostFix && selectPostfix) {
                        var splited = f.id.split(/_/),
                            postfix = splited.pop(),
                            anotherPart =  splited.join('_');
                        if(isNaN(parseInt(postfix)) && selectPostfix === postfix || (anotherPart.indexOf(additionalPropertyName) === 0 && postfix === nameWithPostFix[nameWithPostFix.length - 1])) {
                            obj[key].value = item[splited[0]];
                        }
                    }
                })
            } else if (!$scope.taskForm && $scope.formData && !$scope.formData.params[additionalPropertyName]) {
                for (var stuff in $scope.formData.params) {
                    if($scope.formData.params.hasOwnProperty(stuff)) {
                        if (nameWithPostFix && nameWithPostFix[1]) {
                            var splited = stuff.split(/_/),
                                postfix = splited.pop(),
                                anotherPart = splited.join('_');
                            if (anotherPart.indexOf(additionalPropertyName) === 0 && postfix === nameWithPostFix[nameWithPostFix.length - 1]) {
                                $scope.formData.params[stuff].value = item[splited[0]];
                            }
                        }
                    }
                }
            }
        }
    };
});
