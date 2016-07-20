angular.module('dashboardJsApp').directive('dropdownGroup', function () {
  return {
    restrict: 'EA',
    templateUrl: 'app/users/directives/dropdownGroup.html',
    scope: {
      ngModel: "=",
      data: "=",
      ngRequired: "="
    },
    controller: DropdownGroupController,
    controllerAs: 'vm',
    bindToController: true
  };
});

function DropdownGroupController() {
  var vm = this
    , groups
    , allGroups
    , groupsToAdd = null
    , groupsToRemove = null
    , groupToAdd = null
    , groupToRemove = null;


  activate();

  function activate() {
    setInitialCollection();
  }

  vm.onSelectGroup = function (item) {
    vm.selected = item.name;
    groupToAdd = item;
  };

  function setInitialCollection() {
    groupsToAdd = vm.data.groupsToAdd;
    groupsToRemove = vm.data.groupsToRemove;
    groups = vm.data.groupsList = vm.data.groupsList ? vm.data.groupsList : [];

    vm.allGroups = vm.data.allGroupsList.filter(function (element) {
      if (Array.isArray(groups)) {
        for (var i = 0; i < groups.length; i++) {
          if (element.id === groups[i].id) {
            return false;
          }
        }
      }
      return true;
    });
  }

  vm.addGroup = function () {

    if (groupToAdd) {
      var index = vm.allGroups.indexOf(groupToAdd);

      if (!isGroupIsUsed(groupToAdd)) {
        addGroupToArr(groupsToAdd, groupToAdd);
      }

      groups.unshift(vm.allGroups.splice(index, 1)[0]);
      removeGroupFromArr(groupsToRemove, groupToAdd);
    }
    groupToAdd = null;
    vm.selected = null;
  };

  vm.removeGroup = function (item) {
    groupToRemove = item;

    if (groupToRemove) {
      var index = groups.indexOf(groupToRemove);

      if (isGroupIsUsed(groupToRemove)) {
        addGroupToArr(groupsToRemove, groupToRemove);
      }

      vm.allGroups.unshift(groups.splice(index, 1)[0]);
      removeGroupFromArr(groupsToAdd, groupToRemove);

    }
    groupToRemove = null;
  };

  function isGroupIsUsed(item) {
    return !(Array.isArray(groups) ? (groups.indexOf(item) < 0) : false);
  }

  function addGroupToArr(grArray, item) {
    if (grArray.indexOf(item) < 0) {
      grArray.push(item);
    }
  }

  function removeGroupFromArr(grArray, item) {
    var index = grArray.indexOf(item);
    if (index >= 0) {
      grArray.splice(index, 1);
    }
  }

}
