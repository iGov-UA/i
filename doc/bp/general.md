
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
# Детальная информация

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
