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
