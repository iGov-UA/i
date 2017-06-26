package org.igov.io.db.kv.analytic;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.igov.io.db.kv.statical.IFileStorage;
import org.igov.io.db.kv.statical.exceptions.RecordNotFoundException;
import org.igov.io.db.kv.statical.impl.BytesDataStorage;
import org.igov.io.db.kv.statical.model.UploadedFile;
import org.igov.io.db.kv.statical.model.UploadedFileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by dpekach on 05.06.17.
 */
public abstract class SaveToMongoStorageFile implements IFileStorage {

    private static final Logger LOG = LoggerFactory.getLogger(SaveToMongoStorageFile.class);

    public abstract GridFsTemplate getGridTemplate();

    private static String getExtension(MultipartFile oFile) {
        return FilenameUtils.getExtension(oFile.getOriginalFilename());
    }

    @Override
    public String createFile(MultipartFile oFile) {
        String sKey = UUID.randomUUID().toString() + getExtension(oFile);
        if (saveFile(sKey, oFile)) {
            return sKey;
        }
        return null;
    }

    @Override
    public boolean saveFile(String sKey, MultipartFile oFile) {
        GridFSFile oGridFSFile;
        try {
            oGridFSFile = getGridTemplate().store(oFile.getInputStream(), sKey);
            oGridFSFile.put("contentType", oFile.getContentType());
            oGridFSFile.put("originalName", oFile.getOriginalFilename());
            oGridFSFile.save();
            return true;
        } catch (IOException e) {
            LOG.error("Can't save content by this key: {} (sKey={})", e.getMessage(), sKey);
            LOG.trace("FAIL:", e);
            return false;
        }
    }

    private static Query getKeyQuery(String sKey) {
        return new Query(GridFsCriteria.whereFilename().is(sKey));
    }

    private GridFSDBFile findLatestEdition(String sKey) {
        List<GridFSDBFile> aGridFSDBFile = getGridTemplate().find(
                getKeyQuery(sKey)
                        .with(new Sort(Sort.Direction.DESC, "uploadDate"))
                        .limit(1));
        if (aGridFSDBFile == null || aGridFSDBFile.isEmpty()) {
            return null;
        }
        return aGridFSDBFile.get(0);
    }

    @Override
    public boolean remove(String sKey) {
        try {
            getGridTemplate().delete(getKeyQuery(sKey));
            return true;
        } catch (Exception e) {
            LOG.error("Can't remove content by this key: {} (sKey={})", e.getMessage(), sKey);
            LOG.trace("FAIL:", e);
            return false;
        }
    }

    @Override
    public boolean keyExists(String sKey) {
        return findLatestEdition(sKey) != null;
    }

    private UploadedFileMetadata getFileMetadataInternal(GridFSFile oGridFSFile) {
        if (oGridFSFile == null) {
            return null;
        }

        Object sName = oGridFSFile.get("originalName");
        return new UploadedFileMetadata(oGridFSFile.getContentType(),
                sName == null ? null : sName.toString(),
                oGridFSFile.getUploadDate());
    }

    @Override
    public UploadedFile getFile(String sKey) {
        GridFSDBFile oGridFSDBFile = findLatestEdition(sKey);

        try (InputStream oInputStream = oGridFSDBFile.getInputStream()) {
            UploadedFileMetadata oUploadedFileMetadata = getFileMetadataInternal(oGridFSDBFile);
            byte[] aByte = IOUtils.toByteArray(oInputStream);
            return new UploadedFile(aByte, oUploadedFileMetadata);
        } catch (NullPointerException | IOException e) {
            LOG.error("FAIL: {} (sKey={})", e.getMessage(), sKey);
            LOG.trace("FAIL:", e);
            return null;
        }
    }

    @Override
    public UploadedFileMetadata getFileMetadata(String sKey) {
        return getFileMetadataInternal(findLatestEdition(sKey));
    }

    @Override
    public InputStream openFileStream(String sKey) throws RecordNotFoundException {
        GridFSDBFile oGridFSDBFile = findLatestEdition(sKey);

        if (oGridFSDBFile == null) {
            LOG.warn("Content by this key not found! Check existing before open stream! (sKey={})", sKey);
            throw new RecordNotFoundException(String.format("Value for key '%s' not found", sKey));
        }
        return oGridFSDBFile.getInputStream();
    }

}
