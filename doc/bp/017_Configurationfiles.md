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

### ServiceData
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
***
### SubjectAccount
**nID** - номер по порядку  ## 
**sLogin** - в точности скопированный логин пользователя или в точности скопированный ИД группы  
**sNote** - имя чиновника или название органа (рекомендуется синхронизировать с sLabel из файла Subject.csv)  
**nID_SubjectAccountType** - ставим всегда 1  
**nID_Server** - ставим всегда 0
**nID_Subject** - номер строки из файла Subject - связка с конкретным чиновником или органом  
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
