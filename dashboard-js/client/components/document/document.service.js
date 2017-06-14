'use strict';

angular.module('dashboardJsApp')
  .factory('DocumentsService', ['$http', '$q', 'Auth', function tasks($http, $q, Auth) {
    function simpleHttpPromise(req, callback) {
      var cb = callback || angular.noop;
      var deferred = $q.defer();

      $http(req).then(
        function (response) {
          deferred.resolve(response.data);
          return cb();
        },
        function (response) {
          deferred.reject(response);
          return cb(response);
        }.bind(this));
      return deferred.promise;
    }

    return {

      downloadDocument: function (taskId, callback) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/tasks/' + taskId + '/document'
        }, callback);
      },

      getDocumentStepRights: function (nID_Process) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getDocumentStepRights',
          params: {
            nID_Process: nID_Process
          }
        })
      },

      getDocumentStepLogins: function (nID_Process) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getDocumentStepLogins',
          params: {
            nID_Process: nID_Process
          }
        })
      },

      getProcessSubject: function (id) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getProcessSubject',
          params: {
            snID_Process_Activiti: id,
            nDeepLevel: 1
          }
        })
      },

      getProcessSubjectTree: function (id) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getProcessSubjectTree',
          params: {
            snID_Process_Activiti: id,
            nDeepLevel: 0
          }
        })
      },

      isUserHasDocuments: function (login) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getBPs_ForReferent',
          params: {
            sLogin: login
          }
        })
      },

      createNewDocument: function (bpID) {
        return simpleHttpPromise({
          method: 'GET',
          url: 'api/documents/setDocument',
          params: {
            sID_BP: bpID
          }
        })
      },

      delegateDocToUser : function (params) {
        if (params)
          return simpleHttpPromise({
            method: 'GET',
            url: '/api/documents/delegateDocument',
            params: params
          })
      },

      getUnsignedDocsList: function () {
        var currentUser = Auth.getCurrentUser();
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/getDocumentSubmittedUnsigned',
          params: {
            sLogin: currentUser.id
          }
        })
      },

      removeDocumentSteps: function (nID_Process) {
        return simpleHttpPromise({
          method: 'GET',
          url: '/api/documents/removeDocumentSteps',
          params: {
            snID_Process_Activiti: nID_Process
          }
        })
      }

    }
  }]);
