package org.igov.io.mail;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.igov.io.GeneralConfig;

/**
 * User: goodg_000
 * Date: 25.08.2015
 * Time: 22:54
 */
public class NotificationService {

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    Mail mail;

    public void sendTaskCreatedInfoEmail(String receiverEmail, String sID_Order) throws EmailException {

      /*
      String sHead = String.format("Ви подали заяву №%s на послугу через портал %s", nID_Protected,
              generalConfig.sHostCentral());

      String sBody = String.format("Ви подали заяву №%s на послугу через портал %s", nID_Protected,
              generalConfig.sHostCentral()) +
              "<br>(Ви завжди можете подивитись її статус на порталі у разділі \"Статуси\")" +
              "<br>" +
              "При надходжені Вашої заявки у систему госоргану - Вам буде додатково направлено персональний лист - повідомленя.<br>";
      */

        String sHead = String.format("Ваша заявка %s прийнята!", sID_Order);

        String sBody = String.format("Ваша заявка %s прийнята!", sID_Order) +
                "<br>Ви завжди зможете переглянути її поточний статус у розділі <a href=\""+generalConfig.sHostCentral() + "/order/search?sID_Order=" + sID_Order+"\">\"Статуси\"</a>. Також на кожному етапі Ви будете отримувати email-повідомлення.	";

        mail.reset();

        mail._To(receiverEmail)._Head(sHead)._Body(sBody);

        mail.send();
    }
}
