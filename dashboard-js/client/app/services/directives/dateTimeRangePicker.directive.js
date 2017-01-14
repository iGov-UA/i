// (function () {
//
//   var sSeparator = ' - ';
//
//   var defaultConfig = {
//     singleDatePicker: true,
//     timePicker: true,
//     timePicker24Hour: true,
//     autoApply: true,
//     locale: {
//       format: 'YYYY.MM.DD HH:mm',
//       separator: sSeparator
//     },
//     opens: 'right',
//     drops: 'down',
//     buttonClasses: 'btn btn-sm',
//     applyClass: 'btn-success',
//     cancelClass: 'btn-default',
//     applyLabel: 'Прийняти',
//     cancelLabel: 'Відминити',
//     customRangeLabel: 'Вільний вибір проміжку'
//   };
//
//   var adjustConfig = function (config, defaultValue) {
//     var asDate = defaultValue.split(sSeparator);
//
//     if (!asDate) {
//       config.startDate = new Date();
//       config.endDate = config.startDate;
//     } else {
//       config.startDate = asDate[0];
//       if (asDate.length > 0 && asDate[1]) {
//         config.endDate = asDate[1];
//       } else {
//         config.endDate = config.startDate;
//       }
//     }
//
//   };
//
//   var initDateRangePicker = function ($scope, element, ngModelController, config) {
//     var isInitialized = false;
//     $scope.$watch(
//       function () {
//         return ngModelController.$modelValue;
//       },
//       function (newValue, oldVal, scope) {
//         if (!isInitialized) {
//           if (newValue) {
//             adjustConfig(config, newValue);
//             element.daterangepicker(config);
//           } else {
//             element.daterangepicker(config);
//             ngModelController.$setViewValue(null);
//             ngModelController.$render();
//           }
//
//           isInitialized = true;
//         }
//       }
//     );
//   };
//
//   angular.module('dashboardJsApp')
//     .directive('datetimepicker', function ($timeout) {
//       return {
//         restrict: 'AEC',
//         require: 'ngModel',
//         link: function ($scope, element, attrs, ngModelController) {
//
//           var config = angular.copy(defaultConfig);
//           config.singleDatePicker = true;
//           if (attrs.hasOwnProperty('options') && attrs.options) {
//             var options = JSON.parse(attrs.options);
//             for (var attrname in options) {
//               config[attrname] = options[attrname];
//             }
//           }
//
//           if (attrs.hasOwnProperty('format') && attrs.format) {
//             config.locale.format = attrs.format;
//           }
//
//           if (attrs.hasOwnProperty('separator') && attrs.separator) {
//             config.locale.separator = attrs.separator;
//           }
//
//           var unregisterWatch = $scope.$watch(function(){
//             return ngModelController.$modelValue;
//           }, initialize);
//
//           function initialize(value){
//             if(value){
//               adjustConfig(config, value);
//             }
//             element.daterangepicker(config);
//             unregisterWatch();
//           }
//
//           ngModelController.$render = function () {
//             //TODO implement it if additional rendering should be done
//           };
//
//           if ((attrs.hasOwnProperty('ngReadonly') && !$scope.$eval(attrs.ngReadonly)) ||
//             (!attrs.hasOwnProperty('ngReadonly') && !attrs.hasOwnProperty('readonly'))) {
//             element.on('apply.daterangepicker', function (ev, picker) {
//               if (ngModelController) {
//                 $timeout(function () {
//                   ngModelController.$setViewValue(picker.startDate.format(config.locale.format));
//                   ngModelController.$render();
//                 });
//               }
//             });
//           }
//         }
//       };
//     })
//     .directive('datetimerangepicker', function () {
//       return {
//         restrict: 'AEC',
//         require: 'ngModel',
//         link: function ($scope, element, attrs, ngModelController) {
//
//           var config = angular.copy(defaultConfig);
//           config.singleDatePicker = false;
//           config.ranges = {
//             'Наступні 7 днів': [moment(), moment().add(6, 'days')],
//             'Наступні 30 днів': [moment(), moment().add(29, 'days')],
//             'До кінця цього місяця': [moment(), moment().endOf('month')],
//             'Наступний місяць': [moment().add(1, 'month').startOf('month'), moment().add(1, 'month').endOf('month')],
//             'До кінця цього року': [moment(), moment().endOf('year')]
//           };
//
//           initDateRangePicker($scope, element, ngModelController, config);
//         }
//       };
//     });
// })();
