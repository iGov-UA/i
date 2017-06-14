package org.igov.model.action.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author idenysenko
 */
public class TaskDataResultVO {
    
    private List<TaskDataVO> aoTaskDataVO = new ArrayList<>();
    private Integer nSize = 10;
    private Integer nStart = 0;
    private String sOrder = "asc";
    private String sSort = "id";

    public TaskDataResultVO() {
    }

    public List<TaskDataVO> getaoTaskDataVO() {
        return aoTaskDataVO;
    }

    public void setaoTaskDataVO(List<TaskDataVO> aoTaskDataVO) {
        this.aoTaskDataVO = aoTaskDataVO;
    }

    public Integer getnSize() {
        return nSize;
    }

    public void setnSize(Integer nSize) {
        this.nSize = nSize;
    }

    public Integer getnStart() {
        return nStart;
    }

    public void setnStart(Integer nStart) {
        this.nStart = nStart;
    }

    public String getsOrder() {
        return sOrder;
    }

    public void setsOrder(String sOrder) {
        this.sOrder = sOrder;
    }

    public String getsSort() {
        return sSort;
    }

    public void setsSort(String sSort) {
        this.sSort = sSort;
    }
      
}
