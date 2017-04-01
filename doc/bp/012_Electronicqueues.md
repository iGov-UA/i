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
