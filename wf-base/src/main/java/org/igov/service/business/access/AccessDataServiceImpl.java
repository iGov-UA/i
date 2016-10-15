package org.igov.service.business.access;

import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.igov.util.Tool;

import java.util.Arrays;
import org.springframework.stereotype.Service;

@Service
public class AccessDataServiceImpl implements AccessDataService {

    private static final String contentMock = "No content!!!";
    private final static Logger LOG = LoggerFactory.getLogger(AccessDataServiceImpl.class);

    @Autowired
    private IBytesDataStorage durableBytesDataStorage;

    @Override
    public String setAccessData(String sContent) {
        LOG.info("(sContent={})", sContent);
        //String sKey=durableBytesDataStorage.saveData(Tool.contentStringToByte(sContent));
        //String sKey=durableBytesDataStorage.saveData(sContent.getBytes());
        String sKey = durableBytesDataStorage.saveData(Tool.aData(sContent));
        LOG.info("(sKey={})", sKey);
        //log.info("(sData(check)={})", getAccessData(sKey));
        return sKey;
    }

    @Override
    public String setAccessData(byte[] aContent) {
        //log.info("sContent={}", (aContent==null?"null":Tool.contentByteToString(aContent)));
        LOG.info("(sContent={}, sByte(aContent)={})",
                (aContent == null ? "null" : Arrays.toString(aContent)), Tool.sData(aContent));
        String sKey = durableBytesDataStorage.saveData(aContent);
        LOG.info("(sKey={})", sKey);
        return sKey;
    }

    @Override
    public String getAccessData(String sKey) {
        byte[] aContent = durableBytesDataStorage.getData(sKey);
        //return aContent != null ? Tool.contentByteToString(aContent) : contentMock;
        String sData = contentMock;
        if (aContent != null) {
            //log.info("[getAccessData]:(sKey={},aContent.length()={})",sKey, aContent.length);
            //sData = Tool.contentByteToString(aContent);
            //sData = Arrays.toString(aContent);
            sData = Tool.sData(aContent);
            //log.info("[getAccessData]:TEST:(sKey={},Arrays.toString(aContent)={})",sKey, Arrays.toString(aContent));
            /*if(sData!=null){
                log.info("[getAccessData]:(sKey={},sData.length()={})",sKey, sData.length());
            }*/
        }
        LOG.info("(sKey={}, sData={})", sKey, sData);
        return sData;
    }

    @Override
    public boolean removeAccessData(String sKey) {
        LOG.info("(sKey={})", sKey);
        return durableBytesDataStorage.remove(sKey);
    }

}
