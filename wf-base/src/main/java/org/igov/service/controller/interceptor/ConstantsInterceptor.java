package org.igov.service.controller.interceptor;

import java.util.regex.Pattern;


/**
 * интерфейс для объявления статических переменных, используемых в интерсепторе
 * @author inna
 *
 */
public interface ConstantsInterceptor {
	static final String SYSTEM_ERR = "SYSTEM_ERR";
	static final String SERVICE_DOCUMENT_GET_DOCUMENT_ABSTRACT = "/service/document/getDocumentAbstract";
	static final String SERVICE_OBJECT_FILE = "/service/object/file/";
	static final String SERVICE_DOCUMENT_SET_DOCUMENT_FILE = "/service/document/setDocumentFile";
	static final String SERVICE_DOCUMENT_GET_DOCUMENTS = "/service/document/getDocuments";
	static final String SERVICE_DOCUMENT_GET_DOCUMENT_FILE = "/service/document/getDocumentFile";
	static final String SERVICE_DOCUMENT_GET_DOCUMENT_CONTENT = "/service/document/getDocumentContent";
	static final String SERVICE_ACTION_EVENT_GET_HISTORY_EVENTS = "/service/action/event/getHistoryEvents";
	static final String SERVICE_ACTION_EVENT_GET_HISTORY_EVENTS_SERVICE = "/service/action/event/getHistoryEventsService";
	static final String SERVICE_ACTION_EVENT_GET_LAST_TASK_HISTORY = "/service/action/event/getLastTaskHistory";
	static final String SERVICE_OBJECT_PLACE_GET_PLACES_TREE = "/service/object/place/getPlacesTree";
	static final String SERVICE_SUBJECT_MESSAGE_GET_SERVICE_MESSAGES = "/service/subject/message/getServiceMessages";
	static final String SERVICE_SUBJECT_MESSAGE_GET_MESSAGES = "/service/subject/message/getMessages";
	static final String SERVICE_ACTION_TASK_GET_LOGIN_B_PS = "/service/action/task/getLoginBPs";
	static final String SERVICE_HISTORY_HISTORIC_TASK_INSTANCES = "/service/history/historic-task-instances";
	static final String SERVICE_RUNTIME_TASKS = "/service/runtime/tasks";
	static final String SERVICE_ACTION_FLOW_GET_FLOW_SLOTS_SERVICE_DATA = "/service/action/flow/getFlowSlots";
	static final String SERVICE_ACTION_TASK_GET_ORDER_MESSAGES_LOCAL = "/service/action/task/getOrderMessages_Local";
	static final String SERVICE_ACTION_TASK_GET_START_FORM_DATA = "/service/action/task/getStartFormData";
	static final String SERVICE_REPOSITORY_PROCESS_DEFINITIONS = "/service/repository/process-definitions";
	static final String SERVICE_FORM_FORM_DATA = "/service/form/form-data";
        static final String SERVICE_CANCELTASK = "/service/action/task/cancelTask";
	static final String SERVICE_ACTION_ITEM_GET_SERVICES_TREE = "/service/action/item/getServicesTree";
	static final String SERVICE_ACTION_ITEM_GET_SERVICE = "/service/action/item/getService";
	static final String URI_SYNC_CONTACTS = "/wf/service/subject/syncContacts";
	static final String DNEPR_MVK_291_COMMON_BP = "dnepr_mvk_291_common|_test_UKR_DOC|dnepr_mvk_889|_doc_justice_171|_doc_justice_172|_doc_justice_173|_doc_justice_11|_doc_justice_12|_doc_justice_13|_doc_justice_14|_doc_justice_15|_doc_justice_16";
	static final Pattern TAG_PATTERN_PREFIX = Pattern.compile("runtime/tasks/[0-9]+$");
	static final Pattern SREQUESTBODY_PATTERN = Pattern.compile("\"assignee\":\"[а-яА-Яa-z_A-z0-9]+\"");
}