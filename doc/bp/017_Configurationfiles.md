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
