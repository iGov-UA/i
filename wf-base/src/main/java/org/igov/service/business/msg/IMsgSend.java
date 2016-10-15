package org.igov.service.business.msg;

//import java.util.HashMap;
import java.util.Map;

import com.pb.ksv.msgcore.data.IMsgObjR;
import com.pb.ksv.msgcore.data.enums.MsgLevel;

public interface IMsgSend {
//    public MsgSend addBusId(String sBusId);
    public IMsgSend _Head(String sHead);
    public IMsgSend _Body(String sBody);
    public IMsgSend _Error(String sError);
    public IMsgSend _SubjectID(Long nID_Subject);
    public IMsgSend _ServerID(Long nID_Server);   
    //public IMsgSend _Data(String smData);
    //public <T> MsgSend _Params(HashMap<String, T> mParam);
    public <T> IMsgSend _Params(Map<String, T> mParam);
    public IMsgSend _Level(MsgLevel msgLevel);
    public IMsgObjR save() throws Exception;
}
