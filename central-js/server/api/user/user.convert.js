module.exports.convertToCanonical = function (type, data) {
  if(type === 'soccard'){
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
  }
  return data;
};
