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
			if (curTask.getAssignee() != null) {
                return currentRow.replace(this.getPattern(), curTask.getAssignee());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
		}
    },
     EMAIL("6", "${email}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            if (curTask.getProcessVariables().get("email") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("email").toString());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
            
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			if (curTask.getProcessVariables().get("email") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("email").toString());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
		}
    },
     PHONE("7", "${phone}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            if (curTask.getProcessVariables().get("phone") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("phone").toString());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
            
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			if (curTask.getProcessVariables().get("phone") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("phone").toString());
            } else {
                return currentRow.replace(this.getPattern(), "+380");
            }
		}
    },
     BANK_ID_INN("8", "${bankIdinn}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            if (curTask.getProcessVariables().get("bankIdinn") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("bankIdinn").toString());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
            
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			if (curTask.getProcessVariables().get("bankIdinn") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("bankIdinn").toString());
            } else {
                return currentRow.replace(this.getPattern(), "");
            }
		}
    },
     CLIENT_FIO("9", "${sClientFIO}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            if (curTask.getProcessVariables().get("sClientFIO") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("sClientFIO").toString());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
            
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			if (curTask.getProcessVariables().get("sClientFIO") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("sClientFIO").toString());
            } else {
                return currentRow.replace(this.getPattern(), "");
            }
		}
    },
     MID_NAME("10", "${bankIdmiddleName}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            if (curTask.getProcessVariables().get("bankIdmiddleName") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("bankIdmiddleName").toString());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
            
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			if (curTask.getProcessVariables().get("bankIdmiddleName") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("bankIdmiddleName").toString());
            } else {
                return currentRow.replace(this.getPattern(), "");
            }
		}
    },
     FIRST_NAME("11", "${bankIdfirstName}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            if (curTask.getProcessVariables().get("bankIdfirstName") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("bankIdfirstName").toString());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
            
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			if (curTask.getProcessVariables().get("bankIdfirstName") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("bankIdfirstName").toString());
            } else {
                return currentRow.replace(this.getPattern(), "");
            }
		}
    },
     LAST_NAME("12", "${bankIdlastName}") {
        @Override
        public String replaceValue(String currentRow, Task curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
            if (curTask.getProcessVariables().get("bankIdlastName") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("bankIdlastName").toString());
            } else {
                return currentRow.replace(this.getPattern(), " ");
            }
            
        }

		@Override
		public String replaceValue(String currentRow,
				HistoricTaskInstance curTask, SimpleDateFormat sDateFormat, GeneralConfig oGeneralConfig) {
			if (curTask.getProcessVariables().get("bankIdlastName") != null) {
                return currentRow.replace(this.getPattern(), curTask.getProcessVariables().get("bankIdlastName").toString());
            } else {
                return currentRow.replace(this.getPattern(), "");
            }
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
