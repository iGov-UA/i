package org.igov.io.db.kv.analytic.impl;


import org.igov.io.db.kv.analytic.FileStorageAnalytic;
import org.igov.io.db.kv.statical.exceptions.RecordNotFoundException;
import org.igov.io.db.kv.statical.impl.FileStorage;
import org.igov.io.db.kv.statical.model.UploadedFile;
import org.igov.io.db.kv.statical.model.UploadedFileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;

public class FileStorageAnalyticImpl
        implements FileStorageAnalytic {

    @Autowired
    private FileStorage fileStorage;


    @Autowired
    @Qualifier("gridAnalyticTemplate")
    private GridFsTemplate oGridFsTemplate;

    @PostConstruct
    private void init() {
        fileStorage.setoGridFsTemplate(oGridFsTemplate);
    }

    @Override
    public String createFile(MultipartFile file) {
        return fileStorage.createFile(file);
    }

    @Override
    public boolean saveFile(String key, MultipartFile file) {
        return fileStorage.saveFile(key, file);
    }

    @Override
    public boolean remove(String key) {
        return fileStorage.remove(key);
    }

    @Override
    public boolean keyExists(String key) {
        return fileStorage.keyExists(key);
    }

    @Override
    public UploadedFile getFile(String key) {
        return fileStorage.getFile(key);
    }

    @Override
    public UploadedFileMetadata getFileMetadata(String key) {
        return fileStorage.getFileMetadata(key);
    }

    @Override
    public InputStream openFileStream(String key) throws RecordNotFoundException {
        return fileStorage.openFileStream(key);
    }
}
