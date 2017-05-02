# Установка ActivitiDesigner, для редактирования бизнес процессов    
**ОСНОВНАЯ ИНСТРУКЦИЯ РАЗВОРАЧИВАНИЯ ПРОЕКТА:**
https://docs.google.com/document/d/1N3vPXv76R2gjjwr03bQ1buzec2fJJg1Uh5Gzg3EPEwI/edit

## Що таке активіті? 
**Вебінар з демонстрацією створення найпростіших моделей (англ.) - 
https://www.youtube.com/watch?v=0PV_8Lew3vg - презентація активіті**



## Налаштування Eclipse для ОС Windows + встановлення плагіну activiti

**Тут знаходиться готовий уже цілісний попередньо встановлений пакет, який достатньо скопіювати та розпакувати на диск "D:\", щоб усі застосунки знаходилися в каталозі "D:\e-gov\":
[4 архіви, з попередньо налаштованим екліпсом, джавою та мавеном для 32х і 64х розрядного Windows](https://www.dropbox.com/sh/u1rcot9rzjk6gsv/AAAiBN75AfXYMzbZ0PjmW80ba?dl=0)**

**Як альтернатива, можете самі подивитися як поставити:**

* https://www.youtube.com/watch?v=Hc9gZp7HNT0 - приклад  встановлення, налаштування та запуску (англ.)

**Або поставити згідно з посібником:**

* Звантажте з https://eclipse.org/downloads/packages/release/juno/sr2 на локальний диск архів з Eclipse, в залежності від розрядності ОС.

* Розпакуйте архів.

*. Завантажте JDK1.7 з сайту http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html. Запустіть інсталяцію та вкажіть шлях для інсталяції, наприклад C:\java.

![](http://i.imgur.com/qQp6BfR.png)

В директорії встановлення буде 2 теки: jre7 та jdk1.7.0_upd, де upd - номер update версії.

* Завантажте Maven  https://drive.google.com/file/d/0BydenhvN5xzeY3hIMG8tUVRpR0E/view?usp=sharing  на локальний диск та розпакуйте архів.

* Завантажте apache-tomcat  https://drive.google.com/file/d/0BydenhvN5xzebzlEb0NyMVBuamc/view?usp=sharing на локальний диск та розпакуйте архів.

* Встановіть системну змінну JAVA_HOME і M2_HOME

![](http://i.imgur.com/fzPQ1oh.png)

* Запустіть eclipse.exe та оберіть робочу директорію під час запуску.  

* Зайдіть в додатку Window - Preferences - Git і виставіть в меню Projects та Synchronize пункти згідно зі скриншотами. Якщо немає пункту Git, то необхідно встановити плагін для Eclipse (https://www.banym.de/eclipse/install-git-plugin-for-eclipse).

![](http://i.imgur.com/w5ZQjY9.png)

![](http://i.imgur.com/jdC6TTW.png)

![](http://i.imgur.com/OTuZO08.png)

* Встановіть плагін Activity. Перейдіть в меню Eclipse Help - Install new software. В рядку Work with пропишіть http://activiti.org/designer/update. Після цього виберіть Activity BPMN Designer.

![](http://i.imgur.com/e2ijJrd.png)

 Якщо нічого не з'явиться, значить плагін вже встановлено. Щоб це перевірити, встановіть пункт Hide items that are already install.

![](http://i.imgur.com/uxB6DP2.png)

* Налаштуйте Java в розділі Installed JREs

![](http://i.imgur.com/IGfQPFS.png)

* Налаштуйте Maven в розділі Installations, вказавши шлях до директорії з Maven

![](http://i.imgur.com/mt1Ipn7.png)

* Встановіть кодування UTF-8

![](http://i.imgur.com/Bluye2f.png)

## Завантаження проекту з GitHub

* Перейдіть File - Import - Git

![](http://i.imgur.com/k0c1rYi.png)

Оберіть URI

![](http://i.imgur.com/KAYvKJK.png)

Введіть URI проекту з вашими обліковими даними від github.

![](http://i.imgur.com/8l2aDKh.png)

Оберіть потрібну гілку.

![](http://i.imgur.com/JBJEpll.png)

Вкажіть директорію, в котрій буде проект (якщо потрібно). 
Встановіть пункт Clone Submodules. 

![](http://i.imgur.com/Yt41U7L.png)

Коли перейдете у вікно Select a wizard to use for importing projects, натисніть кнопку Cancel.

![](http://i.imgur.com/r1w31de.png)

Перейдіть File - Import - Maven та оберіть Existing Maven Projects.

![](http://i.imgur.com/7YbzMnu.png)

Оберіть директорію з завантаженим проектом.

![](http://i.imgur.com/jX9Qx9d.png)

Коли проект додасться до workspace, перезберіть його за допомогою команди clean install.

![](http://i.imgur.com/Yjrkih7.png)

Після того, як зібрався war файл, його необхідно підкласти в директорію ..\tomcat\webapps.
Для запуску томката необхідно завести користувачів з доступом до керування розгортанням застосунків на сервері. Відредагуйте файл ..\tomcat\conf\tomcat-users.xml.

![](http://i.imgur.com/UAWP5kM.png)

Запустіть файл, ..\tomcat\bin\startup.bat, після чого перейдіть в бравзері за адресою http://localhost:8080 , де відкриється стартова сторінка tomcat.
