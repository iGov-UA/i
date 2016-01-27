package org.igov.io.db.kv.statical.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.db.kv.statical.exceptions.RecordNotFoundException;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

public class BytesDataStorage implements IBytesDataStorage {

    private static final Logger LOG = LoggerFactory.getLogger(BytesDataStorage.class);

    @Autowired
    private GridFsTemplate oGridFsTemplate;

    @Override
    public String saveData(byte[] aByte) {

        String sKey = UUID.randomUUID().toString();
        if (setData(sKey, aByte)) {
            return sKey;
        }
        return null;
    }

    @Override
    public boolean setData(String sKey, byte[] data) {
        try (InputStream oInputStream = new ByteArrayInputStream(data)) {
            GridFSFile oGridFSFile = oGridFsTemplate.store(oInputStream, sKey);
            oGridFSFile.save();
        } catch (IOException oException) {
        	LOG.error("Bad: {}, (sKey={}, sData={})",oException.getMessage(), sKey, data);
            LOG.trace("FAIL:", oException);
            return false;
        }
        return true;
    }

    private static Query getKeyQuery(String sKey) {
        return new Query(GridFsCriteria.whereFilename().is(sKey));
    }

    private GridFSDBFile findLatestEdition(String sKey) {
        List<GridFSDBFile> aGridFSDBFile = oGridFsTemplate.find(
                getKeyQuery(sKey)
                .with(new Sort(Direction.DESC, "uploadDate"))
                .limit(1));
        if (aGridFSDBFile == null || aGridFSDBFile.isEmpty()) {
            return null;
        }
        return aGridFSDBFile.get(0);
    }

    @Override
    public boolean remove(String sKey) {
        oGridFsTemplate.delete(getKeyQuery(sKey));
        return true;
    }

    @Override
    public byte[] getData(String sKey) {

        GridFSDBFile oGridFSDBFile = findLatestEdition(sKey);
        try (InputStream is = oGridFSDBFile.getInputStream()) {
            return IOUtils.toByteArray(is);
        } catch (NullPointerException | IOException oException) {
            LOG.error("Bad: {}, (sKey={})",oException.getMessage(), sKey);
            LOG.trace("FAIL:", oException);
            return null;
        }
    }

    @Override
    public InputStream openDataStream(String sKey) throws RecordNotFoundException {
        GridFSDBFile oGridFSDBFile = findLatestEdition(sKey);
        if (oGridFSDBFile == null) {
            throw new RecordNotFoundException(String.format("Value for key '%s' not found", sKey));
        }
        return oGridFSDBFile.getInputStream();
    }

    @Override
    public boolean keyExists(String sKey) {
        GridFSDBFile oGridFSDBFile = findLatestEdition(sKey);
        return oGridFSDBFile != null;
    }

}
