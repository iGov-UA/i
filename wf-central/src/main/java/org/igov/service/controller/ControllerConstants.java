package org.igov.service.controller;

public interface ControllerConstants {
    String SERVICE_HISTORY_REPORT = "Получение отчета о поданных заявках";
    String SERVICE_HISTORY_REPORT_NOTES ="##### Возвращает csv файл с информацией о задачах за указанный период на основании HistoryEventService записей.\n"
            + "В результате возвращаются следующий набор полей - sID_Order, nID_Server, nID_Service, sID_Place, nID_Subject, nRate, sTextFeedback, sUserTaskName, sHead, sBody, nTimeMinutes, sPhone\n"
            + "Результат для колонки sTextFeedback возвращается из сущности SubjectMessage, у которой nID_SubjectMessageType = 2\n"
            + "Результат для колонки sPhone возвращается из стартовой формы процесса из поля phone соответствующего регионального сервера\n"
            + "Примеры:\n"
            + "https://test.igov.org.ua/wf/service/action/event/getServiceHistoryReport?sDateAt=2016-02-09 00:00:00&sDateTo=2016-02-11 00:00:00&sanID_Service_Exclude=1,5,24,56\n\n"
            + "Результат\n"
            + "\n```csv\n"
            + "sID_Order,nID_Server,nID_Service,sID_Place,nID_Subject,nRate,sTextFeedback,sUserTaskName,sHead,sBody,nTimeMinutes,sPhone\n"
            + "0-88625055,0,740,6500000000,20045,,,,,Необхідно уточнити дані, за коментарем: не вірно вказані дані членів родини. Син - не відповідні ПІБ, бат - відсутні обов'язкові дані,,+380 97 225 5363\n"
            + "\n```\n";
    String SERVICE_HISTORY_REPORT_DATE_AT = "строка-Дата начала выборки данных в формате yyyy-MM-dd HH:mm:ss";
    String SERVICE_HISTORY_REPORT_DATE_TO = "строка-Дата окончания выборки данных в формате yyyy-MM-dd HH:mm:ss";
    String SERVICE_HISTORY_REPORT_ID_FILTER_DATE_TYPE = "строка, указывающая на тип даты, по которой идет выгрузка данных";
    String SERVICE_HISTORY_REPORT_INCLUDE_TASK_INFO = "загрузка данных из заявок";
    String SERVICE_HISTORY_REPORT_ID_SERVICE_EXCLUDE = "строка-массив(перечисление) ИД услуг, которые нужно исключить";
    String SERVICE_HISTORY_REPORT_CODEPAGE = "строка, указывающая желаемую кодировку ответа (windows-1251, utf-8)";
}
