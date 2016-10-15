angular.module('dashboardJsApp').directive('dropdownUser', function () {
  return {
    restrict: 'EA',
    templateUrl: 'app/groups/directives/dropdownUser.html',
    scope: {
      ngModel: "=",
      data: "=",
      ngRequired: "="
    },
    controller: DropdownUserController,
    controllerAs: 'vm',
    bindToController: true
  };
});

function DropdownUserController() {
  var vm = this
    , users
    , usersToAdd = null
    , usersToRemove = null
    , userToAdd = null
    , userToRemove = null;


  activate();

  function activate() {
    setInitialCollection();
  }

  vm.onSelectUser = function (item) {
    vm.selected = item.sFirstName + ' ' + item.sLastName;
    userToAdd = item;
  };

  function setInitialCollection() {
    usersToAdd = vm.data.usersToAdd;
    usersToRemove = vm.data.usersToRemove;
    users = vm.data.userInGroup = vm.data.userInGroup ? vm.data.userInGroup : [];

    vm.allUsers = vm.data.allUsers.filter(function (element) {
      if (Array.isArray(users)) {
        for (var i = 0; i < users.length; i++) {
          if (element.sLogin === users[i].sLogin) {
            return false;
          }
        }
      }
      return true;
    });
  }

  vm.addUser = function () {

    if (userToAdd) {
      var index = vm.allUsers.indexOf(userToAdd);

      if (!isUserInGroup(userToAdd)) {
        addUserToArr(usersToAdd, userToAdd);
      }

      users.unshift(vm.allUsers.splice(index, 1)[0]);
      removeUserFromArr(usersToRemove, userToAdd);
    }
    userToAdd = null;
    vm.selected = null;
  };

  vm.removeUser = function (item) {
    userToRemove = item;

    if (userToRemove) {
      var index = users.indexOf(userToRemove);

      if (isUserInGroup(userToRemove)) {
        addUserToArr(usersToRemove, userToRemove);
      }

      vm.allUsers.unshift(users.splice(index, 1)[0]);
      removeUserFromArr(usersToAdd, userToRemove);

    }
    userToRemove = null;
  };

  function isUserInGroup(item) {
    return !(Array.isArray(users) ? (users.indexOf(item) < 0) : false);
  }

  function addUserToArr(usArray, item) {
    if (usArray.indexOf(item) < 0) {
      usArray.push(item);
    }
  }

  function removeUserFromArr(usArray, item) {
    var index = usArray.indexOf(item);
    if (index >= 0) {
      usArray.splice(index, 1);
    }
  }

}
