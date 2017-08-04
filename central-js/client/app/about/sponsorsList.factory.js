(function () {
  'use strict'

  angular.module('about').factory('SponsorsList', function () {

    var aSponsors = [
      {
        url: 'http://contact-call.com',
        alt: 'contact-call',
        image: 'assets/images/ads/contactcall.png',
        isActual: false
      }
      ,{
        url: 'http://www.de-novo.biz/uk',
        alt: 'de-novo',
        image: 'assets/images/ads/denovo.png',
        isActual: false
      }
      ,{
        url: 'http://www.promarmatura.ua',
        alt: 'promarmatura',
        image: 'assets/images/ads/promarmatura.png',
        isActual: false
      }
      ,{
        url: 'http://nic.ua',
        alt: 'nicua',
        image: 'assets/images/ads/nicua.png',
        isActual: false
      }
      ,{
        url: 'http://work.ua',
        alt: 'workua',
        image: 'assets/images/ads/workua.png',
        isActual: true
      }
      ,{
        url: 'http://www.templatemonster.com/ua',
        alt: 'template',
        image: 'assets/images/ads/template.png',
        isActual: true
      }
      ,{
        url: 'http://dou.ua',
        alt: 'dou',
        image: 'assets/images/ads/dou.png',
        isActual: true
      }
      ,{
        url: 'http://bdo.com.ua',
        alt: 'bdo',
        image: 'assets/images/ads/bdo.png',
        isActual: false
      }
      ,{
        url: 'http://dimakovpak.com',
        alt: 'kovpak',
        image: 'assets/images/ads/kovpak.png',
        isActual: true
      }
      ,{
        url: 'http://avante.com.ua',
        alt: 'avante',
        image: 'assets/images/ads/avante.png',
        isActual: false
      }
      ,{
        url: 'http://ukr.net',
        alt: 'ukrnet',
        image: 'assets/images/ads/ukrnet.png',
        isActual: false
      }
      ,{
        url: 'https://www.fabrikant.ua',
        alt: 'fabrikant',
        image: 'assets/images/ads/fabrikant.png',
        isActual: false
      }
      ,{
        url: 'http://www.lun.ua',
        alt: 'lun',
        image: 'assets/images/ads/lun.png',
        isActual: true
      }
      ,{
        url: 'http://www.rbt.te.ua',
        alt: 'ternopil',
        image: 'assets/images/ads/ternopil.png',
        isActual: true
      }
      ,{
        url: 'https://www.facebook.com/3bobra',
        alt: 'tribobra',
        image: 'assets/images/ads/tribobra.png',
        isActual: false
      }
      ,{
        url: 'https://webmoney.ua',
        alt: 'webmoneyua',
        image: 'assets/images/ads/webmoneyua.png',
        isActual: true
      }
      ,{
        url: 'https://leogaming.net',
        alt: 'leogaming',
        image: 'assets/images/ads/leo.png',
        isActual: true
      }
      ,{
        url: 'https://www.facebook.com/%D0%A4%D1%83%D0%BD%D1%82-%D0%9A%D0%B0%D0%B2%D0%B8-209303262475013/',
        alt: 'funtkavy',
        image: 'assets/images/ads/funtkavy.png',
        isActual: false
      }
      ,{
        url: 'https://www.ria.com',
        alt: 'ria',
        image: 'assets/images/ads/ria.png',
        isActual: false
      }
      ,{
        url: 'http://oyy.com.ua',
        alt: 'oyy',
        image: 'assets/images/ads/oyy.png',
        isActual: false
      }
      ,{
        url: 'https://zakupki.prom.ua',
        alt: 'zakupki.prom.ua',
        image: 'assets/images/ads/zakupki-prom-ua.png',
        isActual: false
      }
      ,{
        url: 'http://besplatka.ua',
        alt: 'besplatka.ua',
        image: 'assets/images/ads/besplatka.jpg',
        isActual: false
      }
      ,{
        url: 'http://lawyer.ua',
        alt: 'lawyer.ua',
        image: 'assets/images/ads/lawuer-services.png',
        isActual: true
      }
      ,{
        url: 'https://www.imena.ua',
        alt: 'imena',
        image: 'assets/images/ads/imena.png',
        isActual: true
      }
      ,{
        url: 'https://www.fondy.eu',
        alt: 'fondy.eu',
        image: 'assets/images/ads/fondy.png',
        isActual: true
      }
      ,{
        url: 'http://www.jetbrains.com',
        alt: 'jetbrains.com',
        image: 'assets/images/ads/jetbrain.png',
        isActual: true
      }
      ,{
        url: 'http://www.skywell.com.ua',
        alt: 'skywell',
        image: 'assets/images/ads/skywell.png',
        isActual: false
      }
      ,{
        url: 'https://www.mastercard.ua/uk-ua.html',
        alt: 'mastercard.ua',
        image: 'assets/images/ads/mastercard.png',
        isActual: true
      }
      ,{
        url: 'http://iati.com.ua',
        alt: 'iati.com.ua',
        image: 'assets/images/ads/iati.png',
        isActual: true
      }
      ,{
        url: 'http://www.tcl.eu',
        alt: 'www.tcl.eu',
        image: 'assets/images/ads/TCL.png',
        isActual: true
      }
      ,{
        url: 'http://www.hilton.net.ua/',
        alt: 'www.hilton.net.ua/',
        image: 'assets/images/ads/HILTON.png',
        isActual: true
      }
      ,{
        url: 'https://opendatabot.com/',
        alt: 'https://opendatabot.com/',
        image: 'assets/images/ads/opendatabot.png',
        isActual: true
      }
      ,{
        url: 'https://www.lifecell.ua/',
        alt: 'https://www.lifecell.ua/',
        image: 'assets/images/ads/lifecell.png',
        isActual: true
      }
    ];

    return {
      getAll: function () {
        return angular.copy(aSponsors);
      },
      getActual: function () {
        return angular.copy(aSponsors.filter(function (item) {
          return item.isActual;
        }));
      }
    }

  })
})();
