module.exports.createToken = function (accessToken) {
  return {
    access_token: accessToken,
    token_type: 'bearer',
    refresh_token: '5fd85cd8-2e71-4921-b102-eecfb0024e1a',
    expires_in: 179,
    scope: 'read trust write'
  }
};

module.exports.createErrorInvalidGrantCode = function (code) {
  return {error: 'invalid_grant', error_description: 'Invalid authorization code: ' + code};
};

module.exports.codes = {
  forErrorResponse406: 'code406',
  forErrorResponse501: 'code501',
  forCustomerDataResponse: 'codeCustomerData',
  forCustomerDataResponseError: 'codeCustomerDataError',
  forCustomerDataCryptoResponse: 'codeCustomerDataCrypto'
};

module.exports.accessTokens = {
  forErrorResponse406: 'error token 406',
  forErrorResponse501: 'error token 501',
  forCustomerDataResponse: 'customer data token',
  forCustomerDataResponseError: 'customer data token error',
  forCustomerDataCryptoResponse: 'customer data crypto token'
};

module.exports.customerError = {
  state: "err",
  code: "CLO001",
  desc: "Ошибка получения данных клиента"
};

module.exports.scans = {
  passportPDF: function (baseUrl) {
      return {
        "type": "passport",
        "link": baseUrl + "/89f2fc550e2a0fa72e4d91a10fa41ab4e831b0d1/passport",
        "dateCreate": "01.02.2016",
        "extension": "pdf",
        "number": 1
      }
  }
};

module.exports.customer = {
  "type": "physical",
  "clId": "111111dfdfd22222",
  "clIdText": "Передана інформація є достовірною і підтверджена BankID 26.10.2015 23:23",
  "lastName": "TEST lastName",
  "firstName": "TEST firstName",
  "middleName": "TEST middleName",
  "phone": "+380681112233",
  "birthDay": "01.01.1900",
  "inn": "0000000001",
  "email": "TEST@EMAIL.COM",
  "addresses": [{
    "type": "factual",
    "country": "UA",
    "state": "ДНЕПРОПЕТРОВСКАЯ",
    "city": "ДНЕПРОПЕТРОВСК",
    "street": "нет улицы",
    "houseNo": "00",
    "flatNo": "00",
    "dateModification": "26.03.2015 13:44:10.706"
  }, {
    "type": "birth",
    "country": "UA",
    "state": "ДНЕПРОПЕТРОВСКАЯ",
    "city": "ДНЕПРОПЕТРОВСК",
    "street": "Нет улицы",
    "dateModification": "16.01.2015 15:11:24.24"
  }],
  "documents": [{
    "type": "passport",
    "series": "TT",
    "number": "000001",
    "issue": "БАБУШКИНСКИМ РО ДГУ УМВД",
    "dateIssue": "01.01.1996",
    "issueCountryIso2": "UA"
  }]
};

module.exports.createCustomer = function (customerConfig) {
  var customer = this.customer;
  if(customerConfig.scans){
    var baseUrl = customerConfig.scans.baseUrl;
    customer.scans = [];
    customer.scans.push(this.scans.passportPDF(baseUrl));
  }
};

module.exports.createEncryptedCustomer = function (customerData, bankidNBUUtil, publicKeyPath) {
  return {};
};
