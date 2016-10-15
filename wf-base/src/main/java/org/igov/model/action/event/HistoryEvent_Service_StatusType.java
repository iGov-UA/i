package org.igov.model.action.event;

/**
 * Created by grigoriy-romanenko on 28.12.2015.
 */
public enum HistoryEvent_Service_StatusType {

    CREATED(0L,
            "Created",
            "Заявка подана",
            "Заявка подана"),

    OPENED(1L,
            "Opened",
            "Заявка открыта",
            "Заявка вiдкрита"),

    OPENED_ASSIGNED(2L,
            "Opened_Assigned",
            "Заявка открыта и взята в работу",
            "Заявка вiдкрита i взята у роботу"),

    OPENED_REMARK_EMPLOYEE_QUESTION(3L,
            "Opened_RemarkEmployeeQuestion",
            "Заявка открыта и получила замечание работника",
            "Заявка вiдкрита i отримала зауваження вiд робiтника"),

    OPENED_REMARK_CLIENT_ANSWER(4L,
            "Opened_RemarkClientAnswer",
            "Заявка открыта и получила ответ на замечание от клиента",
            "Заявка вiдкрита i отримала вiдповiдь на зауваження вiд клiєнта"),

    OPENED_COMMENT_CLIENT(5L,
            "Opened_CommentClient",
            "Заявка открыта и прокомментирована клиентом",
            "Заявка вiдкрита i прокоментована клiєнтом"),

    OPENED_COMMENT_EMPLOYEE(6L,
            "Opened_CommentEmployee",
            "Заявка открыта и прокомментирована работником",
            "Заявка вiдкрита i прокоментована робітником"),

    OPENED_ESCALATION(7L,
            "Opened_Escalation",
            "Заявка открыта и запущена эскалация",
            "Заявка вiдкрита i почата ескалація"),

    CLOSED(8L,
            "Closed",
            "Заявка закрыта",
            "Заявка закрита"),

    CLOSED_REJECTED(9L,
            "Closed_Rejected",
            "Заявка закрыта и отклонена",
            "Заявка закрита і відхилена"),

    CLOSED_RATED(10L,
            "Closed_Rated",
            "Заявка закрыта и оценена",
            "Заявка закрита і оцінена"),

    CLOSED_FEEDBACK(11L,
            "Closed_Feedback",
            "Заявка закрыта с отзывом",
            "Заявка закрита з відгуком"),

    REMOVED(12L,
            "Removed",
            "Заявка удалена",
            "Заявка видалена"),

    UNKNOWN(13L,
            "Unknown",
            "Заявка в неизвестном статусе",
            "Заявка у невідомому статусі");

    private final Long nID;
    private final String sID;
    private final String sName_RU;
    private final String sName_UA;

    HistoryEvent_Service_StatusType(Long nID, String sID, String sName_RU, String sName_UA) {
        this.nID = nID;
        this.sID = sID;
        this.sName_RU = sName_RU;
        this.sName_UA = sName_UA;
    }

    public Long getnID() {
        return nID;
    }

    public String getsID() {
        return sID;
    }

    public String getsName_RU() {
        return sName_RU;
    }

    public String getsName_UA() {
        return sName_UA;
    }

    public static HistoryEvent_Service_StatusType getInstance(Long nID_StatusType) {
        for (HistoryEvent_Service_StatusType availableValue : values()) {
            if (availableValue.getnID().equals(nID_StatusType)) {
                return availableValue;
            }
        }
        throw new IllegalArgumentException("nID_StatusType="+nID_StatusType+" is out of available values range");
    }
}