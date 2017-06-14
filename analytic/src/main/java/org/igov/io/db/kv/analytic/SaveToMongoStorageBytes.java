package org.igov.io.db.kv.analytic;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.db.kv.statical.exceptions.RecordNotFoundException;
import org.igov.io.db.kv.statical.impl.BytesDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by dpekach on 05.06.17.
 */
public abstract class SaveToMongoStorageBytes implements IBytesDataStorage {
    private static final Logger LOG = LoggerFactory.getLogger(SaveToMongoStorageBytes.class);

    public abstract GridFsTemplate getGridTemplate();

    @Override
    public String saveData(byte[] aByte) {
        //LOG.info("Start seve Data aByte  size = {}", aByte.length);
        String sKey = UUID.randomUUID().toString();
        if (setData(sKey, aByte)) {
            //LOG.info("Save data completed with sKey = {}", sKey);
            return sKey;
        }
        //LOG.info("Save data completed NULL");
        return null;
    }

    @Override
    public boolean setData(String sKey, byte[] data) {
        try (InputStream oInputStream = new ByteArrayInputStream(data)) {
            //LOG.info("Start create oGridFSFile");
            GridFSFile oGridFSFile = getGridTemplate().store(oInputStream, sKey);
            //LOG.info("Start save oGridFSFile");
            oGridFSFile.save();
            //LOG.info("End save oGridFSFile File size = {}", oGridFSFile.getLength());
        } catch (IOException oException) {
            LOG.error("Bad: {}, (sKey={}, sData={})", oException.getMessage(), sKey, data);
            LOG.trace("FAIL:", oException);
            return false;
        }
        return true;
    }

    private static Query getKeyQuery(String sKey) {
        return new Query(GridFsCriteria.whereFilename().is(sKey));
    }

    private GridFSDBFile findLatestEdition(String sKey) {
        //LOG.info("sKey = {}", sKey);
        List<GridFSDBFile> aGridFSDBFile = getGridTemplate().find(
                getKeyQuery(sKey)
                        .with(new Sort(Sort.Direction.DESC, "uploadDate"))
                        .limit(1));
        if (aGridFSDBFile == null || aGridFSDBFile.isEmpty()) {
            /*
            if(aGridFSDBFile == null){
                LOG.info("aGridFSDBFile == null");
            } else {
                LOG.info("aGridFSDBFile.isEmpty");
            }
            LOG.warn("findLatestEdition return NULL");
            */
            return null;
        }
        LOG.warn("findLatestEdition return GridFSDBFile");
        return aGridFSDBFile.get(0);
    }

    @Override
    public boolean remove(String sKey) {
        getGridTemplate().delete(getKeyQuery(sKey));
        return true;
    }

    @Override
    public byte[] getData(String sKey) {

        //LOG.info("Start getData sKey = {}", sKey);
        GridFSDBFile oGridFSDBFile = findLatestEdition(sKey);
        try {
            if (oGridFSDBFile != null) {
                //LOG.info("oGridFSDBFile = null");
                oGridFSDBFile.getInputStream();
                //LOG.info("InputStream created");
            } else {
                //LOG.info("oGridFSDBFile = null");
            }
        } catch (Exception e) {
            LOG.error("getData oGridFSDBFile.getInputStream Exeption: " + e.getMessage());
        }

        try (InputStream is = oGridFSDBFile.getInputStream()) {
            //LOG.info("Strrt fill InputStream");
            byte[] result = IOUtils.toByteArray(is);
            //LOG.info("IOUtils.toByteArray(InputStream) size = {}", result.length);
            return result;
        } catch (NullPointerException | IOException oException) {
            LOG.error("Bad: {}, (sKey={})", oException.getMessage(), sKey);
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
