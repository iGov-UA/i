package org.igov.model.action.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author idenysenko
 */
public class TaskDataResultVO {
    
    private List<TaskDataVO> aoTaskDataVO = new ArrayList<>();
    private Integer nSize;
    private Integer nStart;
    private String sOrder;
    private String sSort;
    private long nTotal;

    public TaskDataResultVO() {
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

    public List<TaskDataVO> getAoTaskDataVO() {
        return aoTaskDataVO;
    }

    public void setAoTaskDataVO(List<TaskDataVO> aoTaskDataVO) {
        this.aoTaskDataVO = aoTaskDataVO;
    }

    public long getnTotal() {
        return nTotal;
    }

    public void setnTotal(long nTotal) {
        this.nTotal = nTotal;
    }
      
}
