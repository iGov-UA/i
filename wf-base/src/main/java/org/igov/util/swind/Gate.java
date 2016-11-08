/**
 * Gate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public interface Gate extends javax.xml.rpc.Service {

/**
 * <h1>WEB-сервіс обміну документами та квитанціями з приймальним
 * шлюзом</h1>
 * <span>
 * <h3>Сценарій подання звітності</h3>
 * 1. Відправити документ на шлюз, використовуючи метод <b>Send</b><br
 * />
 * 2. Зачекати певний час (3-5 секунд)<br />
 * 3. Отримати список кодів повідомлень, використовуючи метод <b>GetMessages</b>
 * або <b>GetMessagesEx</b><br />
 * 4. Якщо список порожній, зачекати певний час (30-60 секунд) і перейти
 * до шагу 3<br />
 * 5. Отримати повідомлення, використовуючи метод <b>Receive</b><br />
 * 6. Вилучити отримане повідомлення, використовуючи метод <b>Delete</b><br
 * />
 * 7. Якщо повідомлення не останнє в списку, перейти до шагу 5 для наступного
 * повідомлення<br />
 * 8. Якщо отримано не всі очікувані квитанції, перейти до шагу 3
 * <h3>Примітка</h3>
 * Рекомендується періодично (1-2 рази на добу) виконувати перевірку
 * наявності повідомлень
 * для отримання документів, що відправлено за ініціативою шлюзу.
 * </span>
 * <span>
 * <h3>Коди повернення функцій</h3>
 * GATE_OK (0) - Успішно<br />
 * GATE_SEND_FAILED (1) - Помилка збереження вхідного повідомлення<br
 * />
 * GATE_EMPTY_FILENAME (2) - Не визначено ім'я файлу<br />
 * GATE_EMPTY_MESSAGE (3) - Блок документу не визначено<br />
 * GATE_FILENAME_TOOLONG (4) - Некоректне ім'я файлу<br />
 * GATE_FILENAME_INVALID (5) - Недопустимі символи в імені файлу<br />
 * GATE_PARSESIGN_FAILED (6) - Помилка перевірки підпису<br />
 * GATE_DB_INTERNAL (7) - Помилка роботи з базою повідомлень<br />
 * GATE_MSGID_INVALID (8) - Некоректний код повідомлення<br />
 * GATE_MSGID_ABSENT (9) - Відсутнє запитане повідомлення<br />
 * GATE_EMPTY_EMAIL (10) - Не визначено адресу електронної пошти<br />
 * GATE_TEMPORARY_UNAVAIL (11) - Сервіс тимчасово недоступний<br />
 * </span>
 */
    public java.lang.String getGateSoapAddress();

    public org.igov.util.swind.GateSoap getGateSoap() throws javax.xml.rpc.ServiceException;

    public org.igov.util.swind.GateSoap getGateSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
