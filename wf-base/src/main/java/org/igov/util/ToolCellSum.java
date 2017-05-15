package org.igov.util;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

http://localhost:8082/wf/service/action/task/download_bp_timing?sID_BP_Name=lviv_mvk-1&sDateAt=2015-06-28&sDateTo=2015-07-01&saFieldSummary=sRegion;nSum=sum(nMinutes);nVisites=count()* */
//https://test.region.igov.org.ua/wf/service/action/task/download_bp_timing?sID_BP_Name=dnepr_dms_passport&sDateAt=2015-10-01&sDateTo=2015-10-31
// &saFieldSummary=sRegion;nSum=sum(nMinutes);nVisites=count()


public class ToolCellSum {

    private static final Logger LOG = LoggerFactory.getLogger(ToolCellSum.class);
    
    private static final String DELIMITER_COMMA = ";";
    private static final String DELIMITER_EQUALS = "=";
    private static final String DELIMITER_LEFT_BRACE = "(";
    private static final String DELIMITER_RIGHT_BRACE = ")";

    public List<List<String>> getFieldsSummary(List<Map<String, Object>> csvLines, String saFieldSummary) {

        List<List<String>> result = new LinkedList<>();

        List<String> csvHeaders = new LinkedList<>();
        String[] conditions = saFieldSummary.split(DELIMITER_COMMA);//"sRegion;nSum=sum(nMinutes);nVisites=count()"
        String keyFieldName = conditions[0];
        csvHeaders.add(keyFieldName);
        LOG.info("(keyFieldName={})",  keyFieldName);
        List<ColumnObject> columnHeaderObjects = getObjectLines(saFieldSummary);

        Map<Object, List<ColumnObject>> objectLines = new HashMap<>();
        for (Map<String, Object> csvLine : csvLines) {
            LOG.debug("currTask: {}",  csvLine.get("nID_Process"));
            LOG.info("-----------------task variables:----");
            for (String taskKey : csvLine.keySet()) {
                LOG.info("[{}]={}",  taskKey, csvLine.get(taskKey));
            }

            Object keyFieldValue = csvLine.get(keyFieldName);
            LOG.info("current keyFieldValue={}",  keyFieldValue);
            if (keyFieldValue != null) {//??if keyFieldValue null or doesnt exist ??
                List<ColumnObject> currentLine = (objectLines.containsKey(keyFieldValue))
                        ? objectLines.get(keyFieldValue) : copyColumnObjects(columnHeaderObjects);

                for (ColumnObject cell : currentLine) {
                    Object value = csvLine.get(cell.field);
                    LOG.info("csvLine.get({})={}",  cell.field ,  value);
                    cell.calculateValue(csvLine.get(cell.field));
                    LOG.info("total cell={}",  cell);
                }
                LOG.info("currentLine[result]=" + currentLine);
                objectLines.put(keyFieldValue, currentLine);
            }
        }
        for (ColumnObject columnObject : columnHeaderObjects) {
            csvHeaders.add(columnObject.header);
        }
        LOG.info("(csvHeaders={})",  csvHeaders);
        result.add(csvHeaders);

        for (Object keyValue : objectLines.keySet()) {
            List<String> cells = new LinkedList<>();
            cells.add("" + keyValue);
            for (ColumnObject cell : objectLines.get(keyValue)) {
                cells.add(cell.calculateTotalValue());
            }
            LOG.info("total cells = {}",  cells);
            result.add(cells);
        }

        return result;
    }

    private List<ColumnObject> copyColumnObjects(List<ColumnObject> columnObjects) {
        List<ColumnObject> lines = new LinkedList<>();
        for (ColumnObject columnObject : columnObjects) {
            lines.add(new ColumnObject(columnObject.header, columnObject.field, columnObject.operation));
        }
        return lines;
    }

    private List<ColumnObject> getObjectLines(
            String saFieldSummary) {//example: "sRegion;nSum=sum(nMinutes);nVisites=count()"
        List<ColumnObject> lines = new LinkedList<>();
        String[] conditions = saFieldSummary.split(DELIMITER_COMMA);
        String keyFieldName = conditions[0];
        for (int i = 1; i < conditions.length; i++) {
            String condition = conditions[i];
            LOG.info("(column={})",  condition);
            String[] conditionArr = condition.split(DELIMITER_EQUALS);
            String headerName = conditionArr[0];
            String fieldName = keyFieldName;
            OperationType operation = OperationType.COUNT;
            if (conditionArr.length > 1) {
                String conditionStr = conditionArr[1];
                int leftBracePos = conditionStr.indexOf(DELIMITER_LEFT_BRACE);
                int rightBracePos = conditionStr.indexOf(DELIMITER_RIGHT_BRACE);
                if (leftBracePos != -1 && rightBracePos != -1) {
                    String operationStr = conditionStr.substring(0, leftBracePos);
                    operation = OperationType.getValueOf(operationStr);
                    fieldName = conditionStr.substring(leftBracePos + 1, rightBracePos);
                    fieldName = fieldName.isEmpty() ? keyFieldName : fieldName;
                }
            }
            LOG.info("(headerName={})", headerName);
            LOG.info("(fieldName={})", fieldName);
            LOG.info("(operation={})",  operation);
            ColumnObject currentLine = new ColumnObject(headerName, fieldName, operation);
            lines.add(currentLine);
        }
        return lines;
    }

    enum OperationType {
        SUM,
        COUNT,
        AVG;

        static OperationType getValueOf(String valueStr) {
            if (valueStr == null || valueStr.isEmpty()) {
                throw new IllegalArgumentException("aggregation-value is empty!");
            }
            OperationType result = null;
            switch (valueStr.toLowerCase()) {
            case "count":
                result = COUNT;
                break;
            case "sum":
                result = SUM;
                break;
            case "avg":
            case "average":
                result = AVG;
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("aggregation-value [%s] is out of possible range!", valueStr));
            }
            return result;
        }
    }

    class ColumnObject {
        private String header;
        private String field;
        private OperationType operation;
        private long count = 0L;
        private double sum = 0.0;
        private double avg = 0.0;

        ColumnObject(String header, String field, OperationType operation) {
            this.header = header;
            this.field = field;
            this.operation = operation;
        }

        void calculateValue(Object value) {
            if (value == null) {
                return;
            }
            if (OperationType.SUM.equals(operation) || OperationType.AVG.equals(operation)) {
                if (value instanceof Double) {
                    sum += (double) value;
                } else if (value instanceof Long) {
                    sum += ((long) value * 1.0);
                } else if (value instanceof Integer) {
                    sum += ((int) value * 1.0);
                } else if (value instanceof String) {
                    try {
                        Long castedValue = Long.valueOf((String) value);
                        sum += (castedValue * 1.0);
                    } catch (NumberFormatException e) {
                        try {
                            Double castedValue = Double.valueOf((String) value);
                            sum += castedValue;
                        } catch (NumberFormatException ex) {
                            /*NOP*/
                        }
                    }
                } else {
                    try {
                        sum += (double) value;
                    } catch (Exception ex) {
                            /*NOP*/
                    	    LOG.error("calculate sum error", ex);
                    }
                }

            }
            count++;
        }

        String calculateTotalValue() {
            String result = "";
            switch (operation) {
            case SUM:
                result = "" + sum;
                break;
            case COUNT:
                result = "" + count;
                break;
            case AVG:
                avg = (count > 0 ? (sum / count) : 0.0);
                result = "" + avg;
                break;
            }
            return result;
        }

        @Override
        public String toString() {
            return "\n{" +
                    "header='" + header + '\'' +
                    ", field='" + field + '\'' +
                    ", operation=" + operation +
                    ", count=" + count +
                    ", sum=" + sum +
                    "}";
        }

    }
}
