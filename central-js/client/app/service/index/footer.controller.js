angular.module('app').controller('FooterController', function ($scope) {
  $scope.adsset = [
    {
      url: 'http://contact-call.com',
      alt: 'contact-call',
      image: 'assets/images/ads/contactcall.png',
      height: 50
    },
    {
      url: 'http://www.de-novo.biz/uk',
      alt: 'de-novo',
      image: 'assets/images/ads/denovo.png',
      height: 50
    }
  ];

  function randomizeIndexes(indexes, item, arr) {
    var result = true;
    var count = 100;
    var sortid;
    do {
      sortid = Math.floor(Math.random() * arr.length);
    } while (indexes.includes(sortid) && count-- > 0);

    indexes.push(sortid);
    item.sortid = sortid;

    return result;
  }

  var indexes = [];
  $scope.adsset.forEach(function (item, i, arr) {
    randomizeIndexes(indexes, item, arr);
  });

  $scope.adsset.sort(function (a, b) {
    return a.sortid - b.sortid;
  });
});
