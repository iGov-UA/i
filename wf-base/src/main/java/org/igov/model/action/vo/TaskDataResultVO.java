package org.igov.model.action.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author idenysenko
 */
public class TaskDataResultVO {
    
    private List<TaskDataVO> aoTaskDataVO = new ArrayList<>();
    private Integer size;
    private Integer start;
    private String order;
    private String sort;
    private long total;

    public TaskDataResultVO() {
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<TaskDataVO> getAoTaskDataVO() {
        return aoTaskDataVO;
    }

    public void setAoTaskDataVO(List<TaskDataVO> aoTaskDataVO) {
        this.aoTaskDataVO = aoTaskDataVO;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
      
}
