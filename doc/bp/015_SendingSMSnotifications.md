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
* ![14_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/14_0.JPG)

* Если  емейл не предусматривает отправку файла, то  указываем “ “ в качестве  значения  параметра файла для отправки:  
```xml
       <activiti:field name="saAttachmentsForSend">
          <activiti:expression><" "></activiti:expression>
        </activiti:field>
```

