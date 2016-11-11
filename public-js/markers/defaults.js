angular.module('iGovMarkers')
  .constant('iGovMarkersDefaults', {
    validate: {
      PhoneUA: {
        aField_ID: ['privatePhone', 'workPhone', 'phone', 'tel']
      }
      ,Mail: {
        aField_ID: ['privateMail', 'email']
      }
      ,AutoVIN: {
        aField_ID: ['vin_code', 'vin_code1', 'vin']
      }
      ,TextUA: {
        aField_ID: ['textUa',
          'lastName_UA1','firstName_UA1','middleName_UA1',
          'lastName_UA2','firstName_UA2','middleName_UA2',
          'lastName_UA3','firstName_UA3','middleName_UA3',
          'lastName_UA4','firstName_UA4','middleName_UA4',
          'lastName_UA5','firstName_UA5','middleName_UA5',
          'sFamily_UA','sName_UA','sSurname_UA', 'adminFio',
          'CNAPName'
        ]
      }
      ,TextRU: {
        aField_ID: ['textRu',
          'lastName_RU1','firstName_RU1','middleName_RU1',
          'lastName_RU2','firstName_RU2','middleName_RU2',
          'lastName_RU3','firstName_RU3','middleName_RU3',
          'lastName_RU4','firstName_RU4','middleName_RU4',
          'lastName_RU5','firstName_RU5','middleName_RU5',
          'sFamily_RU','sName_RU','sSurname_RU'
        ]
      }
      ,DateFormat: {
        aField_ID: ['dateFormat'],
        sFormat: 'YYYY-MM-DD'
      }
      ,DocumentDate: {
        aField_ID: ['DocumentDate'],
        sFormat: 'DD.MM.YYYY'
      }
      ,DateElapsed: {
        aField_ID: ['dateOrder'],
        bFuture: true, // якщо true, то дата modelValue має бути у майбутньому
        bLess: true, // якщо true, то 'дельта' між modelValue та зараз має бути 'менше ніж' вказана нижніми параметрами
        nDays: 10,
        nMonths: 0,
        nYears: 0,
        sFormat: 'YYYY-MM-DD'
        //,sDebug: 'Додаткова опція - інформація для дебагу'
        //,bDebug: false; // Опція для дебагу
      }
      ,DateElapsed_1: {
        inheritedValidator: 'DateElapsed', // наслідуємо існуючий валідатор 'DateElapsed'
        aField_ID: ['date_of_birth'],
        sFormat: 'YYYY-MM-DD',  // задаємо формат дати
        bFuture: false,         // дата має бути у минулому
        sMessage: 'Виберіть коректну дату - дата не може бути більше поточної'
      }
      ,CodeKVED: {
        aField_ID: ['kved']
      }
      ,CodeEDRPOU: {
        aField_ID: ['edrpou']
      }
      ,CodeMFO: {
        aField_ID: ['mfo']
      }
      ,StringRange: { 
    	  aField_ID: ['string'],
    	  aField_Type: ['string'], 
    	  nMin: 0,
    	  nMax: 200,
    	  sMessage: 'Повинно бути від 0 до 200 символів'
      }
      ,LongNumber: {
    	  aField_ID: ['long'],
    	  aField_Type: ['long'],
    	  nMin: 1,
    	  nMax: 1000, 
    	  sMessage: 'Повинно бути подільним числом від 1 до 1000 '
      }
      ,DoubleNumber: {
    	  aField_ID: ['double'],
    	  aField_Type: ['double'],
    	  nMin: 1, 
    	  nMax: 10000000,
    	  sSeparator: '.',
    	  sMessage: 'Повинно бути неподільним числом та розділене "."'
      }
      ,NumberBetween: { //Целочисленное между
        aField_ID: ['numberBetween'],
        nMin: 1,
        nMax: 999,
        sMessage: 'Перевірте правильність заповнення - число поверхів складається максимум з трьох цифр'
      }
      ,NumberFractionalBetween: { //Дробное число между
        aField_ID: ['total_place'],
        nMin: 0,
        nMax: 99999999,
        sMessage: 'Перевірте правильність заповнення поля - площа приміщення може складатися максимум з 8 цифр'
      }
      ,Numbers_Accounts: { //разрешены цифры и дефисы, буквы любые запрещены
        aField_ID: ['!', 'number_cnap'],
        //aField_ID: ['house_number', 'gas_number', 'coolwater_number', 'hotwater_number', 'waterback_number', 'warming_number', 'electricity_number', 'garbage_number'],
        sMessage: 'Перевірте правильність уведеного номеру (літери не дозволені до заповнення)'
      }
      //,CustomFormat_NumberKadastr:  //унифицированный валидатор проверки кодов/номеров(с кастомным сообщением), с заданным количеством и последовательностью знаков
      ,CustomFormat_1: { //унифицированный валидатор проверки кодов/номеров(с кастомным сообщением), с заданным количеством и последовательностью знаков
        aField_ID: ['landNumb'],
        //sFormat: 'хххххххххх:хх:ххх:хххх',
        sFormat: '##########:##:###:####',
        sMessage: 'Невірний кадастровий номер, введіть кадастровий номер у форматі хххххххххх:хх:ххх:хххх'
      }
      ,FileSign : {
        aField_ID: ['bankId_scan_inn1','bankId_scan_passport1']
      }
      ,FileExtensions_1: { //унифицированный валидатор расщирений файлов
        aField_ID: ['file1','file2'],
        saExtension: 'jpg,pdf,png',
        sMessage: 'Недопустимий формат файлу! Повинно бути: {saExtension}!'
      }
      ,FieldNotEmptyAndNonZero: {
        aField_ID: ['sFieldNotEmptyAndNonZero', 'sID_Public_SubjectOrganJoin']
      }
    },
    attributes: {
      Editable_1: {aField_ID:['sPhone_User1', 'sMail_User1', 'bankIdlastName1'], bValue: true}
    },
    motion: {
      ReplaceTextSymbols_1: {
        sID_Field: "Registrationservice",
        nSymbols: 2,
        sValueNew: "ого",
        sID_Element_sValue: "registration-service"
      }
    }
});
