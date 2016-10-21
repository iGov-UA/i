module.exports.createToken = function (accessToken) {
  return {
    access_token: accessToken,
    token_type: "bearer",
    refresh_token: "5fd85cd8-2e71-4921-b102-eecfb0024e1a",
    expires_in: 179
  }
};

module.exports.codes = {
  forErrorResponse406: 'code406',
  forErrorResponse501: 'code501',
  forCustomerDataResponse: 'codeCustomerData',
  forCustomerDataCryptoResponse: 'codeCustomerDataCrypto'
};

module.exports.accessTokens = {
  forErrorResponse406: 'error token 406',
  forErrorResponse501: 'error token 501',
  forCustomerDataResponse: 'customer data token',
  forCustomerDataCryptoResponse: 'customer data crypto token'
};

module.exports.customerData = {
  "state": "ok",
  "customer": {
    "type": "physical",
    "inn": "112233445566",
    "sex": "м",
    "email": "TESTENКО@gmail.com",
    "birthDay": "01.01.1933",
    "firstName": "TESTO",
    "lastName": "TESTENКО",
    "middleName": "TESTENOVICH",
    "phone": "+380000000011",
    "addresses": [
      {
        "type": "factual",
        "country": "UA",
        "state": "Testuncka",
        "city": "Testivci",
        "street": "Testinosti",
        "houseNo": "62",
        "flatNo": "12"
      }
    ],
    "documents": [
      {
        "type": "passport",
        "series": "АА",
        "number": "121131",
        "issue": "Кфффрцівсвким РО УМВД",
        "dateIssue": "01.01.1989",
        "issueCountryIso2": "UA"
      }
    ],
    "scans": [
      {
        "type": "passport",
        "link": "https://id.bank.gov.ua/v1/bank/resource/client/scan/pasport",
        "dateCreate": "09.04.2015",
        "extension": "zip"
      }
    ]
  }
};

module.exports.createEncryptedCustomer = function (customerData, bankidNBUUtil, publicKeyPath) {
  var customer = JSON.parse(JSON.stringify(customerData.customer));

  delete customer.scans;
  delete customer.addresses;
  delete customer.documents;

  var fs = require('fs');
  var constants = require('constants');

  var publicKey = {
    key: fs.readFileSync(publicKeyPath),
    padding: constants.RSA_PKCS1_PADDING
  };

  return {
    state: "ok",
    customerCrypto: bankidNBUUtil.encryptData(customer, publicKey)
  };
};
