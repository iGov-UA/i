package org.igov.io.db.kv.analytic.impl;


import org.igov.io.db.kv.analytic.IFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

public class FileStorage 
        extends org.igov.io.db.kv.statical.impl.FileStorage 
        implements IFileStorage {
    
    private final GridFsTemplate oGridFsTemplate;

    @Autowired
    public FileStorage(@Qualifier("gridAnalyticTemplate") GridFsTemplate oGridFsTemplate) {
        super(oGridFsTemplate);
        this.oGridFsTemplate = oGridFsTemplate;
    }
}
