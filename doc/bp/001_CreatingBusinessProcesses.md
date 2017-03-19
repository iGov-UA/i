# сбор информации и предварительная подготовка
* получить информационную и технологическую карточку услуги
* пример заявления, бланки, шаблоны документов
* реквизиты для оплаты (для платных услуг)
* график приема граждан (для услуг с электронными очередями)
* контакты ответственного лица за отработку заявок
* Если необходимо - создать справочник административных органов по регионам - название, адрес, телефон, график работы. Путь к справочникам: i\wf-base\src\main\resources\patterns\dictionary
* поставить задачу на гитхабе

# разработка бизнес-процесса
* создать новую диаграмму. Для Eclipse:New-Other-Activiti Diagram. Путь для размещения bpmn-файлов: \i\wf-egion\src\main\resources\bpmn\autodeploy.
* ![screenshot of sample1] (i/doc/bp/1bp.jpg)
* ![screenshot of sample2](https://github.com/e-government-ua/i/blob/test/doc/bp/2bp.jpg)
* ![screenshot of sample3](https://github.com/e-government-ua/i/blob/test/doc/bp/3bp.jpg)
* [Правила при именовании бинес-процессов](https://github.com/e-government-ua/iBP/wiki/%D0%9E%D0%B1%D1%89%D0%B8%D0%B5-%D0%BF%D1%80%D0%B0%D0%B2%D0%B8%D0%BB%D0%B0-%D0%BF%D1%80%D0%B8-%D0%B8%D0%BC%D0%B5%D0%BD%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B8:#%D0%B1%D0%B8%D0%B7%D0%BD%D0%B5%D1%81-%D0%BF%D1%80%D0%BE%D1%86%D0%B5c%D1%81%D1%8B).  
* проработать инфокарты и бланки заявлений - составить перечень необходимых полей и условий
* прорисовать всю схему процесса от начала до конца, наполнить блоки информацией.
* создать принтформы

# подключение всех необходимых сущностей
создать пользов* ателей и группы на дельте - связать их между собой
* написать "Як це працює" в формате html. Путь для размещения файла:\i\wf-central\src\main\resources\patterns\services\Info
* добавить файл с принтформой в формате html. Путь для размещения файла: i\wf-region\src\main\resources\pattern\print. Желательно использовать папку по конкретному органу или направлению.
* заполнить [Service](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#service), [Создание бизнес-процесса дополнительно](https://docs.google.com/document/d/1B3OIYjj3S2YLwUR-PVD3FAcErl_2ua0CYUB5vys6O4U/edit ), [ServiceData](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicedata), [SubjectOrganJoin](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorganjoin), [SubjectOrgan](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#subjectorgan)
* Если необходимо добавить населенный пункт которого нет в списке - добавить его в сущности [Place](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#place), [PlaceTree](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#placetree), [City](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#city).
* При необходимости - добавить [электронные очереди](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D0%BB%D0%B5%D0%BA%D1%82%D1%80%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D0%B8). 
* Прописать [эскалации](https://github.com/e-government-ua/iBP/wiki/%D0%AD%D1%81%D0%BA%D0%B0%D0%BB%D0%B0%D1%86%D0%B8%D0%B8-%D0%B8-%D1%84%D0%B8%D0%B4%D0%B1%D0%B5%D0%BA%D0%B8).

# тестирование и проливка на бету и боевой
* перед каждой проливкой на сервер проект необходимо собирать и запускать локально
* пройти процесс от начала до конца по всем возможным путям
* выслать заказчику инструкцию, ссылки, логин и пароль.
* в случае необходимости - вносить изменения в процесс. после внесения даже небольшого изменения - обязательное тестирование
* создать пользователей и группы на дельте - связать их между собой
* скопировать процессы в подпапки бета и прод. i\wf-region\src\main\resources\bpmn\autodeploy\prod
* заменить тестовые почты чиновников на настоящие
* перенести все связанные сущности в подпапки прод (при наличии там файлов, совпадающих по названиям в обоих папках)
* Если проливка происходит напрямую в ветках test-version или master обязательно необходимо осущетсвлять обратный мерж в нижние ветки
* после проливки на боевой проверить процесс хотя бы по одному сценарию, закрыть ишью.
