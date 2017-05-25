package org.igov.model.action.event;

public enum HistoryEventType {

    CUSTOM(0L,
            "custom",
            "Частный тип",
            ""),
    GET_SERVICE(1L,
            "getService",
            "Пользователь воспользовался услугой на портале",
            "Ви подали заявку на послугу " + HistoryEventMessage.SERVICE_NAME
                    + ". \n Cтатус: " + HistoryEventMessage.SERVICE_STATE),
    SET_DOCUMENT_INTERNAL(2L,
            "setDocument_internal",
            "В Мои документы пользователя загружен новый документ – через наш портал",
            HistoryEventMessage.ORGANIZATION_NAME + " завантажує " + HistoryEventMessage.DOCUMENT_TYPE
                    + " " + HistoryEventMessage.DOCUMENT_NAME + " у Ваш розділ Мої документи"),
    SET_DOCUMENT_EXTERNAL(3L,
            "setDocument_external",
            "В Мои документы пользователя загружен новый документ – внешняя организация",
            HistoryEventMessage.ORGANIZATION_NAME + " завантажує " + HistoryEventMessage.DOCUMENT_TYPE
                    + " " + HistoryEventMessage.DOCUMENT_NAME + " у Ваш розділ Мої документи"),
    SET_DOCUMENT_ACCESS_LINK(4L,
            "setDocumentAccessLink",
            "Пользователь предоставил доступ к своему документу",
            "Ви надаєте доступ до документу "
                    + HistoryEventMessage.DOCUMENT_TYPE + " " + HistoryEventMessage.DOCUMENT_NAME
                    + " іншій людині: " + HistoryEventMessage.FIO
                    + " (телефон: " + HistoryEventMessage.TELEPHONE
                    + ", e-mail: " + HistoryEventMessage.EMAIL
                    + ", термiн дії: " + HistoryEventMessage.DAYS + " днів)"),
    SET_DOCUMENT_ACCESS(5L,
            "setDocumentAccess",
            "Кто-то воспользовался доступом к документу через OTP, который ему предоставил пользователь",
            "" + HistoryEventMessage.FIO + " скористався доступом, який Ви надали, та переглянув документ "
                    + HistoryEventMessage.DOCUMENT_TYPE + " " + HistoryEventMessage.DOCUMENT_NAME + ""),
    ACTIVITY_STATUS_NEW(6L,
            "ActivitiStatusNew",
            "Изменение статуса заявки",
            "Ваша заявка №" + HistoryEventMessage.TASK_NUMBER
                    + " змiнила свiй статус на " + HistoryEventMessage.SERVICE_STATE + ""),
    GET_DOCUMENT_ACCESS_BY_HANDLER(7L,
            "getDocumentAccessByHandler",
            "Кто-то воспользовался доступом к документу, который ему предоставил пользователь",
            "Організація " + HistoryEventMessage.ORGANIZATION_NAME
                    + " скористалась доступом, який Ви надали, та переглянула документ "
                    + HistoryEventMessage.DOCUMENT_TYPE + " " + HistoryEventMessage.DOCUMENT_NAME + ""),
    FINISH_SERVICE(8L,
            "ActivitiFinish",
            "Выполнение заявки",
            "Ваша заявка №" + HistoryEventMessage.TASK_NUMBER + " виконана"),
    SET_TASK_QUESTIONS(9L,
            "ActivitiFinish",
            "Запрос на уточнение данных",
            "По заявці №" + HistoryEventMessage.TASK_NUMBER + " задане прохання уточнення:\n"
                    + HistoryEventMessage.S_BODY + "\n"
                    + HistoryEventMessage.TABLE_BODY),
    SET_TASK_ANSWERS(10L,
            "ActivitiFinish",
            "Ответ на запрос об уточнении данных",
            "По заявці №" + HistoryEventMessage.TASK_NUMBER + " дана відповідь громадянином:\n"
                    + HistoryEventMessage.S_BODY + "\n"
                    + HistoryEventMessage.TABLE_BODY),
    CREATING_DOCUMENT(11L,
            "DocumentCreating",
            "Нажата кнопка \"створити документ\"",
            "Створення документу " + HistoryEventMessage.ORDER_ID),
    CREATE_DOCUMENT(12L,
            "DocumentCreated",
            "Нажата кнопка \"створити\"",
            HistoryEventMessage.FIO + "  - документ відредаговано автором"),
    SIGNE_DOCUMENT(13L,
            "DocumentSigned",
            "Нажата кнопка \"підписати\" или \"ознайомлен\"",
            HistoryEventMessage.FIO + " - документ завізовано"),
    CHANGE_DOCUMENT(14L,
            "DocumentChanged",
            "Внесены изменения в документ или задачу (Слушатель SetTask)",
            HistoryEventMessage.FIO + " - документ відредаговано:\n\n" +  
            "<table style=\"width:30%\"><tr><th>Було</th><th>Cтало</th></tr>" +         
            HistoryEventMessage.OLD_DATA + "\n" 
            + HistoryEventMessage.NEW_DATA + "</table>"),         
    CREATE_SUBDOCUMENT(15L,
            "SubDocumentCreated",
            "В текущем процессе вызван другой процесс (с помощью элемента БП callActiviti)",
            "Створено вкладений документ\n" + HistoryEventMessage.ORDER_ID 
            + "\n" + HistoryEventMessage.BP_ID),
    CHANGE_DOCUMENT_STEP(16L,
            "DocumentChangeStep",
            "Изменился статус документа (переход с одной юзертаски на другую)",
            "Статус документа змінено - " + HistoryEventMessage.SERVICE_STATE),
    MENTION_DOCUMENT(17L,
            "DocumentMentioned",
            "Документ упомянули в другом документе",
            "Документ" + HistoryEventMessage.ORDER_ID + "пов'язаний з іншим" 
            + HistoryEventMessage.LINKED_ORDER_ID),
    CLOSE_DOCUMENT(18L,
            "DocumentClosed",
            "Процесс дошел до конца и закрылся",
            "Документ перміщено до архіву"),    
    TASK_CANCELED(19L,
            "TaskCanceled",
            "Вы самосточтельно отменили заявку №" + HistoryEventMessage.TASK_NUMBER,
            "Ви самостійно скасували заявку"),
    TASK_REQUEST_DONE(20L,
            "TaskRequestDone",
            "Исполнитель отработал задачу (sID_ProcessSubjectStatus = executed, sLoginRole= Executor)",
            HistoryEventMessage.PIP + "(" + HistoryEventMessage.LOGIN + ")" + " - завдання виконано"),
    TASK_REQUEST_NOT_DONE(21L,
            "TaskRequestNotDone",
            "исполнитель отработал задачу (sID_ProcessSubjectStatus = notExecuted, sLoginRole= Executor)",
            HistoryEventMessage.PIP + "(" + HistoryEventMessage.LOGIN + ")" + " - завдання не виконано"),
    TASK_REQUEST_NOT_ACTUAL(22L,
            "TaskRequestNotActual",
            "исполнитель отработал задачу (sID_ProcessSubjectStatus = unactual, sLoginRole= Executor)",
            HistoryEventMessage.PIP + "(" + HistoryEventMessage.LOGIN + ")" + " - завдання не актуальне"),
    TASK_REQUEST_TRANSFERED(23L,
            "TaskRequestTransfered",
            "исполнитель попросил перенести срок (sID_ProcessSubjectStatus = requestTransfered, sLoginRole= Executor)",
            HistoryEventMessage.PIP + "(" + HistoryEventMessage.LOGIN + ")" + " - прохання перенести термін виконання завдання"),
    TASK_TRANSFERED(24L,
            "TaskTransfered",
            "Контролирующий перенес срок исполнения (sID_ProcessSubjectStatus = transfered, sLoginRole= Controller)",
            HistoryEventMessage.PIP + "(" + HistoryEventMessage.LOGIN + ")" + " - перенесення терміну виконання завдання"),
    TASK_REJECTED(25L,
            "TaskRejected",
            "Контролирующий отклонил отчет (sID_ProcessSubjectStatus = rejected, sLoginRole= Controller)",
            HistoryEventMessage.PIP + "(" + HistoryEventMessage.LOGIN + ")" + " - відхилено звіт про виконання завдання"),
    TASK_DONE(26L,
            "TaskDone",
            "Контролирующий снял задание как выполненное (sID_ProcessSubjectStatus = executed, sLoginRole= Controller)",
            HistoryEventMessage.PIP + "(" + HistoryEventMessage.LOGIN + ")" + " - завдання виконано"),
    TASK_NOT_DONE(27L,
            "TaskNotDone",
            "Контролирующий снял задание как выполненное (sID_ProcessSubjectStatus = notExecuted, sLoginRole= Controller)",
            HistoryEventMessage.PIP + "(" + HistoryEventMessage.LOGIN + ")" + " - завдання не виконано"),
    TASK_NOT_ACTUAL(28L,
            "TaskNotActual",
            "Контролирующий снял задание как выполненное (sID_ProcessSubjectStatus = unactual, sLoginRole= Controller)",
            HistoryEventMessage.PIP + "(" + HistoryEventMessage.LOGIN + ")" + " - завдання не актуальне");
    

    private Long nID;
    private String sID;
    private String sName;
    private String sTemplate;

    private HistoryEventType(Long nID, String sID, String sName, String sTemplate) {
        this.nID = nID;
        this.sID = sID;
        this.sName = sName;
        this.sTemplate = sTemplate;
    }

    public static HistoryEventType getById(Long id) {
        if (id != null) {
            for (HistoryEventType eventType : values()) {
                if (eventType.nID.equals(id)) {
                    return eventType;
                }
            }
        }
        return null;
    }

    public Long getnID() {
        return nID;
    }

    public String getsID() {
        return sID;
    }

    public String getsName() {
        return sName;
    }

    public String getsTemplate() {
        return sTemplate;
    }

}

