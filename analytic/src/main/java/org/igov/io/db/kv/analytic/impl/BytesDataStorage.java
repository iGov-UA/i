package org.igov.io.db.kv.analytic.impl;


import org.igov.io.db.kv.analytic.IBytesDataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

public class BytesDataStorage 
        extends org.igov.io.db.kv.statical.impl.BytesDataStorage 
        implements IBytesDataStorage {
    
    @Autowired
    @Qualifier("gridAnalyticTemplate")
    private GridFsTemplate oGridFsTemplate;
}
