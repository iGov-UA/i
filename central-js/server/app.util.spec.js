module.exports.queryStringToObject = function (urlString) {
  return urlString.split(/&|\?/g)
    .filter(function (item, i) {
      return i > 0
    }).reduce(function (toObject, item) {
      var pair = item.split(/=/);
      toObject[pair[0]] = pair[1];
      return toObject;
    }, {});
};

module.exports.pathFromURL = function (urlString) {
  return urlString.split(/\?/).filter(function (item, i) {
    return i == 0
  }).reduce(function (previous, item) {
    return item
  })
};

module.exports.extractAccessTokenBankID = function (authHeaderValue, type) {
  return this.extractAccessToken(authHeaderValue, 'bankid');
};

module.exports.extractAccessTokenBankIDNBU = function (authHeaderValue, type) {
  return this.extractAccessToken(authHeaderValue, 'bankid-nbu');
};

module.exports.extractAccessToken = function (authHeaderValue, type) {
  if(type === 'bankid'){
    return authHeaderValue && authHeaderValue.split(/Bearer /)[1].split(/,/)[0];
  } else if (type === 'bankid-nbu') {
    return authHeaderValue && authHeaderValue.split(/Bearer /)[1];
  }
};
