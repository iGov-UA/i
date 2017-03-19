# Подготовка среды

1. Проверяем, что локально установлена версия Firefox 46.0 (если стоит более старшая, то просто поверх нее инсталлируем необходимую версию по [ссылке](https://ftp.mozilla.org/pub/firefox/releases/46.0/win64/ru/).
2. Отключаем автоматическое обновление в Firefox  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sNmM4ZW94R05NOE0)
3. Подключаем в Eclipse / NetBeans 
Репозиторий кода автотестов BPMN - Github: **https://github.com/e-government-ua/iTest.git**  
Ветка единственная - Master  
_Для тестирования используется сервер https://beta.test.igov.org.ua/ Соответственно тесты проверяют структуру бизнес-процессов, которые развернуты на БЕТЕ (в ветке Бета основного хранилища)._
4. Автотесты пишутся для каждого БП отдельно. Создается (копируется) файл, имя которого совпадает с именем BPMN для которого будем создавать (описывать) тесты (конкретного БП) и с расширением java (например, dnepr_cnap_39.java). В файле может быть описано несколько сценариев (в зависимости от выбранного пункта select\enum). 

**ВНИМАНИЕ!** Если в имени BPMN использовался символ “-” (минус), то его необходимо заменить на “_” (подчеркивание). Техническое ограничение, связанное с тем, что имена классов в JAVA не могут содержать символ “-” (минус). 
Например:  
dnepr_cnap_39.bpmn -> dnepr_cnap_39.java  
nikopol_mvd-1.bpmn -> nikopol_mvd_1.java  

[Путь к автотестам](https://github.com/e-government-ua/iTest/tree/master/src/test/java/autoTests/TestSiute)  
Локальный  
iTest/src/test/java/autoTests/TestSiute  

***

# Добавление файла с автотестом
Алгоритм работы с файлом `<Name BPMN>`.java
## 1. Изменяем имя класса и имя конструктора (функции вызова автотеста)
#### Изменяем строку public class 
```java
public class Test_Example_Fill_Field  extends CustomMethods {
```
#### меняем имя класса на имя нашего файла (с учетом замененных знаков “-”).  
Например:
```java
public class dnepr_cnap_39 extends CustomMethods {
```
####Изменяем строку public void 
```java
public void Test_Example_Fill_Field() throws Exception {
```
Например
```java
public void default_test() throws Exception {
```
####Заполняем блок данными
```java
TemplatePage o = new TemplatePage(driver);
//  Вносим в переменные название услуги начиная с точки ._test_fields_bankid_--_ и до начала названия поля
String sBP = "dnepr_cnap_39";
String email = "autotestbeta@gmail.com";

_step("1. Вход по прямому URL на услугу");
openURLservice(driver, CV.baseUrl + "/service/139/general");

_step("2. Проверить, что открылась нужная услуга");
assertThis(driver, o.usluga, "Копії рішень міської (селищної) ради про надання дозволу на розроблення проекту відведення земельної ділянки");

_step("3. Выбор области/города");
o.selectRegion("Київська");
o.selectCity("Ірпінь");

_step("4. Авторизация Off AuthMock/BankID");
o.mokAuthorization();

_step("5. Заполняем форму услуги");
setFieldAutocomplete(driver,"sID_Public_SubjectOrganJoin","ЦНАП м. Ірпінь");
setFieldValue(driver, sBP, "phone", "+380623155533");
setFieldValue(driver, sBP, "email", email);
setFieldValue(driver, sBP, "sObjName", "номер, дату та назву рішення ради");
setFieldValue(driver, sBP, "sObjAdress", "Місцезнаходження (адреса) об’єкта");
setFieldValue(driver, sBP, "sDavName", "повне найменування юридичної особи");
setFieldValue(driver, sBP, "kved", "11.11");
setFieldValue(driver, sBP, "edrpou_inn", "12345678");
setFieldValue(driver, sBP, "sRukov", "П.І.Б. керівника юридичної особи");
setFieldValue(driver, sBP, "sOrgAdress", "Місцезнаходження юридичної особи");
setFieldValue(driver, sBP, "sMailClerk", email);

setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","0", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","0", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","0", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","0", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","0", "2017/03/05");

addTableRow(driver, sBP, "sTable1");
 
setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","1", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","1", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","1", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","1", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","1", "2017/03/05");

_step("6. Отправка формы");
click(driver, o.buttonSendingForm);

_step("7. Проверка сообщения о успешной отправке");
o.checkMessageSuccess("Шановний(-а) MockUser MockUser!\n" +
"Ваше звернення х-хххххххх успішно зареєстровано\n" +
"(номер також відправлено Вам електронною поштою на Ваш e-mail "+email+") Результати будуть спрямовані також на email.\n" +
"Звертаємо увагу, що Іноді листи потрапляють у спам або у розділ \"Реклама\" (для Gmail).");

_step("8. Нажать кнопку Выйти");
click(driver, o.buttonLogOut);
    }
```
## 2. Изменяем имя тестируемого BPMN
Имя БП задаем в формате  
String sn = "<Имя bpmn-ника>";  
Например, 
```xml
String sBP = "dnepr_cnap_39";
```
## 3. Указываем URL для тестируемого BPMN
```java
addStepToTheReport("1. Вход по прямому URL на услугу");
openURLservice(driver, CV.baseUrl + "/service/720/general");
```
Например,
```java
addStepToTheReport("1. Вход по прямому URL на услугу");
openURLservice(driver, CV.baseUrl + "/service/139/general");
```
## 4. Указываем name тестируемого BPMN (название услуги)
```java
addStepToTheReport("2. Проверить, что открылась нужная услуга");
assertThis(driver, tp.usluga, "_test_fields_bankid");
```
Например,
```java
 addStepToTheReport("2. Проверить, что открылась нужная услуга");
assertThis(driver, tp.usluga, "Копії рішень міської (селищної) ради про надання дозволу на розроблення проекту відведення земельної ділянки");
```
## 5. Указываем для тестирования область / город. 
Обратите внимание, что указывать необходимо текстовые значения области и города.  
Например,
```java
        addStepToTheReport("3. Выбор области/города");
        tp.selectRegion("Київська");
        tp.selectCity("Ірпінь");
```
## 6. Проверяем\уточняем авторизацию для тестирования услуги (на текущий момент - Mock-аторизация )
```java
addStepToTheReport("4. Авторизация Off AuthMock/BankID");
tp.mokAuthorization();
```
## 7. Заполняем обязательные поля на старт-таске БП (исключая заполненные поля Mock-аторизацией).  
Обратите внимание, что указывать необходимо корректные с точки зрения возможной валидации значения.  
Для заявителя указывайте служебные реквизиты почты (чтобы не “мусорить” атотестами свою почту)  
autotestbeta@gmail.com     
пароль:  igov2016  

Выбор sID_Public_SubjectOrganJoin из выпадающего списка
```java
setFieldAutocomplete(driver,"sID_Public_SubjectOrganJoin","Text_Value_For_Choice");
```
Текстовые поля заполняются по формату
```java
setFieldValue(driver, sn, "id_Field", "Value_Field");
```
Загрузка файлов по формату
```java
setFieldFile(driver, sBP, "id_Field", "src/test/resources/files/test.jpg");
```
Выбор из выпадающего списка
```java
setFieldSelectByText(driver,sBP,"id_Field","Text_Value_For_Choice");
```
Даты заполняются по формату
```java
setFieldCalendar(driver,sBP,"id_Field_Date","2016/12/25");
```
Электронная очередь выбирается по формату
```java
setFieldSelectSlotDate(driver, sBP, "."+sBP+"_--_"+"visitDay"); 
setFieldSelectSlotTime(driver, sBP, "."+sBP+"_--_"+"visitDay");
```
CheckBox с именем bID_CheckBox выбирается по формату
```java
setFieldCheckBox(driver, sBP, "."+sBP+"_--_"+"bID_CheckBox");
```
Обработка таблиц с разными типами данных. Параметрами передаются имя_таблицы, Имя_Атрибута (столбца), номер_строки, вводимое_значение  
```java
setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","0", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","0", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","0", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","0", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","0", "2017/03/05");

addTableRow(driver, sBP, "sTable1");
 
setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","1", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","1", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","1", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","1", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","1", "2017/03/05");
```
Например
```java
setFieldAutocomplete(driver,"sID_Public_SubjectOrganJoin","ЦНАП м. Ірпінь");
setFieldValue(driver, sBP, "sObjName", "номер, дату та назву рішення ради");
setFieldFile(driver, sBP, "sFile1", "src/test/resources/files/test.jpg");
setFieldSelectByText(driver,sBP,"client","нет");
setFieldCalendar(driver, sBP, "dDate_Beg", "2003/01/01");
setFieldSelectSlotDate(driver, sBP, "."+sBP+"_--_"+"visitDay"); 
setFieldSelectSlotTime(driver, sBP, "."+sBP+"_--_"+"visitDay");
setFieldCheckBox(driver, sBP, "."+sBP+"_--_"+"#bID_CheckBox");

setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","0", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","0", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","0", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","0", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","0", "2017/03/05");

addTableRow(driver, sBP, "sTable1");
 
setTableCellsInputTypeString(driver, sBP, "sTable1","sTables1FieldA","1", "Найменування товару 1");
setTableCellsInputTypeEnum(driver, sBP, "sTable1","sTables1FieldC","1", "кілограм|кг");
setTableCellsInputTypeSelect(driver, sBP, "sTable1","sObjectCustoms","1", "-0-0");
setTableCellsInputTypeFile(driver, sBP, "sTable1", "sTables2FieldB","1", "src/test/resources/files/test.jpg");
setTableCellsTypeCalendar(driver, sBP, "sTable1", "sTables2FieldC","1", "2017/03/05");
```
## 8. Если необходимо осуществить тестирование несколько разных сценариев для БП, то необходимо скопировать и соответствующим образом заполнить блок операторов  
```java
addStepToTheReport("1. Вход по прямому URL на услугу");
openURLservice(driver, CV.baseUrl + "/service/720/general");

addStepToTheReport("8. Нажать кнопку Выйти");
click(driver, tp.buttonLogOut);
```
или  создать новые методы, имена которых несут смысловую нагрузку для тестов
```java
    public void for_validators() throws Exception {
    public void for_selectors() throws Exception {
    public void for_sign() throws Exception {
    public void test2() throws Exception {
```
## 9. Если необходимо отключить автотест для БП
то необходимо указать enabled = false в строке 
```java
@Test(enabled = true, groups = {"Main", "Критический функционал"}, priority = 1)
```
и переместить файл в каталог SuspendTests 

***

# Локальное тестирование
создайте профиль и запустите его на выполнение
 
Результаты локального тестирования (вплоть до скриншотов iTest\TestReport\html\Screens) находятся локально по пути выгруженного проекта \iTest\TestReport

**ВНИМАНИЕ!**
_Не посылайте служебные письма о наличии заявок на реальную служебную почту! Рекомендуется либо захардкодить email разработчика на БЕТЕ либо сделать поле видимым и его изменить как стандартную переменную на email  разработчика БП_
