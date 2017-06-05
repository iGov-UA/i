package org.igov.io.db.kv.analytic.impl;


import org.igov.io.db.kv.analytic.SaveToMongoStorage;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.gridfs.GridFsTemplate;


public class FileStorageAnalyticImpl extends SaveToMongoStorage {

    private final GridFsTemplate gridAnalyticTemplate;

    @Autowired
    public FileStorageAnalyticImpl(GridFsTemplate gridAnalyticTemplate) {
        this.gridAnalyticTemplate = gridAnalyticTemplate;
    }

    @Override
    public GridFsTemplate getGridTemplate() {
        return this.gridAnalyticTemplate;
    }
}
