module.exports.convertToCanonical = function (type, data) {
  if (type === 'soccard') {
    /*
     {
     "firstName" : "Костянтин",
     "secondName" : "Анатолійович",
     "lastName" : "Ребров",
     "email" : "user@example.com",
     "activeCard" : "2300273165600897",
     "personNumber" : "0100483165600018"
     }
     */
    data.type = 'physical';
    data.middleName = data.secondName;
    delete data.secondName;
    data.inn = data.personNumber;
    delete data.personNumber;
  } else if (type === 'email') {
    data.type = 'physical';
    delete data.nID;
    delete data.sSB;
    delete data.oSubject;
    if (!data.middleName && data.sSurname) {
      data.middleName = data.sSurname;
      delete data.sSurname;
    }
    if (!data.lastName && data.sFamily) {
      data.lastName = data.sFamily;
      delete data.sFamily;
    }
    if (!data.firstName && data.sName) {
      data.firstName = data.sName;
      delete data.sName;
    }
    if (!data.inn && data.sINN) {
      data.inn = data.sINN;
      delete data.sINN;
    }
    if (data.sPassportSeria && data.sPassportNumber) {
      data.documents = [];
      data.documents.push({
        "type": "passport",
        "series": data.sPassportSeria,
        "number": data.sPassportNumber
      });
      delete data.sPassportSeria;
      delete data.sPassportNumber;
    }
  }
  return data;
};
