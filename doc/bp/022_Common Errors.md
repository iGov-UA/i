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
