
var pkcs7 = require('pkcs7');

var str = "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";
var enctcrypted = encrypt(pkcs7.pad(str));
console.log('the secret is out! ' + enctcrypted);

//var forge = require('node-forge');
////str = forge.util.decode64(str);
////str = forge.asn1.fromPem(str);
////var p7 = forge.pkcs7.createEnvelopedData();
//var p7 = forge.pkcs7.messageToPem(str);

console.log(p7);

console.log("**");

