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
