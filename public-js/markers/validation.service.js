/**
 * Сервіс ValidationService надає послуги з валідації форм, використовуючи маркери валідації.
 * Вони входять до об`єкту markers і мають посилання на поля, що потребують валідації, а також параметри валідації, наприклад формат дати для валідатора дати і т.п.
 * Посилання на поля форми записані у масиві aField_ID маркера як назви полів форми ($name). Це можливість пов'язати маркер валідації і валідатор з даним полем, див. @setValidatorByMarker.
 * Маркер має опціональний параметр sMessage - повідомлення про помилку валідації для користувача. TODO: перевірити
 *
 * Різні маркери можуть призначатися одним і тим же полям форми.
 * Фактична назва полів может бути довільною (NumberBetween_1, NumberBetween_Floor_Subs, NumberBetween_MaxBlocks і т.д.)
 *
 * Валідатори можуть доповнювати один одного (в елементах motion це також зроблено). TODO: перевірити ще раз.
 * Працює наслідування маркерів (як і в motion)
 * Валідатори за замовчуванням вважають пусті поля валідними. TODO: уточнити, чи усі без винятку валідатори мають лдіяти саме так.
 *
 * Більше вимог до валідації: i/issues/375, 654 та 685.
 */

angular.module('iGovMarkers').service('ValidationService', ['moment', 'amMoment', 'angularMomentConfig', 'iGovMarkers', ValidationService])
  .constant('angularMomentConfig', {
    preprocess: 'utc',
    timezone: 'Europe/Kiev',
    format: 'HH:mm:ss, YYYY-MM-DD'
  });

// TODO .value('defaultDateFormat', 'YYYY-MM-DD' );

function ValidationService(moment, amMoment, angularMomentConfig, MarkersFactory) {

  this.markers = MarkersFactory.getMarkers();

  var self = this;

  self.sFormat = 'YYYY-MM-DD';
  self.oFormDataParams = null;

  // Це для того, щоб бачити дати в українському форматі.
  // FIXME: хардкод значення locale
  (moment.locale || moment.lang)('uk');
  amMoment.changeLocale('uk');

  self.getValidationMarkers = function () {
    return self.markers;
  };

  /**
   * Основний метод валідації. Призначає валідатори полям форми form (за назвами полів з параметру markers),
   * а також проводить першу валідіацію, якщо параметр immediateValidation === true;
   *
   * Параметри:
   * @param form - форма, яку треба валідувати за маркерами валідації. Обов'язковий параметр.
   * @param {object} markers - маркери валідації. Необов'язковий параметр. Якщо він відсутній, то спрацюють маркери за замовчуванням, див. _resolveValidationMarkers
   * @param {boolean} immediateValidation - необов'язковий, вказує, чи треба проводити першу валідацію одразу після призначення валідаторів.
   * @param {boolean} newRow - необов'язковий, використовується для валідації таблицi при додаваннi нового рядка
   */
  self.validateByMarkers = function (form, markers, immediateValidation, data, newRow) {

    // Якщо маркери валідації прийшли зовні - то використати їх
    function _resolveValidationMarkers(markers) {
      if (markers) {
        self.markers = markers;
      }
      return self.markers;
    }

    markers = _resolveValidationMarkers(markers);

    // Немає маркерів - немає валідатора. Виходимо з функції
    if (!markers || !markers.validate || markers.validate.length < 1) {
      return;
    }

    // поместил в проверку тк ранее при сабмите, если параметр data не передавали, formData не могла взять параметры с undefined
    // и были ошибки
    if (data) {
      self.oFormDataParams = data.formData.params || {};
    }

    angular.forEach(markers.validate, function (marker, markerName) {
      /*
      var isOrganJoinInclude = false;
      var isOrganNoInclude = true;
      for(var nFieldID = 0; nFieldID < marker.aField_ID.length; nFieldID++){
        if (marker.aField_ID[nFieldID] === "sID_Public_SubjectOrganJoin"){
          isOrganJoinInclude = true;
        }
        if (marker.aField_ID[nFieldID] === "organ"){
          isOrganNoInclude = false;
        }
      }
      if(isOrganJoinInclude && isOrganNoInclude){
        marker.aField_ID.push("organ");
        if(self.oFormDataParams.sID_Public_SubjectOrganJoin){
          marker.original = self.oFormDataParams.sID_Public_SubjectOrganJoin;
        }
      }
*/
      angular.forEach(form, function (formField) {

        self.setValidatorByMarker(marker, markerName, formField, immediateValidation, false, newRow);
      });
    });
  };

  self.trimMarkerName = function(markerName){
    if (markerName.indexOf('CustomFormat_') == 0)
      markerName = 'CustomFormat'; //in order to use different format rules at the same time
    if (markerName.indexOf('FileExtensions_') == 0) {
      markerName = 'FileExtensions';
    }
    if (markerName.indexOf('FieldNotEmptyAndNonZero_') == 0) {
      markerName = 'FieldNotEmptyAndNonZero';
    }
    if (markerName.indexOf('NumberBetween_') == 0) {
      markerName = 'NumberBetween';
    }
    if (markerName.indexOf('NumberFractionalBetween_') == 0) {
      markerName = 'NumberFractionalBetween';
    }
    if (markerName.indexOf('Numbers_Accounts_') == 0) {
      markerName = 'Numbers_Accounts';
    }
    return markerName;
  };

  self.setValidatorByMarker = function (marker, markerName, formField, immediateValidation, forceValidation, newRow) {

    markerName = self.trimMarkerName(markerName);

    var keyByMarkerName = self.validatorNameByMarkerName[markerName];
    var fieldNameIsListedInMarker = formField && formField.$name && _.indexOf(marker.aField_ID, formField.$name) !== -1;
    var existingValidator = formField && formField.$validators && formField.$validators[keyByMarkerName];

    // для того чтобы валидация работала в таблице, нужно добраться до полей, вводится доп проверка.
    if(!fieldNameIsListedInMarker) {
      angular.forEach(formField, function (field) {
        if(field && typeof field === 'object' && field.$name && field.$name !== 'form') {
          if(!newRow) field = formField;
          angular.forEach(field, function (table) {
            if(table && table.$name && table.$name !== 'form') {
              for (var i = 0; i < marker.aField_ID.length; i++) {
                if (table.$name.indexOf(marker.aField_ID[i]) !== -1) {
                  var keyByMarkerName = self.validatorNameByMarkerName[markerName];
                  var fieldNameIsListedInMarker = table;
                  var existingValidator = table && table.$validators && table.$validators[keyByMarkerName];
                  if ((fieldNameIsListedInMarker /* || forceValidation */ ) && !existingValidator) {
                    var markerOptions = angular.copy(marker) || {};
                    if (marker.aField_ID && !table.$name.indexOf(marker.aField_ID[i]) > -1) marker.aField_ID.push(table.$name);
                    markerOptions.key = keyByMarkerName;
                    if (!table.$validators) return true;
                    table.$validators[keyByMarkerName] = self.getValidatorByName(markerName, markerOptions, table);
                    if (immediateValidation === true) table.$validate();
                    if (markerOptions.inheritedValidator && typeof markerOptions.inheritedValidator === 'string') {
                      self.setValidatorByMarker(marker, markerOptions.inheritedValidator, table, immediateValidation, true);
                    }
                  } else if (fieldNameIsListedInMarker && existingValidator) {
                    if (immediateValidation === true) table.$validate();
                  }
                }
              }
            }
          });
        }
      })
    }
    // Встановлюємо валідатор Angular - тільки для поля, що згадується у маркері валідації, і тільки один раз.
    if ((fieldNameIsListedInMarker /* || forceValidation */ ) && !existingValidator) {

      // запам'ятовуємо опції маркера щоб передати параметри типу sMessage, sFormat, bFuture, bLess, nDays ітн.
      var markerOptions = angular.copy(marker) || {};
      markerOptions.key = keyByMarkerName;

      // TODO на бете иногда не передается $validators из-за чего форма виснет, хардкод
      if(!formField.$validators) {
        return true
      }

      formField.$validators[keyByMarkerName] = self.getValidatorByName(markerName, markerOptions, formField);

      // ...і проводимо першу валідацію, якщо треба
      if (immediateValidation === true) {
        formField.$validate();
      }

      // реалізація наслідування
      if (markerOptions.inheritedValidator && typeof markerOptions.inheritedValidator === 'string') {
        // використати існуючий валідатор
        self.setValidatorByMarker(marker, markerOptions.inheritedValidator, formField, immediateValidation, true);
      }
    } else if (fieldNameIsListedInMarker && existingValidator) {
      if (immediateValidation === true) {
        formField.$validate();
      }
    }
  };

  // Це необхідно також для відображення помилок валідації у UI
  // @todo FIXME це хардкод, треба зробити його частиною маркерів валідації
  self.validatorNameByMarkerName = {
    Mail: 'email',
    AutoVIN: 'autovin',
    PhoneUA: 'tel',
    TextUA: 'textua',
    TextRU: 'textru',
    DateFormat: 'dateformat',
    DateElapsed: 'dateelapsed',
    CodeKVED: 'CodeKVED',
    CodeEDRPOU: 'CodeEDRPOU',
    CodeMFO: 'CodeMFO',
    NumberBetween: 'numberbetween',
    NumberFractionalBetween: 'numberfractionalbetween',
    Numbers_Accounts: 'numbersaccounts',
    DateElapsed_1: 'dateofbirth',
    CustomFormat: 'CustomFormat',
    FileSign: 'FileSign',
    FileExtensions: 'FileExtensions',
    FieldNotEmptyAndNonZero: 'FieldNotEmptyAndNonZero'
  };

  /**
   * Отримати замикання функції-валідатора за назвою маркера валідації
   * @param {string} markerName Назва маркера валідації з об'єкту markers
   * @param {object} markerOptions Опції маркера валідацїї, передаються у функцію-валідатор
   * @param formField - поле форми, яке будемо валідувати даним маркером, Необов`язковий параметр.
   * @returns {function|null} функція-замикання або null для неіснуючого валідатора (тобто такого, що не знаходиться за даним markerName).
   */
  self.getValidatorByName = function (markerName, markerOptions, formField) {
    var fValidator = self.validatorFunctionsByFieldId[markerName];
    // замикання для збереження опцій
    var validationClosure = function (modelValue, viewValue) {
      var result = null;
      // зберігаємо опції для замикання
      var savedOptions = markerOptions || {};
      if (fValidator) {
        result = fValidator.call(self, modelValue, viewValue, savedOptions);
        // Якщо валідатор зберіг помилку у savedOptions.lastError, то привласнити її полю форми
        if (formField && formField.$error && savedOptions.lastError) {
          formField.lastErrorMessage = savedOptions.lastError;
        }
      }
      if(result === null){
        result = true;
      }
      return result;
    };
    return validationClosure;
  };

  /** Об`єкт з переліком функцій-валідаторів.
   @todo Розглянути можливість винесення цих функцій назовні (структура нечітка, файл розростається).
   */
  self.validatorFunctionsByFieldId = {
    /**
     * 'Mail' - перевіряє адресу електронної пошти
     */
    'Mail': function (modelValue, viewValue) {
      var bValid = true;
      var EMAIL_REGEXP = /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i;
      bValid = bValid && EMAIL_REGEXP.test(modelValue);
      return bValid;
    },

    /**
     * 'AutoVIN' - Логика: набор из 17 символов.
     * Разрешено использовать все арабские цифры и латинские буквы (А В C D F Е G Н J К L N М Р R S Т V W U X Y Z),
     * За исключением букв Q, O, I. (Эти буквы запрещены для использования, поскольку O и Q похожи между собой, а I и O можно спутать с 0 и 1.)
     */
    'AutoVIN': function (sValue) {
      if (!sValue) {
        return false;
      }

      var bValid = true;
      bValid = bValid && (sValue !== null);
      bValid = bValid && (sValue.length === 17);
      bValid = bValid && (/^[a-zA-Z0-9]+$/.test(sValue));
      bValid = bValid && (sValue.indexOf('q') < 0 && sValue.indexOf('o') < 0 && sValue.indexOf('i') < 0);
      bValid = bValid && (sValue.indexOf('Q') < 0 && sValue.indexOf('O') < 0 && sValue.indexOf('I') < 0);
      return bValid;
    },

    'PhoneUA': null,

    /**
     * 'TextUA' - Усі українскі літери, без цифр, можливий мінус (дефіс) та пробіл
     * Текст помилки: 'Текст може містити тількі українські літери або мінус чи пробіл'
     */
    'TextUA': function (modelValue, viewValue) {
      if (modelValue === null || modelValue === '') {
        return true;
      }
      var TEXTUA_REGEXP = /^[ААБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯабвгґдеєжзиіїйклмнопрстуфхцчшщьюя`'-\s]+$/g;
      var TEXTRU_ONLY = /[ЁёЪъЫыЭэ]+/g;
      var bValid = TEXTUA_REGEXP.test(modelValue) && !TEXTRU_ONLY.test(modelValue);
      return bValid;
    },

    /**
     * 'TextRU' - Усі російські літери, без цифр, можливий мінус (дефіс) та пробіл
     * Текст помилки: 'Текст може містити тількі російські літери або мінус че пробіл'
     */
    'TextRU': function (modelValue, viewValue) {
      if (modelValue === null || modelValue === '') {
        return true;
      }
      var TEXTRU_REGEXP = /^[АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя-\s]+$/g;
      var TEXTUA_ONLY = /[ҐЄІЇґєії]+/g;
      var bValid = TEXTRU_REGEXP.test(modelValue) && !TEXTUA_ONLY.test(modelValue);
      return bValid;
    },

    /**
     * 'DateFormat' - Дата у заданому форматі DATE_FORMAT
     * Текст помилки: 'Дата може бути тільки формату DATE_FORMAT'
     * Для валідації формату використовується  moment.js
     */
    'DateFormat': function (modelValue, viewValue, options) {
      if (!options || !options.sFormat) {
        return false;
      }

      // Строга відповідніcть (нестрога - var bValid = moment(modelValue, options.sFormat).isValid())
      var bValid = (moment(modelValue, options.sFormat).format(options.sFormat) === modelValue);

      if (bValid === false) {
        options.lastError = options.sMessage || ('Дата може бути тільки формату ' + options.sFormat);
      }

      return bValid;
    },

      //test

    'DocumentDate': function (modelValue, viewValue, options) {
      if (!options || !options.sFormat) {
        return false;
      }

      var bValid = (moment(modelValue, options.sFormat).format(options.sFormat) === modelValue);

      if (bValid === false) {
        options.lastError = options.sMessage || ('Дата може бути тільки формату ' + options.sFormat);
      }

      return bValid;
    },

    /**
     * 'DateElapsed' - З/до дати у полі з/після поточної, більше/менше днів/місяців/років
     *
     *  Параметри:
     *  bFuture: false,  // якщо true, то дата modelValue має бути у майбутньому
     *  bLess: true,     // якщо true, то 'дельта' між modelValue та зараз має бути 'менше ніж' вказана нижніми параметрами
     *  nDays: 3,
     *  nMonths: 0,
     *  nYears: 1,
     *  sFormat: 'YYYY-MM-DD'
     *
     * Текст помилки: 'Від/до дати до/після сьогоднішньої має бути більше/менше ніж х-днів, х-місяців, х-років.
     * х-___        - підставляти тільки, якщо x не дорівнює 0
     * З/До         - в залежності від bFuture
     * більше/менше - в залежності від bLess
     */
    'DateElapsed': function (modelValue, viewValue, options) {

      // bFuture, bLess, nDays, nMonths, nYears
      var o = options;
      var bValid = true;
      var errors = [];
      var now = moment();
      var fmt = self.sFormat || options.sFormat;
      var modelMoment = moment(modelValue, fmt);

      // if (o.bDebug) {
      //   console.log((o.sDebug ? o.sDebug : '') + ' - зараз: ' + now.format(fmt) + ', ви увели: ' + modelMoment.format(fmt) + ', різниця: ' + deltaDays);
      // }

      options.lastError = '';

      // Повертаємо помилку, якщо опції не вказані або дата невалідна:
      if (!o || typeof o.bFuture === 'undefined' || !modelMoment.isValid()) {
        return false;
      }

      var nDays = o.nDays || 0;
      var nMonths = o.nMonths || 0;
      var nYears = o.nYears || 0;

      // Визначаємо різницю між датами
      var deltaDays = modelMoment.diff(now, 'days');
      var deltaMonths = modelMoment.diff(now, 'months');
      var deltaYears = modelMoment.diff(now, 'years');

      // myLog('DateElapsed: ', o);

      // Перевірка, чи виконується bFuture (дата має бути у майбутньому):
      var errorSuffix;

      if (o.bFuture === true && deltaDays < 1) {
        addError('Дата має бути у майбутньому, а ця — ' + (deltaDays === 0 ? 'ні' : getRealDeltaStr(true)));
        bValid = false;
      } else if (o.bFuture === false && deltaDays >= 1) {
        addError('Дата має бути у минулому, а ця — ' + (deltaDays === 0 ? 'ні' : getRealDeltaStr()));
        bValid = false;
      }

      function finalize() {
        // зберегти повідомлення про помилку у зовніщньому об'єкті опцій - замикання
        for (var errorName in errors) {
          // myLog(errors[errorName], 1);
          o.lastError = o.sMessage || errors[errorName];
        }
      }

      // Якщо вже є помилка - повертаємо помилку, далі перевіряти немає сенсу:
      if (bValid === false) {
        finalize();
        // myLog('-------------break ---------------', 2);
        return bValid;
      }

      // Якщо інших опцій немає - повертаємо рез-т
      if (typeof o.bLess === 'undefined') {
        return bValid;
      }

      addError(getErrorMessage());

      finalize();

      return bValid;

      // Допоміжні функції:

      // Генеруємо повідомлення про помилку
      function getErrorMessage() {
        // VID_DO: Від/до
        var sVidDo = o.bFuture ? 'До' : 'Від';
        // DO_PISLYA: до/після
        var sDoPislya = o.bFuture ? 'після' : 'до';
        // BIL_MEN: більше/менше
        var sBilMen = o.bLess ? 'менше' : 'більше';

        var maxDelta = moment.duration({
          days: nDays,
          months: nMonths,
          years: nYears
        }).as('days');

        var message = '{VID_DO} дати у полі {DO_PISLYA} поточної має бути {BIL_MEN} ніж {N_DAYS} {N_MONTHES} {N_YEARS}'; // - max Delta = ' + maxDelta

        // delta занадто велика - а o.bLess каже, що має бути менша:
        // TODO test it more
        if (o.bLess === true && Math.abs(deltaDays) > maxDelta || o.bLess === false && Math.abs(deltaDays) < maxDelta) {
          bValid = false;
          message = '' + message
              .replace('{VID_DO}', sVidDo)
              .replace('{DO_PISLYA}', sDoPislya)
              .replace('{BIL_MEN}', sBilMen)
              .replace('{N_DAYS}', self.pluralize(nDays, 'days'))
              .replace('{N_MONTHES}', self.pluralize(nMonths, 'months'))
              .replace('{N_YEARS}', self.pluralize(nYears, 'years'));
        } else {
          message = null;
        }
        return message;
      }

      // Альтеративний формат відображення помилки:
      function getAlternativeErrorMessage() {
        return [
          'Дата має бути у ',
          '' + (o.bFuture ? 'майбутньому' : 'минулому'),
          ' та відрізнятися ',
          '' + (o.bLess ? 'менше' : 'більше'),
          ', ніж на ' + getDeltaStr(options)
        ].join('');
      }

      function addError(msg) {
        if (msg) {
          errors.push(msg);
        }
      }

      // Використовуємо Moment для отримання рядків типу "рік тому", "за два дні" etc.
      function getRealDeltaStr(getFrom) {
        return getFrom ? modelMoment.from(now) : modelMoment.to(now);
      }

      function getDeltaStr() {
        var d = self.pluralize(nDays, 'days');
        var m = self.pluralize(nMonths, 'months');
        var y = self.pluralize(nYears, 'years');
        return (d ? d : '') + (m ? ', ' + m : '') + (y ? ', ' + y : '');
      }

      // TODO disable in release - it's dev only
      // function myLog(sMessage, l) {
      //   // чим більший рівень, тим більше інфи у консолі
      //   var logLevel = 1;
      //   if (l <= logLevel) {
      //     console.log('\t\t' + sMessage);
      //   }
      // }
    },

    /**
     Логика: две цифры точка две цифры (первые две цифры не могут быть 04, 34, 40, 44, 48, 54, 57, 67, 76, 83, 89)
     Сообщение: Такого КВЕД не існує - (ви не можете вписувати літери)
     */
    'CodeKVED': function (sValue) { //вид экономической деятельности по КВЕД.

      if (!sValue) {
        return false;
      }

      var bValid = true;
      bValid = bValid && (sValue !== null);
      bValid = bValid && (sValue.trim().length === 5);
      bValid = bValid && (sValue.trim().substr(2, 1) === '.');
      var s = bValid ? sValue.trim().substr(0, 2) : '';
      bValid = bValid && (s !== '04' && s !== '34' && s !== '40' &&
        s !== '44' && s !== '48' && s !== '54' && s !== '57' &&
        s !== '67' && s !== '76' && s !== '83' && s !== '89');

      // console.log('Validate CodeKVED: ', sValue, ' is valid: ' + bValid);
      //bValid = bValid && (/^[a-zA-Z0-9]+$/.test(sValue));
      //bValid = bValid && (sValue.indexOf('q') < 0 && sValue.indexOf('o') < 0 && sValue.indexOf('i') < 0);
      //bValid = bValid && (sValue.indexOf('Q') < 0 && sValue.indexOf('O') < 0 && sValue.indexOf('I') < 0);
      return bValid;
    },

    /**
     11) EDRPOU //код ЄДРПОУ.
     Логика: жестко восемь цифр, тип стринг(чтобы можно было ставить default=” ”)
     Сообщение: Такий код ЄДРПОУ не існує - (ви не можете вписувати літери)
     Поля: edrpou
     */
    'CodeEDRPOU': function (sValue) { //вид экономической деятельности по КВЕД.

      if (!sValue) {
        return false;
      }

      var bValid = true;
      bValid = bValid && (sValue !== null);
      bValid = bValid && (sValue.trim().length === 8);
      /* bValid = bValid && (sValue.trim().substr(2,1) === '.');
       var s=bValid ? sValue.trim().substr(0,2) : "";
       bValid = bValid && (s !== '04' && s !== '34' && s !== '40'
       && s !== '44' && s !== '48' && s !== '54' && s !== '57'
       && s !== '67' && s !== '76' && s !== '83' && s !== '89');
       */
      // console.log('Validate CodeEDRPOU: ', sValue, ' is valid: ' + bValid);
      //bValid = bValid && (/^[a-zA-Z0-9]+$/.test(sValue));
      //bValid = bValid && (sValue.indexOf('q') < 0 && sValue.indexOf('o') < 0 && sValue.indexOf('i') < 0);
      //bValid = bValid && (sValue.indexOf('Q') < 0 && sValue.indexOf('O') < 0 && sValue.indexOf('I') < 0);

      return bValid;
    },

    /**
     12) MFO //код банка.
     Логика: жестко шесть цифр.тип стринг.(чтобы можно было ставить default=” ”)
     Сообщение: Такого коду банку не існує - (ви не можете вписувати літери)
     Поля: mfo
     */
    'CodeMFO': function (sValue) { //вид экономической деятельности по КВЕД.

      if (!sValue) {
        return false;
      }

      var bValid = true;
      bValid = bValid && (sValue !== null);
      bValid = bValid && (sValue.trim().length === 6);
      /*var s=bValid ? sValue.trim().substr(0,2) : "";
       bValid = bValid && (s !== '04' && s !== '34' && s !== '40'
       && s !== '44' && s !== '48' && s !== '54' && s !== '57'
       && s !== '67' && s !== '76' && s !== '83' && s !== '89');
       */
      // console.log('Validate CodeMFO: ', sValue, ' is valid: ' + bValid);
      //bValid = bValid && (/^[a-zA-Z0-9]+$/.test(sValue));
      //bValid = bValid && (sValue.indexOf('q') < 0 && sValue.indexOf('o') < 0 && sValue.indexOf('i') < 0);
      //bValid = bValid && (sValue.indexOf('Q') < 0 && sValue.indexOf('O') < 0 && sValue.indexOf('I') < 0);

      return bValid;
    },

    /**
     'NumberBetween' - тільки цифри, максимум 3
     Текст помилки: options.sMessage або 'Число має бути між ' + options.nMin + ' та ' + options.nMax;
     Формат маркера:
     NumberBetween: { // Целочисленное между
        aField_ID: ['floors'],
        nMin: 1,
        nMax: 3,
        sMessage: ''
      }
     */
    'NumberBetween': function (modelValue, viewValue, options) {
      if (modelValue === null || modelValue === '') {
        return true;
      }
      if (!options || options.nMin === null || options.nMax === null) {
        return false;
      }
      var DIGITS_REGEXP = /^\d+$/;
      var bValid = DIGITS_REGEXP.test(modelValue) && modelValue >= options.nMin && modelValue <= options.nMax;

      if (bValid === false) {
        options.lastError = options.sMessage || ('Перевірте правильність заповнення - число має бути між ' + options.nMin + ' та ' + options.nMax);
      }

      return bValid;
    },

    /**
     Формат маркера:
     NumberFractionalBetween: { //Дробное число между
        aField_ID: ['total_place', 'warming_place'],
        nMin: 0,
        nMax: 99999999
      }
     Логика: разрешены только цифры, максимум 8
     Сообщение: Проверьте правильность заполнения поля - площадь помещения может состоять максимум из 8 цифр
     */
    'NumberFractionalBetween': function (modelValue, viewValue, options) {
      if (modelValue === null || modelValue === '') {
        return true;
      }
      if (!options || options.nMin === null || options.nMax === null) {
        return false;
      }

      var FRACTIONAL_REGEXP = /^[0-9]+[\.?[0-9]*]?$/;
      var bValid = FRACTIONAL_REGEXP.test(modelValue) && modelValue >= options.nMin && modelValue <= options.nMax;

      if (bValid === false) {
        options.lastError = options.sMessage || ('Перевірте правильність заповнення поля - число може складатися максимум з ' + options.nMax + ' цифр');
      }
      return bValid;
    },

    /**
     Формат маркера:
     Numbers_Accounts: { //разрешены цифры и дефисы, буквы любые запрещены
        aField_ID: ['house_number', 'gas_number', 'coolwater_number', 'hotwater_number', 'waterback_number', 'warming_number', 'electricity_number', 'garbage_number']
      }
     Логика: разрешены цифры и дефисы, буквы любые запрещены
     Сообщение: Проверьте правильность вводимого номера (буквы не разрешены к заполнению)
     */
    'Numbers_Accounts': function (modelValue, viewValue, options) {
      if (modelValue === null || modelValue === '') {
        return true;
      }
      if (!options) {
        return false;
      }

      var DIGITS_AND_DASH_REGEXP = /^[\d+\-*]*\d+$/g;
      var bValid = DIGITS_AND_DASH_REGEXP.test(modelValue);

      if (bValid === false) {
        options.lastError = options.sMessage || ('Перевірте правильність уведеного номеру (використовувати літери не можна)');
      }
      return bValid;
    },

    /**
     'CustomFormat' - кастомный формат номера
     Текст помилки: 'Невірний кадастровий номер, введіть кадастровий номер у форматі хххххххххх:хх:ххх:хххх'
     Формат маркера:
     CustomFormat_NumberKadastr: {
        aField_ID: ['landNumb'],
        sFormat: 'хххххххххх:хх:ххх:хххх'
        sMessage: 'Невірний кадастровий номер, введіть кадастровий номер у форматі хххххххххх:хх:ххх:хххх'
     }
     Исходная информация по валидации кадастрового номера: id: landNumb type: string формат:
     10 цифр:2 цифры:3 цифры:4 цифры (хххххххххх:хх:ххх:хххх)
     При неправильном введении выводить ошибку "Невірний кадастровий номер,
     введіть кадастровий номер у форматі хххххххххх:хх:ххх:хххх"
     }*/
    'CustomFormat': function (modelValue, viewValue, options) {
      console.log("viewValue=" + viewValue);
      if (modelValue === null || modelValue === '' || modelValue === undefined) {
        return true;
      }
      var bValid = true;

      console.log("modelValue=" + modelValue);
      if (bValid && (!options || options.sFormat === null)) {
        //return false;
        bValid = false;
      }
      console.log("options.sFormat=" + options.sFormat);
      var sValue = modelValue.trim();
      console.log("sValue=" + sValue);
      if (bValid && options.sFormat.length !== sValue.length) {
        //return false;
        bValid = false;
      }

      var nCount = sValue.length;
      console.log("nCount=" + nCount);
      var n = 0;
      while (bValid && n < nCount) {
        console.log("n=" + n);
        var s = sValue.substr(n, 1);
        console.log("s=" + s);
        var sF = options.sFormat.substr(n, 1);
        console.log("sF=" + sF);
        var b = false;
        if (sF === "#") {//х//#
          b = (s === "0" || s === "1" || s === "2" || s === "3" || s === "4" || s === "5" || s === "6" || s === "7" || s === "8" || s === "9");
        } else {
          b = (s === sF);
        }
        console.log("b=" + b);
        if (!b) {
          bValid = false;
          break;
        }
        n++;
      }
      console.log("bValid=" + bValid);
      //if (bValid === false) {
      if (!bValid) {
        options.lastError = options.sMessage || ('Невірний номер, введіть номер у форматі ' + options.sFormat);
      }
      return bValid;
    },

    'FileSign': function (modelValue, viewValue, options) {
      var bValid = true;
      if (modelValue && !modelValue.signInfo && !modelValue.fromDocuments) {
        bValid = false;
      }

      if (bValid === false) {
        options.lastError = options.sMessage || ('Підпис не валідний або відсутній');
      }
      return bValid;
    },

    /**
     * Кастомний валідатор розширення завантажуємих файлів
     * Формат маркеру:
     * FileExtensions_ExtValue: {
        aField_ID: ['bankId_scan_inn','file1','file2'],
        saExtension: 'jpg, pdf, png',
        sMessage: 'Недопустимий формат файлу! Повинно бути: {saExtension}!'
      }
     */
    'FileExtensions': function (modelValue, viewValue, options) {
      console.log("viewValue=" + viewValue);
      if (modelValue === null || modelValue === '') {
        return true;
      }
      var bValid = true;

      console.log("modelValue=" + modelValue);
      if (bValid && (!options || options.saExtension === null)) {
        bValid = false;
      }
      console.log("options.saExtension=" + options.saExtension);

      var sFileName = "";

      var params = self.oFormDataParams;
      for(var paramObj in params) if (params.hasOwnProperty(paramObj)){
        if(params[paramObj].value && params[paramObj].value.id && params[paramObj].fileName){
          if(modelValue.id === params[paramObj].value.id){
            sFileName = params[paramObj].fileName;
            break;
          }
        }
      }

      var aExtensions = options.saExtension.split(',');
      for(var convertedItem = 0; convertedItem < aExtensions.length; convertedItem++){
        aExtensions[convertedItem] = $.trim(aExtensions[convertedItem]);
        aExtensions[convertedItem].toLowerCase();
      }

      /* old validate algorithm
      var ext = sFileName.split('.').pop().toLowerCase();
      for (var checkingItem = 0; checkingItem < aExtensions.length; checkingItem++){
        if (ext === aExtensions[checkingItem]){
          bValid = true;
          break;
        } else {
          bValid = false;
        }
      }
      */

      // start new validate algorithm
      var sReversFileName = "";
      for (var charInd = sFileName.length - 1; charInd >= 0; charInd--){
        sReversFileName = sReversFileName + sFileName.charAt(charInd);
      }
      for (var checkingItem = 0; checkingItem < aExtensions.length; checkingItem++){
        var bCharValid = true;
        for(var chInd = 0; chInd < aExtensions[checkingItem].length; chInd++){
          if(bCharValid == true && sReversFileName.charAt(chInd).toLowerCase() === aExtensions[checkingItem].charAt((aExtensions[checkingItem].length - 1) - chInd).toLowerCase()){
            bCharValid = true;
          } else {
            bCharValid = false;
            break;
          }
        }
        if (bCharValid){
          bValid = true;
          break;
        } else {
          bValid = false;
        }
      }
      // end new validate algorithm

      console.log("bValid=" + bValid);

      if (!bValid) {
        if (!options.sMessage || options.sMessage === null || options.sMessage === "") {
          options.sMessage = 'Недопустимий формат файлу! Повинно бути: {saExtension}!';
        }
        var sMessage = MarkersFactory.interpolateString(options.sMessage, options, '{', '}');
        options.lastError = sMessage.value;
      }
      return bValid;
    },
    
    /**
     Логика: Не ипустота и не ноль
     */
    'FieldNotEmptyAndNonZero': function (modelValue) {
      if(modelValue == null || modelValue == "" && !oSubjectOrganJoin.required){
        return true
      }

      var sValue = modelValue;

      if (!sValue) {
        return false;
      }

      var oSubjectOrganJoin = this.oFormDataParams.sID_Public_SubjectOrganJoin;

      // if(options.original){
      sValue = oSubjectOrganJoin.value;

      var bValid = true;
      bValid = bValid && (sValue !== null);
      bValid = bValid && (sValue!==null && sValue.trim() !== "");
      bValid = bValid && (sValue!==null && sValue.trim() !== "0");
      return bValid;
    }
  };

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Утиліти
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  self.fromDateToDate = function (dateA, dateB, fmt) {
    var sFormat = fmt ? fmt : self.sFormat;
    return moment(dateA, sFormat).from(moment(dateB, sFormat));
  };

  /**
   * Перетворює числа nUnits та ключ sKey на слова такі як:
   * - 1 день, 2 дні, 5 днів,
   * - 1 місяць, 3 місяці, 10 місяців
   * - 1 рік, 4 роки, 5 років
   * @param {number} nUnits кількість днів, місяців чи років, які треба привести до множини
   * @param {string} sKey назва одиниці виміру часу: день, місяць чи рік.
   * @returns {string} множинна форма на кшталт "5 днів", "2 роки" і т.д.
   */
  self.pluralize = function (nUnits, sKey) {
    var types = {
      'days': {
        single: 'день',
        about: 'дні',
        multiple: 'днів'
      },
      'months': {
        single: 'місяць',
        about: 'місяці',
        multiple: 'місяців'
      },
      'years': {
        single: 'рік',
        about: 'роки',
        multiple: 'років'
      }
    };
    var sPluralized = nUnits === 0 ? '' : Math.abs(nUnits) === 1 ? types[sKey].single : Math.abs(nUnits) < 5 ? types[sKey].about : types[sKey].multiple;
    sPluralized = sPluralized === '' ? '' : Math.abs(nUnits) + ' ' + sPluralized;
    return sPluralized;
  };

  /**
   * What is it? Check here: http://planetcalc.ru/2464/
   */
  this.getLunaValue = function (id) {

    // TODO: Fix Alhoritm Luna
    // Number 2187501 must give CRC=3
    // Check: http://planetcalc.ru/2464/
    // var inputNumber = 3;

    var n = parseInt(id);

    //var n = parseInt(2187501);

    var nFactor = 1;
    var nCRC = 0;
    var nAddend;

    while (n !== 0) {
      nAddend = Math.round(nFactor * (n % 10));
      nFactor = (nFactor === 2) ? 1 : 2;
      nAddend = nAddend > 9 ? nAddend - 9 : nAddend;
      nCRC += nAddend;
      n = parseInt(n / 10);
    }

    nCRC = nCRC % 10;

    // console.log(nCRC%10);
    // nCRC=Math.round(nCRC/10)
    // console.log(nCRC%10);
    // console.log(nCRC);

    return nCRC;

  };

}
