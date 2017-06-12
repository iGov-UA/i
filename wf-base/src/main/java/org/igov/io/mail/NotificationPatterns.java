package org.igov.io.mail;

import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.commons.mail.EmailException;
import org.igov.io.GeneralConfig;
import org.igov.io.fs.FileSystemData;
import static org.igov.service.business.action.task.core.ActionTaskService.amFieldMessageQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.igov.service.business.action.task.core.ActionTaskService.createTable_TaskProperties;

/**
 * User: goodg_000 Date: 25.08.2015 Time: 22:54
 */
public class NotificationPatterns {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationPatterns.class);

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private ApplicationContext context;

    /*public void sendTaskCreatedInfoEmail(String sMailTo, String sID_Order, String bankIdFirstName, String bankIdLastName) throws EmailException {

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

            Mail oMail = context.getBean(Mail.class);

            oMail._To(sMailTo)._Head(sHead)._Body(sBody)._ToName(makeStringAsName(bankIdFirstName),
                                                                 makeStringAsName(bankIdLastName));

            oMail.send();
            LOG.info("Send email with sID_Order={} to the sMailTo={}", sID_Order, sMailTo);
        } catch (Exception oException) {
            LOG.warn("Refused: {} (sMailTo={},sID_Order={})", oException.getMessage(), sMailTo, sID_Order);
            LOG.error("FAIL:", oException);
        }
    }*/
    public void sendTaskCreatedInfoEmail(String sMailTo, String sID_Order, String bankIdFirstName, String bankIdLastName) throws EmailException {

        try {
            String sHead;
            if (bankIdFirstName == null || bankIdFirstName.equalsIgnoreCase("null")) {
                sHead = String.format("Вітаємо, Ваша заявка %s прийнята!", sID_Order);
            } else {
                bankIdFirstName = makeStringAsName(bankIdFirstName);
                sHead = String.format("Вітаємо, %s, Ваша заявка %s прийнята!", bankIdFirstName, sID_Order);
            }

            //"patterns/mail/auto_client_notify.html"
            File oFile = FileSystemData.getFile(FileSystemData.SUB_PATH_PATTERN_EMAIL, "auto_client_notify.html");
            String sBody = Files.toString(oFile, Charset.defaultCharset());
            
            LOG.info("sBody testing: " + sBody);
            
            if (sID_Order != null) {
                sBody = sBody.replaceAll("\\[sID_Order\\]", sID_Order);
                LOG.info("!!!after sID_Order replace sBody: " + sBody);
            }
            if (bankIdFirstName != null) {
                sBody = sBody.replaceAll("\\[sClientName\\]", bankIdFirstName);
                LOG.info("!!!after bankIdFirstName replace sBody: " + sBody);
            }
            if (bankIdLastName != null) {
                sBody = sBody.replaceAll("\\[sClientSurname\\]", bankIdLastName);
                LOG.info("!!!after bankIdLastName replace sBody: " + sBody);
            }

            Mail oMail = context.getBean(Mail.class);
            oMail._To(sMailTo)._Head(sHead)._Body(sBody)
                    ._ToName(makeStringAsName(bankIdFirstName), makeStringAsName(bankIdLastName));
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
            String sHead = "Верифікація адреси";
            String sBody = "Код підтвердження: " + sToken;
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

    public void sendTaskEmployeeQuestionEmail(String sHead, String sCommentary, String sMailTo, String sToken,
            Long nID_Process, String saField, String soParams)
            throws EmailException, Exception {
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
            List<Map<String, String>> amReturn = amFieldMessageQuestion(saField, false);
            osBody.append("<br>").append("<br>").append(createTable_TaskProperties(amReturn, false, true));//saField
            osBody.append("<br>").append("Примітка:").append("<br>");
            osBody.append(sCommentary).append("<br>");
            osBody.append("<br/>").append("Щоб уточнити дані, Вам потрібно <a href=\"" + sURL
                    + "\"> перейти в розділ \"Мій журнал\" порталу iGov</a> "
                    + "і внести інформацію у відповідні поля.").append("<br/>");
            osBody.append("<br/>").append("Дякуємо за порозуміння").append("<br/>");
            //osBody.append("Для уточнення - перейдіть по цьому посіланню: ").append(sURL).append("<br/>");

            String sBody = osBody.toString();
            Mail oMail = context.getBean(Mail.class);
            oMail._To(sMailTo)._Head(sHead)._Body(sBody)._ToName(sClientFIO);
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
            sHead = ((sHead == null || "".equals(sHead.trim())) ? 
                    "Просимо ознайомитись із коментарем держслужбовця, по Вашій заявці на iGov" : sHead);
            LOG.info("sendTaskEmployeeMessageEmail: sHead = " + sHead);
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
                    .append(sID_Order) //.append("&sToken=").append(sToken)
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

}
