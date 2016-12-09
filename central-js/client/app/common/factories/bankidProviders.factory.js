angular.module('app').factory('bankidProviders', [
  function () {
    return [
      {
        name: 'ПриватБанк',
        key: 'privat',
        auth: 'BankID'
      },
      {
        name: 'А-банк',
        key: 'abank',
        auth: 'BankID'
      },
      {
        name: 'Банк Південний',
        key: 'pivd',
        auth: 'BankID'
      },
      {
        name: 'Конкорд',
        key: 'concord',
        auth: 'BankID'
      },
      {
        name: 'Ощадбанк',
        key: 'oshad',
        auth: 'BankID-NBU'
      }
    ];
  }
]);
