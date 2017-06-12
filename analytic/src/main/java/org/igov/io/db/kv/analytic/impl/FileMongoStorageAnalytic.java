package org.igov.io.db.kv.analytic.impl;


import org.igov.io.db.kv.analytic.SaveToMongoStorageFile;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.gridfs.GridFsTemplate;


public class FileMongoStorageAnalytic extends SaveToMongoStorageFile {

    private final GridFsTemplate gridAnalyticTemplate;

    @Autowired
    public FileMongoStorageAnalytic(GridFsTemplate gridAnalyticTemplate) {
        this.gridAnalyticTemplate = gridAnalyticTemplate;
    }

    @Override
    public GridFsTemplate getGridTemplate() {
        return this.gridAnalyticTemplate;
    }
}
