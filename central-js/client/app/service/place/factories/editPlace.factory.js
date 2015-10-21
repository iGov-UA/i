angular.module('app')
  .factory('EditPlaceFactory', function($http, $modal, ServiceService, messageBusService) {

    return function () {

      var oService = ServiceService.oService;

      var openModal = function (category) {
        var modalInstance = $modal.open({
          animation: true,
          size: 'lg',
          templateUrl: 'app/service/place/templates/editModal.html',
          controller: 'PlaceEditModalController',
          resolve: {
            entityToEdit: true
          }
        });

        modalInstance.result.then(function (editedService) {

        });

      };

      return {
        add: function () {
          openModal();
        },
        edit: function (category) {
          openModal(category)
        },
        remove: function (category) {
          var title = 'Видалення категоії';
          var message = 'Ви впевнені, що бажаєте видалити категорію ' + category.sName + '?';
          var numberOfSubcategories = category.aSubcategory.length;
          if (numberOfSubcategories > 1) {
            message = 'Ви намагєтесь видалити категорію з ' + numberOfSubcategories + ' підкатегоріями. ' + message;
          } else if (numberOfSubcategories === 1) {
            message = 'Ви намагєтесь видалити категорію з однією підкатегорію. ' + message;
          }

          var confirmationLevel = 0;
          if (numberOfSubcategories > 0) {
            confirmationLevel = 2;
          }

          openDeletionModal(title, message, confirmationLevel)
            .then(function () {
              CatalogService.removeCategory(category.nID, true)
                .then(function () {
                  initCatalogUpdate();
                });
            });
        }
      }
    }();
});
