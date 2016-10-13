package org.igov.service.business.action.task.core;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

import java.text.SimpleDateFormat;
import org.igov.io.GeneralConfig;

public enum TaskReportField {

    REQUEST_NUMBER("1", "${nID_Task}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            return currentRow.replace(this.getPattern(), curTask.getId());
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			return currentRow.replace(this.getPattern(), curTask.getId());
		}
    },
    REQUEST_ORDER("3", "${sID_Order}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            Long nID_Process = new Long(curTask.getProcessInstanceId());
            String sID_Order = oGeneralConfig.getOrderId_ByProcess(nID_Process);
            return currentRow.replace(this.getPattern(), sID_Order);
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
                        Long nID_Process = new Long(curTask.getProcessInstanceId());
                        String sID_Order = oGeneralConfig.getOrderId_ByProcess(nID_Process);
			return currentRow.replace(this.getPattern(), sID_Order);
		}
    },
    CREATE_DATE("2", "${sDateCreate}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            return currentRow.replace(this.getPattern(), sDateFormat.format(curTask.getCreateTime()));
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			return currentRow.replace(this.getPattern(), sDateFormat.format(curTask.getCreateTime()));
		}
    },
    CLOSE_DATE("4", "${sDateClose}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            return currentRow.replace(this.getPattern(), sDateFormat.format(curTask.getCreateTime()));
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			return currentRow.replace(this.getPattern(), sDateFormat.format(curTask.getEndTime()));
		}
    },
     ASSIGNEE("5", "${sLoginAssignee}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            if (curTask.getAssignee() != null) {
                return currentRow.replace(this.getPattern(), curTask.getAssignee());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
            
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			return currentRow.replace(this.getPattern(), curTask.getAssignee());
		}
    };

    private String id;
    private String pattern;

    TaskReportField(String id, String pattern) {
        this.id = id;
        this.pattern = pattern;
    }

    public static TaskReportField getReportFieldForId(String id) {
        for (TaskReportField curr : TaskReportField.values()) {
            if (curr.getId().equals(id)) {
                return curr;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getPattern() {
        return pattern;
    }

    public abstract String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig);
    
    public abstract String replaceValue(String currentRow, HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig);

}
