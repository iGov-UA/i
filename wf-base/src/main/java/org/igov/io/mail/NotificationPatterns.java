package org.igov.io.mail;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.commons.mail.EmailException;
import org.igov.io.GeneralConfig;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.igov.service.business.action.task.core.ActionTaskService.createTable_TaskProperties_Notification;

/**
 * User: goodg_000
 * Date: 25.08.2015
 * Time: 22:54
 */
public class NotificationPatterns {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationPatterns.class);

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ActionTaskService oActionTaskService;

    public void sendTaskCreatedInfoEmail(String sMailTo, String sID_Order, String bankIdFirstName, String bankIdLastName) throws EmailException {

      /*
      String sHead = String.format("Ви подали заяву №%s на послугу через портал %s", nID_Protected,
              generalConfig.getSelfHostCentral());

      String sBody = String.format("Ви подали заяву №%s на послугу через портал %s", nID_Protected,
              generalConfig.getSelfHostCentral()) +
              "<br>(Ви завжди можете подивитись її статус на порталі у разділі \"Мій журнал\")" +
              "<br>" +
              "При надходжені Вашої заявки у систему госоргану - Вам буде додатково направлено персональний лист - повідомленя.<br>";
      */
        try {
            String sHead;
            if(bankIdFirstName == null || bankIdFirstName.equalsIgnoreCase("null")) {
                sHead = String.format("Вітаємо, Ваша заявка %s прийнята!", sID_Order);
            } else {
                bankIdFirstName = makeStringAsName(bankIdFirstName);
                sHead = String.format("Вітаємо, %s, Ваша заявка %s прийнята!", bankIdFirstName, sID_Order);
            }

            String sBody = sHead +
                    "<br>Ви завжди зможете переглянути її поточний статус у розділі <a href=\"" + generalConfig
                    .getSelfHostCentral() + "/order/search?sID_Order=" + sID_Order
                    + "\">\"Мій журнал\"</a>. Також на кожному етапі Ви будете отримувати email-повідомлення."
                    +"<br>У разі необхідності Ви можете спрямувати питання та пропозиції скориставшись формою за <a href=\"https://docs.google.com/forms/u/0/d/1d2ZttyUR9DBqSInCXsx_llvg8xp8ecjTgHVJnTtb2HM/viewform?edit_requested=true\" target=\"_blank\">посиланням</a>"
                    ;
            
            /*
            Long nID_Task = oActionTaskService.getTaskIDbyProcess(null, sID_Order, Boolean.FALSE);
            Map<String, Object> mContact = oActionTaskService.getStartFormData(nID_Task);

            String sFirstName = (String) mContact.get("bankIdFirstName");
            LOG.info("sFirstName = {}", sFirstName);
            String sMiddleName = (String) mContact.get("bankIdmiddleName");
            LOG.info("sMiddleName = {}", sMiddleName);
            String sRecipient = sFirstName + " " + sMiddleName;
            String sEmail = (String) mContact.get("email");
            LOG.info("sEmail = {}", sEmail);

            String sHead = String.format("Ваша заявка %s прийнята!", sID_Order);

            // issue #1151
            //String sBody = String.format("Ваша заявка %s прийнята!", sID_Order) +
            //        "<br>Ви завжди зможете переглянути її поточний статус у розділі <a href=\""+generalConfig.getSelfHostCentral() + "/order/search?sID_Order=" + sID_Order+"\">\"Мій журнал\"</a>. Також на кожному етапі Ви будете отримувати email-повідомлення.	";
            

            String sBody = String.format("Шановний(-а) %s!", sRecipient)
                    + "<br>Ваше звернення <a href=\""+generalConfig.getSelfHostCentral() + "/order/search?sID_Order=" + sID_Order+"\">" + sID_Order + "</a> успішно зареєстровано (номер також відправлено Вам електронною поштою на " + sEmail + "). Результати будуть спрямовані також на email."
                    + "<br>Звертаємо увагу, що іноді листи потрапляють у спам або у розділ \"Реклама\" (для Gmail).";
            */

            Mail oMail = context.getBean(Mail.class);

            oMail._To(sMailTo)._Head(sHead)._Body(sBody)._ToName(makeStringAsName(bankIdFirstName),
                                                                 makeStringAsName(bankIdLastName));

            oMail.send();
            LOG.info("Send email with sID_Order={} to the sMailTo={}", sID_Order, sMailTo);
        } catch (Exception oException) {
            LOG.warn("Refused: {} (sMailTo={},sID_Order={})", oException.getMessage(), sMailTo, sID_Order);
            LOG.error("FAIL:", oException);
        }
    }



    private String makeStringAsName(String name) {
        name.toLowerCase();
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return String.valueOf(chars);
    }

    public void sendVerifyEmail(String sMailTo, String sToken) throws EmailException {

        try {
            //String sMailTo = sQuestion;
            String sHead = "Верификация адреса";
            String sBody = "Код подтверждения: " + sToken;
            Mail oMail = context.getBean(Mail.class);
            oMail._To(sMailTo)
                    ._Head(sHead)
                    ._Body(sBody);
            oMail.send();
            LOG.info("Send email with sToken={} to the sMailTo={}", sToken, sMailTo);
        } catch (Exception oException) {
            LOG.warn("FAIL: {} (sMailTo={},sToken={})", oException.getMessage(), sMailTo, sToken);
            throw oException;
        }

    }

    //oNotificationPatterns.sendTaskEmployeeQuestionEmail(nID_Process, saField, sBody, sToken);

    //nID_Process, saField, sBody, sToken
    //String sHead, String sBody, String sMailTo
    public void sendTaskEmployeeQuestionEmail(String sHead, String sBody, String sMailTo, String sToken,
        Long nID_Process, String saField, String soParams)
        throws EmailException {
        try {
            sHead = sHead == null ? "Просимо уточнити дані по Вашій заявці на iGov" : sHead;
            String sClientFIO = null;
            String sEmployerFIO = null;
            try {
                if (soParams != null && !"".equals(soParams.trim())) {
                    JSONObject oParams = new JSONObject(soParams);
                    if (oParams.has("sEmployerFIO")) {
                        sEmployerFIO = oParams.getString("sEmployerFIO");
                    }
                    if (oParams.has("sClientFIO")) {
                        sClientFIO = oParams.getString("sClientFIO");
                    }
                }
            } catch (Exception oException) {
                LOG.warn("FAIL: {} (soParams={})", oException.getMessage(), soParams);
            }

            if (sClientFIO == null) {
                sClientFIO = "громадянин";
            }
            String sURL = (new StringBuilder(generalConfig.getSelfHostCentral()).append("/order/search?sID_Order=")
                    .append(generalConfig.getOrderId_ByProcess(nID_Process))
                    .append("&sToken=").append(sToken)).toString();

            String sText = "<b>Шановний (-а) " + sClientFIO + "!</b><br><br>"
                    + "Для успішної обробки Вашого звернення, державний службовець "
                    + (sEmployerFIO != null ? "(" + sEmployerFIO + ")" : "") + " потребує уточнення деяких даних.";

            StringBuilder osBody = new StringBuilder(sText);
            osBody.append("<br>").append("<br>").append(createTable_TaskProperties_Notification(saField, false));
            osBody.append("<br>").append("Примітка:").append("<br>");
            osBody.append(sBody).append("<br>");
            osBody.append("<br/>").append("Щоб уточнити дані, Вам потрібно <a href=\"" + sURL
                    + "\"> перейти в розділ \"Мій журнал\" порталу iGov</a> "
                    + "і внести інформацію у відповідні поля.").append("<br/>");
            osBody.append("<br/>").append("Дякуємо за порозуміння").append("<br/>");
            //osBody.append("Для уточнення - перейдіть по цьому посіланню: ").append(sURL).append("<br/>");

            String sMailBody = osBody.toString();
            Mail oMail = context.getBean(Mail.class);
            oMail._To(sMailTo)._Head(sHead)._Body(sMailBody)._ToName(sClientFIO);
            oMail.send();
        } catch (Exception oException) {
            LOG.warn("FAIL: {} (sMailTo={},sToken={},nID_Process={},saField={})", oException.getMessage(), sMailTo,
                    sToken, nID_Process, saField);
            throw oException;
        }
    }

    public void sendTaskEmployeeMessageEmail(String sHead, String sBody, String sMailTo, String sID_Order, String soParams)
            throws EmailException {
        try {
            sHead = sHead == null ? "Просимо ознайомитись із коментарем держслужбовця, по Вашій заявці на iGov" : sHead;
            String sClientFIO = null;
            String sEmployerFIO = null;
            try {
                if (soParams != null && !"".equals(soParams.trim())) {
                    JSONObject oParams = new JSONObject(soParams);
                    if (oParams.has("sEmployerFIO")) {
                        sEmployerFIO = oParams.getString("sEmployerFIO");
                    }
                    if (oParams.has("sClientFIO")) {
                        sClientFIO = oParams.getString("sClientFIO");
                    }
                }
            } catch (Exception oException) {
                LOG.warn("FAIL: {} (soParams={})", oException.getMessage(), soParams);
            }

            if (sClientFIO == null) {
                sClientFIO = "громадянин";
            }
            
            String sURL = (new StringBuilder(generalConfig.getSelfHostCentral()).append("/order/search?sID_Order=")
                    .append(sID_Order)
                    //.append("&sToken=").append(sToken)
                    ).toString();

            String sText = "<b>Шановний (-а) " + sClientFIO + "!</b><br><br>"
                    + "У процесі обробки Вашого звернення, державний службовець "
                    + (sEmployerFIO != null ? "(" + sEmployerFIO + ")" : "")
                    + " надіслав Вам повідомлення:";

            StringBuilder osBody = new StringBuilder(sText);
            osBody.append("<br>");
            osBody.append(sBody).append("<br>");
            osBody.append("<br/>").append("Щоб відповісти, Вам потрібно <a href=\"" + sURL
                    + "\"> перейти в розділ \"Мій журнал\" порталу iGov</a> "
                    //+ "і внести інформацію у відповідні поля."
                ).append("<br/>");
            osBody.append("<br/>").append("Дякуємо за увагу").append("<br/>");
            //osBody.append("Для уточнення - перейдіть по цьому посіланню: ").append(sURL).append("<br/>");

            String sMailBody = osBody.toString();
            Mail oMail = context.getBean(Mail.class);
            oMail._To(sMailTo)._Head(sHead)._Body(sMailBody)._ToName(sClientFIO);
            oMail.send();
        } catch (Exception oException) {
            LOG.warn("FAIL: {} (sMailTo={},sID_Order={})", oException.getMessage(), sMailTo,
                    sID_Order);
            throw oException;
        }
    }    
    
    /*
    public void sendEmail(String sHead, String sBody, String recipient) throws EmailException {
        oMail.reset();
        oMail._To(recipient)._Head(sHead)._Body(sBody);
        oMail.send();
    }
    public String createEmailBody(Long nID_Process, String soData, String sBody, String sToken) throws UnsupportedEncodingException {
        StringBuilder emailBody = new StringBuilder(sBody);
        emailBody.append("<br/>").append(createTable_TaskProperties(soData)).append("<br/>");
        String link = (new StringBuilder(generalConfig.getSelfHostCentral()).append("/order/search?sID_Order=")
                .append(generalConfig.getOrderId_ByProcess(nID_Process))
                .append("&sToken=").append(sToken)).toString();
        emailBody.append(link).append("<br/>");
        return emailBody.toString();
    }*/

}
