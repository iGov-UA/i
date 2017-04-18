angular.module('dashboardJsApp')
  .controller('edsViewCtrl', ['$scope', 'Modal', '$state', 'tasks', 'unsignedDocsList',
    function ($scope, Modal, $state, tasks, unsignedDocsList) {

      $scope.documents = {selectAll :false};

      $scope.isTaskFilterActive = function (tab) {
        if(tab === 'ecp' && $state.current.name === "tasks.ecp")
          return true;
      };

      $scope.unsignedDocumentsList = unsignedDocsList;

      $scope.selectAllDocuments = function () {
       angular.forEach($scope.unsignedDocumentsList, function (doc) {
          doc.selected = $scope.documents.selectAll;
        })
      };

      $scope.signDocuments = function () {
        var selectedDocuments = $scope.unsignedDocumentsList.filter(function (doc) {
          return doc.selected === true;
        });

        if(selectedDocuments.length > 0) {
          // sign documents
          console.log(selectedDocuments);
        } else {
          Modal.inform.warning()('Ви не обрали жодного документу.')
        }
      }
  }]);
