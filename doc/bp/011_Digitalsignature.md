## Создание подписанного ЭЦП документа cо стартовой формы
Необходимо добавить строки на стартовую таску:
```xml
<activiti:formProperty id="form_signed" name="Заява з ЕЦП" type="file" required="true"></activiti:formProperty>` 
<activiti:formProperty id="PrintFormAutoSign_1" name="Шаблон для наложения ЭЦП" type="invisible" default="pattern/print/example_print_01.html"></activiti:formProperty>
```
где pattern/print/example_print_01.html -  шаблон печатной формы заявления, на которую будет накладываеться ЭЦП.  

Если вместо ид **form_signed** будет поставлен ид **form_signed_all**, то ЕЦП будет наложена так же на все подгружаемые файлы.  

Если убрать свойство **required="true"**, то наложение ЕЦП на указанную форму будет необязательной опцией.

При использовании простого "name" как в примере ниже - используется BankID-конвертер "html в pdf", который имеет гарантированную работоспособность но налагающий массу требований по форматированию исходного html-файла.
```xml
<activiti:formProperty id="form_signed" name="Заява з ЕЦП" type="file" required="true"></activiti:formProperty>` 
```
## Конвертирование html в PDF
Как альтернатива вышеуказанному способу существует конвертор собственный - его использование задается в дополнительном параметре описанном в "name". Данный конвертор более качественно переводит в PDF формат исходный html-файл. Также он позволяет успешно использовать встроенные в html-файл java-скрипты.
```xml
<activiti:formProperty id="form_signed" name="Заява з ЕЦП; ;bPrintFormFileAsPDF=true" type="file" required="true"></activiti:formProperty>` 
```
На юзертасках добавить стандартный набор листнеров для подгрузки файлов.  

[валидатор файлов, на которые должен быть наложена ЕЦП](https://github.com/e-government-ua/iBP/wiki/%D0%9C%D0%B0%D1%80%D0%BA%D0%B5%D1%80%D1%8B-%D0%B8-%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80%D1%8B#filesign---%D0%92%D0%B0%D0%BB%D0%B8%D0%B4%D0%B0%D1%82%D0%BE%D1%80-%D0%95%D0%A6%D0%9F)
