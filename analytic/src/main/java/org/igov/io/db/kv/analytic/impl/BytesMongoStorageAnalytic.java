package org.igov.io.db.kv.analytic.impl;

import org.igov.io.db.kv.analytic.SaveToMongoStorageBytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;


public class BytesMongoStorageAnalytic extends SaveToMongoStorageBytes {

    private final GridFsTemplate oGridFsTemplate;

    @Autowired
    public BytesMongoStorageAnalytic(@Qualifier("gridAnalyticTemplate") GridFsTemplate oGridFsTemplate) {
        this.oGridFsTemplate = oGridFsTemplate;
    }

    @Override
    public GridFsTemplate getGridTemplate() {
        return oGridFsTemplate;
    }
}
