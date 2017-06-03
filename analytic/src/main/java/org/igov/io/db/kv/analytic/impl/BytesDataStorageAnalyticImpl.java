package org.igov.io.db.kv.analytic.impl;


import org.igov.io.db.kv.analytic.BytesDataStorageAnalytic;
import org.igov.io.db.kv.statical.exceptions.RecordNotFoundException;
import org.igov.io.db.kv.statical.impl.BytesDataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.InputStream;

public class BytesDataStorageAnalyticImpl
        implements BytesDataStorageAnalytic {
    @Autowired
    private BytesDataStorage bytesDataStorage;

    @Autowired
    @Qualifier("gridAnalyticTemplate")
    private GridFsTemplate oGridFsTemplate;

    @Override
    public String saveData(byte[] data) {
        return null;
    }

    @Override
    public boolean setData(String key, byte[] data) {
        return false;
    }

    @Override
    public boolean remove(String key) {
        return false;
    }

    @Override
    public boolean keyExists(String key) {
        return false;
    }

    @Override
    public byte[] getData(String key) {
        return new byte[0];
    }

    @Override
    public InputStream openDataStream(String key) throws RecordNotFoundException {
        return null;
    }
}
