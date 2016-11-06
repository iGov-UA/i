package org.igov.io.db.kv.temp.impl;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Запись/вычитка данных кейвелью-хранилища (инмемори).
 *
 * @author BW
 */
@Service("redisService")
public class BytesDataInmemoryStorage implements IBytesDataInmemoryStorage {

    static final transient Logger LOG = LoggerFactory
            .getLogger(BytesDataInmemoryStorage.class);

    @Autowired
    private RedisTemplate<String, byte[]> oTemplateByteArray;

    @Autowired
    private RedisTemplate<String, String> oTemplateString;

    @Value("${redis.storageTimeMinutes}")
    private String nStorageTimeMinutes;

    @Override
    public String putBytes(byte[] aByte) throws RecordInmemoryException {
        String sKey = null;
        try {
            if(aByte == null){
                LOG.error("FAIL: aByte is null");
                throw new RecordInmemoryException(RecordInmemoryException.CODE_UNKNOWN_EXCEPTION, "[putBytes](aByte is null");
            }
            sKey = UUID.randomUUID().toString();
            LOG.info("sKey ={} sValueLength ={}", sKey, aByte.length);
            while (oTemplateByteArray.hasKey(sKey)) {
                sKey = UUID.randomUUID().toString();
            }
            oTemplateByteArray.boundValueOps(sKey).set(aByte);
            oTemplateByteArray.expire(sKey, Long.valueOf(nStorageTimeMinutes), TimeUnit.MINUTES);
        } catch (Exception oException) {
            LOG.error("FAIL: {} (sKey={},aByte={})", oException.getMessage(), sKey, Arrays.toString(aByte));
            LOG.trace("FAIL", oException);
            throw new RecordInmemoryException(RecordInmemoryException.CODE_UNKNOWN_EXCEPTION, "[putBytes](sKey=" + sKey + ",aByte=" + Arrays.toString(aByte) + "):" + oException.getMessage(), oException);
        }
        return sKey;

    }

    @Override
    public byte[] getBytes(String sKey) throws RecordInmemoryException {
        byte[] aByte;
        try {
            aByte = oTemplateByteArray.boundValueOps(sKey).get();
            if(aByte == null){
                LOG.error("FAIL: aByte is null sKey={}", sKey);
                throw new RecordInmemoryException(RecordInmemoryException.CODE_UNKNOWN_EXCEPTION, "[getBytes](aByte is null sKey=" + sKey + "):");
            }
        } catch (Exception oException) {
            LOG.error("FAIL: {} (sKey={})", oException.getMessage(), sKey);
            throw new RecordInmemoryException(RecordInmemoryException.CODE_UNKNOWN_EXCEPTION, "[getBytes](sKey=" + sKey + "):" + oException.getMessage(), oException);
        }
        return aByte;
    }

    @Override
    public String putString(String sKey, String sValue) throws RecordInmemoryException {
        try {
            if(sValue == null){
                LOG.error("FAIL: sValue is null");
                throw new RecordInmemoryException(RecordInmemoryException.CODE_UNKNOWN_EXCEPTION, "[putString](sValue is null");
            }
            LOG.info("sKey ={} sValueLength ={}", sKey, sValue.length());
            oTemplateString.boundValueOps(sKey).set(sValue);
            oTemplateString.expire(sKey, Long.valueOf(nStorageTimeMinutes), TimeUnit.MINUTES);
        } catch (Exception oException) {
            LOG.error("FAIL: {} (sKey={},sValue={})", oException.getMessage(), sKey, sValue);
            LOG.trace("FAIL", oException);
            throw new RecordInmemoryException(RecordInmemoryException.CODE_UNKNOWN_EXCEPTION, "[putString](sKey=" + sKey + ",sValue=" + sValue + "):" + oException.getMessage(), oException);
        }
        return sKey;
    }

    @Override
    public String getString(String sKey) throws RecordInmemoryException {
        String sReturn = null;
        try {
            sReturn = oTemplateString.boundValueOps(sKey).get();
            if(sReturn == null){
                LOG.error("FAIL: sReturn is null sKey={}", sKey);
                throw new RecordInmemoryException(RecordInmemoryException.CODE_UNKNOWN_EXCEPTION, "[getString](sReturn is null sKey=" + sKey + "):");
            }
        } catch (Exception oException) {
            LOG.error("FAIL: {} (sKey={})", oException.getMessage(), sKey);
            throw new RecordInmemoryException(RecordInmemoryException.CODE_UNKNOWN_EXCEPTION, "[getString](sKey=" + sKey + "):" + oException.getMessage(), oException);
        }
        return sReturn;

    }
}
