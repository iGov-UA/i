angular.module('app').controller('FooterController', function ($scope, SponsorsList, $state) {

  $scope.adsset = SponsorsList.getActual();

  function randomizeIndexes(indexes, item, arr) {
    var result = true;
    var count = 100;
    var sortid;
    do {
      sortid = Math.floor(Math.random() * arr.length);
    } while (indexes.indexOf(sortid) >= 0 && count-- > 0);

    indexes.push(sortid);
    item.sortid = sortid;

    return result;
  }

  $scope.hideSponsorsList = function () {
    return $state.current.name === 'index.about' || $state.current.name === 'index.test'
  };

  var indexes = [];
  $scope.adsset.forEach(function (item, i, arr) {
    item.height = 50;
    randomizeIndexes(indexes, item, arr);
  });

  $scope.adsset.sort(function (a, b) {
    return a.sortid - b.sortid;
  });

  window.addEventListener("resize", snapFooter);
  window.addEventListener("DOMSubtreeModified", snapFooter);

  function snapFooter() {
    var oHeader = $(".main-header");
    var oMain = $(".main");
    var oFooter = $(".main-footer");
    var sStateName = $state.current.name;
    if(sStateName === "index.journal" || sStateName === "index.service.general.place"){
      if(oFooter[0] && oFooter[0].scrollHeight){
        var nFooterHeight = oFooter[0].scrollHeight;
        var nHeaderHeight = 0;
        var nMainHeight = 0;
        if(oHeader[0] && oHeader[0].scrollHeight){
          nHeaderHeight = oHeader[0].scrollHeight;
        }
        if(oMain[0] && oMain[0].scrollHeight){
          nMainHeight = oMain[0].scrollHeight;
        }

        oFooter.css({
          top: Math.max(nHeaderHeight + nMainHeight, this.innerHeight - nFooterHeight),
          left: (this.innerWidth - oFooter[0].scrollWidth)/2 < 0 ? 0 : (this.innerWidth - oFooter[0].scrollWidth)/2,
          position:'absolute'
        });
      }
    } else {
      oFooter.css({
        top: 0,
        left: (this.innerWidth - oFooter[0].scrollWidth)/2 < 0 ? 0 : (this.innerWidth - oFooter[0].scrollWidth)/2,
        position:'relative'
      });
    }
  }
});
