## Устанавливаем JAVA JDK
1. Переходим по [ссылке](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)  
2. Соглашаемся и качаем  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sQlE3TUh3ZmpiUGM)  
3. Устанавливаем скачанный файл, без изменений, со всем соглашаемся, можно выбрать свою директорию.  
4. Прописываем путь к JAVA (**нужно для tomcat для Eclipse это не важно**)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a) Заходим в свойства “Мой компьютер”   
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sXzZCNXBRQkhsOFE)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) б) Выбираем дополнительные параметры системы  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZFRELThmbjFNZHc)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) в) добавляем переменную **JAVA_HOME**  
и путь к установленной JAVA и **обязательно** JDK  
```
C:\Program Files\Java\jdk1.8.0_111  
```
если скачивалась х86 то и соответственно и путь будет 
```  
C:\Program Files (x86)\Java\jdk1.8.0_111  
```
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sVWNQa3N1NThKTm8)  
## Устанавливаем Eclipse
[1. Качаем последнюю версию](
http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/neon/2/eclipse-committers-neon-2-win32-x86_64.zip)  
2. копируем скачанный архив в нужную папку где он будет храниться  
3. распаковываем архив  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sX0xVUGNRV01SdkE)  
4. запускаем eclipse.exe  
5. При первом запуске он спросит где хранить настройки Eclipce  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sNUJYaThZZnpLaTg)  
для лучшей переносимости Eclipce на другой компьютер лучше всего создать в папке где расположен Eclipce новую папку workspace и туда указать хранить настройки, а также поставить галочку что эта папка по умолчанию будет использоваться и для проекта gitа  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sS01ZSkVrczhxWlk)  
6. Закрываем страничку Welcom  
7. Ставим activity  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a) help -> install new software…  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sUHNoZG1DU2d2Yzg)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) b) в появившемся окне вставляем адрес 
```html
http://activiti.org/designer/update
```  
и нажимаем add  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sU1R2R2JSb0pVeHc)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) c) в следующем блоке IDE уточнит как назвать это приложение, название не важно, можно к примеру использовать activiti  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZFplX3ZvR0p5TDA)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) d) далее еклипс по указанному адресу ищет приложение, когда найдет появится возможность выбрать и нажать next  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sU3ptc2pDY1hvc2M)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) e) в процессе установки IDE уточнит хотим ли мы установить неподписанный софт, мы соглашаемся  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sV3ZVcFpwNzdZLTA)  
после установки приложения IDE попросит перезагрузить себя, соглашаемся  
8. По такому же алгоритму как и активити ставим мейвен, только вставляем адрес  
```html
http://download.eclipse.org/technology/m2e/releases 
```
ну и соответственно называем по другому  
9. Затягиваем репозиторий  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a) Кликаем во вкладке Package Explorer правой кнопкой мыши и выбираем импорт  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sTmlESEJBY3ZrdVE)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) b) И движемся по следующей цепочке  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZG55QXRjUS0wMWc)  
![2](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sOXQxSzUwZTM2N2s)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) c) Вставляем ссылку на нужный Гит  https://github.com/e-government-ua/i.git  
и заполняем поля user/password  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZTRSMWZ1N05mZEk)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) d) Убираем галочки со всех веток и выбираем нужную  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_scGR0ZVJyQW4wSmM)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) e)проверяем в ту ли папку будет копироваться репозиторий  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sV1RYZVg5SDdFNVU)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) f) ждем пока докачается репозиторий  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) g) выбираем Import as general project  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sWC15dTZGTUV3Ym8)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) h)делаем пулл  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sRlJONXdwNm1tOEk)  
10. Настраиваем Еклипс  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a) заходим в настройки  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sYkFmX1BzalBOM0U)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) b) настраиваем кодировку создаваемых страниц, выбираем UTF-8  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZW1TVTlJVDRlRTg)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) c)устанавливаем привычное для себя окно коммитов(галочку снимаем)  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sWGRadkctcElkUUU)  
11. Настраиваем сборку  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) a)по проекту правой кнопкой мыши и выбираем конфигурации сервера  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sVWxNQXNHdlNtZ2c)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) b)создаем конфигурацию мейвена  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sVFVzSGVvNUI5Qkk)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) c)выбираем директорию для мейвена  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sSGozRzJGUjRSWmM)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) d)называем проект, вписываем в цель clean install и выбираем больше ядер, чтоб проект быстрей собирался нажимаем apply  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sY1AwUHRBMS1nRjA)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) e) настраиваем Java - Машину  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sb1VHZHdIR3Q0MTQ)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) f) добавляем JDK. Не JRE а именно **JDK**!!!   
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sVjZlam9KaWFhX1U)  
![2](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sbXFVMkxwVDl4eUE)  
![3](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sQVprNy1HLVJ5MlU)  
![#f03c15](https://placehold.it/10/f03c15/000000?text=+) g)и обязательно выбираем ее и ставим по умолчанию  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sNmZWMXF0ZV9YdTQ)  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_say1QMElyNTlBdGM)  
* Руками прописываем кодировку cp1251 на вкладке Общие (Common)
* ![ecl1](https://github.com/e-government-ua/i/blob/test/doc/bp/img/ecl1.JPG)

## Ставим дополнительные утилиты для удобства редактирования JSON и HTML
1. переходим в маркет  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_scWg3N3RiSU56MWM)  
2. ищем нужные нам приложения и устанавливаем, процесс установки такой же как и в пункте 7, только не надо вставлять адрес УРЛ на приложение  и называть его. Теоретически можно было так и активити с майвеном поставить.  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_saWRQWTdOVGdTRW8)  
![2](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sRWx6dEs0aDNJY0E)  
3. приложения JSON дает возможность открыть окно для редактирования или валидации JSON  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sekgtZzdHWWo4b00)  
а также можно настроить как будет форматироваться JSON  если нажать комбинацию ctrl + shift + F  
![1](https://drive.google.com/uc?export=download&id=0B42BBpUHJK_sZzdCMmg0Rm13djQ)  
