добавить в  БП такие поля (можно на стартовой таске):
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
