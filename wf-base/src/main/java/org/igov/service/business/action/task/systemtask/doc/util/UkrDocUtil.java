package org.igov.service.business.action.task.systemtask.doc.util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.AttachmentEntity;
import org.activiti.engine.task.Attachment;
import org.igov.io.GeneralConfig;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UkrDocUtil {

    private static final String DOWNLOAD_FILE_FROM_PATTERN = "%s/wf/service/object/file/download_file_from_storage_static?sId=%s&sFileName=%s&sType=%s";
    private final static Logger LOG = LoggerFactory.getLogger(UkrDocUtil.class);

    public static Map<String, Object> makeJsonRequestObject(String sHeadValue, String sBodyValue, String sLoginAuthorValue,
            String nID_PatternValue, List<Attachment> attachmentsIds, String taskId, GeneralConfig generalConfig,
            String sID_Order_GovPublicValue, String sSourceChannelValue, String shortFIO, String fullIO,
            String sDepartNameFull, String sSex, String sAddress,
            String sZipCode, String sPlace, String sDateAppealValue
            , Boolean bOrganization, String sCompanyName,String sID_EDRPOU, String sBossFirstLastName, String sDateDocIncoming, String sNumberDocIncoming
    ) {

        Map<String, Object> res = new LinkedHashMap<>();

        Map<String, Object> content = new LinkedHashMap<>();
        content.put("name", sHeadValue);
        content.put("text", sBodyValue);
        content.put("paragraphs", new JSONArray());

        res.put("content", content);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("Автор", sLoginAuthorValue);
        attributes.put("Регистрационный номер заявки", sID_Order_GovPublicValue);
        attributes.put("Название канала", sSourceChannelValue);
        attributes.put("Краткое ФИО заявителя", shortFIO);
        attributes.put("Имя и отчество заявителя", fullIO);
        attributes.put("Полное название управления/департамента", sDepartNameFull);
        //attributes.put("Пол заявителя", sSex);
        //attributes.put("Адрес проживания", sAddress);
        attributes.put("Индекс", sZipCode);
        attributes.put("Населенный пункт", sPlace);
        attributes.put("Остальной адрес", sAddress);
        attributes.put("Дата заявки", sDateAppealValue);
        attributes.put("sHost", generalConfig.getSelfHost());

        
        attributes.put("Юридическое лицо", bOrganization+"");
        attributes.put("Название юридического лица", sCompanyName);
        attributes.put("ОКПО", sID_EDRPOU);
        attributes.put("Имя, отчество должностного лица", sBossFirstLastName);
        attributes.put("Дата документа юридического лица", sDateDocIncoming);
        attributes.put("Номер документа юридического лица", sNumberDocIncoming);
        

        Map<String, Object> extensions = new HashMap<>();
        if (attachmentsIds != null && !attachmentsIds.isEmpty()) {
            Map<String, List<?>> tables = new HashMap<>();
            List<List<String>> attachmentsInfo = new LinkedList<>();
            for (Attachment attachInfo : attachmentsIds) {
                LOG.info("attachInfo getId: " + attachInfo.getId() + " getName: " + attachInfo.getName() + " getType: " + attachInfo.getType());
                if (attachInfo.getName() != null && !attachInfo.getName().contains("user form")) {
                    List<String> info = new LinkedList<>();
                    info.add(URLEncoder.encode(attachInfo.getName()));
                    info.add(String.format(DOWNLOAD_FILE_FROM_PATTERN, generalConfig.getSelfHost(), URLEncoder.encode(((AttachmentEntity) attachInfo).getContentId()), URLEncoder.encode(attachInfo.getName()), URLEncoder.encode(attachInfo.getType())));
                    attachmentsInfo.add(info);
                }
            }
            tables.put("Приложения", attachmentsInfo);
            extensions.put("tables", tables);
        }

        extensions.put("attributes", attributes);

        content.put("extensions", extensions);

        res.put("actors", new HashMap<>());

        Map<String, Object> template = new HashMap<>();
        template.put("template", Integer.valueOf(nID_PatternValue));

        res.put("details", template);

        return res;
    }

}
