Маркеры и Валидаторы позволяют работать с уже существующими полями и расширяют их возможности.

[Маркеры группы motion](https://github.com/e-government-ua/iBP/wiki/%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B8-%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80%D1%8B#%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B3%D1%80%D1%83%D0%BF%D0%BF%D1%8B-motion)  
[Маркеры группы validate](https://github.com/e-government-ua/iBP/wiki/%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B8-%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80%D1%8B#%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B3%D1%80%D1%83%D0%BF%D0%BF%D1%8B-validate)  
[Маркеры группы attributes](https://github.com/e-government-ua/iBP/wiki/%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B8-%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80%D1%8B#%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B3%D1%80%D1%83%D0%BF%D0%BF%D1%8B-attributes)  

## Маркеры группы motion
### ShowFieldsOnCondition  
показывают скрытое поле (при выполнении условий)

```java
{
  "motion": {
    "ShowFieldsOnCondition_1": {
      "aField_ID": [
        "foreign"                                    // показать поле foreign
      ],
      "asID_Field": {
        "sClient": "subekt"                          // если в поле subekt
      },
      "sCondition": "[sClient] == 'civil_community'" // выбран вариант civil_community
    }
  }
}
```

***

### ShowFieldsOnNotEmpty
показать доп поля, если заполнено конкретное поле
```java
{
  "motion": {
    "ShowFieldsOnNotEmpty_1": {
      "aField_ID": [
        "email"               // поле email будет показано
      ],
      "sField_ID_s": "client" // если поле client будет заполнено
    }
  }
}
```

***

### RequiredFieldsOnCondition
делают обязательным заполнение поля (при выполнении условий)
```java
{
  "motion": {
    "RequiredFieldsOnCondition_1": {
      "aField_ID": [
        "info1",                            // сделать обязательным для заполнения поле info1
        "file1"                             // сделать обязательным для загрузки файл file1
      ],
      "asID_Field": {
        "sClient": "subekt"                 // если поле subekt
      },
      "sCondition": "[sClient] == 'attr1'"  // был выбран вариант attr1
    }
  }
}
```

***

### ShowElementsOnTrue  
[Issues 816](https://github.com/e-government-ua/i/issues/816)  
маркер для принтформы  
показывает блок с конкретным идшником html кода принтформы  
```java
{
  "motion": {
    "ShowElementsOnTrue_12": {
      "aElement_ID": [
        "shapka",                   // кусок принтформы с идшником shapka будет показан
        "zayava"                    // кусок принтформы с идшником zayava будет показан
      ],
      "asID_Field": {
        "sCond": "sSubekt"          // если в поле sSubekt
      },
      "sCondition": "[sCond]=='ur'" // выбран вариант ur
    }
  }
}
```

***

### ValuesFieldsOnCondition
присваивает определенному полю определенные значения (при выполнении условий)  
[Issues 829](https://github.com/e-government-ua/i/issues/829)   
[Issues 1362](https://github.com/e-government-ua/i/issues/1362)  
```java
{
  "motion": {
    "ValuesFieldsOnCondition_1": {
      "aField_ID": [
        "info1",                            // записать в поле info1 значение oneValue
        "phone",                            // записать в поле phone вот такой номер телефона +380102030405
        "email"                             // записать в поле email значение из поля id_filed_email
      ],
      "asID_Field_sValue": [
        "oneValue",
        "+380102030405",
        "[id_filed_email]"
      ],
      "asID_Field": {
        "sClient": "subekt"                 // если в поле subekt
      },
      "sCondition": "[sClient] == 'attr1'"  // выбран вариант attr1
    }
  }
}
```

***

### WritableFieldsOnCondition
нерадактируемое поле становится редактируемым (при выполнении условий)  
```java
{
  "motion": {
    "WritableFieldsOnCondition_1": {
      "aField_ID": [
        "info1",                            // сделать поле info1 доступным для редактирования
        "file1"                             // дать возможность загрузить файл file1
      ],
      "asID_Field": {
        "sClient": "subekt"                 // если в поле subekt
      },
      "sCondition": "[sClient] == 'attr1'"  // выбран вариант attr1
    }
  }
}
```
     

***
### SplitTextHalf_1 - разделение значения по  знаку разделителя
```java
{
  "motion": {
    "SplitTextHalf_1": {
      "sID_Field": "bankIdPassport",   //поле bankIdPassport
      "sSpliter": " ",                 //парсить по пробелу
      "sID_Element_sValue1": "div1",
      "sID_Element_sValue2": "div2"
    }
  }
}
```

***



***

Для использования  маркеров из внешнего файла, указываем путь к файлу:  
[Issues 840](https://github.com/e-government-ua/i/issues/840)  
```xml
<activiti:formProperty id="markers2" name="extended_marker" type="markers"   
default="${markerService.loadFromFile('testmarkers.json')}" ></activiti:formProperty>
```
Допускается использование вложенных подпапок  
```default="${markerService.loadFromFile('folder_name/testmarkers.json')}" ```  
Маркеры хранятся в папке /wf-region/src/main/resources/bpmn/markers/motion

****

## Маркеры группы validate
### CustomFormat_1 - номеров
[Issues 934 ](https://github.com/e-government-ua/i/issues/934)  
```java
{
  "validate": {
    "CustomFormat_1": {
      "aField_ID": [
        "landNumb"
      ],
      "sFormat": "#########:#####:##",  //формат: 9 цифр:4 цифры:2 цифры
      "sMessage": "Невірний номер, введіть номер у форматі #########:#####:##"  //При неправильном введении выводить такой текст сообщения
    }
  }
}
```

***

### расширений
[Issues 1258](https://github.com/e-government-ua/i/issues/1258)
```java
{
  "validate": {
    "[FileExtensions_1": {
      "aField_ID": [
        "file1",
        "file2"
      ],
      "saExtension": "jpg,pdf,png",  //допускаются файлы с указанным через запятую расширением
      "sMessage": "Недопустимий формат файлу! Повинно бути: {saExtension}"
    }
  }
}
```

***

### NumberBetween - принадлежность значения  диапазону значений  (целочисленные)
```java
{
  "NumberBetween": {
    "aField_ID": [
      "numberBetween"
    ],
    "nMin": "1",
    "nMax": "999",
    "sMessage": "Перевірте правильність заповнення - число поверхів складається максимум з трьох цифр"
  }
}
```

***

### NumberFractionalBetween - принадлежность значения  диапазону значений (дробные)
```java
{
  "NumberFractionalBetween": {
    "aField_ID": [
      "total_place"
    ],
    "nMin": "0",
    "nMax": "99999999",
    "sMessage": "Перевірте правильність заповнення поля - площа приміщення може складатися максимум з 8 цифр"
  }
}
```

***

### Numbers_Accounts - номерных значений  
(разрешены цифры и дефисы, буквы любые запрещены )
```java
{
  "Numbers_Accounts": {
    "aField_ID": [
      "!",
      "number_cnap"
    ],
    "sMessage": "Перевірте правильність введеного номеру (літери не дозволені до заповнення)"
  }
}
```

***

### FileSign - Валидатор ЕЦП
[Issues 921](https://github.com/e-government-ua/i/issues/921)  
```java
{
  "validate": {
    "FileSign": {
      "aField_ID": [
        "form_signed",           //файлы, перечисленные в aField_ID будут проверены на наличие наложенной ЕЦП 
        "bankId_scan_inn",
        "bankId_scan_passport"
      ]
    }
  }
}
```
### Алгоритм Луна
Применяется для поля, в которое пользователь должен будет внести вручную номер заявки  
[Issues 1513](https://github.com/e-government-ua/i/issues/1513)  
```java
{
  "validate": {
    "OrderValue1": {
      "aField_ID": [
        "sCancelDoc"
      ]
    }
  }
}
```
## Маркеры группы attributes
### Line
для отрисовки линии (группирующей/отсекающей) одни поля от других
```java
{
  "attributes": {
    "Line_1": {
      "aElement_ID": [
        "idFieldFirstBefore"   //ИД-шники полей, перед которыми рисовать линию
      ],
      "sValue": "Текст"        //если задано, то по центру линии писать текст
    }
  }
}
```
***
### Style
Для внедрения стилей css на страницу с услугой с целью изменения стандартного отображения элементов на форме
```java
{
   "attributes" : { 
      "Style_1" : {
         "aElement_ID" : [			  //ID к элемента к которому применить
            "sLabel3",
            "bankIdlastName"
         ],
         "aSelectors" : [			 //правило css
            "#field-sLabel1",
            "#field-sLabel2 label:hover"
         ],
         "oCommonStyle" : {			 //перечень свойств и значений css
            "width" : "100%!important", 
            "text-align" : "center"
         }
      } 
   }
}
```
Маркер анализирует правила в свойствах aElement_ID и aSelectors и добавляет стили перечисленный из свойства oCommonStyle в блок ```<head>``` в виде отдельного стиля. Причем стилями можно влиять не только на элементы формы но на всю страницу.

Свойства маркера aElement_ID и aSelectors работают параллельно и **может быть задан только один из них**. 
[подробное описание](https://docs.google.com/document/d/1EE7q2EEBgHW6QMRJEsPXGNE0cU9GuXT2Z3KUYNceF88/edit)  

### атрибут маркера sNote
```java
{
  "motion": {
    "ShowFieldsOnCondition_1": {
      "aField_ID": [
        "info1"
      ],
      "asID_Field": {
        "sClient": "client"
      },
      "sNote": [                         //При необходимости использовать комментарии 
        "Comments_for_Markers"           //допустимо использование атрибута "sNote" в маркере
      ],
      "sCondition": "[sClient] == 'attr1'"
    }
  }
}
```
