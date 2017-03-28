
***

Для сравнения файлов в Notepad++ можно установить плагин Compare  - он позволяет открыть рядышком два файла, синхронно их прокручивает и подкрашивает разными цветами различия.  
В меню плагины - Show Plugin manager - выбираем Compare,  Install  
потом Alt+D 

***
## Как связывать услуги с тегами
Для того чтоб услуга для граждан отобразилась на сайте и в результатах поиска, необходимо ее привязать хотя бы к одной жизненной ситуации.  
Для этого необходимо заполнить несколько конфигурационных файлов:  

1. [Service.csv](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#service)  
Важно проследить, чтоб услуга имела nID_Subcategory, которая относится к гражданам  

2. [ServiceTag.csv](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicetag)
В этом файле вносить изменения не нужно. Нужно найти подходящую жизненную ситуацию и взять ее nID.  
Жизненная ситуация отличается от корневого тега по полю nID_ServiceTagType и имеет в этом поле номер "3".  
Практически все корневые теги находятся вверху списка а жизненные ситуации - после 10 nID.  

3. [ServiceTagLink.csv](https://github.com/e-government-ua/iBP/wiki/%D0%9A%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B#servicetaglink)  
В этом файле связываем услугу и жизненную ситуацию  
nID_Service - ид услуги из файла Service.csv  
nID_ServiceTag - ид жизненной ситуации из файла ServiceTag.csv  

**например.**  
услуга "Подати заяву про державну реєстрацію шлюбу" - номер в Service.csv 703  
Для нее подходит Жизненная ситуация "Одруження" - номер в ServiceTag.csv 18  
Связываем услугу и жизненную ситуацию - заполняем файл ServiceTagLink.csv:703;18  
это всё  
Самостоятельно **НЕ добавляем** новые жизненные ситуации и Корневые Теги, т.е. НЕ редактируем файлы ServiceTag.csv и ServiceTagRelation.csv  

***
## Как правильно выставлять лейблы в репозитории i
**active** - задача не готова к выкладке версии (несовместима с лейблом version)  
**test** - задача готова и отдана наблюдателю в тестирование (несовместима с лейблом testing, bug, version)  
**testing** - задача взята наблюдателем в тестирование (несовместима с лейблом test, bug, version)  
**version** - задача попадет в версию при деплое и после деплоя будет закрыта (несовместима с лейблом active, test, testing, bug)  
**bug** - задача была протестирована и нашли ошибку (несовместима с лейблом test, testing, version)  
**hold** - задача заморожена по какой-то причине (несовместима с лейблом testing)  

Если все будут придерживаться такой схемы, то сможем легко отслеживать этапы разработки:  
1) задачи в работе (ожидающие исполнителя или в работе у программиста или наблюдателя): лейбл active  
2) задачи сделанные программистами, но не взяты в тестирование: лейбл test  
3) задачи взяты в тестирование, но не протестированые: лейбл testing  
4) задачи с ошибкам находящимися в работе у программиста: лейбл bug   
5) задачи готовые к выкладке версии: лейбл version  
* ![24_0](https://github.com/e-government-ua/i/blob/test/doc/bp/img/24_0.jpg)

***
добавить БП в интерсептор, чтоб счетчик заявок срабатывал
src/main/java/org/igov/service/controller/interceptor/RequestProcessingInterceptor.java
```java
   private static final String DNEPR_MVK_291_COMMON_BP = "dnepr_mvk_291_common|_test_UKR_DOC|dnepr_mvk_889|justice_incoming";
```
коммит https://github.com/e-government-ua/i/commit/844a76af959178c327688de752a146fcb0b00646

используется зарезервированная переменная sID_Order_GovPublic
```xml
<activiti:formProperty id="sID_Order_GovPublic" name="Номер звернення ДМР" type="invisible"></activiti:formProperty>
```
счетчик начинает считать с 0 и обнуляется в начале года.
***
Если нужно убрать автоматическое "письмо Привет" нужно добавить id БП в интерсептор
src/main/java/org/igov/service/controller/interceptor/RequestProcessingInterceptor.java
```java
private static final String asID_BP_SkipSendMail = "dnepr_mvk_291_common|rada_0676_citizensAppeals";
```
id БП разделять вертикальным слеш "|"
***
##  Передать значение из одного поля в другое
В активити есть стандартная функция для этого  
![2](https://drive.google.com/uc?export=download&id=0B6mOkUg9oq1zRU5fdTFiNkYyZjg)  
Передать в переменную **sID_Group_Activiti** значение, которое было в поле **sName_SubjectRole**  

***
##  Ссылки на региональные порталы
https://mu-dp.test.region.igov.org.ua/ - мин.юст

