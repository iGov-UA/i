package org.igov.model;

import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.igov.util.Util;
import org.igov.io.db.kv.statical.impl.BytesDataStorage;

import java.util.Arrays;

@Repository
public class AccessDataDaoImpl implements AccessDataDao {

    private static final String contentMock = "No content!!!";
    private final static Logger LOG = LoggerFactory.getLogger(AccessDataDaoImpl.class);

    @Autowired
    private IBytesDataStorage durableBytesDataStorage;

    @Override
    public String setAccessData(String sContent) {
        LOG.info("sContent=" + sContent);
        //String sKey=durableBytesDataStorage.saveData(Util.contentStringToByte(sContent));
        //String sKey=durableBytesDataStorage.saveData(sContent.getBytes());
        String sKey = durableBytesDataStorage.saveData(Util.aData(sContent));
        LOG.info("sKey=" + sKey);
        //log.info("sData(check)="+getAccessData(sKey));
        return sKey;
    }

    @Override
    public String setAccessData(byte[] aContent) {
        //log.info("sContent="+(aContent==null?"null":Util.contentByteToString(aContent)));
        LOG.info("sContent=" + (aContent == null ? "null" : Arrays.toString(aContent))
                + ",sByte(aContent)=" + Util.sData(aContent));
        String sKey = durableBytesDataStorage.saveData(aContent);
        LOG.info("sKey=" + sKey);
        return sKey;
    }

    @Override
    public String getAccessData(String sKey) {
        byte[] aContent = durableBytesDataStorage.getData(sKey);
        //return aContent != null ? Util.contentByteToString(aContent) : contentMock;
        String sData = contentMock;
        if (aContent != null) {
            //            log.info("[getAccessData]:sKey="+sKey+",aContent.length()="+aContent.length);
            //sData = Util.contentByteToString(aContent);
            //sData = Arrays.toString(aContent);
            sData = Util.sData(aContent);
            //log.info("[getAccessData]:TEST:sKey="+sKey+",Arrays.toString(aContent)="+Arrays.toString(aContent));
            /*if(sData!=null){
                log.info("[getAccessData]:sKey="+sKey+",sData.length()="+sData.length());
            }*/
        }
        LOG.info("sKey=" + sKey + ",sData=" + sData);
        return sData;
    }

    @Override
    public boolean removeAccessData(String sKey) {
        LOG.info("sKey=" + sKey);
        return durableBytesDataStorage.remove(sKey);
    }

}
