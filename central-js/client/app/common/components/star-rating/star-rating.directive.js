(function () {
  'use strict';

  angular
    .module('app')
    .directive('starRating', starRating);

  function starRating() {
    return {
      restrict: 'EA',
      templateUrl: 'app/common/components/star-rating/star-rating.html',
      scope: {
        ratingValue: '=ngModel',
        max: '=?',
        onRatingSelect: '&?',
        readonly: '=readonly'
      },
      controller: StarRatingController,
      controllerAs: 'vm',
      bindToController: true
    };
  }


  function StarRatingController() {
    var vm = this;

    vm.toggle = toggle;

    activate();

    function activate(){
      if (vm.max == undefined) {
        vm.max = 5;
      }

      updateStars();
    }


    function updateStars() {
      vm.stars = [];
      for (var i = 0; i < vm.max; i++) {
        vm.stars.push({
          filled: i < vm.ratingValue
        });
      }
    }

    function toggle(index) {
      if (!vm.readonly) {
        vm.ratingValue = index + 1;
        vm.onRatingSelect({
          rating: index + 1
        });
      }
      updateStars();
    }
  }

})();
