'use strict';
angular.module('dashboardJsApp')
  .directive('tableField', function() {
    return {
      restrict: 'E',
      controller: ['$scope', '$rootScope', function TableFieldController( $scope, $rootScope ) {

        $scope.broadcast = function(eventName, obj) { 

           $rootScope.$broadcast(eventName, obj); 

           return true;
        }; 

        $scope.oTable = {}; 
        $scope.isLoaded = false; 

        $scope.onLoad = function( item ) { 

            if( typeof item.aRow[0] !== 'number' )  { 
                $scope.oTable = item; 

                $scope.broadcast( "TableLoaded", { 'table' : item, 'tableName' : item.id } ); 

                $scope.isLoaded = true; 
            } 
            else { 
                $scope.isLoaded = false; 
            } 

            return $scope.isLoaded;
        } 
        
      }], 
      templateUrl: 'app/tasks/form-fields/tableField.html'
    };
});
