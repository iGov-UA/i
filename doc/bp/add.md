# Детальная информация

001_CreatingBusinessProcesses.md

### _Businessprocessdevelopment
 [вернуться...](#Businessprocessdevelopment)
 * ![1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/1bp.jpg)
* ![screenshot of sample2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2bp.jpg)
* ![screenshot of sample3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3bp.jpg)
* [Создание бизнес-процесса дополнительно](https://docs.google.com/document/d/1B3OIYjj3S2YLwUR-PVD3FAcErl_2ua0CYUB5vys6O4U/edit )
* [Правила при именовании бизнес-процессов](https://github.com/e-government-ua/iBP/wiki/%D0%9E%D0%B1%D1%89%D0%B8%D0%B5-%D0%BF%D1%80%D0%B0%D0%B2%D0%B8%D0%BB%D0%B0-%D0%BF%D1%80%D0%B8-%D0%B8%D0%BC%D0%B5%D0%BD%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B8:#%D0%B1%D0%B8%D0%B7%D0%BD%D0%B5%D1%81-%D0%BF%D1%80%D0%BE%D1%86%D0%B5c%D1%81%D1%8B).  
* проработать инфокарты и бланки заявлений - составить перечень необходимых полей и условий
* прорисовать всю схему процесса от начала до конца, наполнить блоки информацией.
* создать принтформы

 * [вернуться...](#разработка бизнес-процесса)


### _Testingonbeta
_тестирование и проливка на бету и боевой
 [вернуться...](#Testingonbeta)
 * ![4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/4.jpg)
* ![5](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5.jpg)
* пройти процесс от начала до конца по всем возможным путям
* выслать заказчику инструкцию, ссылки, логин и пароль.
* в случае необходимости - вносить изменения в процесс. после внесения даже небольшого изменения - обязательное тестирование
* создать пользователей и группы на дельте - связать их между собой
* скопировать процессы в подпапки бета и прод. i\wf-region\src\main\resources\bpmn\autodeploy\prod
* заменить тестовые почты чиновников на настоящие
* перенести все связанные сущности в подпапки прод (при наличии там файлов, совпадающих по названиям в обоих папках)
* Если проливка происходит напрямую в ветках test-version или master обязательно необходимо осуществлять обратный мерж в нижние ветки
* после проливки на боевой проверить процесс хотя бы по одному сценарию, закрыть ишью.

002_Typesofparameters.md

### _string 
[вернуться...](#string)

строка - для текстовых полей ввода (ограничение 256 символов) 
![2_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_3.jpg)

### _enum
[вернуться...](#enum)

выпадающий список - для выбора значения из списка 

![2_4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_4.JPG)

### _enum (checkbox)

[вернуться...](#enum (checkbox))

чекбокс - доступно только 2 выбора да/нет.

Чтоб получить чекбокс, необходимо сделать поле с типом enum с двумя вариантами выпадающего списка.

Третьим атрибутом переменной **name** через ";" добавляем параметр **sID_CheckboxTrue** и приравниваем его к ид первого атрибута енума: sID_CheckboxTrue=first_enum_attribute

![2_11](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_11.JPG)

### _date

[вернуться...](#date)

дата - календарь для выбора даты
![2_5](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_5.JPG)

### _boolean
[вернуться...](#boolean)

принимаемые значения: true / false
### _label
[вернуться...](#label)

```xml
<activiti:formProperty id="info" name="Зверніть увагу" type="label" default="Ви можете здійснити оплату зручним для Вас способом"></activiti:formProperty>
```
 используется для отображения текстовых подсказок на форме подачи / обработки обращения 
 (обязательно добавлять default=” ”, если не указано другое значение). 
 
 Поддерживается форматирование html.
 ![2_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_6.JPG)
 
### _file
[вернуться...](#file)

После стартовой таски добавляем  сервис-таску для подтягивания всех документов в процесс, где указываем
`activiti:delegateExpression="#{fileTaskUpload}"`

На первой же юзертаске процесса необходи добавить только листнер:
```xml
<activiti:taskListener event="create" delegateExpression="${fileTaskUploadListener}"></activiti:taskListener>
```
на второй и далее только этот
```xml
<activiti:taskListener event="create" delegateExpression="${fileTaskInheritance}">
  <activiti:field name="aFieldInheritedAttachmentID">
    <activiti:expression>${file_id}</activiti:expression>
  </activiti:field>
</activiti:taskListener>
```
где file_id - id файла, который нужно отобразить
можно в виде ${file1},${file2},${file1} если файлов несколько
* ![2_8](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_8.JPG)

### _file (New)
[вернуться...](#file (New))

Чтобы обозначить что прикрепляемый файл должен использоваться по новой схеме, добавляем в нейм поля такую конструкцию:
**; ;bNew=true**  
```xml
    <activiti:formProperty id="sDoc1" name="Електронна форма документа; ;bNew=true" type="file"></activiti:formProperty>
```
* ![2_13](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_13.JPG)
Чтобы этот файл появился на следующем шаге процесса, Вам снова его нужно объявить как обычное поле. Не забудьте указать параметр  writable="false"
```xml
<activiti:formProperty id="sDoc1" name="Електронна форма документа; ;bNew=true" type="file" writable="false"></activiti:formProperty>
```
Этот же принцип касается полей типа table, т.к. тейблы превращаются в файлы.  
Если объявляете тейбл на втором шаге, и хотите чтоб тейбл был недоступен для редактирвоания - ставите флаг writable="false", при этом убираете весь параметр **default**.  

**Важно!** В новой схеме аттачей больше не нужно, **на второй и всех последующих юзертасках**, использовать листенер ${fileTaskInheritance}, т.к. аттач существует как обычная переменнная процесса и его не нужно перетаскивать с одного шага на другой при помощи листенера, достаточно её указать как переменную на юзертаске.
 
**Но !!!** на первую юзертаску необходимо цеплять листенер ${fileTaskUploadListener}  
```xml
<activiti:taskListener event="create" delegateExpression="${fileTaskUploadListener}"></activiti:taskListener>
```
(соответственно все файлы которые были подгружены на старттаске будут автоматически видны на первой юзертаске и их не нужно отдельно объявлять)
т.к. аттачи подгружаемые на централе хранятся во временной базе, и этот листенер их перетаскивает в постоянную.  

Теперь так же доступна подгрузка файлов в тейбл.

***
### _textArea
[вернуться...](#textArea)
* ![2_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_9.JPG)
### _queueData
[вернуться...](#queueData)
* ![2_31](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_31.JPG)

### _invisible

[вернуться...](#invisible)
* ![2_30](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_30.JPG)

### _select

[вернуться...](#_select)

Для того чтоб в выпадающем списке селекта выпадал заданный массив данных, необходимо правильно заполнить файлы [Subject](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subject), [SubjectOrgan](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorgan), [SubjectOrganJoin](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorganjoin), [SubjectOrganJoinAttribute](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorganjoinattribute), [ServiceData](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicedata) соответственно описанию.

###_table

[вернуться...](#_table)

**в бизнес-процессе**

синтаксис поля:  
name="Поточні рахунки у національній валюті; ;nRowLimit1" - третья позиция в name задает ограничение по количеству добавляемых строк.  
> name="имя поля; ;nRowsLimit=1" - кнопки "додати рядок" не будет, т.к. 1 строка только  
> name="имя поля; ;nRowsLimit=5" - кнопка "додати рядок" пропадет после добавления 5-й строки  

type="table"   
default=” “ - в дефолте в виде json - объекта прописываются свойства каждого столбца. Заложено наследование свойств каждого типа, можно задавать: обязательность заполнения полей, дефолтные записи и т.д. Атрибуты енама прописываются в качестве массива подобъектов. Пример джейсона со всеми типами данных:
```java
{
  "aField": [
    {
      "id": "sTableFieldString1",
      "name": "Стринг",
      "type": "string",
      "required": "true",
      "writable": "true",
      "bVisible": "false",
      "default": "дефолтная запись",
      "nWidth": "100"
    },
 {
      "id": "sFile01",
      "name": "file1",
      "type": "file",
      "required": "true",
      "writable": "true"
    }
]
}
```
**"nWidth": "100"** - это размер поля таблицы в пикселях   
**"nWidth": "40%"** - можно еще так, это означает, что этот столбец займет 40% ширины всей таблицы   
**"bVisible": "false"** - скрывает колонку, но она существует. По умолчанию значение - true   

Как и маркер, тейбл можно делать выносным:
```xml
<activiti:formProperty id="markers16" name="тейбл3 вынесенный в отдельный файл" type="table" default="${markerService.loadFromFile('table/VED/_test_ved_table3.json')}"></activiti:formProperty>
```
* ![2_32](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_32.JPG)
Если объявляете тейбл на втором шаге, и хотите чтоб тейбл был недоступен для редактирвоания - ставите флаг writable="false", при этом убираете весь параметр **default**.  

**на дашборде**

На дашборд каждая объявленная таблица приходит в виде отдельного файла, который можно только просмотреть, но не загрузить.
Т.к. таблица приходит на дашборд в виде файла - необходимо передать ИД-шник таблицы в листенере вместе с остальными файлами.
Отдельно в переменных таблицу прописывать не нужно.

**в принтформе**

Для того чтобы в принтформе отображались все добавленные заявителем строки таблицы необходимо добавить такой тип комментирования:
```html
<!--[sTable3-->
тут строчки которые клонируем
<!--sTable3]-->
```
комментрировать можно как целую строчку таблицы, так и отдельную ячейку

строка таблицы:
```html
<!--[sTable3-->
	<tr>
		<td >[sTableFieldString1]</td>
		<td >[sTableFieldLong1]</td>
		<td >[sTableFieldDate1]</td>
		<td >[sTableFieldEnum1]</td>
		<td >[sObjectCustoms]</td>
		<td >[sID_UA_ObjectCustoms]</td>
	</tr>
<!--sTable3]-->
```
отдельная ячейка:
12. Базисні умови поставки товару
```html
<!--[sTable4-->
[sBasis] [sBasisName]<br>
<!--sTable4]-->
```

***
### _line
[вернуться...](#_line)
* ![6_0M](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_0%D0%9C.JPG)

### _variableAttributes
(Атрибуты переменных)

[вернуться...](#variableAttributes)


**writable** - редактируемость поля  (true/false).  Необязательный элемент.
На стартовой таске все поля должны быть редактируемы (по умолчанию стоит флаг true).
Все без исключения. Иначе  при запуске процесса будут ошибки. 
На юзертаске все поля с флагом true  могут быть отмечены для уточнения (после нажатия на “внести зауваження”). 
На юзертаске все поля с флагом false недоступны для уточнения. Недоступны для перезаписи значения. Не должны быть отмечены как обязательные (иначе процесс нельзя будет  перевести на новый шаг)

**readable** - отображаемость поля. (true / false) по умолчанию стоит флаг true. Если поставить false, то поле не будет видно, но запись значения в такое поле блокируется. Необязательный атрибут.

**required** - обязательность поля к заполнению. (true / false) по умолчанию стоит флаг false. Необязательный атрибут.

**name** - в нашей расширенной версии активити тег состоит из нескольких частей. Сепаратором выступает точка с запятой.
name= "Имя;[description];[флаги]". Имя переменной/поля - отображается на  интерфейсе. Обязательный атрибут.

**description** - описание или  подсказка к этой переменной/полю. Отображается на интерфейсе на старттаске серым цветом. На юзертасках не отображается. Необязательный атрибут.

## флаги аттрибута name  
Используются для переопределения стандартных  атрибутов, имеющие более высокий приоритет на уровне отрисовки интерфейса на юзертасках. 

### **writable=false**  
Cделает текущее поле нередактируемым для пользователя интерфейса, при этом на уровне процесса поле остается редактируемым.
```xml
<activiti:formProperty id="sPlaceBirthChild9" name="Місце народження дев'ятої дитини; ;writable=false" type="string"></activiti:formProperty>
```
### **bNew=true**
Для обозначения, что прикрепляемый файл должен использоваться по новой схеме  
```xml
 <activiti:formProperty id="sDoc1" name="Електронна форма документа; ;bNew=true" type="file"></activiti:formProperty>
```
### **nRowsLimit=5**  
Задает ограничение по количеству добавляемых строк в таблице
```xml
 <activiti:formProperty id="sTable1" name="Поточні рахунки у національній валюті; ;nRowsLimit=5" type="table" default=""}]}"></activiti:formProperty>
``` 
### **html = sTextInHtmlFormat** 
В атрибуте name HTML-текст отделяется двойной точкой с запятой:  
`name=" sTitleText ; sDescription ;; html = sTextInHtmlFormat ;; key2 = value2 ;; key3 = value3 "`  
где:  
`sTitleText` - наименование поля;  
`sDescription` - описание поля (комментарий), которое будет выведено под наименование поля;  
`sTextInHtmlFormat` - текст HTML-кода, который будет выведен под комментарием к полю;  
`key2`, `key3`, `value2`, `value3` - прочие параметры, которые нужно передать в `property` в виде ключ-значение, разделенные двойными точками с запятыми.  
```xml
 <activiti:formProperty id="sString4" name="sString4; ниже добавится HTML с несколькими элементами;;html=&lt;span&gt;Відкити пошукову сторінку &lt;a href=&quot;https://www.google.com.ua/&quot; target=&quot;_blank&quot; title=&quot;Має відкритись пошукова сторінка Google&quot;&gt;Google&lt;/a&gt; або &lt;a href=&quot;https://yandex.ua/&quot; target=&quot;_blank&quot; title=&quot;Має відкритись пошукова сторінка Яндексу&quot;&gt;Яндекс&lt;/a&gt;.&lt;br&gt; Тут HTML-текст закінчується.&lt;/span&gt;" type="string"></activiti:formProperty>
```

003_ReservedandSystemVariables.md
### _attributesBankID

[вернуться...](#attributesBankID)
* **bankId_scan_passport** - file - скан паспорта гражданина
* **bankIdAddressFactual** - string - адрес регистрации гражданина
* **bankIdAddressFactual_country** - string/invisible - страна 
* **bankIdAddressFactual_state** - string/invisible - область
* **bankIdAddressFactual_area** - string/invisible - район
* **bankIdAddressFactual_city** - string/invisible - город
* **bankIdAddressFactual_street** - string/invisible - улица
* **bankIdAddressFactual_houseNo** - string/invisible - дом
* **bankIdAddressFactual_flatNo** - string/invisible - квартира регистрации
* **bankIdinn** - string - инн заявителя
* **bankIdbirthDay** - string - дата рождения гражданина (у форматі ДД.ММ.РРРР)
* **bankIdemail** - string - емейл гражданина
* **bankIdphone** - string -телефон гражданина
* **bankIdsID_Country** - string - гражданство
* **bankId_scan_inn** - file - Скан копия ИНН гражданина
* ![3_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_0.JPG)

### _reservedvariablesforelectronicqueues

[вернуться...](#reservedvariablesforelectronicqueues)
* ![3_5](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_5.JPG)

### _reservedattributevariables
Зарезервированные переменные атрибутов

[вернуться...](#reservedattributevariables)
* **sAddress** - string/invisible/label - адрес органа
* **sMailClerk** - string/invisible/label - почта чиновника
* **sArea** - string/invisible/label - yазвание нас.пункта/района куда подается заявка
* **nArea** - string/invisible/label - yомер в справочнике нас.пункта/района куда подается заявка
* **sShapka** - string/invisible/label - шапка принтформы
* ![3_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_1.JPG)

### _variablesforprintforms
Переменные принтформ

[вернуться...](#variablesforprintforms)
* ![3_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_6.JPG)
* **[sDateTimeCreateProcess]** - Возвращает значение системной даты-времени на момент сохранения\подачи заявки гражданином.
* **[sDateCreateProcess]**- Возвращает значение системной даты на момент сохранения\подачи заявки гражданином.
* **[sTimeCreateProcess]** - Возвращает значение системного времени на момент сохранения\подачи заявки гражданином.
* **[sCurrentDateTime]** - Возвращает значение системной даты и времени на текущий момент.
* **sBody** - invisible - задать печатную форму.  
Прописывается в юзертаске. Для корректной работы обязательно надо прописать листнер “fileTaskInheritance”
Путь на печатную форму в папке patterns задается в поле name (типа [pattern/print/subsidy_zayava.html]) 
* **PrintForm** - Позволяет автоматически создавать файл из соответствующей принтформы, который потом можно подгружать к вложениям в письмо на сервис-таске (используем ${PrintForm_1} при отправке письма с вложениями). Номер PrintForm должен совпадать с номером sBody.

### _validatedvariables
Валидируемые переменные

[вернуться...](#validatedvariables)
* ![3_4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_4.JPG)
* **privatePhone, workPhone, phone** - string - номер телефона.
Первый символ “+”, остальные 12 цифр
* **lastName_UA1,  firstName_UA1, middleName_UA1, lastName_UA2,  firstName_UA2, middleName_UA2**  - string - Название или ФИО с украинскими буквами. Разрешена только кириллица, дефис, апостроф.
* **lastName_RU1,  firstName_RU1, middleName_RU1, lastName_RU2,  firstName_RU2, middleName_RU2** - string - Название или ФИО с русскими буквами. Разрешена только кириллица, дефис.
* **date_of_birth** - date - дата рождения. Не разрешено выбирать дату больше текущей.
* **kved** - string - вид экономической деятельности по КВЕД. Две цифры точка две цифры (первые две цифры не могут быть 04, 34, 40, 44, 48, 54, 57, 67, 76, 83, 89).
* ![3_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_2.JPG)
* **edrpou** - string - восемь цифр.
* ![3_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_3.JPG)
* **mfo** - string - шесть цифр.
* **kids_Birth** - date - не разрешено выбирать дату больше текущей, разница между текущей датой и выбранной не должна превышать 14 лет.
* **privateMail, email** - string - емейлы
* **landNumb**- string - кадастровый номер в формате хххххххххх:хх:ххх:хххх

### _other
Другие

[вернуться...](#other)
* **bReferent** - invisible - признак заполнения заявки референтом (true/false).
* **form_signed** - если объявлена эта переменная на стартовой форме, то при нажатии на кнопку "замовити послугу" заявитель будет перенаправлен на доп.страницу для наложения ЕЦП на заявку.
* ![3_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_9.JPG)
* **form_signed_all** - при наложении ЕЦП на заявку, она так же будет наложена и на все прикрепленные файлы. При этом все файлы, которые прикрепил гражданин, должны иметь расширение *.pdf.

### _autoComplete
Автокомплиты

[вернуться...](#autoComplete)
* ![3_7](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_7.JPG)
* **sID_UA_Country** - Код страны (заполнится автоматически после выбора в селекте sCountry)
* **sCurrency** - select - Валюта 
* **sID_UA_Currenc**y - Код валюти (заполнится автоматически после выбора в селекте sCurrency)
* **sSubjectOrganJoinTax** - select - Таможня
* **sID_UA_SubjectOrganJoinTax** - Код таможни (заполнится автоматически после выбора в селекте sSubjectOrganJoinTax)
* **sID_Place_UA** - string - В переменную передается КОАТУУ выбранного населенного пункта (поле Place)

### _requestvariables
Переменные-запросы

[вернуться...](#requestvariables)

* **sShortName_SubjectOrgan_** - string - Краткое наименование
* **sLocation_SubjectOrgan_** - textArea - Адрес регистрации
* **sCEOName_SubjectOrgan_** - string - ФИО руководителя
* **sID_SubjectActionKVED_SubjectOrgan_** - string - Основной КВЕД - доступен для редактирования как автокомплит
* **sNote_ID_SubjectActionKVED_SubjectOrgan_** - label - Полное наименование выбранного КВЕДа - автоматически обновится при изменении **sID_SubjectActionKVED_SubjectOrgan_**
* **sDateActual_SubjectOrgan_** - label - Дата получения данных из ЄДРПОУ
``` xml
        <activiti:formProperty id="markers_01" name="лінія ЄДРПОУ 01" type="markers" default="{&quot;attributes&quot;:{&quot;Line_01&quot;:{&quot;aElement_ID&quot;:[&quot;sID_SubjectOrgan_OKPO_01&quot;],&quot;sValue&quot;:&quot;Дані з ЄДРПОУ - 01&quot;}}}"></activiti:formProperty>
        <activiti:formProperty id="sID_SubjectOrgan_OKPO_01" name="Введіть код ЄДРПОУ" type="string" required="true"></activiti:formProperty>
        <activiti:formProperty id="sFullName_SubjectOrgan_01" name="Найменування повне" type="textArea" required="true"></activiti:formProperty>
        <activiti:formProperty id="sShortName_SubjectOrgan_01" name="Найменування скорочене" type="string" required="true"></activiti:formProperty>
        <activiti:formProperty id="sLocation_SubjectOrgan_01" name="Адреса" type="textArea" required="true"></activiti:formProperty>
        <activiti:formProperty id="sCEOName_SubjectOrgan_01" name="ПІБ керівника" type="string" required="true"></activiti:formProperty>
        <activiti:formProperty id="sID_SubjectActionKVED_SubjectOrgan_01" name="Основний КВЕД" type="string" required="true"></activiti:formProperty>
        <activiti:formProperty id="sNote_ID_SubjectActionKVED_SubjectOrgan_01" name="" type="label" default=" "></activiti:formProperty>
        <activiti:formProperty id="sDateActual_SubjectOrgan_01" name="Дата отримання інформації з ЄДРПОУ" type="label" default=" "></activiti:formProperty>
```
Приведенные переменные не обязательны для вывода, использование и порядок вывода - произвольные. Однако желательно придерживаться общего дизайна. Для единообразного использования данного виртуального "блока данных" установлен дизайн, который обязательно начинается с разделительной линии содержащей общее наименование идущих за ним данных.
Полный блок выглядит на форме следующим образом.

**Незаполненный**
![Незаполенный](https://goo.gl/dCRcRQ)

**Заполненный**
![Заполненный](https://goo.gl/UtPH43)

***


### 004_Generalrulesfornaming.md
### _variables

[вернуться...](#variables)
* a = Array (массивы) - anPrice, asName, aDepartament
* o = Object (обьекты/когда трудно определить строка или число) - oVisitDate
* b = boolean 

Для элементов enum помимо id доступно поле name.
например (скоро будет реализовано):
name="name;13,40;decimal;depCode=dep9345,depNum=44"

где:
* 1-й разряд = название (выводится в интерфейс)
* 2-й разряд = значение //опционально
* 3-й разряд = тип //опционально (те-же типы, что и в активити)
* 4-й разряд = набор присвоений переменных, через запятую //опционально

## бизнес-процеcсы
Наименование процесса должно строиться по принципу  
`{орган}_{номер услуги в Service}_{суть}_{приставка}`  
{суть} и {приставка} - опционально (т.е. не обязательно)  
{номер услуги в Service} - будет всегда состоять из 4х цифр, чтоб была правильная сортировка  
приставка нужна для того, что если у Вас на один и тот же номер сервиса будет несколько бп  
Наименование и ID процесса должны совпадать.
например
  
zags_0705_changeName   
zags_0710_death  

eco_0520_auditori  
eco_0521  
* ![4_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/4_0.JPG)
### {орган} Приставки для именования БП
**dfs** - налоговая  
**dms** - миграционная  
**dpss** - ДержПродСпоживСлужба (екс-СЕС)  
**zem** - услуги по земле  
**eco** - экология  
**zags** - загсы  
**upszn** - услуги соц.помощи  
**justice** - юстиция  
**infrastr** - услуги мин.инфраструктуры  
**rvk** - военкомат  
**dvs** - исполнительная служба  
**kids** - служба по делам детей  
**oda** - услуги ОДА  
**rada** - услуги местных органов власти (гор.советы, сельские советы, районные рады, поселковые советы)  
**med** - медицинские услуги  
* ![4_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/4_1.JPG)
## группы и пользователи
## принтформы
## выносные файлы

### _userscolor
Подсвечивать этап (юзертаску) в дашборде  цветом

[вернуться...](#userscolor)

* "_green" - подкрашивать строку - зеленым цветом (класс: "bg_green")
* "usertask1" - подкрашивать строку - салатовым цветом (класс: "bg_first")
* ![4_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/4_2.jpg)
* ![4_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/4_3.JPG)

005_TypesListeneranddelegateExpression.md
### _listener

[вернуться...](#listener)
   * ![5_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_0.jpg)
   * ${CreateDocument_UkrDoc}
   * ${GetDocument_UkrDoc}
   * ${UpdateStatusTask}
   * ${DocumentInit_iDoc}  
   * ![5_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_3.JPG)
   
### _setTasks

[вернуться...](#setTasks)
**sTaskProcessDefinition** - сюда прописываем ИД БП в который нужно пробросить данные **(3)**  
далее перечисляем обязательные поля **(5)**  
**sID_Attachment**  
**sDateRegistration**  
**sDateDoc**  
**sName_SubjectRole**  
**sDateExecution**  
**processDefinitionId**  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sNG44eU1GSmlkUjg)  
Так же в параметре листенера **soData** **(4)** можно передать другие поля, необходимые в процессе в формате:

```
sContent::${sContent};;sAutorResolution::${sAutorResolution};;
```  
разделитель между переменными - две точки с запятой.

### _documentInit_iDoc

[вернуться...](#documentInit_iDoc)
Листенер тянет из файла json данные, которые задают права определенных групп на просмотр или редактирование отдельных полей в данном бизнес-процессе.  
Файл json должен иметь такое же имя как ИД БП, в котором установлен листенер.  
Например:  
**_doc_justice_171.bpmn**  
**_doc_justice_171.json**  
Путь, где должны хранится файлы json  
**i\wf-region\src\main\resources\pattern\document**  
Рассмотрим подробнее файл джейсона  
```java
{
  "_": {
    "MJU_Dnipro_Top1_Dep1_Exec1": {			// вот этому логину
      "sName": "Контроллирующий всех этапов",
      "bWrite": true,					//даны права на редактирование полей
      "asID_Field_Read": [
        "*"						//чтение всех полей
      ],
      "asID_Field_Write": [
        "*"						//редактировоание всех полей
      ]
    },
    "MJU_Dnipro_Top1_Dep1_Exec3": {			//вот этому логину
      "sName": "Основной контролирующий",
      "bWrite": true,					//даны права на редактирование полей
      "asMask_FieldID_Read": [				//чтение всех полей
        "*",						
        "!sID_Group_Activiti",				// кроме sID_Group_Activiti, nDeepLevel
        "!nDeepLevel"
      ],
      "asMask_FieldID_Write": [
        "sDateExecution",
        "sContent"
      ]
    }
  },
  "checker": {
    "MJU_Dnipro_Top1_Dep1_Exec5": {
      "sName": "Проверяющий",
      "bWrite": false
    }
  }
}
```
***

### _updateStatusTask
[вернуться...](#updateStatusTask)
Все статусы задаются в файле: _i\wf-base\src\main\resources\data\ProcessSubjectStatus.csv_  
В енаме (saStatusTask) порожденной задачи должны присутствовать только статусы из этого файла и передаваться затем в переменную sID_ProcessSubjectStatus:
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sb1J3RUx6Ti1HSGc)
***

### _delegateExpression

[вернуться...](#delegateExpression)
* #{MailTaskWithoutAttachment} - для отправки емейлов без  вложений
   * ![5_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_1.JPG)
   * #{MailTaskWithAttachments} - для отправки емейлов c  вложениями
   * #{MailTaskWithAttachmentsAndSMS} - для отправки емейлов смс обязательно должно быть вложение, при отсутствии вложения в поле saAttachmentsForSend должен быть пробел " "
   * ![5_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_2.JPG)
   * #{ProcessCountTaskListener}
   * #{SendObject_Corezoid_New}
   * #{releaseTicketsOfQueue} - При создании сервистаски с таким параметром инициализируется отмена заявки и высвобождение слота  электронной очереди по инициативе сотрудника или системы 

006_Assigngroupsandusers.md
### _addingauser
[вернуться...](#addingauser)
* ![6_7](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_7.JPG)

### _addingausertoagroup
[вернуться...](#addingausertoagroup) 
* ![6_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_6.jpg)


007_Mathematicalactionswithvariablesandconditionoperators.md
### _conditionstatementsinprocesses
[вернуться...](#conditionstatementsinprocesses) 
* ${form_attr1 == form_attr2} - сравнение значений 2х переменных в процессе
* ${form_attr1 == "N" || form_attr2 == "N"} - логическое “или” для 2х условий
* ${form_attr1 == "Y" && form_attr2 == "Y" } - логические “и” для 2х условий 
Если сложное условие прописывается в скрипттаске прямо в редакторе БП, то необходимо вместо && указать &amp;&amp; Последовательность “||” может быть указана явно. 

008_Workingwithdatesandtimers.md
### _usingtimers
[вернуться...](#usingtimers) 
Для настройки эскалации или автопроброса процесса дальше на этап используем элемент **TimerBoundaryEvent** (крепится на юзертаску).  
Обязательно изменить автоматически создаваемый ID этого элемента  “boundarytimer1 ”на  id="escalationTimer1"  

[формат даты/времени](https://en.wikipedia.org/wiki/ISO_8601#Durations), задаваемый  на срабатывание таймера. 
-общие шаблоны в указанном стандарте:  
P[n]Y[n]M[n]DT[n]H[n]M[n]S  
P[n]W   
`P<date>T<time>  `

Период указывается в соответствующем теге :

Установим таймер на **конкретное дату и время** срабатывания
```xml
<boundaryEvent id="escalationTimer" name="Timer" attachedToRef="usertask1" cancelActivity="true">
  		<timerEventDefinition>`
    			<timeDate>2011-03-11T12:13:14</timeDate>
  		</timerEventDefinition>
	</boundaryEvent>
```
Установим таймер на срабатывание  через **период**
```xml
<boundaryEvent id="escalationTimer" name="Timer" attachedToRef="usertask1" cancelActivity="true">
  		<timerEventDefinition>
    			<timeDuration>PT5S</timeDuration>
  		</timerEventDefinition>
	</boundaryEvent>
```

009_MarkersandValidators.md
### _showFieldsOnCondition

[вернуться...](#showFieldsOnCondition)
* ![6_2M](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_2%D0%9C.JPG)

### _requiredFieldsOnCondition

[вернуться...](#requiredFieldsOnCondition)
* ![9_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_2.JPG)

### _showElementsOnTrue

[вернуться...](#showElementsOnTrue)
* ![9_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_0.JPG)

### _valuesFieldsOnCondition

[вернуться...](#valuesFieldsOnCondition)
* ![9_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_3.JPG)

### _writableFieldsOnCondition

[вернуться...](#writableFieldsOnCondition)
* ![9_4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_4.JPG) 

### _splitTextHalf_1

[вернуться...](#splitTextHalf_1)

Для использования  маркеров из внешнего файла, указываем путь к файлу:  
[Issues 840](https://github.com/e-government-ua/i/issues/840)  
```xml
<activiti:formProperty id="markers2" name="extended_marker" type="markers"   
default="${markerService.loadFromFile('testmarkers.json')}" ></activiti:formProperty>
```
Допускается использование вложенных подпапок  
default="${markerService.loadFromFile('folder_name/testmarkers.json')}"   
Маркеры хранятся в папке /wf-region/src/main/resources/bpmn/markers/motion
* ![6_1M](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_1%D0%9C.JPG)

## Маркеры группы validate
### _customFormat_1

[вернуться...](#customFormat_1)

* ![9_7](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_7.JPG) 

### _extensions

[вернуться...](#extensions)

* ![9_8](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_8.JPG)

### _fileSign

[вернуться...](#fileSign)

* ![9_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_6.JPG)

## Маркеры группы attributes
### _line

[вернуться...](#line)

* ![6_0M](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_0%D0%9C.JPG)

### _style

[вернуться...](#Style)

* ![9_5](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_5.JPG)

Маркер анализирует правила в свойствах aElement_ID и aSelectors и добавляет стили перечисленный из свойства oCommonStyle в блок ```<head>``` в виде отдельного стиля. Причем стилями можно влиять не только на элементы формы но на всю страницу.

Свойства маркера aElement_ID и aSelectors работают параллельно и **может быть задан только один из них**. 
[подробное описание](https://docs.google.com/document/d/1EE7q2EEBgHW6QMRJEsPXGNE0cU9GuXT2Z3KUYNceF88/edit)  

### _sNote

[вернуться...](#sNote)

* ![9_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_9.JPG)

010_Printform.md
### _printformmd

[вернуться...](#printformmd)

* ![10_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/10_0.JPG)
      
* При необходимости, сформированную принтформу можно отправить в письме как Attachment {PrintForm_1}
* Динамически содержимое принтформы можно изменять маркерами: [issue #816](https://github.com/e-government-ua/i/issues/816)

### _display_hidefields

[вернуться...](#display_hidefields)

* ![9_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_0.JPG)

011_Digitalsignature.md
### _creationofasignedEDSdocument

[вернуться...](#creationofasignedEDSdocument)

* ![3_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_9.JPG)
где pattern/print/example_print_01.html -  шаблон печатной формы заявления, на которую будет накладываеться ЭЦП.  

Если вместо ид **form_signed** будет поставлен ид **form_signed_all**, то ЕЦП будет наложена так же на все подгружаемые файлы.  

Если убрать свойство **required="true"**, то наложение ЕЦП на указанную форму будет необязательной опцией.

При использовании простого "name" как в примере ниже - используется BankID-конвертер "html в pdf", который имеет гарантированную работоспособность но налагающий массу требований по форматированию исходного html-файла.
```xml
<activiti:formProperty id="form_signed" name="Заява з ЕЦП" type="file" required="true"></activiti:formProperty>` 
```
### _converthtmltoPDF

[вернуться...](#ConverthtmltoPDF)

* ![11_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/11_0.JPG)
На юзертасках добавить стандартный набор листнеров для подгрузки файлов.  

[валидатор файлов, на которые должен быть наложена ЕЦП](https://github.com/e-government-ua/iBP/wiki/%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B8-%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80%D1%8B#filesign---%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80-%D0%95%D0%A6%D0%9F)

012_Electronicqueues.md
### _flow_ServiceData.csv

[вернуться...](#flow_ServiceData.csv)

**nID_ServiceData** - номер строки в [ServiceData](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicedata) , для которой создается поток  
**nID_SubjectOrganDepartment** - номер департамента, из файла [SubjectOrganDepartment](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8#subjectorgandepartmentcsv)   
**sID_BP** - id самого процесса  

### _SubjectOrganDepartment.csv

[вернуться...](#SubjectOrganDepartment.csv)

**sGroup_Activiti** - id группы активити, созданной для этого департамента  
**nID_SubjectOrgan** - id номер из файла [SubjectOrgan](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorgan)  

### _flowLink.csv

[вернуться...](#flowLink.csv)

**nID_Service** - ИД услуги из [Service](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#service)  
**nID_SubjectOrganDepartment** - номер департамента, из файла [SubjectOrganDepartment](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8#subjectorgandepartmentcsv) 

### _flowProperty.csv

[вернуться...](#flowProperty.csv)

**sData** - [набор правил для формирования слотов](http://www.cronmaker.com/) (например - `{"0 0/15 8-15 ? * MON-FRI *":"PT15M"} `)  
**nID_Flow_ServiceData** - номер потока  
**bExclude** - по умолчанию false  
**sName** - название описываемого промежутка (например "Прийом в робочі дні")  
**sRegionTime** - промежуток времени приёма (например 08:00-15:00)  
**saRegionWeekDay** - дни приема через запятую, обозначаются первыми двумя английскими буквами дня недели(например mo,tu,we,th,fr)  
**sDateTimeAt** - дата и время с (например 31.08.2015 8:00)  
**sDateTimeTo** - дата и время по (например 30.09.2015 15:00)  
**nLen** - промежуток времени для слота (например 15)   
**sLenType** - единица измерения на английском (например Min) 

013_Paymentfortheservice.md
Оплата услуги



014_Emails.md
Емайлы
### _usingmultipleelectronicqueues

[вернуться...](#usingmultipleelectronicqueues)

* ![12_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/12_0.JPG)
в эту переменную будут передаваться данные по свободным слотам выбранной электронной очереди.

* на этой же  таске указываем переменную с id = **nID_Department_visitDate**, где **_visitDate** - это ИД необходимой переменной с нужной нам электронной очередью. 
```xml
<activiti:formProperty id="nID_Department_visitDate" name="Департамент" type="invisible"></activiti:formProperty>
```
значение для переменной id="nID_Department_visitDate" берем из  файла [SubjectOrganDepartment](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8#subjectorgandepartmentcsv)

### _variant

[вернуться...](#variant)

Использование тэга позволяет закрыть заявку и высвободить тэг электронной очереди.  
Тэг можно использовать только в процессе с электронной очередью.
* ![12_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/12_1.JPG) 
### Вариант №2. Использование системного тега [cancelTaskSimple]
в емейл добавляем системный тэг **[cancelTaskSimple]**, который преобразуется в кнопку **вже неактуально, закрити заявку**. Можно использовать в  любых процессах.  
На первом этапе  отмена заявки по этому тэгу не  освобождает слот электронной очереди.

### _cancellationrequest

[вернуться...](#cancellationrequest)

[Скрипт для получения даты/времени напоминания о выбранной дате из электронной очереди](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82-%D0%B4%D0%BB%D1%8F-%D0%BF%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D1%8F-%D0%B4%D0%B0%D1%82%D1%8B%D0%B2%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%B8-%D0%BD%D0%B0%D0%BF%D0%BE%D0%BC%D0%B8%D0%BD%D0%B0%D0%BD%D0%B8%D1%8F-%D0%BE-%D0%B2%D1%8B%D0%B1%D1%80%D0%B0%D0%BD%D0%BD%D0%BE%D0%B9-%D0%B4%D0%B0%D1%82%D0%B5-%D0%B8%D0%B7-%D1%8D%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D0%BE%D0%B9-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8)

### _changetheorderofanelectronicqueue

[вернуться...](#changetheorderofanelectronicqueue)

Объединение нескольких слотов по потоку  
Задание количества дней отсрочки по показу слотов очереди  
Автогенерация слотов  
Подключение сторонних очередей  


### _usingVariablesinEmailTemplates

[вернуться...](#usingVariablesinEmailTemplates)

тогда в письме нужно вставить: **enum{[typeOfDocument]}**  
тогда туда подставится значение выбранного в форме пункта энума  
**enum{[ … ]}** - тег приоритетной подстановки значение енума (можно использовать в шаблонах емейлов)  
**value{[ … ]}** - тег приоритетной подстановки  значения переменной (можно использовать в шаблонах емейлов)  
если нужно отправить  системный тег - например номер заявки - то пишем просто в квадратных скобках - **[sID_Order]** 
* ![14_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/14_1.JPG)

### _workingwithdatadirectoriesinemails

[вернуться...](#workingwithdatadirectoriesinemails)

Например:  
23423421;Запорожская обл.;Адрес1  
23423422;Днепропетровская обл.;Адрес2  
23423423;Киевская обл.;Адрес3  

**2) в теле письма должен быть тег, типа:**  
[pattern_dictonary:MVD_Department.csv:23423421:2]  
где по коду "23423421" должна искаться строка в справочнике (всегда по первой колонке)
а подставляться вместо тега значение из колонки "2" (т.е. "Днепропетровская обл.")  
при этом файл справочник должен браться MVD_Department.csv  
находящийся по пути: /patterns/dictonary/  

**3) Для динамической работы со справочниками используем тэг  приоритетной подстановки**  
[Issue 865](https://github.com/e-government-ua/i/issues/865)  
в виде value{[название переменной]}  
где вместо "название переменной" должно быть название переменной, которую нужно будет взять из текущей юзертаски, при этом 
value != enum.  
Например:  
для того чтобы из справочника бралось значение в зависимости от значения переменной ${region} используем выражение такого типа:
[pattern_dictonary:zhytomir_reg_cnap.csv:value{[region]}:4]
* ![14_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/14_0.JPG)

**Важно:** замена полей происходит если у таски навешан какой-либо из следующих компонентов.  
`activiti:delegateExpression="#{MailTaskWithAttachments}"`  
`activiti:delegateExpression="#{MailTaskWithoutAttachment}"`  
`activiti:delegateExpression="#{mailTaskWithAttachment}"`  

Файл словарь находится в проекте wf-base по пути wf-base/src/main/resources/patterns/dictionary/MVD_Department.csv

### _emailTemplates

[вернуться...](#emailTemplates)


`<h3>Шановний(-а) ${bankIdfirstName} ${bankIdmiddleName}.</h3><br />`  
[pattern/mail/test/_test_body.html]  
[pattern/mail/_common_signature.html]  
[pattern/mail/_common_feedback.html]  
[pattern/mail/_common_footer.html]  
* ![14_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/14_0.JPG)
кастомизированная подпись в письме с использованием  шаблонов  
[pattern/mail/_common_signature_start.html]  
%любой необходимый текст%  
[pattern/mail/_common_signature_end.html]  
`<h3>Шановний(-а) ${bankIdfirstName} ${bankIdmiddleName}.</h3>`  
Ваше звернення успішно зареєстровано.  
Результат обробки звернення будуть спрямовані на цей е-мейл  

[pattern/mail/new_design/_common_employee_notify.html] - общий шаблон уведомлений для гос.служащего (новый) 
[pattern/mail/_common_client_notify.html] - общий шаблон уведомления гражданина / отмены заявки,  в случае если заявка очень долго не берется в работу  

### _newEmailTemplates

[вернуться...](#newEmailTemplates)

* ![14_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/14_3.JPG)
здесь мы пишем наш контент
```
[pattern/mail/new_design/_common_content_end.html]
[pattern/mail/new_design/_common_feedback.html]   	 
[pattern/mail/new_design/_common_signature_start.html]
```
здесь мы можем добавить орган в подпись <br/>
```
[pattern/mail/new_design/_common_signature_end.html]
[pattern/mail/new_design/_common_footer.html]
```

015_SendingSMSnotifications.md

### _sMSnotifications

[вернуться...](#sMSnotifications)

Заявка попала на первую юзертаску  
**Status Vashoho zvernennya [sID_Order] zmineno na %название статуса%**  
**Detali: igov.org.ua/journal abo u Vashomu email**

По заявке вынесено замечание (автоматически)  
**Za Vashym zvernennyam [sID_Order] vyneseno zauvazhennya**  
**Detali: igov.org.ua/journal abo u Vashomu email**

Сотрудник ответил на вопрос заявителя (автоматически)  
**Za zvernennyam [sID_Order] otrymana vidpovidʹ na Vash komentar**  
**Detali : igov.org.ua/journal abo u Vashomu email**  

Заявка отработана (последняя юзертаска)  
**Vashe zvernennya [sID_Order] obrobleno.**  
**Detali: igov.org.ua/journal abo u Vashomu email**  

Заявка отработана с отрицательным результатом  
**Za Vashym zvernennyam  [sID_Order]  vyneseno vidmovu v nadanni poslugy.**  
**Detali: igov.org.ua/journal abo u Vashomu email**  

Для отправки СМС необходимо в сервистасках по отправке емейлов MailTaskWithAttachmentsAndSMS  дополнительно указать 2 параметра:  
**sPhone_SMS** - номер для отправки смс в формате +380….  
**sText_SMS** - текст отправляемого сообщения. Транслитом. не более 160 символов. 

**В тексте СМС не поддерживаются символы:** №, не пропускает апостроф ( ʹ ), но пропускает одинарную кавычку ( ‘ ).  
Такой апостроф получается после транслитерации в  https://translate.google.com.ua

Пример  в процессе:  
```xml
        <activiti:field name="sPhone_SMS">
          <activiti:expression>${phone}</activiti:expression>
        </activiti:field>
        <activiti:field name="sText_SMS">
          <activiti:expression>Status Vashoho zvernennya [sID_Order] zmineno.   
                               Detali: igov.org.ua/journal abo u Vashomu emaill
          </activiti:expression>
        </activiti:field>
      
```
* ![14_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/14_1.JPG)

* Если  емейл не предусматривает отправку файла, то  указываем “ “ в качестве  значения  параметра файла для отправки:  
```xml
       <activiti:field name="saAttachmentsForSend">
          <activiti:expression><" "></activiti:expression>
        </activiti:field>
```

016_Scripts.md

### _scripts

[вернуться...](#scripts)

Пример 1:  
```javaScript 
<scriptTask id="scripttask1" name="Script Task" scriptFormat="javascript" activiti:autoStoreVariables="false">
  <script>
    var result = ' new value '
    if(execution.getVariable('var1') == 'value'){
    execution.setVariable('var2', result)}
  </script>
</scriptTask>
```
Пример 2.  
**Cохранить текущее значение даты в переменную MyDateToday**
```javaScript 
<scriptTask id="scripttask1" name="Script Task" scriptFormat="groovy" activiti:autoStoreVariables="false">
  <script>
    execution.setVariable('MyDateToday', new Date().format("dd.MM.yyyy").toString())
  </script>
</scriptTask>
``` ```
* ![16_7](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_7.JPG)
[описание операторов и переменных для написания скриптов Javascript](http://javascript.ru/)  

Для получения значения даты на момент срабатывания скрипта можно использовать:
```javaScript
var unixdate= Math.round((new Date()).getTime())   // в формате UNIX
```
и далее работать с этим числом, прибавляя-отнимая время в милисекундах


### _formatthedateintheprocess

[вернуться...](#formatthedateintheprocess)

* ![16_8](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_8.JPG)
где  
**docDate** - имя параметра даты в процессе с форматом типа date.  Дата выбиралась из календарика.  
**yyyy-MM-dd** - итоговый требуемый формат даты.  регистрозависимо!!!   
**docDateFormat** - переменная, где сохранится  дата уже в необходимом указанном формате.  

### _gettingdatetime

[вернуться...](#gettingdatetime)

* ![16_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_6.JPG)
где
**dCreate** - это поле в которое поместится результат (Время берется по Гринвичу)  
**format("dd.MM.yyyy")** - задаем формат получаемого времени. Если формат не указываем то  по умолчанию дата / время будут в формате Tue Apr 26 14:51:17 UTC 2016  
**toString()** - не обязательно, это перестраховка  


### _receivingdatetimeelectronicqueue

[вернуться...](#receivingdatetimeelectronicqueue)

* ![16_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_9.JPG)

**sNotification_day**  - это заранее созданная в процессе переменная, в которую вернем результат работы скрипта 


### _gettingID

[вернуться...](#gettingID)

* ![16_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_2.JPG)

### _counteraddingnumber

[вернуться...](#counteraddingnumber)

* ![16_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_3.JPG)

### _obtainingBPID

[вернуться...](#obtainingBPID)

* ![16_4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_4.JPG)

### _gettingloginandname

[вернуться...](#gettingloginandname)

* ![16_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_1.JPG)


017_Configurationfiles.md

### _service

[вернуться...](#service)

Пример.
* nID;sName;nOrder;nID_Subcategory;sInfo;sFAQ;sLaw;nOpenedLimit;sSubjectOperatorName
* 1;Надання довідки про притягнення до кримінальної відповідальності, відсутність (наявність) судимості або обмежень, передбачених 
 кримінально-процесуальним законодавством України;1;3;[*];;;0;Міністерство внутрішніх справ
* 788;Надання дозволу на знесення аварійних будівель;100;1;;;;0;Сільська рада
* 40;Повідомлення про проведення зборів, мітингів, маніфестацій і демонстрацій, спортивних, видовищних та інших масових заходів;100;2;;;;0;Сільська рада

* 1038;40;467;467;NULL;4;{"processDefinitionId":"spend_meeting_404:1:1"};;false;1;true;;BankID,EDS;0
* ![17_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_1.JPG)

### _serviceData

[вернуться...](#serviceData)

* Пример.
* nID;nID_Service;nID_Place;nID_City;nID_Region;nID_ServiceType;oData;sURL;bHidden;nID_Subject_Operator;bTest;sNote;asAuth;nID_Server
* 1;1;2;2;NULL;1;{};https://null.igov.org.ua;true;1;true;Перейдя на этот сайт Вы сможете получить услугу;BankID,EDS,KK;0
* 1032;788;467;467;NULL;4;{"processDefinitionId":"znes_bud_393:1:1"};;false;1;true;;BankID,EDS;0
* 038;1471;467;467;NULL;4;{"processDefinitionId":"spend_meeting_404:1:1"};;false;1;true;;BankID,EDS;0
* ![17_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_0.JPG)

### _subject

[вернуться...](#subject)

* ![17_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_2.JPG)

### _subjectAccount

[вернуться...](#subjectAccount)

* ![17_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_3.JPG)

### _subjectOrganJoin

[вернуться...](#subjectOrganJoin)

* ![17_4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_4.JPG)




018_Workingwiththegithandrepository.md

### _conflictresolutioneclipse

[вернуться...](#conflictresolutioneclipse)

**Обратите внимание!** Это очень неудобная фишка в eclipse - строки добавляются, а не заменяются.  
Поэтому дубли нужно удалить вручную. И затем сохранить файл.  

Если более актуальная версия файла ветки в которую производится мерж - ничего не делаем, закрываем файл.  
В случае если Вы принимали изменения или не принимали их (оставили файл как есть) - в любом из этих случаев необходимо присвоить индекс версии.  
Правой кнопкой мыши нажимаем на конфликтном файле и выбираем **team-Add to index**  
Красный ромбик пропадет, появляется звездочка.  

Разрешение конфликтов необходимо сделать на каждом файле отдельно.  
После присвоения индексов каждом конфликтному файлу необходимо собрать проект локально.
Если локальная сборка прошла успешно - закомитить и вытолкнуть все изменения. **Commit-Pull-Push**

Installationeclipse.md

### _installJAVAJDK

[вернуться...](#installJAVAJDK)

![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sQlE3TUh3ZmpiUGM)  
3. Устанавливаем скачанный файл, без изменений, со всем соглашаемся, можно выбрать свою директорию.  
4. Прописываем путь к JAVA (**нужно для tomcat для Eclipse это не важно**)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a) Заходим в свойства “Мой компьютер”   
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sXzZCNXBRQkhsOFE)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) б) Выбираем дополнительные параметры системы  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZFRELThmbjFNZHc)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) в) добавляем переменную **JAVA_HOME**  
и путь к установленной JAVA и **обязательно** JDK  
```
C:\Program Files\Java\jdk1.8.0_111  
```
если скачивалась х86 то и соответственно и путь будет 
```  
C:\Program Files (x86)\Java\jdk1.8.0_111  
```
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sVWNQa3N1NThKTm8)  

![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sX0xVUGNRV01SdkE)  

### _installEclipse

[вернуться...](#installEclipse)
4. запускаем eclipse.exe  
5. При первом запуске он спросит где хранить настройки Eclipce  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sNUJYaThZZnpLaTg)  
для лучшей переносимости Eclipce на другой компьютер лучше всего создать в папке где расположен Eclipce новую папку workspace и туда указать хранить настройки, а также поставить галочку что эта папка по умолчанию будет использоваться и для проекта gitа  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sS01ZSkVrczhxWlk)  
6. Закрываем страничку Welcom  
7. Ставим activity  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a) help -> install new software…  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sUHNoZG1DU2d2Yzg)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) b) в появившемся окне вставляем адрес 
```html
http://activiti.org/designer/update
```  
и нажимаем add  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sU1R2R2JSb0pVeHc)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) c) в следующем блоке IDE уточнит как назвать это приложение, название не важно, можно к примеру использовать activiti  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZFplX3ZvR0p5TDA)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) d) далее еклипс по указанному адресу ищет приложение, когда найдет появится возможность выбрать и нажать next  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sU3ptc2pDY1hvc2M)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) e) в процессе установки IDE уточнит хотим ли мы установить неподписанный софт, мы соглашаемся  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sV3ZVcFpwNzdZLTA)  
после установки приложения IDE попросит перезагрузить себя, соглашаемся  
8. По такому же алгоритму как и активити ставим мейвен, только вставляем адрес  
```html
http://download.eclipse.org/technology/m2e/releases 
```
ну и соответственно называем по другому  
9. Затягиваем репозиторий  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a) Кликаем во вкладке Package Explorer правой кнопкой мыши и выбираем импорт  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sTmlESEJBY3ZrdVE)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) b) И движемся по следующей цепочке  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZG55QXRjUS0wMWc)  
![2](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sOXQxSzUwZTM2N2s)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) c) Вставляем ссылку на нужный Гит  https://github.com/e-government-ua/i.git  
и заполняем поля user/password  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZTRSMWZ1N05mZEk)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) d) Убираем галочки со всех веток и выбираем нужную  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_scGR0ZVJyQW4wSmM)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) e)проверяем в ту ли папку будет копироваться репозиторий  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sV1RYZVg5SDdFNVU)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) f) ждем пока докачается репозиторий  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) g) выбираем Import as general project  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sWC15dTZGTUV3Ym8)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) h)делаем пулл  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sRlJONXdwNm1tOEk)  
10. Настраиваем Еклипс  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a) заходим в настройки  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sYkFmX1BzalBOM0U)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) b) настраиваем кодировку создаваемых страниц, выбираем UTF-8  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZW1TVTlJVDRlRTg)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) c)устанавливаем привычное для себя окно коммитов(галочку снимаем)  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sWGRadkctcElkUUU)  
11. Настраиваем сборку  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a)по проекту правой кнопкой мыши и выбираем конфигурации сервера  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sVWxNQXNHdlNtZ2c)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) b)создаем конфигурацию мейвена  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sVFVzSGVvNUI5Qkk)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) c)выбираем директорию для мейвена  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sSGozRzJGUjRSWmM)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) d)называем проект, вписываем в цель clean install и выбираем больше ядер, чтоб проект быстрей собирался нажимаем apply  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sY1AwUHRBMS1nRjA)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) e) настраиваем Java - Машину  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sb1VHZHdIR3Q0MTQ)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) f) добавляем JDK. Не JRE а именно **JDK**!!!   
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sVjZlam9KaWFhX1U)  
![2](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sbXFVMkxwVDl4eUE)  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sQVprNy1HLVJ5MlU)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) g)и обязательно выбираем ее и ставим по умолчанию  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sNmZWMXF0ZV9YdTQ)  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_say1QMElyNTlBdGM)  
* Руками прописываем кодировку cp1251 на вкладке Общие (Common)
* ![ecl1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/ecl1.JPG)

### _addJSONandHTML
[вернуться...](#addJSONandHTML)
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_saWRQWTdOVGdTRW8)  
![2](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sRWx6dEs0aDNJY0E)  
3. приложения JSON дает возможность открыть окно для редактирования или валидации JSON  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sekgtZzdHWWo4b00)  
а также можно настроить как будет форматироваться JSON  если нажать комбинацию ctrl + shift + F  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZzdCMmg0Rm13djQ)  



019_Autotests.md
 
020_Escalationsandfeedback.md

### _sCondition
[вернуться...](#sCondition)

Пример самых ходовых правил (sCondition):
```
"(nDays >= 0)&&(bAssigned==false)”
"(nDays >= 0)&&(bAssigned==true)”
"(nElapsedDays >= nDaysLimit)&&(bAssigned==false)"
"(nElapsedDays >= nDaysLimit)&&(bAssigned==true)"
```
Так же стоит учитывать знаки условия:  
nDays >= 5 - правило будет срабатывать каждый день, начиная с пятого дня  
nDays ==5 - правило сработает только в пятый день  
nDays != 5 - правило сработает во все дни кроме пятого  

023_UsefulInquiries.md

### _downloadmaximumdate
[вернуться...](#downloadmaximumdate)

* ![23_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/23_0.jpg)

### _numberofservicesbyregion
[вернуться...](#numberofservicesbyregion)

* ![23_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/23_1.jpg)


024_ LifeHacking.md


