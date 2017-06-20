angular.module('app').controller('FooterController', function ($scope) {
  $scope.adsset = [
    /*{
      url: 'http://contact-call.com',
      alt: 'contact-call',
      image: 'assets/images/ads/contactcall.png',
      height: 50
    }*/
    /*,{
      url: 'http://www.de-novo.biz/uk',
      alt: 'de-novo',
      image: 'assets/images/ads/denovo.png',
      height: 50
    }*/

    /*,{
      url: 'http://www.promarmatura.ua',
      alt: 'promarmatura',
      image: 'assets/images/ads/promarmatura.png',
      height: 50
    }*/
    /*,{
      url: 'http://nic.ua',
      alt: 'nicua',
      image: 'assets/images/ads/nicua.png',
      height: 50
    }*/
    ,{
      url: 'http://work.ua',
      alt: 'workua',
      image: 'assets/images/ads/workua.png',
      height: 50
    }
    ,{
      url: 'http://www.templatemonster.com/ua',
      alt: 'template',
      image: 'assets/images/ads/template.png',
      height: 50
    }
    ,{
      url: 'http://dou.ua',
      alt: 'dou',
      image: 'assets/images/ads/dou.png',
      height: 50
    }
    /*,{
      url: 'http://bdo.com.ua',
      alt: 'bdo',
      image: 'assets/images/ads/bdo.png',
      height: 50
    }*/
    ,{
      url: 'http://dimakovpak.com',
      alt: 'kovpak',
      image: 'assets/images/ads/kovpak.png',
      height: 50
    }
    /*,{
      url: 'http://avante.com.ua',
      alt: 'avante',
      image: 'assets/images/ads/avante.png',
      height: 50
    }*/
    /*,{
      url: 'http://ukr.net',
      alt: 'ukrnet',
      image: 'assets/images/ads/ukrnet.png',
      height: 50
    }*/
    /*,{
      url: 'https://www.fabrikant.ua',
      alt: 'fabrikant',
      image: 'assets/images/ads/fabrikant.png',
      height: 50
    }*/
    ,{
      url: 'http://www.lun.ua',
      alt: 'lun',
      image: 'assets/images/ads/lun.png',
      height: 50
    }
    ,{
      url: 'http://www.rbt.te.ua',
      alt: 'ternopil',
      image: 'assets/images/ads/ternopil.png',
      height: 50
    }
    /*,{
      url: 'https://www.facebook.com/3bobra',
      alt: 'tribobra',
      image: 'assets/images/ads/tribobra.png',
      height: 50
    }*/
    ,{
      url: 'https://webmoney.ua',
      alt: 'webmoneyua',
      image: 'assets/images/ads/webmoneyua.png',
      height: 50
    }

    ,{
      url: 'https://leogaming.net',
      alt: 'leogaming',
      image: 'assets/images/ads/leo.png',
      height: 50
    }

    /*,{
      url: 'https://www.facebook.com/%D0%A4%D1%83%D0%BD%D1%82-%D0%9A%D0%B0%D0%B2%D0%B8-209303262475013/',
      alt: 'funtkavy',
      image: 'assets/images/ads/funtkavy.png',
      height: 50
    }*/

    ,{
      url: 'https://privatbank.ua',
      alt: 'privatbank',
      image: 'assets/images/ads/privatbank.png',
      height: 50
    }

    /*,{
      url: 'https://www.ria.com',
      alt: 'ria',
      image: 'assets/images/ads/ria.png',
      height: 50
    }*/

    /*,{
      url: 'http://oyy.com.ua',
      alt: 'oyy',
      image: 'assets/images/ads/oyy.png',
      height: 50
    }*/

    /*,{
      url: 'https://zakupki.prom.ua',
      alt: 'zakupki.prom.ua',
      image: 'assets/images/ads/zakupki-prom-ua.png',
      height: 50
    }*/

    /*,{
      url: 'http://besplatka.ua',
      alt: 'besplatka.ua',
      image: 'assets/images/ads/besplatka.jpg',
      height: 50
    }*/

    /*,{
      url: 'http://lawyer.ua',
      alt: 'lawyer.ua',
      image: 'assets/images/ads/lawuer-services.png',
      height: 50
    }*/

    ,{
      url: 'https://www.imena.ua',
      alt: 'imena',
      image: 'assets/images/ads/imena.png',
      height: 50
    }

    ,{
      url: 'https://www.fondy.eu',
      alt: 'fondy.eu',
      image: 'assets/images/ads/fondy.png',
      height: 50
    }

    ,{
      url: 'http://www.jetbrains.com',
      alt: 'jetbrains.com',
      image: 'assets/images/ads/jetbrain.png',
      height: 50
    }

    /*,{
      url: 'http://www.skywell.com.ua',
      alt: 'skywell',
      image: 'assets/images/ads/skywell.png',
      height: 50
    }*/

     ,{
      url: 'https://www.mastercard.ua/uk-ua.html',
      alt: 'mastercard.ua',
      image: 'assets/images/ads/mastercard.png',
      height: 50
    }

    ,{
      url: 'http://iati.com.ua',
      alt: 'iati.com.ua',
      image: 'assets/images/ads/iati.png',
      height: 50
    }

  ];

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

  var indexes = [];
  $scope.adsset.forEach(function (item, i, arr) {
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
  }
});
