# Детальная информация

### _string 
[вернуться...](#string)

строка - для текстовых полей ввода (ограничение 256 символов) 
![2_3](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_3.jpg)
### _enum
[вернуться...](#enum)

выпадающий список - для выбора значения из списка 

![2_4](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_4.JPG)
### _enum (checkbox)
[вернуться...](#enum (checkbox))

чекбокс - доступно только 2 выбора да/нет.

Чтоб получить чекбокс, необходимо сделать поле с типом enum с двумя вариантами выпадающего списка.

Третьим атрибутом переменной **name** через ";" добавляем параметр **sID_CheckboxTrue** и приравниваем его к ид первого атрибута енума: sID_CheckboxTrue=first_enum_attribute

![2_11](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_11.JPG)
### _date
[вернуться...](#date)

дата - календарь для выбора даты
![2_5](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_5.JPG)
### _boolean
[вернуться...](#boolean)

принимаемые значения: true / false
### _label
[вернуться...](#label)

```xml
<activiti:formProperty id="info" name="Зверніть увагу" type="label" default="Ви можете здійснити оплату зручним для Вас способом"></activiti:formProperty>
```
 используется для отображения текстовых подсказок на форме подачи / обработки обращения 
 (обязательно добавлять default=” ”, если не указано другое значение). 
 
 Поддерживается форматирование html.
 ![2_6](https://github.com/e-government-ua/i/blob/test/doc/bp/img/2_6.JPG)
