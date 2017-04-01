
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


* добавить в  БП такие поля (можно на стартовой таске):
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

Создаем сервис таску, для которой указываем [один из трех](https://github.com/e-government-ua/iBP/wiki/%D0%A2%D0%B8%D0%BF%D1%8B-Listener-%D0%B8-delegateExpression#delegateexpression) delegateExpression:  
`#{MailTaskWithoutAttachment}`  
`#{MailTaskWithAttachments}`  
`#{MailTaskWithAttachmentsAndSMS}`  

Параметры сервис таски:  
**from** (expression)  
**to** (expression)  
**subject** (expression)  
**text** (expression) - Тело письма. Поддержимает html формат. Переменные прописываются в виде ${id}  
**saAttachmentsForSend** (expression) - указывается id файла ( в виде ${id} ) для отправки с емейлом.  Используется только  для MailTaskWithAttachments  

Чтобы отправить автоматически сформированную принт-форму, добавленную в юзер-таске через id="sBody_N", необходимо добавлять строку в юзер-таске типа   
```xml
<activiti:formProperty id="PrintForm_1" name="File label;File title;pattern/print/dnepr_cnap_184_print.html" type="file"></activiti:formProperty>
```
* ![14_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/14_2.JPG)
а потом подгружать к вложениям в письмо на сервис-таске соответствующую ${PrintForm_1}

## Использование переменных в шаблонах емейлов

в БП есть поля с типом enum  
у них есть id ,как и у любых других полей  
нужно взять этот id, (какого-то БП), и на базе него прописать где-то в письме тэг **enum{[*]}**  
например поле с id="typeOfDocument"  
тогда в письме нужно вставить: **enum{[typeOfDocument]}**  
тогда туда подставится значение выбранного в форме пункта энума  
**enum{[ … ]}** - тег приоритетной подстановки значение енума (можно использовать в шаблонах емейлов)  
**value{[ … ]}** - тег приоритетной подстановки  значения переменной (можно использовать в шаблонах емейлов)  
если нужно отправить  системный тег - например номер заявки - то пишем просто в квадратных скобках - **[sID_Order]** 
* ![14_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/14_1.JPG)

## Работа со справочниками данных в емейлах
[Issue 839](https://github.com/e-government-ua/i/issues/839)  
Расположены \wf-base\src\main\resources\patterns\dictionary  
**1) в патернах (wf-base)**  
должен находиться патерн-справочник, например по пути: /patterns/dictonary/MVD_Department.csv
в котором, через точку с запятой должны быть данные по строкам.  
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

## Шаблоны емейлов
расположены : \wf-region\src\main\resources\pattern\mail    
В сервис-таске прописываем тэги с учётом того что из шаблонов не подтягиваются значения переменных активити:  
Тело письма с обращением к клиенту и опросом качества  в таком случае будет выглядеть как:  
[pattern/mail/_common_header.html]  
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

## обновленные шаблоны емейлов
```
[pattern/mail/new_design/_common_header.html]
[pattern/mail/new_design/_common_content_start.html]
```
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

[тексты шаблонов](https://docs.google.com/document/d/1iU1hv8B51We6D62_WDHysqXn1O-c5huXierQrhCViuI/edit)  

Заявка успешно подана гражданином  
**Vashe zvernennya [sID_Order] zareestrovano**

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


1. [Форматирование даты в процессе](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%A4%D0%BE%D1%80%D0%BC%D0%B0%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-%D0%B4%D0%B0%D1%82%D1%8B-%D0%B2-%D0%BF%D1%80%D0%BE%D1%86%D0%B5%D1%81%D1%81%D0%B5)  
1. [Получение даты/времени на нужном этапе процесса](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%B4%D0%B0%D1%82%D1%8B%D0%B2%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%B8-%D0%BD%D0%B0-%D0%BD%D1%83%D0%B6%D0%BD%D0%BE%D0%BC-%D1%8D%D1%82%D0%B0%D0%BF%D0%B5-%D0%BF%D1%80%D0%BE%D1%86%D0%B5%D1%81%D1%81%D0%B0)  
1. [Получение даты/времени напоминания о выбранной дате из электронной очереди](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82-%D0%B4%D0%BB%D1%8F-%D0%BF%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D1%8F-%D0%B4%D0%B0%D1%82%D1%8B%D0%B2%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%B8-%D0%BD%D0%B0%D0%BF%D0%BE%D0%BC%D0%B8%D0%BD%D0%B0%D0%BD%D0%B8%D1%8F-%D0%BE-%D0%B2%D1%8B%D0%B1%D1%80%D0%B0%D0%BD%D0%BD%D0%BE%D0%B9-%D0%B4%D0%B0%D1%82%D0%B5-%D0%B8%D0%B7-%D1%8D%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D0%BE%D0%B9-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8)
1. [Вычисление любой даты до (для напоминания) или после (для удаления заявки) даты визита](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%92%D1%8B%D1%87%D0%B8%D1%81%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BB%D1%8E%D0%B1%D0%BE%D0%B9-%D0%B4%D0%B0%D1%82%D1%8B-%D0%B4%D0%BE-%D0%B4%D0%BB%D1%8F-%D0%BD%D0%B0%D0%BF%D0%BE%D0%BC%D0%B8%D0%BD%D0%B0%D0%BD%D0%B8%D1%8F-%D0%B8%D0%BB%D0%B8-%D0%BF%D0%BE%D1%81%D0%BB%D0%B5-%D0%B4%D0%BB%D1%8F-%D1%83%D0%B4%D0%B0%D0%BB%D0%B5%D0%BD%D0%B8%D1%8F-%D0%B7%D0%B0%D1%8F%D0%B2%D0%BA%D0%B8-%D0%B4%D0%B0%D1%82%D1%8B-%D0%B2%D0%B8%D0%B7%D0%B8%D1%82%D0%B0)  
1. [Удаление конкретного слота за продолжителный период](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%A3%D0%B4%D0%B0%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BA%D0%BE%D0%BD%D0%BA%D1%80%D0%B5%D1%82%D0%BD%D0%BE%D0%B3%D0%BE-%D1%81%D0%BB%D0%BE%D1%82%D0%B0-%D0%B7%D0%B0-%D0%BF%D1%80%D0%BE%D0%B4%D0%BE%D0%BB%D0%B6%D0%B8%D1%82%D0%B5%D0%BB%D0%BD%D1%8B%D0%B9-%D0%BF%D0%B5%D1%80%D0%B8%D0%BE%D0%B4)  
1. [Формирование динамического списка документов (для принтформ)](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%A4%D0%BE%D1%80%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-%D0%B4%D0%B8%D0%BD%D0%B0%D0%BC%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B3%D0%BE-%D1%81%D0%BF%D0%B8%D1%81%D0%BA%D0%B0-%D0%B4%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D0%BE%D0%B2-%D0%B4%D0%BB%D1%8F-%D0%BF%D1%80%D0%B8%D0%BD%D1%82%D1%84%D0%BE%D1%80%D0%BC)  
1. [Получение ИД текущего процесса](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%98%D0%94-%D1%82%D0%B5%D0%BA%D1%83%D1%89%D0%B5%D0%B3%D0%BE-%D0%BF%D1%80%D0%BE%D1%86%D0%B5%D1%81%D1%81%D0%B0)  
1. [Формирование динамического названия юзертаски](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%A4%D0%BE%D1%80%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-%D0%B4%D0%B8%D0%BD%D0%B0%D0%BC%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B3%D0%BE-%D0%BD%D0%B0%D0%B7%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F-%D1%8E%D0%B7%D0%B5%D1%80%D1%82%D0%B0%D1%81%D0%BA%D0%B8)  
1. [Счетчик добавления номера в поле входящего номера](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%A1%D1%87%D0%B5%D1%82%D1%87%D0%B8%D0%BA-%D0%B4%D0%BE%D0%B1%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D1%8F-%D0%BD%D0%BE%D0%BC%D0%B5%D1%80%D0%B0-%D0%B2-%D0%BF%D0%BE%D0%BB%D0%B5-%D0%B2%D1%85%D0%BE%D0%B4%D1%8F%D1%89%D0%B5%D0%B3%D0%BE-%D0%BD%D0%BE%D0%BC%D0%B5%D1%80%D0%B0)  
1. [Счетчик добавления номера в поле входящего номера](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%9D%D0%B0%D0%B7%D0%BD%D0%B0%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%B4%D0%B0%D1%82%D1%8B-%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D0%BD%D0%B5%D0%BD%D0%B8%D1%8F---%D0%BF%D0%BE-%D1%83%D0%BC%D0%BE%D0%BB%D1%87%D0%B0%D0%BD%D0%B8%D1%8E--30-%D0%B4%D0%BD%D0%B5%D0%B9-%D0%BA-%D1%82%D0%B5%D0%BA%D1%83%D1%89%D0%B5%D0%B9-%D0%B4%D0%B0%D1%82%D1%8B-%D0%B8-%D0%BF%D0%B5%D1%80%D0%B5%D0%B2%D0%BE%D0%B4-%D0%B4%D0%B0%D1%82%D1%8B-%D0%B2-%D0%BD%D1%83%D0%B6%D0%BD%D1%8B%D0%B9-%D1%84%D0%BE%D1%80%D0%BC%D0%B0%D1%82-%D0%B4%D0%B4%D0%BC%D0%BC%D0%B3%D0%B3%D0%B3%D0%B3)  
1. [Получение ИД БП](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%98%D0%94-%D0%91%D0%9F)  
1. [Получение логина и ФИО основного исполнителя (человека которого в таблице выбрали первым)](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B/_edit#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BB%D0%BE%D0%B3%D0%B8%D0%BD%D0%B0-%D0%B8-%D0%A4%D0%98%D0%9E-%D0%BE%D1%81%D0%BD%D0%BE%D0%B2%D0%BD%D0%BE%D0%B3%D0%BE-%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D0%BD%D0%B8%D1%82%D0%B5%D0%BB%D1%8F-%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D0%BA%D0%B0-%D0%BA%D0%BE%D1%82%D0%BE%D1%80%D0%BE%D0%B3%D0%BE-%D0%B2-%D1%82%D0%B0%D0%B1%D0%BB%D0%B8%D1%86%D0%B5-%D0%B2%D1%8B%D0%B1%D1%80%D0%B0%D0%BB%D0%B8-%D0%BF%D0%B5%D1%80%D0%B2%D1%8B%D0%BC)  



execution.getVariable('var1') - обращение к переменной var1 для получения её значения  
execution.setVariable('var2', value) - запись  значения value в переменную var2  

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
```
* ![16_7](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_7.JPG)
[описание операторов и переменных для написания скриптов Javascript](http://javascript.ru/)  

Для получения значения даты на момент срабатывания скрипта можно использовать:
```javaScript
var unixdate= Math.round((new Date()).getTime())   // в формате UNIX
```
и далее работать с этим числом, прибавляя-отнимая время в милисекундах



***

## Форматирование даты в процессе.
Создать скрипт-таску, в main-config задать: script lang - “groovy”
```groovy
execution.setVariable("docDateFormat", execution.getVariable("docDate").format("yyyy-MM-dd"))
```
* ![16_8](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_8.JPG)
где  
**docDate** - имя параметра даты в процессе с форматом типа date.  Дата выбиралась из календарика.  
**yyyy-MM-dd** - итоговый требуемый формат даты.  регистрозависимо!!!   
**docDateFormat** - переменная, где сохранится  дата уже в необходимом указанном формате.  

***

## Получение даты/времени на нужном этапе процесса
надо создать скрипт groovy, а во второй аргумент функции  setVariable вписать создание объекта Date с ключевым словом new.
В общем скрипт будет выглядеть вот так:
```xml
<scriptTask id="scripttask1" name="Script Task" scriptFormat="groovy" activiti:autoStoreVariables="false">
  <script>
  execution.setVariable('dCreate', new Date().format("dd.MM.yyyy").toString())
  </script>
</scriptTask>
```
* ![16_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_6.JPG)
где
**dCreate** - это поле в которое поместится результат (Время берется по Гринвичу)  
**format("dd.MM.yyyy")** - задаем формат получаемого времени. Если формат не указываем то  по умолчанию дата / время будут в формате Tue Apr 26 14:51:17 UTC 2016  
**toString()** - не обязательно, это перестраховка  

## Получение даты/времени напоминания о выбранной дате из электронной очереди
Пример использования  доступен в процессе dnepr_dms_passport  
Необходимо создать скрипт-таску. Указать язык javaScript  

```javaScript 
var src=execution.getVariable('date_of_visit')   //  получаем значение, выбранного времени из электронной очереди из зарезервированной переменной из date_of_visit

var year=src.substr(0,4)
var month=src.substr(5,2)
var day=src.substr(8,2)
var hour=src.substr(11,2)
var minutes=src.substr(14,2)
var seconds='00'
var delta=1 				//  задаем количество дней, за которое необходимо будет напомнить о визите
if (day!='01')				//  парсим полученную дату/время из date_of_visit
	{
    	day=day-delta
	}
else
	{
    	if ((month!='01')&&(month!='03'))
      	{
          	month=month-1
          	day=30
      	}
    	else
      	{
        	if (month=='03')
        	{
          	month='02'
          	day='28'
        	}       	 
      	}
 	}
var timer=year+'-'+month+'-'+day+'T'+hour+':'+minutes+':'+seconds //склеиваем строку в нужном формате

execution.setVariable('sNotification_day', timer)  //возвращаем в процесс итоговую переменную в нужном формате для таймера
```
* ![16_9](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_9.JPG)

**sNotification_day**  - это заранее созданная в процессе переменная, в которую вернем результат работы скрипта 

## Вычисление любой даты до (для напоминания) или после (для удаления заявки) даты визита:
```javaScript 
var src=execution.getVariable('date_of_visit');  //получаем значение, выбранного времени из электронной очереди из зарезервированной переменной из date_of_visit
//Парсим
var year=src.substr(0,4);
var month=src.substr(5,2);
var day=src.substr(8,2);
var hour=src.substr(11,2);
var minutes=src.substr(14,2);
var seconds='00';
var delta='10';//переменная, указывает на +\- сдвиг во времени (дни)
//Конвертируем в юникстайм
var date1 = year+'-'+month+'-'+day+' '+hour+':'+minutes+':'+seconds;
var date2 = Date.parse(date1)
var unixdate= date2/1000;
//Работаем с юникстаймом
var unixdate=unixdate+86400*delta; //умножаем на сколько нужно дней в секундах
//Конвертируем назад в дату
var new_src = new Date(unixdate*1000);
var d = new_src.getDate();
if (d < 10) d = "0"+d;
var m = new_src.getMonth();
var m = (+m + +1);
if (m < 10) m = "0"+m;

var new_year = new_src.getFullYear();
var new_hour = new_src.getHours();
var new_minutes = new_src.getMinutes();
var timer = new_year+'-'+m+'-'+d+'T'+new_hour+':'+new_minutes+':'+seconds;
execution.setVariable('sDelete_day', timer)
```
Текущую дату на момент срабатывания скрипта можно получить так:  
```javaScript 
var unixdate= Math.round((new Date()).getTime() / 1000);
```
## Удаление конкретного слота за продолжителный период
```javaScript 
var aDate=[
"2017-01-19"
,"2017-01-20"
,"2017-01-21"
,"2017-01-22"
,"2017-01-23"
    ];
for(n=0;n<aDate.length;n++){
    var tok = 'system' + ':' + 'system';
        hash = btoa(tok);
        authInfo = "Basic " + hash;
$.ajax({
    type: 'DELETE',
    url: 'https://delta.test.region.igov.org.ua/wf/service/action/flow/clearFlowSlots?nID_Flow_ServiceData=34&sDateStart='+aDate[n]+' 10:00:00.00&sDateStop='+aDate[n]+' 10:14:00.00',
    beforeSend: function (xhr) { xhr.setRequestHeader ("Authorization", authInfo); },
    crossDomain: true,
    dataType: 'json',
    
    success: function(result) {
        // Do something with the result
//  alert(result);
console.info("n="+n+",aDate[n]="+aDate[n]);
    }
});
}
alert("ok");
```
нужно зайти на регион сервера, с которого будет удаляться свободные слоты,  например  
https://delta.test.region.igov.org.ua  
открываем консоль F12 и туда вставляем, проверяем массив дат, свой логин/пароль  

***

## Формирование динамического списка документов (для принтформ)
```javaScript 
var per1 = "<li>Документ, що посвідчує особу (паспорт, свідоцтво про народження);</li>"
var per6 = " "
var per7 = " "
var res = 'res'
if(execution.getVariable('sDocPay') != null){
per6 = '<li>Квитанція про сплату державного мита;</li>'
}
if(execution.getVariable('sDocMarriage') != null){
per7 = '<li>Свідоцтво про шлюб;</li>'
}
res = '<ol>'+per1+per6+per7+'</ol>'
execution.setVariable('sListDocumentsPrint', res)
```
## Получение ИД текущего процесса
```javaScript 
var processInstanceId = execution.getProcessInstanceId();
execution.setVariable('processInstanceId', processInstanceId)
```
* ![16_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_2.JPG)

## Формирование динамического названия юзертаски
```javaScript 
var sUsertask1_name =""
if (execution.getVariable('asCategoryIncoming')=='sEnumCentralGov'){
sUsertask1_name="документ центральних органів влади"
}
if (execution.getVariable('asCategoryIncoming')=='sEnumActsRevision'){
sUsertask1_name="акт ревізії"
}
if (execution.getVariable('asCategoryIncoming')=='sEnumLogisticalSupport'){
sUsertask1_name="заявка на матеріально-технічне забезпечення"
}
execution.setVariable('sUsertask1_name', sUsertask1_name)
```

***

## Счетчик добавления номера в поле входящего номера
```javaScript 
var number=execution.getVariable('sID_Order_GovPublic')
var fullNumber="вх-"+number+"/"
execution.setVariable('sID_Order_GovPublic', fullNumber)
```
* ![16_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_3.JPG)
***

## Назначение даты исполнения - по умолчанию + 30 дней к текущей даты и перевод даты в нужный формат дд/мм/гггг
```javaScript 
var updatedDate = new Date();
var days = 30;
updatedDate.setDate(new Date().getDate() + days);
var d = updatedDate.getDate();
if (d < 10) d = "0"+d;
var m = updatedDate.getMonth() + 1;
if (m < 10) m = "0"+m;
var sNewDate = (d + "/" + m + "/" + updatedDate.getFullYear());
execution.setVariable('sDateExecution', sNewDate)
```

***

## Получение ИД БП
```javaScript 
var executeProcessDefinitionId = execution.getProcessDefinitionId();
var splittedProcessDefinitionId = executeProcessDefinitionId.split(':')[0];
execution.setVariable('processDefinitionId', splittedProcessDefinitionId)
```
* ![16_4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_4.JPG)

***

## Получение логина и ФИО основного исполнителя (человека которого в таблице выбрали первым)
```javaScript 
var processInstanceId = execution.getVariable('processInstanceId');
var obj = processSubjectTreeService.getCatalogProcessSubjectTree(processInstanceId, 1, null, false, 1);
var sMainExecutor = " ";
var sFirstName = " ";
var sLastName = " ";

var i = 0;
do {
i++
} while (obj.aProcessSubjectTree[0].sLogin != obj.aProcessSubjectTree[0].aUser[i].sLogin)
execution.setVariable('sMainExecutor', obj.aProcessSubjectTree[0].aUser[i].sLogin)
execution.setVariable('sFirstName', obj.aProcessSubjectTree[0].aUser[i].sFirstName)
execution.setVariable('sLastName', obj.aProcessSubjectTree[0].aUser[i].sLastName)
var sExecutor = obj.aProcessSubjectTree[0].aUser[i].sFirstName + " " + obj.aProcessSubjectTree[0].aUser[i].sLastName
execution.setVariable('sExecutor', sExecutor) 

* ![16_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/16_1.JPG)
***

# wf-central  
путь к конфигурационным файлам: **\i\wf-central\src\main\resources\data\**  

### Category
### City
### Country
### Currency
### Document
### DocumentAccess
### DocumentContentType
### DocumentOperator_SubjectOrgan
### DocumentType
### HistoryEvent_Service
### HistoryEvent
### Merchant
### ObjectCustoms
### ObjectEarthTarget
### ObjectPlace_UA
### Place_
### Place
### PlaceTree_
### PlaceTree
### PlaceType_
### PlaceType
### Region
### Server
### Service
* nID  - идентификатор услуги. Задаем новый (смотрим максимальный в конце файла и добавляем +1). В исключительных случаях не добавляем новую, а “занимаем” место старой, которая распознается подчеркиванием, стоящим перед названием услуги (без согласования такое не делать). Если есть пропуск в нумерации - можно и нужно занимать их (в идеале не должно быть пропусков в номерах). Найти такую строку, и отписаться о ее заполнении можно в файле https://docs.google.com/spreadsheets/d/13L8S76lHPjXi28Nk09CSB4JwQjL8k8pMQwnMNQcZMME/edit#gid=0 
* sName - название услуги. 
* nOrder - Целочисленный коэффициент, определяющий “Важность” услуги, ее очередность в показе пользователю. По умолчанию ставим 100. Чем меньше, тем выше по списку стоит услуга. У услуг с одинаковым номером сортируется по алфавиту.
* nID_Subcategory - Идентификатор подкатегории (Subcategory.csv) (1-Будівництво, нерухомість, земля, 2-Взаємодія з державними органами, 3- Поліція и т.т.). Особое внимание необходимо уделить nID_Category в подкатегории, так как разделяют подкатегории для Громадян (nID_Category=1) и Бизнес (nID_Category=2), т.е. одна и та же по названию подкатегория представлена двумя записями (для граждан и юрлиц).
* sInfo - Информация об услуге. Пока оставляем пустым
* sFAQ - FAQ. Пока оставляем пустым
* sLaw - ссылки за законы, инструкции. Пока оставляем пустым
* nOpenedLimit - По умолчанию 0. Максимальное количество  одновременно поданных (открытых) заявок от одного гражданина. Если 0, то ограничения нет. 
* sSubjectOperatorName - Название органа, отвечающего за услугу (Міністерство внутрішніх справ, Міська рада и т.д.)
* Пример.
* nID;sName;nOrder;nID_Subcategory;sInfo;sFAQ;sLaw;nOpenedLimit;sSubjectOperatorName
* 1;Надання довідки про притягнення до кримінальної відповідальності, відсутність (наявність) судимості або обмежень, передбачених 
 кримінально-процесуальним законодавством України;1;3;[*];;;0;Міністерство внутрішніх справ
* 788;Надання дозволу на знесення аварійних будівель;100;1;;;;0;Сільська рада
* 40;Повідомлення про проведення зборів, мітингів, маніфестацій і демонстрацій, спортивних, видовищних та інших масових заходів;100;2;;;;0;Сільська рада

* 1038;40;467;467;NULL;4;{"processDefinitionId":"spend_meeting_404:1:1"};;false;1;true;;BankID,EDS;0
* ![17_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_1.JPG)

### ServiceData
* nID - идентификатор процесса. Всегда новый (смотрим максимальный в конце файла и добавляем +1) 
* nID_Service - номер услуги в файле Service.csv. 
* nID_Place - идентификатор местности (номер в Place.csv) 
* nID_City - идентификатор города или села (номер в City.csv). Ставим NULL, если услуга открывается для всей области
* nID_Region - идентификатор региона (номер в Region.csv). Ставим NULL, если услуга для города или села
* nID_ServiceType - идентификатор типа сервиса (4 - услуга портала, 1-внешняя и т.д.). Большинство услуг портальных. 
* oData - информация для системного запуска процесса, где указывается ID (имя файла) диаграммы процесса, которую надо запускать и версию 1:1 по формату {"processDefinitionId":"< ID процесса >:1:1"}.
* Пример {"processDefinitionId":"znes_bud_393:1:1"}. Для внешних услуг указываем пустые скобки {}.
* sURL - указание URL для внешней реализации услуги. Например, http://www.cnap.if.ua/posl/4345
* bHidden - скрытая (true) или видимая (false) услуга (позволяет скрывать услуги, которые даже уже запущены)
* nID_Subject_Operator - Используется, когда один процесс на несколько городов. По умолчанию 1. При необходимости берем номер из SubjectOrgan.csv
* bTest - Услуга в процессе тестирования (true, т.е. желтого цвета) или рабочая (false, т.е. “зеленая”)
* sNote - Комментарии. Используется при редиректе. Могут быть не заполнены
* asAuth - тип авторизации. По умолчанию BankID,EDS 
* nID_Server - Идентификатор сервера. По умолчанию 0

* Пример.
* nID;nID_Service;nID_Place;nID_City;nID_Region;nID_ServiceType;oData;sURL;bHidden;nID_Subject_Operator;bTest;sNote;asAuth;nID_Server
* 1;1;2;2;NULL;1;{};https://null.igov.org.ua;true;1;true;Перейдя на этот сайт Вы сможете получить услугу;BankID,EDS,KK;0
* 1032;788;467;467;NULL;4;{"processDefinitionId":"znes_bud_393:1:1"};;false;1;true;;BankID,EDS;0
* 038;1471;467;467;NULL;4;{"processDefinitionId":"spend_meeting_404:1:1"};;false;1;true;;BankID,EDS;0
* ![17_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_0.JPG)

### ServiceOperator_SubjectOrgan
### ServiceTag
### ServiceTagLink
### ServiceTagRelation
### ServiceTagType
### ServiceType
### Subcategory

***
### Subject
**nID** - номер по порядку  
**sID** - оставляем пустым  
**sLabel** - имя чиновника или название органа, название органа должно начинаться с нижнего подчеркивания  
**sLabelShort** - оставляем пустым 
* ![17_2](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_2.JPG)
***
### SubjectAccount
**nID** - номер по порядку  ## 
**sLogin** - в точности скопированный логин пользователя или в точности скопированный ИД группы  
**sNote** - имя чиновника или название органа (рекомендуется синхронизировать с sLabel из файла Subject.csv)  
**nID_SubjectAccountType** - ставим всегда 1  
**nID_Server** - ставим всегда 0
**nID_Subject** - номер строки из файла Subject - связка с конкретным чиновником или органом 
* ![17_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_3.JPG)
***

### SubjectAccountType
### SubjectActionKVED

***
### SubjectContact
**nID** - номер по порядку  
**nID_Subject** - номер строки из файла Subject - связка с конкретным чиновником или органом  
**nID_SubjectContactType** - тип контакта - взять из файла SubjectContactType.csv. Чаще всего используются (0-телефон, 1-почта)  
**sValue** - непосредственно значение контакта. Если это телефон - то номер телефона, если это почта - то емайл  
***

### SubjectContactType
### SubjectHuman
### SubjectMessage
### SubjectMessageType
### SubjectOrgan
### SubjectOrganJoin
**nID** - номер строки, добавляется инкрементом  
**nID_SubjectOrgan** - номер подтягивать из файла SubjectOrgan  
**sNameUa** - название административного органа на украинском языке  
**sNameRu** - название административного органа на русском языке  
**sID_Privat** - ИД  
**sID_Public**  
**sGeoLongitude**  
**sGeoLatitude**  
**nID_Region**  
**nID_City**  
**sID_UA**  
* ![17_4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/17_4.JPG)
### SubjectOrganJoinAttribute
### SubjectOrganJoinTax

# wf-base  
Путь:i\wf-base\src\main\resources\data\  

### EscalationRule
**nID** - номер по порядку  
**sID_BP** - ИД бизнес-процесса, для которого настраиваем эскалацию (например - kiev_soc_help_177)  
**sID_UserTask** - в кавычках название юзертаски на которую настраиваем эскалацию (нужно на все юзертаски это сделать) (например - "usertask1")  
**sCondition*** - условие  
**soData** - тут прописываем количество дней - через сколько должна сработать эскалация. Если тип эскалации - письмо, то прописываем адрес электронной почты на которое нужно это письмо отправить, можно несколько адресов указать через запятую. Если нужно чтоб эскалация срабатывала тут же в момент подачи заявки - ставим отрицательное значение в количестве дней (например - "{nDaysLimit:-1, asRecipientMail:['darja.grek@gmail.com']}")  
**sPatternFile** - системный файл, всегда пишем одно и тоже ("escalation/escalation_template.html")  
**nID_EscalationRuleFunction** - одно из существующих правил эскалации. 1- это письмо, 2 - это порождаемый бизнес-процесс

### EscalationRuleFunction
### Flow_ServiceData
### FlowLink
### FlowProperty
### FlowPropertyClass
### FlowSlot
### FlowSlotTicket_FlowSlot
### FlowSlotTicket
### SubjectOrganDepartment

**[18.0 Установка Eclipce](https://github.com/e-government-ua/iBP/wiki/%D0%A3%D1%81%D1%82%D0%B0%D0%BD%D0%BE%D0%B2%D0%BA%D0%B0-Eclipce)**   
**[18.1 Ветки](https://github.com/e-government-ua/iBP/wiki/%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D0%B3%D0%B8%D1%82%D0%BE%D0%BC-%D0%B8-%D1%80%D0%B5%D0%BF%D0%BE%D0%B7%D0%B8%D1%82%D0%BE%D1%80%D0%B8%D0%B5%D0%BC#%D0%92%D0%B5%D1%82%D0%BA%D0%B8)**  
**[18.2 Переключение между ветками](https://github.com/e-government-ua/iBP/wiki/%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D0%B3%D0%B8%D1%82%D0%BE%D0%BC-%D0%B8-%D1%80%D0%B5%D0%BF%D0%BE%D0%B7%D0%B8%D1%82%D0%BE%D1%80%D0%B8%D0%B5%D0%BC#%D0%9F%D0%B5%D1%80%D0%B5%D0%BA%D0%BB%D1%8E%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BC%D0%B5%D0%B6%D0%B4%D1%83-%D0%B2%D0%B5%D1%82%D0%BA%D0%B0%D0%BC%D0%B8)**  
**[18.3 Мерж](https://github.com/e-government-ua/iBP/wiki/%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D0%B3%D0%B8%D1%82%D0%BE%D0%BC-%D0%B8-%D1%80%D0%B5%D0%BF%D0%BE%D0%B7%D0%B8%D1%82%D0%BE%D1%80%D0%B8%D0%B5%D0%BC#%D0%9C%D0%B5%D1%80%D0%B6)**  
**[18.4 Разрешение конфликтов при мерже (в Eclipse)](https://github.com/e-government-ua/iBP/wiki/%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D0%B3%D0%B8%D1%82%D0%BE%D0%BC-%D0%B8-%D1%80%D0%B5%D0%BF%D0%BE%D0%B7%D0%B8%D1%82%D0%BE%D1%80%D0%B8%D0%B5%D0%BC#%D0%A0%D0%B0%D0%B7%D1%80%D0%B5%D1%88%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BA%D0%BE%D0%BD%D1%84%D0%BB%D0%B8%D0%BA%D1%82%D0%BE%D0%B2-%D0%BF%D1%80%D0%B8-%D0%BC%D0%B5%D1%80%D0%B6%D0%B5-%D0%B2-eclipse)**  
**[18.5 Выкладка версии](https://github.com/e-government-ua/iBP/wiki/%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D0%B3%D0%B8%D1%82%D0%BE%D0%BC-%D0%B8-%D1%80%D0%B5%D0%BF%D0%BE%D0%B7%D0%B8%D1%82%D0%BE%D1%80%D0%B8%D0%B5%D0%BC#%D0%92%D1%8B%D0%BA%D0%BB%D0%B0%D0%B4%D0%BA%D0%B0-%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D0%B8)**  
**[18.6 Как накатить ветку одну ветку на другую (в NetBeans)](https://github.com/e-government-ua/iBP/wiki/%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D0%B3%D0%B8%D1%82%D0%BE%D0%BC-%D0%B8-%D1%80%D0%B5%D0%BF%D0%BE%D0%B7%D0%B8%D1%82%D0%BE%D1%80%D0%B8%D0%B5%D0%BC#%D0%9A%D0%B0%D0%BA-%D0%BD%D0%B0%D0%BA%D0%B0%D1%82%D0%B8%D1%82%D1%8C-%D0%B2%D0%B5%D1%82%D0%BA%D1%83-%D0%BE%D0%B4%D0%BD%D1%83-%D0%B2%D0%B5%D1%82%D0%BA%D1%83-%D0%BD%D0%B0-%D0%B4%D1%80%D1%83%D0%B3%D1%83%D1%8E-%D0%B2-netbeans)**  
**[18.7 (в Eclipse)](https://github.com/e-government-ua/iBP/wiki/%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D0%B3%D0%B8%D1%82%D0%BE%D0%BC-%D0%B8-%D1%80%D0%B5%D0%BF%D0%BE%D0%B7%D0%B8%D1%82%D0%BE%D1%80%D0%B8%D0%B5%D0%BC#%D0%9E%D1%82%D0%BA%D0%B0%D1%82-%D0%B8%D0%B7%D0%BC%D0%B5%D0%BD%D0%B5%D0%BD%D0%B8%D0%B9-%D0%B2-eclipse)**  

# Ветки
Очень упрощенная схема веток, с которыми работают бизнес-аналитики приведена на рисунке:  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sTU1vOU12R2tLc0k)  
Основная разработка и тестирование бизнес-процессов происходит в ветке test-delta.
Как установить и зятнуть себе локально репозиторий описано [здесь](https://github.com/e-government-ua/i/wiki/%D0%A3%D1%81%D1%82%D0%B0%D0%BD%D0%BE%D0%B2%D0%BA%D0%B0-ActivitiDesigner,-%D0%B4%D0%BB%D1%8F-%D1%80%D0%B5%D0%B4%D0%B0%D0%BA%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F-%D0%B1%D0%B8%D0%B7%D0%BD%D0%B5%D1%81-%D0%BF%D1%80%D0%BE%D1%86%D0%B5%D1%81%D1%81%D0%BE%D0%B2). 
Програмисты ведут свою разработку в ветке test.  
Когда появляется необходимость синхронизировать доработки программистов и бизнес-процессы - это осуществляется мержем через ветку test-version.
# Переключение между ветками
Правой кнопкой мыши на ветке: **Team-Swith To-New Branch**  
![2](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sRlZCdHZLb2ZEV1E)  
Select  
выбрать ветку на которую хотите переключиться  
сделать pull  
# Мерж
1. Необходимо находится в ветке **в которую** будет осуществляться мерж  
![2](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sdFM5VnJpb01yZkE)  
1. сделать Pull  
1. нажать правой кнопкой на проекте и выбрать **team**, затем **merge**  
![4](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_seldVN1hxaW9DU1E)  
1. выбрать ветку **из которой** будем мержить и нажимаем "Merge"  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sVUFnWXliUk5aeTQ)  
1. При мерже могут возникнуть конфликты в файлах, которые необходимо разрешить. Конфликты обозначены красным ромбиком.  
![2](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_scmhKTTNmUDRQN0k)

# Разрешение конфликтов при мерже (в eclipse)
нажимаем правой кнопкой на конфликтном файле: **team-merge tool**  
выбираем отображение панелей (нажимайте "ок")  
на экране будут показаны 2 варианта файла с разных веток, которые конфликтуют между собой. Необходимо принять решение версию какого файла считать актуальной.
Если производится мерж альфы и дельты, то по все файлам из папки autodeploy отдавать предпочтение ветке delta (принимать как более актулаьные файлы с ветки delta).
Если более актуальная версия файла ветки из которой производится мерж - необходимо добавить эти изменения нажав на квадратик между версиями  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sMlRxbU40T2pIQ1k)  

**Обратите внимание!** Это очень неудобная фишка в eclipse - строки добавляются, а не заменяются.  
Поэтому дубли нужно удалить вручную. И затем сохранить файл.  

Если более актуальная версия файла ветки в которую производится мерж - ничего не делаем, закрываем файл.  
В случае если Вы принимали изменения или не принимали их (оставили файл как есть) - в любом из этих случаев необходимо присвоить индекс версии.  
Правой кнопкой мыши нажимаем на конфликтном файле и выбираем **team-Add to index**  
Красный ромбик пропадет, появляется звездочка.  

Разрешение конфликтов необходимо сделать на каждом файле отдельно.  
После присвоения индексов каждом конфликтному файлу необходимо собрать проект локально.
Если локальная сборка прошла успешно - закомитить и вытолкнуть все изменения. **Commit-Pull-Push**

# Выкладка версии
Перед переброской файлов желательно сделать бекап текущей ветки мастер, чтобы откатывать было легче  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sMHRrOGVjSkhuM0E)  
Файлы копируются из дельты в отдельную папку (папки), потом переключаемся в ветку мастер, делаем бекап и просто через ctrl+c, ctrl+v копируем файлы и папки, при предложении заменить - соглашаемся.  
При переносе версии аналитиков из дельты в мастер переносятся следующие файлы и папки:  
### wf-base  
копируем из корня, перебрасываем в корень и в папку prod  
```
wf-base\src\main\resources\patterns\dictionary
wf-base\src\main\resources\data\SubjectOrganDepartment.csv 
wf-base\src\main\resources\data\Flow_ServiceData.csv
wf-base\src\main\resources\data\FlowLink.csv
wf-base\src\main\resources\data\FlowProperty.csv
```
копируем из папки prod и перебрасываем только в папку prod
```
wf-base\src\main\resources\data\prod\EscalationRule.csv
```
### wf-central
папка wf-central\src\main\resources\patterns   
копируем из корня, перебрасываем в корень и в папку prod  
```
wf-central\src\main\resources\data\SubjectOrganJoin.csv
```
копируем из корня и перебрасываем только в корень
```
wf-central\src\main\resources\data\City.csv
wf-central\src\main\resources\data\Merchant.csv
wf-central\src\main\resources\data\Place.csv
wf-central\src\main\resources\data\Service.csv
wf-central\src\main\resources\data\ServiceData.csv
wf-central\src\main\resources\data\ServiceTag.csv
wf-central\src\main\resources\data\ServiceTagLink.csv
wf-central\src\main\resources\data\Subject.csv
wf-central\src\main\resources\data\SubjectOrgan.csv
wf-central\src\main\resources\data\SubjectOrganJoinAttribute.csv
```
### wf-region
```
wf-region\src\main\resources\bpmn\markers
wf-region\src\main\resources\bpmn\autodeploy
wf-region\src\main\resources\pattern\print
```
* По умолчанию деплоим оба бека и рестартовать фронт централа (новые города, изменения бпмн по услугам и т.п.)  
После всех проверок на боевом делаем обратный мерж.  
Путь обратного мержа на данный момент master - delta - alpha.  
При конфликтах в наших файлах дельте принимаем дельту (если не договорено иное), в альфе - альфу.  
По файлам программистов или спрашиваем Вову или принимаем мастер для дельты и альфу для альфы.  

# Как накатить ветку одну ветку на другую (в NetBeans)
1. По сути это процесс копирования файлов из одной ветки и замена в другой.  
IDE NetBeans позволяет автоматизировать этот процесс.  
2. Выбираем нужную ветку, то есть ту куда надо внести изменения.  
В вкладке Файлы кликаем по проекту ПКМ и в контекстном меню выбираем   
**git -> получение -> получение файлов**  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_seThOSFp2bHlhTjA)  
3. Появляется окно, в нем ставим галку в поле **Обновить элементы индекса по выбранной редакции**:  и кликаем кнопку выбрать, где выбираем ветку с которой будем брать необходимые обновления  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sakQ4MlowWkhtZ1U)  
4. В выбранную ветку скопируется все файлы с другой ветки, и те файлы которые были изменены (имели отличие) разукрасятся в разные цвета.  
5. Необходимо откатить изменения которые мы не хотим добавлять, для этого в вкладе Файлы выделяем файлы(можно папками) для того чтоб откатить.  
**ПКМ -> Git -> Откатить изменения.**  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sdFZUa05JdWZyblE)  
6. Ставим галку **Также удалять новые файлы и папки** и кликаем по **Откатить**  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sazNwQm9DTUxqWjg)  
5 и 6 шаг  повторяем для всех файлов которые нам не нужно обновлять.  
7. Собираем локально  
8. Фиксим изменения  
9. Выталкиваем  

### Откат изменений (в eclipse)

Installationeclipse.md

***
### Устанавливаем JAVA JDK
1. Переходим по [ссылке](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)  
2. Соглашаемся и качаем    
[детальнее...](#_Устанавливаем JAVA JDK)

### Устанавливаем Eclipse
[1. Качаем последнюю версию](
http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/neon/2/eclipse-committers-neon-2-win32-x86_64.zip) *** 
2. копируем скачанный архив в нужную папку где он будет храниться  
3. распаковываем архив 
[детальнее...](#_Устанавливаем Eclipse)


### Ставим дополнительные утилиты для удобства редактирования JSON и HTML
1. переходим в маркет  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_scWg3N3RiSU56MWM)  
2. ищем нужные нам приложения и устанавливаем, процесс установки такой же как и в пункте 7, только не надо вставлять адрес УРЛ на приложение  и называть его. Теоретически можно было так и активити с майвеном поставить. 
[детальнее...](#_Ставим дополнительные утилиты для удобства редактирования JSON и HTML)


019_Autotests.md
# Подготовка среды

1. Проверяем, что локально установлена версия Firefox 46.0 (если стоит более старшая, то просто поверх нее инсталлируем необходимую версию по [ссылке](https://ftp.mozilla.org/pub/firefox/releases/46.0/win64/ru/).
2. Отключаем автоматическое обновление в Firefox  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sNmM4ZW94R05NOE0)
3. Подключаем в Eclipse / NetBeans 
Репозиторий кода автотестов BPMN - Github: **https://github.com/e-government-ua/iTest.git**  
Ветка единственная - Master  
_Для тестирования используется сервер https://beta.test.igov.org.ua/ Соответственно тесты проверяют структуру бизнес-процессов, которые развернуты на БЕТЕ (в ветке Бета основного хранилища)._
4. Автотесты пишутся для каждого БП отдельно. Создается (копируется) файл, имя которого совпадает с именем BPMN для которого будем создавать (описывать) тесты (конкретного БП) и с расширением java (например, dnepr_cnap_39.java). В файле может быть описано несколько сценариев (в зависимости от выбранного пункта select\enum). 

**ВНИМАНИЕ!** Если в имени BPMN использовался символ “-” (минус), то его необходимо заменить на “_” (подчеркивание). Техническое ограничение, связанное с тем, что имена классов в JAVA не могут содержать символ “-” (минус). 
Например:  
dnepr_cnap_39.bpmn -> dnepr_cnap_39.java  
nikopol_mvd-1.bpmn -> nikopol_mvd_1.java  

[Путь к автотестам](https://github.com/e-government-ua/iTest/tree/master/src/test/java/autoTests/TestSiute)  
Локальный  
iTest/src/test/java/autoTests/TestSiute  

***

# Добавление файла с автотестом
Алгоритм работы с файлом `<Name BPMN>`.java
## 1. Изменяем имя класса и имя конструктора (функции вызова автотеста)
#### Изменяем строку public class 
```java
public class Test_Example_Fill_Field  extends CustomMethods {
```
#### меняем имя класса на имя нашего файла (с учетом замененных знаков “-”).  
Например:
```java
public class dnepr_cnap_39 extends CustomMethods {
```
####Изменяем строку public void 
```java
public void Test_Example_Fill_Field() throws Exception {
```
Например
```java
public void default_test() throws Exception {
```
####Заполняем блок данными
```java
TemplatePage o = new TemplatePage(driver);
//  Вносим в переменные название услуги начиная с точки ._test_fields_bankid_--_ и до начала названия поля
String sBP = "dnepr_cnap_39";
String email = "autotestbeta@gmail.com";

_step("1. Вход по прямому URL на услугу");
openURLservice(driver, CV.baseUrl + "/service/139/general");

_step("2. Проверить, что открылась нужная услуга");
assertThis(driver, o.usluga, "Копії рішень міської (селищної) ради про надання дозволу на розроблення проекту відведення земельної ділянки");

_step("3. Выбор области/города");
o.selectRegion("Київська");
o.selectCity("Ірпінь");

_step("4. Авторизация Off AuthMock/BankID");
o.mokAuthorization();

_step("5. Заполняем форму услуги");
setFieldAutocomplete(driver,"sID_Public_SubjectOrganJoin","ЦНАП м. Ірпінь");
setFieldValue(driver, sBP, "phone", "+380623155533");
setFieldValue(driver, sBP, "email", email);
setFieldValue(driver, sBP, "sObjName", "номер, дату та назву рішення ради");
setFieldValue(driver, sBP, "sObjAdress", "Місцезнаходження (адреса) об’єкта");
setFieldValue(driver, sBP, "sDavName", "повне найменування юридичної особи");
setFieldValue(driver, sBP, "kved", "11.11");
setFieldValue(driver, sBP, "edrpou_inn", "12345678");
setFieldValue(driver, sBP, "sRukov", "П.І.Б. керівника юридичної особи");
setFieldValue(driver, sBP, "sOrgAdress", "Місцезнаходження юридичної особи");
setFieldValue(driver, sBP, "sMailClerk", email);

setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","0", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","0", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","0", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","0", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","0", "2017/03/05");

addTableRow(driver, sBP, "sTable1");
 
setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","1", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","1", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","1", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","1", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","1", "2017/03/05");

_step("6. Отправка формы");
click(driver, o.buttonSendingForm);

_step("7. Проверка сообщения о успешной отправке");
o.checkMessageSuccess("Шановний(-а) MockUser MockUser!\n" +
"Ваше звернення х-хххххххх успішно зареєстровано\n" +
"(номер також відправлено Вам електронною поштою на Ваш e-mail "+email+") Результати будуть спрямовані також на email.\n" +
"Звертаємо увагу, що Іноді листи потрапляють у спам або у розділ \"Реклама\" (для Gmail).");

_step("8. Нажать кнопку Выйти");
click(driver, o.buttonLogOut);
    }
```
## 2. Изменяем имя тестируемого BPMN
Имя БП задаем в формате  
String sn = "<Имя bpmn-ника>";  
Например, 
```xml
String sBP = "dnepr_cnap_39";
```
## 3. Указываем URL для тестируемого BPMN
```java
addStepToTheReport("1. Вход по прямому URL на услугу");
openURLservice(driver, CV.baseUrl + "/service/720/general");
```
Например,
```java
addStepToTheReport("1. Вход по прямому URL на услугу");
openURLservice(driver, CV.baseUrl + "/service/139/general");
```
## 4. Указываем name тестируемого BPMN (название услуги)
```java
addStepToTheReport("2. Проверить, что открылась нужная услуга");
assertThis(driver, tp.usluga, "_test_fields_bankid");
```
Например,
```java
 addStepToTheReport("2. Проверить, что открылась нужная услуга");
assertThis(driver, tp.usluga, "Копії рішень міської (селищної) ради про надання дозволу на розроблення проекту відведення земельної ділянки");
```
## 5. Указываем для тестирования область / город. 
Обратите внимание, что указывать необходимо текстовые значения области и города.  
Например,
```java
        addStepToTheReport("3. Выбор области/города");
        tp.selectRegion("Київська");
        tp.selectCity("Ірпінь");
```
## 6. Проверяем\уточняем авторизацию для тестирования услуги (на текущий момент - Mock-аторизация )
```java
addStepToTheReport("4. Авторизация Off AuthMock/BankID");
tp.mokAuthorization();
```
## 7. Заполняем обязательные поля на старт-таске БП (исключая заполненные поля Mock-аторизацией).  
Обратите внимание, что указывать необходимо корректные с точки зрения возможной валидации значения.  
Для заявителя указывайте служебные реквизиты почты (чтобы не “мусорить” атотестами свою почту)  
autotestbeta@gmail.com     
пароль:  igov2016  

Выбор sID_Public_SubjectOrganJoin из выпадающего списка
```java
setFieldAutocomplete(driver,"sID_Public_SubjectOrganJoin","Text_Value_For_Choice");
```
Текстовые поля заполняются по формату
```java
setFieldValue(driver, sn, "id_Field", "Value_Field");
```
Загрузка файлов по формату
```java
setFieldFile(driver, sBP, "id_Field", "src/test/resources/files/test.jpg");
```
Выбор из выпадающего списка
```java
setFieldSelectByText(driver,sBP,"id_Field","Text_Value_For_Choice");
```
Даты заполняются по формату
```java
setFieldCalendar(driver,sBP,"id_Field_Date","2016/12/25");
```
Электронная очередь выбирается по формату
```java
setFieldSelectSlotDate(driver, sBP, "."+sBP+"_--_"+"visitDay"); 
setFieldSelectSlotTime(driver, sBP, "."+sBP+"_--_"+"visitDay");
```
CheckBox с именем bID_CheckBox выбирается по формату
```java
setFieldCheckBox(driver, sBP, "."+sBP+"_--_"+"bID_CheckBox");
```
Обработка таблиц с разными типами данных. Параметрами передаются имя_таблицы, Имя_Атрибута (столбца), номер_строки, вводимое_значение  
```java
setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","0", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","0", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","0", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","0", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","0", "2017/03/05");

addTableRow(driver, sBP, "sTable1");
 
setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","1", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","1", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","1", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","1", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","1", "2017/03/05");
```
Например
```java
setFieldAutocomplete(driver,"sID_Public_SubjectOrganJoin","ЦНАП м. Ірпінь");
setFieldValue(driver, sBP, "sObjName", "номер, дату та назву рішення ради");
setFieldFile(driver, sBP, "sFile1", "src/test/resources/files/test.jpg");
setFieldSelectByText(driver,sBP,"client","нет");
setFieldCalendar(driver, sBP, "dDate_Beg", "2003/01/01");
setFieldSelectSlotDate(driver, sBP, "."+sBP+"_--_"+"visitDay"); 
setFieldSelectSlotTime(driver, sBP, "."+sBP+"_--_"+"visitDay");
setFieldCheckBox(driver, sBP, "."+sBP+"_--_"+"#bID_CheckBox");

setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","0", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","0", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","0", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","0", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","0", "2017/03/05");

addTableRow(driver, sBP, "sTable1");
 
setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","1", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","1", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","1", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","1", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","1", "2017/03/05");
```
## 8. Если необходимо осуществить тестирование несколько разных сценариев для БП, то необходимо скопировать и соответствующим образом заполнить блок операторов  
```java
addStepToTheReport("1. Вход по прямому URL на услугу");
openURLservice(driver, CV.baseUrl + "/service/720/general");

addStepToTheReport("8. Нажать кнопку Выйти");
click(driver, tp.buttonLogOut);
```
или  создать новые методы, имена которых несут смысловую нагрузку для тестов
```java
    public void for_validators() throws Exception {
    public void for_selectors() throws Exception {
    public void for_sign() throws Exception {
    public void test2() throws Exception {
```
## 9. Если необходимо отключить автотест для БП
то необходимо указать enabled = false в строке 
```java
@Test(enabled = true, groups = {"Main", "Критический функционал"}, priority = 1)
```
и переместить файл в каталог SuspendTests 

***

# Локальное тестирование
создайте профиль и запустите его на выполнение
 
Результаты локального тестирования (вплоть до скриншотов iTest\TestReport\html\Screens) находятся локально по пути выгруженного проекта \iTest\TestReport

**ВНИМАНИЕ!**
_Не посылайте служебные письма о наличии заявок на реальную служебную почту! Рекомендуется либо захардкодить email разработчика на БЕТЕ либо сделать поле видимым и его изменить как стандартную переменную на email  разработчика БП_

020_Escalationsandfeedback.md
# Эскалации

Эскалации создаются для каждой юзертаски каждого бизнес-процесса. Бывает так что для некоторых юзертасок не нужно генерировать эскалации - аналитик решает это сам, учитывая специфику бизнес-процесса.

На данный момент эскалации бывают 2-х типов - уведомление на почту и генерирование (порождение) эскалационной заявки. 
Порождение эскалационной заявки на одной юзертаске происходит только один раз, в то время как письма могут генерироваться на одной юзертаске более одного раза.  
Рекомендуется прописывать оба этих типа эскалаций для каждой юзертаски.  
Правила вносятся в файл [EscalationRule](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#escalationrule). Путьк файлу: i\wf-base\src\main\resources\data\prod\EscalationRule.csv

## Подробное описание одного из параметров: sCondition
Правила запуска эскалаций можно настроить более гибко комбинируя между собой 2 условия:  
ассайнутость таски и количество дней/часов  
**bAssigned==false** - заявка находится в "необроблених"  
**bAssigned==true** - заявка находится у кого-то "в работе"  
**nDaysLimit** - количество дней при достижении которого сработает эскалация - параметр необходим для создания условия.   Вместо параметра nDaysLimit условие можно создавать напрямую прописывая конкретные цифры. Например, вот это равные условия:  
```
"nElapsedHours >= nDaysLimit";"{nDaysLimit:0}"  
"nElapsedHours >= 0";"{nDaysLimit:0}"  
```
**nDueElapsedDays**  - количество дней с момента последнего действия с заявкой  
**nElapsedDays** - количество дней с момента создания заявки (вроде как одно и тоже с nCreateElapsedDays)  
**nCreateElapsedDays** - количество дней с момента создания заявки на юзертаске  
**nElapsedHours**  - количество часов с момента создания заявки (вроде как одно и тоже с nCreateElapsedHours)  
**nCreateElapsedHours** - количество часов с момента создания заявки на юзертаске  
**nDays** - разница (в днях) между сегодняшней датой и датой подачи заявки  

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

**отправить письмо** 
```
431;dnepr_soc_help_148;"usertask1";"nElapsedHours >= nDaysLimit";"{nDaysLimit:-1, asRecipientMail:['darja.grek@gmail.com']}";"escalation/escalation_template.html";1  
442;dnepr_cnap_261;"usertask1";"(nDays >= 0)&&(bAssigned==false)";"{nDaysLimit:-1, asRecipientMail:['darja.grek@gmail.com']}";"escalation/escalation_template.html";1
```
**породить эскалационную заявку** 
```
432;dnepr_soc_help_148;"usertask1";"nElapsedHours >= nDaysLimit";"{nDaysLimit:-1}";"escalation/escalation_template.html";2
443;dnepr_cnap_261;"usertask1";"(nDays >= 0)&&(bAssigned==false)";"{nDaysLimit:-1}";"escalation/escalation_template.html";2
```
	
## Порядок работы с эскалационными заявками:
1. За каждым аналитиком закреплен свой населенный пункт, за отработку заявок которого он отвечает  
2. Аналитик берет в работу только заявки которые относятся к его населенному пункту, звонит в орган который не отработал или долго удерживает заявку, выясняет причины и вносит в поле: “Коментар за результатами контакту з адміністративним органом.” [Речевой модуль](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D1%81%D0%BA%D0%B0%D0%BB%D0%B0%D1%86%D0%B8%D0%B8-%D0%B8-%D1%84%D0%B8%D0%B4%D0%B1%D0%B5%D0%BA%D0%B8#%D0%A0%D0%B5%D1%87%D0%B5%D0%B2%D0%BE%D0%B9-%D0%BC%D0%BE%D0%B4%D1%83%D0%BB%D1%8C-%D0%BF%D1%80%D0%B8-%D0%B7%D0%B2%D0%BE%D0%BD%D0%BA%D0%B5-%D0%B2-%D0%B3%D0%BE%D1%81%D0%BE%D1%80%D0%B3%D0%B0%D0%BD).  
3. В случае если в исходной заявке произошли какие-то изменения (заявка взята в работу из необработанных, заявка перешла на новую юзертаску, заявка была отработана), то эскалационная заявка автоматически удаляется. Другого пути закрыть эскалационную заявку не существует - чтоб эскалационная заявка закрылась - необходимо движение по исходной заявке. Отработка эскалационной заявки подразумевает выяснение причины неотработки и перенос срока рассмотрения эскалационной заявки.  
4. Все комментарии которые внес волонетёр - будут переданы в историю исходной заявки и будут отображены в “Мій журнал”, т.е. заявитель и чиновник их смогут увидеть. Если после переноса срока заявка снова вернется в необработанные, это будет означать что никакого движения по ней не произошло и нужно снова звонить в гос.орган.  

**Для того чтоб в эскалационную заявку передались контакты гос.органа, с которым нужно связаться, нужно заполнить 3 файла:**

[Subject](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subject)  
действия аналитика: добавить имя чиновника или название органа (то есть name пользователя или name группы)  
**пример внесения чиновника:**  
323;;Мартинов Іван Станіславович;  
**пример внесения органа:**  
539;;_УПСЗН м.Бориспіль;  

[SubjectAccount](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectaccount)  
действия аналитика: добавить ИД пользователя или ИД ГРУПЫ  
**пример внесения чиновника:**  
51;cnap_chortT_1;Артур Шимків;1;0;51  
**пример внесения органа:**  
1086;upszn66;УПСЗН смт.Оратів;1;0;1086  

[SubjectContact](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectcontact)  
действия аналитика: добавить для одного и того же Subject (это будет или конкретный пользователь или орган) все возможные контакты  
пример:  
3001;1524;0;(05133)24335,23614  
3002;1524;1;utszn_kroz@ukr.net  
Для Subject 1524(это УПСЗН-смт.Криве) внесен телефон  и емайл  

## Речевой модуль при звонке в гос.орган
**Українська версія**
> Доброго дня!  
> Мене звуть %ім'я%, я один з координаторів порталу iGov в %назву% області.  
> Кілька днів тому було відправлено звернення в електронному вигляді на порталі iGov в вашу службу.  
> Ми отримали інформацію про те, що вона до сьогодні не взята в роботу, тому хотіли б уточнити у Вас, з якої саме причини?   Можливо, у вас виникли проблеми в роботі з порталом?  
> Можливо, я би міг допомогти вам у вирішенні даних питань?  
(Записуєте результат розмови в коментарі).  
> Дякую за приділений час, на все добре, до побачення!  

**Російська версія**
> Добрый день!  
> Меня зовут %имя%, я один из координаторов портала iGov в %название% области.  
> Несколько дней назад было отправлено обращение в электронном виде на портале  iGov в вашу службу.  
> Мы получили информацию о том, что она до сих пор не взята в работу, поэтому хотели бы уточнить у Вас, по какой именно причине, возможно, у вас возникли сложности в работе с порталом?  
> Возможно, я мог(ла) бы помочь вам в решении данных вопросов?  
> (Записываете результат разговора в комментарии).  
> Спасибо за уделенное время, всего хорошего!  


021_Statisticsanduploads.md
Empty

022_CommonErrors.md
### Case 1. Не отображается тест выбранного Enum в принтформе, а отображается ID
Возможные проблемы:
Enum объявлен на юзертаске, как type="invisible"
***
### Case 2. Повідомленя: Transaction rolled back because it has been marked as rollback-only Код: SYSTEM_ERRІнші дані (обь'єкт): {"code":null,"message":null}
Возможные проблемы:
Используемый email получателя блокируется юнисендером
***
### Case 3. Повідомленя: add the file to sendКод: SYSTEM_ERR
Повідомленя: Unknown property used in expression: ${PrintForm_1}  
Повідомленя: Unknown property used in expression: "${file}"  

Возможные проблемы:
проверить delegateExpression - #{MailTaskWithAttachments} если есть вложения, #{MailTaskWithoutAttachment} - если нет вложений
***
### Case 4. Подвисание процесса с ошибками выполнения скрипта

Возможные проблемы:
Некорректное условие в маркере. Типа лишней скобки:   
"sCondition":	"[sClient] == 'nik_pervomayskT' && [sReason] == 'first_permission' ) "  
так же внимательно смотрите на кавычки -   "sCondition": "[sClient] == 'namesurname'||[sClient] == 'прізвище та ім'я'" - тут ошибка ім'я'

***
### Case 5. Отсутствие default значения в переменной с типом label
Есть типы данных (label), для которых обязательно наличие параметра default, даже если он равен пробелу (default=" "), иначе при входе в услугу можно ничего не увидеть

***
### Case 6. Поломанный код xml из-за спецсимволов
Все спецсимволы в xml (<,>,&) должны быть экранированы, иначе завалится сборка (локальная или портал)

***
### Case 7. При локальной сборке возникает ошибка
```xml
Tests in error: 
  testAlphaProcesses(org.activiti.test.bp.ActivitiProcessesTest): Errors while parsing:(..)
  testProdProcesses(org.activiti.test.bp.ActivitiProcessesTest): couldn't create db schema: create table ACT_HI_PROCINST ( (..)
```
**решение**: на одной из сервистасок не хватает параметера delegateExpression

023_UsefulInquiries.md
**запустить правило эскалации** (метод GET)  
```
https://alpha.test.region.igov.org.ua/wf/service/action/escalation/runEscalationRule?nID=425
```

***

**удалить заявку** (метод DEL)   
```
https://alpha.test.region.igov.org.ua/wf/service/action/task/delete-process?nID_Order=020978170
```

***

**выгрузить максимум данных по заявке** (метод GET)  
```
https://alpha.test.region.igov.org.ua/wf/service/action/task/getTaskData?sID_Order=0-219200017&bIncludeStartForm=true&bIncludeGroups=true
```
* ![23_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/23_0.jpg)

***

**выгрузка закрытых заявок за определенный период** (метод GET)   
можно исключать некоторые номера услуг  
```
https://igov.org.ua/wf/service/action/event/getServiceHistoryReport?sDateAt=2016-11-17 00:00:00&sDateTo=2016-11-21 00:00:00&sanID_Service_Exclude=1397,676&sID_FilterDateType=Close&bIncludeTaskInfo =False   
```
чтоб выгрузить открытые за определенный период, нужно поставить параметр sID_FilterDateType=Open

***

**количество услуг по областям за период** (метод GET)  
``` 
https://alpha.test.igov.org.ua/wf/service/action/event/getServicesStatistic?sDate_from=2016-06-01 00:00:00&sDate_to=2016-08-11 00:00:00
```
* ![23_1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/23_1.jpg)

***

**генерация слотов очереди** (метод POST)   
``` 
https://alpha.test.region.igov.org.ua/wf/service/action/flow/buildFlowSlots?nID_Flow_ServiceData=162&sDateStart=2016-09-21 00:00:00.000&sDateStop=2016-11-21 00:00:00.000
```

***
**удаление слотов очереди** (метод DEL) 
``` 
https://alpha.test.region.igov.org.ua/wf/service/action/flow/clearFlowSlots?nID_Flow_ServiceData=110&sDateStart=2016-05-03 00:00:00.000&sDateStop=2016-05-04 00:00:00.000
```
***

**разассайн заявки** (метод GET)   
```
https://alpha.test.region.igov.org.ua/wf/service/action/task/resetUserTaskAssign?nID_UserTask=56679928 
```
берется номер **ТАСКИ** а не Заявки  
***
**статистическая инфо по конкретной услуге в разрезе областей** (метод GET)
```
https://alpha.test.igov.org.ua/wf/service/action/event/getStatisticServiceCounts?nID_Service=176
```

***
**количество заявок, которые видны в дашборде у конкретного логина** (метод GET)  
```
https://alpha.test.region.igov.org.ua/wf/service/action/task/getCountTask?amFilter=[{"sFilterStatus":"OpenedUnassigned"},{"sFilterStatus":"OpenedAssigned"},{"sFilterStatus":"Opened"},{"sFilterStatus":"Closed"}]&sLogin=GrekD  
```
OpenedUnassigned - необроблені  
OpenedAssigned - в роботі  
Opened - усі  
Closed - історія  

***
**выгрузка заявок с полями по конкретному БПшнику за конкретный период** (метод GET)  
```
https://region.igov.org.ua/wf/service/action/task/downloadTasksData?sID_BP=subsidies_Ukr_result&bHeader=true&sTaskEndDateAt=2016-11-02&sTaskEndDateTo=2016-11-03&saFields=${sNameOrgan};${sID_Order};${sDateCreate};${sDateClose}&sID_Codepage=win1251&nASCI_Spliter=59&sDateCreateFormat=dd.MM.yyyy%20HH:mm:ss&sFileName=create_2016-09-26_2016-10-04.csv  
```
две даты - это период ЗАКРЫТИЯ таски: закрыто от и до

***
**получить содержимое аттача, зная ИД аттача и ИД процесса (обычно используется для тейблов)** (метод GET)  
```
https://alpha.test.region.igov.org.ua/wf/service/object/file/download_file_from_db?taskId=24520049&attachmentId=24520026
```

024_ LifeHacking.md

***

Для сравнения файлов в Notepad++ можно установить плагин Compare  - он позволяет открыть рядышком два файла, синхронно их прокручивает и подкрашивает разными цветами различия.  
В меню плагины - Show Plugin manager - выбираем Compare,  Install  
потом Alt+D 

***
## Как связывать услуги с тегами
Для того чтоб услуга для граждан отобразилась на сайте и в результатах поиска, необходимо ее привязать хотя бы к одной жизненной ситуации.  
Для этого необходимо заполнить несколько конфигурационных файлов:  

1. [Service.csv](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#service)  
Важно проследить, чтоб услуга имела nID_Subcategory, которая относится к гражданам  

2. [ServiceTag.csv](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicetag)
В этом файле вносить изменения не нужно. Нужно найти подходящую жизненную ситуацию и взять ее nID.  
Жизненная ситуация отличается от корневого тега по полю nID_ServiceTagType и имеет в этом поле номер "3".  
Практически все корневые теги находятся вверху списка а жизненные ситуации - после 10 nID.  

3. [ServiceTagLink.csv](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicetaglink)  
В этом файле связываем услугу и жизненную ситуацию  
nID_Service - ид услуги из файла Service.csv  
nID_ServiceTag - ид жизненной ситуации из файла ServiceTag.csv  

**например.**  
услуга "Подати заяву про державну реєстрацію шлюбу" - номер в Service.csv 703  
Для нее подходит Жизненная ситуация "Одруження" - номер в ServiceTag.csv 18  
Связываем услугу и жизненную ситуацию - заполняем файл ServiceTagLink.csv:703;18  
это всё  
Самостоятельно **НЕ добавляем** новые жизненные ситуации и Корневые Теги, т.е. НЕ редактируем файлы ServiceTag.csv и ServiceTagRelation.csv  

***
## Как правильно выставлять лейблы в репозитории i
**active** - задача не готова к выкладке версии (несовместима с лейблом version)  
**test** - задача готова и отдана наблюдателю в тестирование (несовместима с лейблом testing, bug, version)  
**testing** - задача взята наблюдателем в тестирование (несовместима с лейблом test, bug, version)  
**version** - задача попадет в версию при деплое и после деплоя будет закрыта (несовместима с лейблом active, test, testing, bug)  
**bug** - задача была протестирована и нашли ошибку (несовместима с лейблом test, testing, version)  
**hold** - задача заморожена по какой-то причине (несовместима с лейблом testing)  

Если все будут придерживаться такой схемы, то сможем легко отслеживать этапы разработки:  
1) задачи в работе (ожидающие исполнителя или в работе у программиста или наблюдателя): лейбл active  
2) задачи сделанные программистами, но не взяты в тестирование: лейбл test  
3) задачи взяты в тестирование, но не протестированые: лейбл testing  
4) задачи с ошибкам находящимися в работе у программиста: лейбл bug   
5) задачи готовые к выкладке версии: лейбл version  
* ![24_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/24_0.jpg)

***
добавить БП в интерсептор, чтоб счетчик заявок срабатывал
src/main/java/org/igov/service/controller/interceptor/RequestProcessingInterceptor.java
```java
   private static final String DNEPR_MVK_291_COMMON_BP = "dnepr_mvk_291_common|_test_UKR_DOC|dnepr_mvk_889|justice_incoming";
```
коммит https://github.com/e-government-ua/i/commit/844a76af959178c327688de752a146fcb0b00646

используется зарезервированная переменная sID_Order_GovPublic
```xml
<activiti:formProperty id="sID_Order_GovPublic" name="Номер звернення ДМР" type="invisible"></activiti:formProperty>
```
счетчик начинает считать с 0 и обнуляется в начале года.
***
Если нужно убрать автоматическое "письмо Привет" нужно добавить id БП в интерсептор
src/main/java/org/igov/service/controller/interceptor/RequestProcessingInterceptor.java
```java
private static final String asID_BP_SkipSendMail = "dnepr_mvk_291_common|rada_0676_citizensAppeals";
```
id БП разделять вертикальным слеш "|"
***
##  Передать значение из одного поля в другое
В активити есть стандартная функция для этого  
![2](https://drive.google.com/uc?export=download&id=0B6mOkUg9oq1zRU5fdTFiNkYyZjg)  
Передать в переменную **sID_Group_Activiti** значение, которое было в поле **sName_SubjectRole**  

***
##  Ссылки на региональные порталы
https://mu-dp.test.region.igov.org.ua/ - мин.юст

025_CheckListTestBranch.md
(в скобках номер услуги на которой можно проверить)
- [x] **подача услуги с файлами и приход файлов в дашборд** (любая)
- [ ] **смена статусов в "мой журнал"** (любая)
- [ ] **валидатор ЕЦП на стартовой форме** (344)
- [ ] **подача заявки с наложением ЕЦП и с прикрепелением файла с наложенным ЕЦП** (344)
- [ ] **маркеры ShowFieldsOnCondition на дашборде** (456)  
> Воспроизвести: Львовская-Любинь - выбрать отказ гражданину - должно появится поле для комментария

- [ ] **маркеры RequiredFieldsOnCondition на дашборде** (1515)
- [ ] **маркеры ValuesFieldsOnCondition на дашборде** ()
- [ ] **маркер ValuesFieldsOnCondition на старте** (1515)
- [ ] **Маркер обязательности подписания ЕЦП** (654) Днепр
- [ ] **проверка ЕЦП на дашборде** (344)
- [ ] **порождение и отработка эскалаций**
> Воспроизвести:  
> 1. подаем заявку: Киев-Киев-Бабушкинский район  
> https://omega.test.igov.org.ua/service/786/general  
> 2. порождаем по ней эскалацию:  
> https://omega.test.region.igov.org.ua/wf/service/action/escalation/runEscalationRule?nID=835  
> 3. находим и отрабатываем эскалацию в дашборде  
> https://omega.test.region.igov.org.ua  
> логин/пароль: volont_escalation  
> 4. следим за статусами:  
> https://omega.test.igov.org.ua/search?sID_Order= номер заявки  

- [ ] **отзывы и фидбеки** - (любая)
> Воспроизвести:  
> 1. закрыть любую заявку  
> 2. зайти в дашборд под логином/паролем: volont_feedback и проверить породилась ли фидбековая заявка  
- [ ] **принтформы** (любая)
- [ ] **письма** (любая)
- [ ] **смски** (невозможно проверить на тестовых серверах)
- [ ] **подача заявки с электронной очередью** (710)
- [ ] **файл от гражданина, прикрепленный через "мой журнал" приходит в дашборд** (любая)
- [ ] **комментарии и уточнения полей в обе стороны+письма по ним**
- [ ] **заявки приходят в "мій розклад", выбранные дата и время корректны** (710)
- [ ] **автокомплит полей которые идут в паре с SOJ** (160)
- [ ] **автозаполнение данными предыдущей заявки** (любая)
- [ ] **редирект** (101)
- [ ] **подача заявки на всю Украину** (118)
- [ ] **кастомная принтформа для наложение ЭЦП на стартформе** (1497)

026_IDoc.md
### [Переменные iDoc](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%AD%D0%94#%D0%9F%D0%B5%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%BD%D1%8B%D0%B5-idoc-1)
### [Листенеры iDoc](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%AD%D0%94#%D0%9B%D0%B8%D1%81%D1%82%D0%B5%D0%BD%D0%B5%D1%80%D1%8B-idoc-1)
### Степы  
### Построение дерева подчинения организации
### [Порождение задач](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%AD%D0%94/_edit#%D0%BF%D1%80%D0%BE%D1%86%D0%B5%D1%81%D1%81-system_taskbpmn)  
### [Callactivity](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%AD%D0%94#callactivity)  
### [Скрипты, которые используются в СЕДе](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%AD%D0%94/_edit#%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B-%D0%BA%D0%BE%D1%82%D0%BE%D1%80%D1%8B%D0%B5-%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D1%83%D1%8E%D1%82%D1%81%D1%8F-%D0%B2-%D0%A1%D0%95%D0%94%D0%B5)  

## Переменные iDoc
**processInstanceId** - ИД процеса активити ([скрипт](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%98%D0%94-%D1%82%D0%B5%D0%BA%D1%83%D1%89%D0%B5%D0%B3%D0%BE-%D0%BF%D1%80%D0%BE%D1%86%D0%B5%D1%81%D1%81%D0%B0) получения)- используется в дальнейшем для вызова всяких сервисов iDoc - обязательное поле для iDoc  

***

**processDefinitionId** - есть [скрипт](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%98%D0%94-%D0%91%D0%9F), который вытащит в эту переменную ИД бизнес-процесса, используется в дальнейшем в листенере, который порождает задачи. Если задачи не будут порождаться, это поле необязательное для iDoc 

***
 
**sKey_Step_Document** - возвращает шаг документа из джейсона - обязательное поле для iDoc. Значение в это поле прописывается автоматически листенером [${DocumentInit_iDoc}](https://github.com/e-government-ua/iBP/wiki/%D0%A2%D0%B8%D0%BF%D1%8B-Listener-%D0%B8-delegateExpression#documentinit_idoc)  

***

**sID_Group_Activiti** - возвращает группу-узел с которого строить выпадающий список в селекте  
**nDeepLevel** - глубина дерева людей организации, на которую показывать выпадающий список. (Например: 1 - покажет только первый уровень подчиненных (для диреткора покажет его замов), уровень 2 - замы и их подчиненные и т.д.)  
Переменные sID_Group_Activiti и nDeepLevel используются для селекта людей. Селект формируется так:  
```xml 
<activiti:formProperty "sAutorResolution" name="Руководитель; ;sID_SubjectRole=Executor,sID_Group_Activiti=sID_Group_Activiti,nDeepLevel=nDeepLevel" type="select"></activiti:formProperty>
```		
Если же нам в селекте надо выбирать не людей, а органы (отделы), то селект формируется так:  
```xml 	 
<activiti:formProperty id="sPrepareted" name="Підготував; ;sID_SubjectRole=ExecutorDepart,sID_Group_Activiti=sID_Group_Activiti_Depart,nDeepLevel=nDeepLevelDepart" type="select" required="true"></activiti:formProperty>
```
* ![26_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/26_0.JPG)
В селекте отделов используюются соответственно переменные  
**sID_Group_Activiti_Depart** - id группы отдела (корень с которого формируем селект)  
**nDeepLevelDepart** - глубина отделов  

***

**sID_Order_GovPublic**  
В любом СЕДе, как правило необходимо автоматически формировать номер входящего документа. Этот номер может иметь различную структуру, в которую как правило входит счетчик, прибавляющий инкриментом одну цифру.
Для того чтоб добавить в БП такой счетчик необходимо:

1. объявить переменную sID_Order_GovPublic
2. Записать значение в эту переменную с помощью сервис таски:
```xml
<serviceTask id="servicetask1" name="ProcessCountTaskCustom" activiti:delegateExpression="#{ProcessCountTaskCustomListener}">
      <extensionElements>
        <activiti:field name="sKey">
          <activiti:string><![CDATA[_doc_justice_6]]></activiti:string>
        </activiti:field>
        <activiti:field name="sPattern">
          <activiti:string><![CDATA[[sID_Custom_GovPublic]]]></activiti:string>
        </activiti:field>
        <activiti:field name="nDigits">
          <activiti:string><![CDATA[5]]></activiti:string>
        </activiti:field>
      </extensionElements>
    </serviceTask>
```	
**sKey** - ключ по которому генерится счетчик, можете задавать любой. Если Вы вставите этот же ключ в другой БП, то номера продолжат генерироваться по нему в двух процессах (будут присваиваться по порядку)
**sPattern** - всегда sID_Custom_GovPublic   
**nDigits** - количество циферок в генерируемом номере

***

## Листенеры iDoc
[листенер ${SetTasks}](https://github.com/e-government-ua/iBP/wiki/%D0%A2%D0%B8%D0%BF%D1%8B-Listener-%D0%B8-delegateExpression#settasks)  
[листенер ${DocumentInit_iDoc}](https://github.com/e-government-ua/iBP/wiki/%D0%A2%D0%B8%D0%BF%D1%8B-Listener-%D0%B8-delegateExpression#documentinit_idoc)  
[листенер ${UpdateStatusTask}](https://github.com/e-government-ua/iBP/wiki/%D0%A2%D0%B8%D0%BF%D1%8B-Listener-%D0%B8-delegateExpression#updatestatustask)  
[листенер ${UpdateStatusTaskTreeAndCloseProcess}](https://github.com/e-government-ua/iBP/wiki/%D0%A2%D0%B8%D0%BF%D1%8B-Listener-%D0%B8-delegateExpression#updatestatustasktreeandcloseprocess)  

### Порождение задач
Системный процесс - порождает задачи, расписанные на исполнителей в родительском процессе.  
В system_task данные пробрасываются с помощью листенера [SetTasks](https://github.com/e-government-ua/iBP/wiki/%D0%A2%D0%B8%D0%BF%D1%8B-Listener-%D0%B8-delegateExpression#settasks). Поддерживается мультипорождение и синхронизация данных в процесс-родитель.

```javascript
var number=execution.getVariable('sID_Order_GovPublic')
var fullNumber="вх-"+number+"/"
execution.setVariable('sID_Order_GovPublic', fullNumber)
```

на выходе получим номер формата: вх-0000007/  
3. добавить БП в интерсептор (разделитель - вертикальная черта), чтоб счетчик заявок срабатывал.  
Путь к интерсептору: _src/main/java/org/igov/service/controller/interceptor/RequestProcessingInterceptor.java_
```java
private static final String DNEPR_MVK_291_COMMON_BP = "dnepr_mvk_291_common|_test_UKR_DOC|dnepr_mvk_889|justice_incoming";
```

### callactivity
Вызов другого процесса из текущего стандартными средствами активити. Отличается от вызова при помощи листенера [SetTasks](https://github.com/e-government-ua/iBP/wiki/%D0%A2%D0%B8%D0%BF%D1%8B-Listener-%D0%B8-delegateExpression#settasks) тем, что не может принимать значение обратно в случае мультипорождения нескольких подпроцессов.
Очень хорошо его можно применять, когда в случае входящего документа необходимо породить 1 исходящий из входящего.
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sdmtSVzlWamhER3c)
Во вкладке **Main Config**  в поле **Called element** прописываем ИДБП который необходимо вызвать **(1)**, в блоке **Input parameters** прописываем поля, которые необходимо пробросить в порождаемый процесс **(2)**.
После выполнения **callactivity** процесс идет дальше, в данном случае закрывается, при этом порождается новый процесс _doc_justice_22.
Если же так же указать параметры в блоке **Output parameters**, то  callactivity будет ждать эти поля из порожденного процесса и пока они не будут переданы - дальше не пойдет.

**мультипорождение подпроцессов с использованием callactivity**  
Во вкладке **Multi instance** заполняем поля:   
**1. Sequential**
* **true** задачи будут порождаться последовательно - сначала на одного пользователя, после того как он отработает - на другого; На схеме это будет отражено горизонтальными черточками на узле callactivity.  
* **false** - задачи породятся одновременно (параллельно) на всех пользователей. На схеме это будет отражено вертикальными черточками на узле callactivity.  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_scWk3UWtJSko4Q3M)   

**2. collection** - тут надо прописать **${usersService.getUsersLoginByGroup('admin')}** - это означает что будут выбраны пользователи из группы admin (можно подставить любую группу активити). Этот запрос возвращает джейсон, из которого надо выбрать элемент, который мы объявим в поле elementVariable, в данном случае это sLogin.  
**3. elementVariable** - **sLogin**.  
Далее уже sLogin можно поставить в поле assignee порожденного процесса - и процессы будут порождаться сразу в работу этим логинам.
Пока не будут отработаны все порожденные задачи, процесс в котором был вызван callactivity продвигаться по схеме не будет.  

Тестовые процессы, на которых можно посмотреть:  
_test_StartMultiStandaloneProcess - родительский процесс  
_testSimpleSubProces - порожденный  
***

### Скрипты, которые используются в СЕДе
[Получение ИД текущего процесса](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%98%D0%94-%D1%82%D0%B5%D0%BA%D1%83%D1%89%D0%B5%D0%B3%D0%BE-%D0%BF%D1%80%D0%BE%D1%86%D0%B5%D1%81%D1%81%D0%B0)  
[Счетчик добавления номера в поле входящего номера](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%A1%D1%87%D0%B5%D1%82%D1%87%D0%B8%D0%BA-%D0%B4%D0%BE%D0%B1%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D1%8F-%D0%BD%D0%BE%D0%BC%D0%B5%D1%80%D0%B0-%D0%B2-%D0%BF%D0%BE%D0%BB%D0%B5-%D0%B2%D1%85%D0%BE%D0%B4%D1%8F%D1%89%D0%B5%D0%B3%D0%BE-%D0%BD%D0%BE%D0%BC%D0%B5%D1%80%D0%B0)  
[Назначение даты исполнения](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%9D%D0%B0%D0%B7%D0%BD%D0%B0%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%B4%D0%B0%D1%82%D1%8B-%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D0%BD%D0%B5%D0%BD%D0%B8%D1%8F---%D0%BF%D0%BE-%D1%83%D0%BC%D0%BE%D0%BB%D1%87%D0%B0%D0%BD%D0%B8%D1%8E--30-%D0%B4%D0%BD%D0%B5%D0%B9-%D0%BA-%D1%82%D0%B5%D0%BA%D1%83%D1%89%D0%B5%D0%B9-%D0%B4%D0%B0%D1%82%D1%8B-%D0%B8-%D0%BF%D0%B5%D1%80%D0%B5%D0%B2%D0%BE%D0%B4-%D0%B4%D0%B0%D1%82%D1%8B-%D0%B2-%D0%BD%D1%83%D0%B6%D0%BD%D1%8B%D0%B9-%D1%84%D0%BE%D1%80%D0%BC%D0%B0%D1%82-%D0%B4%D0%B4%D0%BC%D0%BC%D0%B3%D0%B3%D0%B3%D0%B3)   
[Получение ИД БП](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%98%D0%94-%D0%91%D0%9F)   
[Получение логина и ФИО основного исполнителя](https://github.com/e-government-ua/iBP/wiki/%D0%A1%D0%BA%D1%80%D0%B8%D0%BF%D1%82%D1%8B#%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BB%D0%BE%D0%B3%D0%B8%D0%BD%D0%B0-%D0%B8-%D0%A4%D0%98%D0%9E-%D0%BE%D1%81%D0%BD%D0%BE%D0%B2%D0%BD%D0%BE%D0%B3%D0%BE-%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D0%BD%D0%B8%D1%82%D0%B5%D0%BB%D1%8F-%D1%87%D0%B5%D0%BB%D0%BE%D0%B2%D0%B5%D0%BA%D0%B0-%D0%BA%D0%BE%D1%82%D0%BE%D1%80%D0%BE%D0%B3%D0%BE-%D0%B2-%D1%82%D0%B0%D0%B1%D0%BB%D0%B8%D1%86%D0%B5-%D0%B2%D1%8B%D0%B1%D1%80%D0%B0%D0%BB%D0%B8-%D0%BF%D0%B5%D1%80%D0%B2%D1%8B%D0%BC)  
