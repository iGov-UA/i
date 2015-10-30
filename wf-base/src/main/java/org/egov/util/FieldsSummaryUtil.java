package org.egov.util;

import org.activiti.engine.history.HistoricTaskInstance;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/*
тот элемент, который задан первым в параметре saFieldSummary - является "ключевым полем"
 2) формат каждого элемента поля такой(на примере):
"nSum=sum(nMinutes)"
где:
sum - "оператор сведения"
nMinutes - переменная, которая хранит в себе значение уже существующего или посчитанного поля формируемой таблицы
nSum - название поля, куда будет попадать результат
 3) полный перечень поддерживаемых "операторов сведения":
count() - число строк/элементов (не содержит аргументов)
sum(fiels) - сумма чисел (содержит аргумент - название обрабатываемого поля)
avg(field) - среднее число (содержит аргумент - название обрабатываемого поля)
 4) Значение "ключевого поля" переносится в новую таблицу без изменений в виде единой строки,
 и все остальные сводные поля подсчитываются исключительно в контексте значения этого ключевого поля,
 и проставляютя соседними полями в рамках этой единой строки.
 5) В итоге, параметр saFieldSummary может содержать примерно такое значение:
"sRegion;nSum=sum(nMinutes);nVisites=count()"
и это сформирует в итоге таблицу из трех полей(колонок): sRegion;nSum;nVisites
с соответствующими вычеслинными значениями
Напрример:
sRegion;nSum;nVisites
Бабушкинский;435;5
Кировский;343;3

http://localhost:8082/wf/service/rest/file/download_bp_timing?sID_BP_Name=lviv_mvk-1&sDateAt=2015-06-28&sDateTo=2015-07-01&saFieldSummary=sRegion;nSum=sum(nMinutes);nVisites=count()* */

public class FieldsSummaryUtil {
    public static final String DELIMITER_COMMA = ";";
    public static final String DELIMITER_EQUALS = "=";
    private static final Logger LOG = Logger.getLogger(FieldsSummaryUtil.class);

    public static List<List<String>> getFieldsSummary(List<HistoricTaskInstance> tasks, String saFieldSummary) {

        List<List<String>> result = new LinkedList<>();
        List<String> headers = new LinkedList<>();
        String[] conditions = saFieldSummary.split(DELIMITER_COMMA);//"sRegion;nSum=sum(nMinutes);nVisites=count()"
        String keyFieldName = conditions[0];
        headers.add(keyFieldName);
        for (int i = 1; i < conditions.length; i++) {
            LOG.info(conditions[i]);
            headers.add(conditions[i].split(DELIMITER_EQUALS)[0]);
        }
        LOG.info("headers = " + headers);
        result.add(headers);
        result.add(headers);
        for (HistoricTaskInstance task : tasks) {

        }
        return result;
    }
}
