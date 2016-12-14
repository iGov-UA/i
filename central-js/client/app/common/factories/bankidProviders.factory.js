angular.module('app').factory('bankidProviders', [
  function () {
    return [
      {
        name: 'ПриватБанк',
        key: 'privat',
        auth: 'BankID',
        icon: 'assets/images/banks/privat.png'
      },
      {
        name: 'А-банк',
        key: 'abank',
        auth: 'BankID',
        icon: 'assets/images/banks/abank.png'
      },
      {
        name: 'Банк Південний',
        key: 'pivd',
        auth: 'BankID',
        icon: 'assets/images/banks/pivd.png'
      },
      {
        name: 'Конкорд',
        key: 'concord',
        auth: 'BankID',
        icon: 'assets/images/banks/concord.png'
      },
      {
        name: 'Ощадбанк',
        key: 'oshad',
        auth: 'BankID-NBU',
        icon: 'assets/images/banks/oshad.png'
      }
    ];
  }
]);
