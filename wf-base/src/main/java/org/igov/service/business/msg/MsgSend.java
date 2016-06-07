package org.igov.service.business.msg;

import java.util.HashMap;

import com.pb.ksv.msgcore.data.IMsgObjR;
import com.pb.ksv.msgcore.data.enums.MsgLevel;

public interface MsgSend {
//    public MsgSend addBusId(String sBusId);
    public MsgSend addsHead(String sHead);
    public MsgSend addsBody(String sBody);
    public MsgSend addsError(String sError);
    public MsgSend addnID_Subject(Long nID_Subject);
    public MsgSend addnID_Server(Long nID_Server);   
    public MsgSend addsmData(String smData);
    public MsgSend addasParam(HashMap<String, Object> mParam);
    public MsgSend addMsgLevel(MsgLevel msgLevel);
    public IMsgObjR save() throws Exception;
}
