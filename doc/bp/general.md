
001. [Создание бизнес-процессов](https://github.com/e-government-ua/i/blob/test/doc/bp/001_CreatingBusinessProcesses.md)
1. [Основные элементы Activiti Designer](https://github.com/e-government-ua/i/blob/test/doc/bp/0021TheMainElementsOfActivitiDesigner.md)
002. [Типы параметров](https://github.com/e-government-ua/i/blob/test/doc/bp/002_Typesofparameters.md)
1. [Зарезервированные и системные переменные](https://github.com/e-government-ua/i/blob/test/doc/bp/003_ReservedandSystemVariables.md)
1. [Общие правила при именовании](https://github.com/e-government-ua/i/blob/test/doc/bp/004_Generalrulesfornaming.md)
1. [Типы Listener и delegateExpression](https://github.com/e-government-ua/i/blob/test/doc/bp/005_TypesListeneranddelegateExpression.md)
1. [Назначение групп и пользователей](https://github.com/e-government-ua/i/blob/test/doc/bp/006_Assigngroupsandusers.md)
1. [Математические действия с переменными и операторы условий](https://github.com/e-government-ua/i/blob/test/doc/bp/007_Mathematicalactionswithvariablesandconditionoperators.md)
1. [Работа с датами и таймерами](https://github.com/e-government-ua/i/blob/test/doc/bp/008_Workingwithdatesandtimers.md)
1. [Маркеры и Валидаторы](https://github.com/e-government-ua/i/blob/test/doc/bp/009_MarkersandValidators.md)
1. [Принтформы](https://github.com/e-government-ua/i/blob/test/doc/bp/010_Printform.md)
1. [ЭЦП](https://github.com/e-government-ua/i/blob/test/doc/bp/011_Digitalsignature.md)
1. [Электронные очереди](https://github.com/e-government-ua/i/blob/test/doc/bp/012_Electronicqueues.md)
1. [Оплата услуги](https://github.com/e-government-ua/i/blob/test/doc/bp/013_Paymentfortheservice.md)
1. [Емайлы](https://github.com/e-government-ua/i/blob/test/doc/bp/014_Emails.md)
1. [Отправка СМС-оповещений](https://github.com/e-government-ua/i/blob/test/doc/bp/015_SendingSMSnotifications.md)
1. [Скрипты](https://github.com/e-government-ua/i/blob/test/doc/bp/016_Scripts.md)
1. [Конфигурационные файлы](https://github.com/e-government-ua/i/blob/test/doc/bp/017_Configurationfiles.md)
1. [Работа с гитом и репозиторием](https://github.com/e-government-ua/i/blob/test/doc/bp/018_Workingwiththegithandrepository.md)   [Установка Eclipce](https://github.com/e-government-ua/i/blob/test/doc/bp/Installationeclipse.md)
1. [Автотесты](https://github.com/e-government-ua/i/blob/test/doc/bp/019_Autotests.md)
1. [Эскалации и фидбеки](https://github.com/e-government-ua/i/blob/test/doc/bp/020_Escalationsandfeedback.md)
1. [Статистика и выгрузки](https://github.com/e-government-ua/i/blob/test/doc/bp/021_Statisticsanduploads.md)
1. [Часто возникающие ошибки](https://github.com/e-government-ua/i/blob/test/doc/bp/022_CommonErrors.md)
1. [Полезные запросы](https://github.com/e-government-ua/i/blob/test/doc/bp/023_UsefulInquiries.md)
1. [Лайф Хаки](https://github.com/e-government-ua/i/blob/test/doc/bp/024_%20LifeHacking.md)
1. [Чек лист тестирования ветки](https://github.com/e-government-ua/i/blob/test/doc/bp/025_CheckListTestBranch.md)
1. [СЭД](https://github.com/e-government-ua/i/blob/test/doc/bp/026_IDoc.md)
# сбор информации и предварительная подготовка
* получить информационную и технологическую карточку услуги
* пример заявления, бланки, шаблоны документов
* реквизиты для оплаты (для платных услуг)
* график приема граждан (для услуг с электронными очередями)
* контакты ответственного лица за отработку заявок
* Если необходимо - создать справочник административных органов по регионам - название, адрес, телефон, график работы. Путь к справочникам: i\wf-base\src\main\resources\patterns\dictionary
* поставить задачу на гитхабе

# разработка бизнес-процесса
* создать новую диаграмму. Для Eclipse:New-Other-Activiti Diagram. Путь для размещения bpmn-файлов: \i\wf-egion\src\main\resources\bpmn\autodeploy.
* ![1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/1bp.jpg)
* ![screenshot of sample2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2bp.jpg)
* ![screenshot of sample3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3bp.jpg)
* [Создание бизнес-процесса дополнительно](https://docs.google.com/document/d/1B3OIYjj3S2YLwUR-PVD3FAcErl_2ua0CYUB5vys6O4U/edit )
* [Правила при именовании бизнес-процессов](https://github.com/e-government-ua/iBP/wiki/%D0%9E%D0%B1%D1%89%D0%B8%D0%B5-%D0%BF%D1%80%D0%B0%D0%B2%D0%B8%D0%BB%D0%B0-%D0%BF%D1%80%D0%B8-%D0%B8%D0%BC%D0%B5%D0%BD%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B8:#%D0%B1%D0%B8%D0%B7%D0%BD%D0%B5%D1%81-%D0%BF%D1%80%D0%BE%D1%86%D0%B5c%D1%81%D1%8B).  
* проработать инфокарты и бланки заявлений - составить перечень необходимых полей и условий
* прорисовать всю схему процесса от начала до конца, наполнить блоки информацией.
* создать принтформы

# подключение всех необходимых сущностей
* создать пользователей и группы на дельте - связать их между собой
* написать "Як це працює" в формате html. Путь для размещения файла:\i\wf-central\src\main\resources\patterns\services\Info
* добавить файл с принтформой в формате html. Путь для размещения файла: i\wf-region\src\main\resources\pattern\print. Желательно использовать папку по конкретному органу или направлению.
* заполнить [Service](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#service), [ServiceData](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicedata), [SubjectOrganJoin](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorganjoin), [SubjectOrgan](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorgan)
* Если необходимо добавить населенный пункт которого нет в списке - добавить его в сущности [Place](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#place), [PlaceTree](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#placetree), [City](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#city).
* При необходимости - добавить [электронные очереди](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8). 
* Прописать [эскалации](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D1%81%D0%BA%D0%B0%D0%BB%D0%B0%D1%86%D0%B8%D0%B8-%D0%B8-%D1%84%D0%B8%D0%B4%D0%B1%D0%B5%D0%BA%D0%B8).

# тестирование и проливка на бету и боевой
* перед каждой проливкой на сервер проект необходимо собирать и запускать локально
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
# Типы переменных
### string

```xml
<activiti:formProperty id="Place" name="Назва поля" type="string"></activiti:formProperty>
``` 


gfdsgfdg
[SPOILER]
dfdsgdfsg
[/SPOILER]




[детальней...](#_string)

### long
```xml
<activiti:formProperty id="Place" name="Назва поля" type="long"></activiti:formProperty>
```
число (целое) - для ввода ЦЕЛЫХ чисел

### double
```xml
<activiti:formProperty id="Place" name="Назва поля" type="double"></activiti:formProperty>
```
число (дробное) - для ввода ДРОБНЫХ чисел (разделитель - точка)

### enum
```xml
<activiti:formProperty id="saQuestion1" name="показати поле з данними?" type="enum" default="no">
  <activiti:value id="yes" name="так"></activiti:value>
  <activiti:value id="no" name="ні"></activiti:value>
</activiti:formProperty>
```
[детальней...](#_enum)


### enum (checkbox)
```xml
<activiti:formProperty id="saQuestion1" name="показати поле з данними?; ;sID_CheckboxTrue=yes" type="enum" default="no">
  <activiti:value id="yes" name="так"></activiti:value>
  <activiti:value id="no" name="ні"></activiti:value>
</activiti:formProperty>
```
[детальней...](#_enum (checkbox))


### date
```xml
<activiti:formProperty id="sDateBirth" name="Дата народження" type="date" required="true"></activiti:formProperty>
```
[детальней...](#_date)


### boolean
```xml
<activiti:formProperty id="Place" name="Назва поля" type="boolean"></activiti:formProperty>
```
[детальней...](#_boolean)


### label

Стили лейбла можно назначить в аттрибуте name
```xml
<activiti:formProperty id="color" name=" ; ;labelType=success" type="label" default="Ви можете" ></activiti:formProperty>
```
* labelType=success - зеленый
* labelType=info - голубой
* labelType=warning - желтый
* labelType=danger - красный

[детальней...](#_label)



### file
файл - кнопка для ручной загрузки файлов гражданином на форме подачи обращения и для возможности просмотра  прикрепленного файла на форме обработки обращения.
```xml
<activiti:formProperty id="bankId_scan_passport" name="сканована копія паспорту" type="file"></activiti:formProperty>`
```
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

***
### file (New)
В связи с проблемой с пропавшими файлами, был проведен большой рефакторинг, в результате которого реализована новая схема работы с аттачами, при этом остается функциональной и продолжает работать старая схема.  
Рекомендуется Топ-процессы переводить на новую схему аттачей – с ней файлы не будут теряться.  

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
### textArea
многострочный текст - для ввода/отображения многострочного текста
```xml
<activiti:formProperty id="application_name" name="В цьому полі надайте перелік усіх додатків та специфікацій до договору" 
type="textArea"></activiti:formProperty>
``` 
* ![2_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_9.JPG)
***
### queueData
дата/время - Электронная очередь.
```xml
<activiti:formProperty id="visitDay" name="Оберіть день та час, коли Вам буде зручно з'явитись для реєстрації народження?"
type="queueData" required="true"></activiti:formProperty>
```
* ![2_31](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_31.JPG)

***
### [markers](https://github.com/e-government-ua/iBP/wiki/%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B8-%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80%D1%8B)
Маркеры и позволяют работать с уже существующими полями и расширяют их возможности.
***
### invisible
Невидимый тип данных. Используется, как правило для записи технических полей, которые нужны в процессе, но заявителю или чиновнику не должны быть показаны.
```xml
<activiti:formProperty id="sID_Payment" name="ИД транзакции платежа" type="invisible"></activiti:formProperty>
```
* ![2_30](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_30.JPG)
***
### select
Тип данных, который формирует динамические выпадающие списки (в зависимости от параметров).
Например, можно динамически сформировать перечень населенных пунктов, в зависимости от выбранной области.
```xml
<activiti:formProperty id="sID_Public_SubjectOrganJoin" name="Відділення" type="select" default="0"></activiti:formProperty>
```

Для того чтоб в выпадающем списке селекта выпадал заданный массив данных, необходимо правильно заполнить файлы [Subject](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subject), [SubjectOrgan](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorgan), [SubjectOrganJoin](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorganjoin), [SubjectOrganJoinAttribute](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorganjoinattribute), [ServiceData](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicedata) соответственно описанию.

***
### table
отображается в виде таблицы, в которую может быть добавлено произвольное количество строк. В коде задается шапка таблицы, которая потом клонируется. Для каждого столбца задаются отдельные параметры. Внутри таблицы поддерживаются типы данных: string, date, long, select, enum.

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
### line
маркер-атрибут для отрисовки линии (группирующей/отсекающей) одни поля от других
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
* ![6_0M](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_0%D0%9C.JPG)
***
## Атрибуты переменных

**id** - уникальный идентификатор переменной. Обязательный атрибут.

**type** - тип переменной. Преобразования типов нет. Обязательный атрибут.

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
## Переменные BankID
* **bankIdlastName** - string - фамилия гражданина
* **bankIdfirstName** - string - имя гражданина
* **bankIdmiddleName** - string - отчество гражданина
* **bankIdPassport** - string  -паспортные данные гражданина
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

## Зарезервированные переменные для электронных очередей
* **date_of_visit** - invisible - автоматом принимает значение выбранное  из электронной очереди
* **nID_Department_visitDay** - string/invisible/label - номер органа для электронной очереди, где visitDay это id  электронной очереди, к которой относится текущий департамент
* **nSlots_visitDay** - string/invisible/label - количество слотов очереди , которые резервируются пользователем. (где visitDay это id  электронной очереди, к которой относится текущий размер слота)
* ![3_5](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_5.JPG)

## Зарезервированные переменные атрибутов
* **sNameOrgan** - string/invisible/label - название органа в подписи письма
* **sWorkTime** - string invisible/label - график работы
* **sPhoneOrgan** - string/invisible/label - телефон для справок
* **sAddress** - string/invisible/label - адрес органа
* **sMailClerk** - string/invisible/label - почта чиновника
* **sArea** - string/invisible/label - yазвание нас.пункта/района куда подается заявка
* **nArea** - string/invisible/label - yомер в справочнике нас.пункта/района куда подается заявка
* **sShapka** - string/invisible/label - шапка принтформы
* ![3_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_1.JPG)


## Переменные принтформ
* **[sID_Order]** - системный тег для принтформы, смс или емейла  для размещения ИД заявки. [Issue 1023](https://github.com/e-government-ua/i/issues/1023).  
* **[sDateCreate]** - Системный тег даты. Возвращает значение системного времени на момент срабатывания таски. Можно использовать как время начала обработки обращения (взятия в работу чиновником).
* ![3_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_6.JPG)
* **[sDateTimeCreateProcess]** - Возвращает значение системной даты-времени на момент сохранения\подачи заявки гражданином.
* **[sDateCreateProcess]**- Возвращает значение системной даты на момент сохранения\подачи заявки гражданином.
* **[sTimeCreateProcess]** - Возвращает значение системного времени на момент сохранения\подачи заявки гражданином.
* **[sCurrentDateTime]** - Возвращает значение системной даты и времени на текущий момент.
* **sBody** - invisible - задать печатную форму.  
Прописывается в юзертаске. Для корректной работы обязательно надо прописать листнер “fileTaskInheritance”
Путь на печатную форму в папке patterns задается в поле name (типа [pattern/print/subsidy_zayava.html]) 
* **PrintForm** - Позволяет автоматически создавать файл из соответствующей принтформы, который потом можно подгружать к вложениям в письмо на сервис-таске (используем ${PrintForm_1} при отправке письма с вложениями). Номер PrintForm должен совпадать с номером sBody.

## Валидируемые переменные
* **vin_code, vin_code1, vin** - string - VIN-код авто.
Набор из 17 символов. Разрешено использовать все арабские цифры и латинские буквы (А В C D F Е G Н J К L N М Р R S Т V W U X Y Z) , за исключением букв Q, O, I. Эти буквы запрещены для использования, поскольку O и Q похожи между собой, а I и O можно спутать с 0 и 1.
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

## Другие
* **response** - invisible - задать кастомизированный текст на спасибо странице, после подачи обращения (с поддержкой html)
* **footer** - string - задать кастомизированный текст на стандартной форме для печати в дашборде( с поддержкой html)
* **sNotifyEvent_AfterSubmit** - invisible - Отображение кастомного текста в дашборде после нажатия на кнопку “Опрацювати”. Текст  подсказки задаем в аттрибуте default. [Issue 1027](https://github.com/e-government-ua/i/issues/1027).
* **bReferent** - invisible - признак заполнения заявки референтом (true/false).
* **form_signed** - если объявлена эта переменная на стартовой форме, то при нажатии на кнопку "замовити послугу" заявитель будет перенаправлен на доп.страницу для наложения ЕЦП на заявку.
* ![3_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_9.JPG)
* **form_signed_all** - при наложении ЕЦП на заявку, она так же будет наложена и на все прикрепленные файлы. При этом все файлы, которые прикрепил гражданин, должны иметь расширение *.pdf.

## Автокомплиты
* **sObjectCustoms** - select - Товар 
* **sID_UA_ObjectCustoms** - Код товара (заполнится автоматически после выбора в селекте sObjectCustoms)
* ![3_8](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_8.JPG)
* **sCountry** - select - Страна 
* ![3_7](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_7.JPG)
* **sID_UA_Country** - Код страны (заполнится автоматически после выбора в селекте sCountry)
* **sCurrency** - select - Валюта 
* **sID_UA_Currenc**y - Код валюти (заполнится автоматически после выбора в селекте sCurrency)
* **sSubjectOrganJoinTax** - select - Таможня
* **sID_UA_SubjectOrganJoinTax** - Код таможни (заполнится автоматически после выбора в селекте sSubjectOrganJoinTax)
* **sID_Place_UA** - string - В переменную передается КОАТУУ выбранного населенного пункта (поле Place)

## Переменные-запросы
**sID_SubjectOrgan_OKPO_** - string - Делает запрос к базе ранее полученных данных из ЕДРПОУ, по соответствующему коду предприятия, и возвращает результат в несколько зарезервированных переменных (если по запросу найдены данные). Возможно неограниченное количество полей запроса на форме - **sID_SubjectOrgan_OKPO_** используется как префикс, главное обеспечить уникальность **id** каждой следующей переменной-запроса и каждого возвращаемой переменной. 
* **sFullName_SubjectOrgan_** - textArea - Полное наименование
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

## Наличие в любых IDшниках кириллических символов, а также пробелов - недопустимо

## переменные

первый символ названия переменной должен говорить о типе данных:
* n = Number (числовые переменные) - nSum
* s = String (строчные переменные) - sFamily
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

## Подсвечивать этап (юзертаску) в дашборде  цветом

если название юзертаски заканчивается на:
* "_red" - подкрашивать строку - красным цветом (класс: "bg_red")
* "_yellow" - подкрашивать строку - желтым цветом (класс: "bg_yellow")
* "_green" - подкрашивать строку - зеленым цветом (класс: "bg_green")
* "usertask1" - подкрашивать строку - салатовым цветом (класс: "bg_first")
* ![4_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/4_2.jpg)
* ![4_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/4_3.JPG)
# Listener

   * ${fileTaskUploadListener} - тянет ВСЕ атачи из стартовой формы. Указывать на первой Юзертаске.  
   * ${fileTaskInheritance} - слушатель тянет по ид атача атач на юзертаску. Указывать на второй и последующих Юзертасках, перечисляя все id необходимых аттачей. 
   * ![5_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_0.jpg)
   * ${CreateDocument_UkrDoc}
   * ${GetDocument_UkrDoc}
   * ${UpdateStatusTask}
   * ${DocumentInit_iDoc}  
   * ![5_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_3.JPG)

***
### ${SetTasks}
листенер ${SetTasks} - ставится на закрытие таски, т.е. event="complete"  **(1, 2)**  
В этом листенере мы указываем какие поля из текущего БП передать в другой БП:  
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

***
### ${DocumentInit_iDoc}

листенер ${DocumentInit_iDoc} - ставится на открытие таски, т.е. event="create"  
Никаких дополнительных параметров листенера ставить не нужно  
``` xml
<activiti:taskListener event="create" delegateExpression="${DocumentInit_iDoc}"></activiti:taskListener>
```
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
### ${UpdateStatusTask}
обновляет статус порожденной задачи  
обязательный параметр **sID_ProcessSubjectStatus**  
Все статусы задаются в файле: _i\wf-base\src\main\resources\data\ProcessSubjectStatus.csv_  
В енаме (saStatusTask) порожденной задачи должны присутствовать только статусы из этого файла и передаваться затем в переменную sID_ProcessSubjectStatus:
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sb1J3RUx6Ti1HSGc)
***
### ${UpdateStatusTaskTreeAndCloseProcess}
Листенер прикрепляется на процесс-родитель из которого порождаются задачи. В случае, если родительский процесс закрыт, то все незакрытые порожденные задачи автозакроются со статусом “неактуально”

***


# delegateExpression

   * ${assignGroup}
   * #{setMessageFeedback_Indirectly}
   * #{fileTaskUpload} - для электронных очередей. Достает дату из объекта в переменной типа _queueData_ и передает ее в системную переменную _date_of_visit_ . Сервистаска с этим выражением должна следовать сразу за стартивентом.
   * #{MailTaskWithoutAttachment} - для отправки емейлов без  вложений
   * ![5_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_1.JPG)
   * #{MailTaskWithAttachments} - для отправки емейлов c  вложениями
   * #{MailTaskWithAttachmentsAndSMS} - для отправки емейлов смс обязательно должно быть вложение, при отсутствии вложения в поле saAttachmentsForSend должен быть пробел " "
   * ![5_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/5_2.JPG)
   * #{ProcessCountTaskListener}
   * #{SendObject_Corezoid_New}
   * #{releaseTicketsOfQueue} - При создании сервистаски с таким параметром инициализируется отмена заявки и высвобождение слота  электронной очереди по инициативе сотрудника или системы 

# Добавляем пользователя
* Заходим по ссылке https://beta.test.region.igov.org.ua/groups . Нажимаем в левом верхнем углу знак настройки, користувачи, додати користувача, заполняем данные в появившемся окне и сохраняем. В списке пользователей появится ваш созданный пользователь.
* ![6_7](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_7.JPG)

***
# Добавляем пользователя в группу
* Заходим по ссылке https://beta.test.region.igov.org.ua/groups . Нажимаем в левом верхнем углу знак настройки, группи, додати в группу. Вводим в появившемся окне id и название группы и добавляем необходимого пользователя в эту группу
* ![6_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_6.jpg)

## Выполнение простейших математических действий с переменными

digit1 - переменная со значением 1.  (тип long или double)

digit2 - переменная со значением 2. (тип long или double)

digit3 - переменная, куда присвоится результат  (тип long или double) . в дефолтном значении указываю запись типа ${digit2 + digit1}

и в итоге суммируется и в результате   имеем “3”  :)
Аналогично используются операнды сложения, вычитания, умножения, деления.

## Операторы условий в процессах.
* ${form_attr == "N"} - проверка на равенство между переменной form_attr и константой  "N"
* ${form_attr != "N"}  - проверка на НЕравенство
* ${form_attr1 == form_attr2} - сравнение значений 2х переменных в процессе
* ${form_attr1 == "N" || form_attr2 == "N"} - логическое “или” для 2х условий
* ${form_attr1 == "Y" && form_attr2 == "Y" } - логические “и” для 2х условий 

Если сложное условие прописывается в скрипттаске прямо в редакторе БП, то необходимо вместо && указать &amp;&amp; Последовательность “||” может быть указана явно. 
## Использование таймеров
Для использования таймера с целью приостановки процесса, используем стандартный элемент **TimerCatchingEvent** (самостоятельный элемент схемы) который приостанавливает процесс до срабатывания таймера.

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
* ![6_2M](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_2%D0%9C.JPG)

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
* ![9_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_2.JPG)

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
* ![9_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_0.JPG)
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
* ![9_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_3.JPG)
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
* ![9_4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_4.JPG)     

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
* ![6_1M](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_1%D0%9C.JPG)
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
* ![9_7](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_7.JPG) 
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
* ![9_8](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_8.JPG)
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
* ![9_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_6.JPG)
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
* ![6_0M](https://github.com/e-government-ua/i/blob/test/doc/bp/img/6_0%D0%9C.JPG)
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
* ![9_5](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_5.JPG)

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
* ![9_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_9.JPG)
* Принтформа прописывается на Юзертаске:

        activiti:formProperty id="PrintForm_1" name="File label;File title;pattern/print/UPSZN/subsidy_declaration_2.html" type="file"></activiti:formProperty

        activiti:formProperty id="sBody_1" name="[pattern/print/UPSZN/subsidy_zayava_1.html]" type="invisible" default="Заява" writable="false"></activiti:formProperty
 * ![10_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/10_0.JPG)

        
* При необходимости, сформированную принтформу можно отправить в письме как Attachment {PrintForm_1}

* Динамически содержимое принтформы можно изменять маркерами: [issue #816](https://github.com/e-government-ua/i/issues/816)

**отображение/скрытие  полей**

например, "all_table" -  id какого-либо элемента печатной формы


На юзертаск прописываем маркер:

`{"motion": {`

  `"ShowElementsOnTrue_1": {`

     `"aElement_ID": ["all_table"],`

    `"asID_Field": {`

    `"sCond": "condition"`

    `},`

    `"sCondition": "[sCond]=='1'"`

  `}`
 `}`
`}`
* ![9_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/9_0.JPG)


## Создание подписанного ЭЦП документа cо стартовой формы
Необходимо добавить строки на стартовую таску:
```xml
<activiti:formProperty id="form_signed" name="Заява з ЕЦП" type="file" required="true"></activiti:formProperty>` 
<activiti:formProperty id="PrintFormAutoSign_1" name="Шаблон для наложения ЭЦП" type="invisible" default="pattern/print/example_print_01.html"></activiti:formProperty>
```
* ![3_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/3_9.JPG)
где pattern/print/example_print_01.html -  шаблон печатной формы заявления, на которую будет накладываеться ЭЦП.  

Если вместо ид **form_signed** будет поставлен ид **form_signed_all**, то ЕЦП будет наложена так же на все подгружаемые файлы.  

Если убрать свойство **required="true"**, то наложение ЕЦП на указанную форму будет необязательной опцией.

При использовании простого "name" как в примере ниже - используется BankID-конвертер "html в pdf", который имеет гарантированную работоспособность но налагающий массу требований по форматированию исходного html-файла.
```xml
<activiti:formProperty id="form_signed" name="Заява з ЕЦП" type="file" required="true"></activiti:formProperty>` 
```
## Конвертирование html в PDF
Как альтернатива вышеуказанному способу существует конвертор собственный - его использование задается в дополнительном параметре описанном в "name". Данный конвертор более качественно переводит в PDF формат исходный html-файл. Также он позволяет успешно использовать встроенные в html-файл java-скрипты.
```xml
<activiti:formProperty id="form_signed" name="Заява з ЕЦП; ;bPrintFormFileAsPDF=true" type="file" required="true"></activiti:formProperty>` 
```
* ![11_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/11_0.JPG)
На юзертасках добавить стандартный набор листнеров для подгрузки файлов.  

[валидатор файлов, на которые должен быть наложена ЕЦП](https://github.com/e-government-ua/iBP/wiki/%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B8-%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80%D1%8B#filesign---%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80-%D0%95%D0%A6%D0%9F)
## Настройка электронной очереди
Все файлы для настройки электронной очереди расположены в wf-base/src/main/resources/data/ ….   

***

### 1. Создаем поток 

### Flow_ServiceData.csv  
**nID** - id потока  
**sName** - название  
**nID_ServiceData** - номер строки в [ServiceData](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicedata) , для которой создается поток  
**nID_SubjectOrganDepartment** - номер департамента, из файла [SubjectOrganDepartment](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8#subjectorgandepartmentcsv)   
**sID_BP** - id самого процесса  

### SubjectOrganDepartment.csv
здесь могут быть созданы  различные департаменты для возможности использования нескольких потоков в одном процессе  

**nID** - id департамента  
**sName** - название департамента  
**sGroup_Activiti** - id группы активити, созданной для этого департамента  
**nID_SubjectOrgan** - id номер из файла [SubjectOrgan](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorgan)  

### FlowLink.csv
если нужно завести на один общий поток несколько услуг.  
Для привязки одной очереди к нескольким процессам, необходимо в этом файле перечислить **ВСЕ** процессы (nID_Service) которые нужно связать в рамках одного потока очереди  
**nID**  
**nID_Flow_ServiceData** - ИД потока, в который надо свести несколько очередей  
**nID_Service** - ИД услуги из [Service](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#service)  
**nID_SubjectOrganDepartment** - номер департамента, из файла [SubjectOrganDepartment](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8#subjectorgandepartmentcsv) 

***

### 2. Для созданного потока указываем  расписание
### FlowProperty.csv
файл с расписанием (график работы)

**nID**  
**nID_FlowPropertyClass** - класс, который формирует слоты очереди (для нас по-умолчанию - 1 )  
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

***

## Использование в одном процессе несколько электронных очередей (для нескольких департаментов)
* внутри процесса на стартовой таске объявляем переменную с типом [queueData](https://github.com/e-government-ua/iBP/wiki/%D0%A2%D0%B8%D0%BF%D1%8B-%D0%BF%D0%B0%D1%80%D0%B0%D0%BC%D0%B5%D1%82%D1%80%D0%BE%D0%B2#queuedata)
```xml 
<activiti:formProperty id="visitDate" name="Бажана дата візиту" type="queueData" required="true"></activiti:formProperty>
```
* ![12_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/12_0.JPG)
в эту переменную будут передаваться данные по свободным слотам выбранной электронной очереди.

* на этой же  таске указываем переменную с id = **nID_Department_visitDate**, где **_visitDate** - это ИД необходимой переменной с нужной нам электронной очередью. 
```xml
<activiti:formProperty id="nID_Department_visitDate" name="Департамент" type="invisible"></activiti:formProperty>
```
значение для переменной id="nID_Department_visitDate" берем из  файла [SubjectOrganDepartment](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8#subjectorgandepartmentcsv)

***

## Отмена заявки. Высвобождение слота  электронной очереди по инициативе заявителя
### Вариант №1.  Использование системного тэга [cancelTask]
1. в имейле добавить тег **[cancelTask]**. Тэг преобразуется в кнопку “Отменить заявку” +  поле для комментария при закрытии заявки. 
1. в юзертаске, которая подразумевает обработку чиновником, добавить поле с id **sCancelInfo**, тип string - в него запишется комментарий пользователи, с которым он отменял заявку.

Использование тэга позволяет закрыть заявку и высвободить тэг электронной очереди.  
Тэг можно использовать только в процессе с электронной очередью.
* ![12_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/12_1.JPG) 
### Вариант №2. Использование системного тега [cancelTaskSimple]
в емейл добавляем системный тэг **[cancelTaskSimple]**, который преобразуется в кнопку **вже неактуально, закрити заявку**. Можно использовать в  любых процессах.  
На первом этапе  отмена заявки по этому тэгу не  освобождает слот электронной очереди.

***

## Отмена заявки. Высвобождение слота  электронной очереди по инициативе  сотрудника или системы
Для реализации нужно добавить сервис таску:  
```xml
<serviceTask id="servicetask" name="Видалення тікета з черги" activiti:delegateExpression="#{releaseTicketsOfQueue}">
</serviceTask>
```

[Скрипт для получения даты/времени напоминания о выбранной дате из электронной очереди](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82-%D0%B4%D0%BB%D1%8F-%D0%BF%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D1%8F-%D0%B4%D0%B0%D1%82%D1%8B%D0%B2%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%B8-%D0%BD%D0%B0%D0%BF%D0%BE%D0%BC%D0%B8%D0%BD%D0%B0%D0%BD%D0%B8%D1%8F-%D0%BE-%D0%B2%D1%8B%D0%B1%D1%80%D0%B0%D0%BD%D0%BD%D0%BE%D0%B9-%D0%B4%D0%B0%D1%82%D0%B5-%D0%B8%D0%B7-%D1%8D%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D0%BE%D0%B9-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8)

## Изменить срок заказа электронной очереди
По умолчанию: заказ осуществляется на послезавтра  
Если необходимо "сдвинуть" начало генерации слотово (например, не ранее, чем через 4 дня), прописываем в дефолте количество дней
```xml
<activiti:formProperty id="nDiffDays_visitDate1" name="nDiffDays_visitDate1" type="invisible" default="4"></activiti:formProperty>
```


Объединение нескольких слотов по потоку  
Задание количества дней отсрочки по показу слотов очереди  
Автогенерация слотов  
Подключение сторонних очередей  
добавить в  БП такие поля (можно на стартовой таске):
```xml
<activiti:formProperty id="sID_Payment" name="ИД транзакции платежа" type="invisible" default=" "></activiti:formProperty>
<activiti:formProperty id="nID_Subject" name="ИД-номер субъекта" type="invisible" ></activiti:formProperty>
<activiti:formProperty id="sID_Merchant" name="ИД-строковой мерчанта (магазина)" type="invisible" default="i10172968078"></activiti:formProperty>
<activiti:formProperty id="sSum" name="сумма платежа" type="invisible" default="0.01"></activiti:formProperty>
<activiti:formProperty id="sID_Currency" name="ИД-строковой валюты" type="invisible" default="UAH"></activiti:formProperty>
<activiti:formProperty id="sDescription" name="строка-описание платежа" type="invisible" default="Тестовая транзакция"></activiti:formProperty>
```
* ![13_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/13_0.JPG)
* в письмо встроить тэг **[paymentButton_LiqPay]**, где необходимо разместить кнопку для проплаты

* ![13_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/13_1.JPG)

* в дашборде чиновника добавить параметр, ссылающийся на переменную с Id  платежа

```xml
<activiti:formProperty id="sIDPayment" name="id платежа" type="string" default="${sID_Payment}" writable="false" ></activiti:formProperty`>
```
* ![13_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/13_2.JPG)

На бэке (wf-base) доработана обработка тэга **[paymentButton_LiqPay]** так, чтоб он поддерживал множественные кнопки оплаты LiqPay в рамках одного письма. [Issue 789](https://github.com/e-government-ua/i/issues/789) 


