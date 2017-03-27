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
