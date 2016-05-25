angular.module('dashboardJsApp')
  .directive('rules', function () {

    var controller = function ($scope, $modal, processes, Modal) {

      var getAllFunc = $scope.funcs.getAllFunc;
      var setFunc = $scope.funcs.setFunc;
      var deleteFunc = $scope.funcs.deleteFunc;


      var openModal = function (rule) {
        var modalInstance = $modal.open({
          animation: true,
          templateUrl: 'app/escalations/modal/modal.html',
          controller: 'RuleEditorModalCtrl',
          resolve: {
            ruleToEdit: function () {
              return angular.copy(rule);
            },
            processesList: function () {
              return $scope.processesList;
            }
          }
        });

        modalInstance.result.then(function (editedRule) {
          setFunc(editedRule)
            .then(function (editedRule) {
              console.log('fine');
              var i = 0;
              var ruleNotExistedBefore = $scope.rules.every(
                function (element) {
                  if (element.nID == editedRule.nID) {
                    $scope.rules[i] = editedRule;
                    setRuleBPName($scope.rules[i]);
                    return false;
                  }
                  i++;
                  return true;
                }
              );
              if (ruleNotExistedBefore) {
                setRuleBPName(editedRule);
                $scope.rules.push(editedRule);
              }

            });

        });
      };

      $scope.rules = [];
      $scope.get = function () {
        return $scope.rules;
      };

      $scope.areRulesPresent = false;
      $scope.inProgress = false;


      $scope.isInProgress = function () {
        return $scope.inProgress;
      };

      $scope.isShowData = function () {
        return !$scope.inProgress && $scope.areRulesPresent;
      };

      $scope.getRules = function () {

      };

      $scope.isShowWarning = function () {
        return !$scope.inProgress && !$scope.isSlotsPresent;
      };

      $scope.add = function () {
        openModal();
      };

      $scope.edit = function (rule) {
        openModal(rule);
      };

      $scope.copy = function (rule) {
        rule.isCopied = true;
        openModal(rule);
      };

      $scope.delete = function (rule) {
        Modal.confirm.delete(function (event) {
          deleteFunc(rule)
            .then($scope.fillData);
        })('правило для послуги ' + rule.bpName + ' (ID правила ' + rule.nID + ')');
      };

      var setRuleBPName = function (rule) {
        var result = $.grep($scope.processesList, function (e) {
          return e.sID === rule.sID_BP;
        });
        rule.bpName = (result.length > 0) ? result[0].sName : rule.sID_BP + ", бізнес-процес некоректний.";
      };

      $scope.fillData = function () {

        $scope.inProgress = true;
        $scope.areRulesPresent = false;

        processes.getUserProcesses().then(function (data) {
          $scope.processesList = data;
          getAllFunc()
            .then(function (data) {
              $scope.rules = data;
              angular.forEach($scope.rules, function (rule, index) {
                setRuleBPName(rule);
              });
              fillAccordionGroups();
              $scope.areRulesPresent = true;
            });

        }, function () {
          $scope.processesList = "error";
        });
      };

      $scope.processesLoaded = function () {
        return $scope.processesList ? true : false;
      };

      $scope.translate = function (text) {
        if (text == 'Отсылка уведомления на электронную почту') return 'відправити повідомлення на e-mail';
        return text;
      };

      $scope.aSuccessGroups = [];
      $scope.aMissingGroups = [];

      function fillAccordionGroups() {
        var successGroupsTamp = {};
        var missingGroupsTemp = {};
        angular.forEach($scope.rules, function (rule, index) {
          var result = $.grep($scope.processesList, function (e) {
            return e.sID === rule.sID_BP;
          });
          if (result.length > 0) {
            if (!successGroupsTamp[rule.sID_BP]) {
              successGroupsTamp[rule.sID_BP] = [];
            }
            successGroupsTamp[rule.sID_BP].push(rule);
          } else {
            if (!missingGroupsTemp[rule.sID_BP]) {
              missingGroupsTemp[rule.sID_BP] = [];
            }
            missingGroupsTemp[rule.sID_BP].push(rule);
          }
        });

        for (var sucGr in successGroupsTamp) {
          $scope.aSuccessGroups.push({
            sTitle: $.trim(successGroupsTamp[sucGr][0].bpName + ' (' + successGroupsTamp[sucGr][0].sID_BP + ')'),
            aContent: angular.copy(successGroupsTamp[sucGr])
          });
        }
        for (var misGr in missingGroupsTemp) {
          $scope.aMissingGroups.push({
            sTitle: $.trim(missingGroupsTemp[misGr][0].sID_BP),
            aContent: angular.copy(missingGroupsTemp[misGr])
          });
        }
        debugger;
      }
    };

    return {
      restrict: 'E',
      scope: {
        funcs: '='
      },
      controller: controller,
      templateUrl: 'app/escalations/rules/rules.html'
    }
  }
);
