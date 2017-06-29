'use strict';

angular.module('dashboardJsApp')
	.controller('MainCtrl', function($scope, $http) {
    $http.get('/api/env/get-env-config').success(function (data) {
      $scope.ProjectRegion_LogonPage_sName = data.oOrganisation.ProjectRegion_LogonPage_sName;
      $scope.ProjectRegion_LogonPage_sNote = data.oOrganisation.ProjectRegion_LogonPage_sNote;
      $scope.ProjectRegion_LogonPage_sContacts = data.oOrganisation.ProjectRegion_LogonPage_sContacts;
    });
		$scope.weblinks = [{
			name: 'Центральний портал громадян',
			link: 'https://igov.org.ua',
			info: 'Портал громадян'
		}];
	});
