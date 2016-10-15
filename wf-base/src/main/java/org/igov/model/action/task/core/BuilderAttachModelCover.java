package org.igov.model.action.task.core;


import org.igov.model.action.task.core.entity.ListKeyable;
import java.io.Serializable;

public class BuilderAttachModelCover implements ListKeyable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String byteToStringContent;
    private String name;
    private String contentType;
    private String exp;
    private String originalFilename;

    public String getByteToStringContent() {
        return byteToStringContent;
    }

    public void setByteToStringContent(String byteToStringContent) {
        this.byteToStringContent = byteToStringContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    @Override
    public String getKey() {
        return originalFilename + getByteToStringContent().toString() + contentType + exp;
    }

}
