package org.igov.util.swind;

import org.igov.util.swind.holders.*;

public class GateSoapProxy implements GateSoap {
  private String _endpoint = null;
  private GateSoap gateSoap = null;
  
  public GateSoapProxy() {
    _initGateSoapProxy();
  }
  
  public GateSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initGateSoapProxy();
  }
  
  private void _initGateSoapProxy() {
    try {
      gateSoap = (new GateLocator()).getGateSoap();
      if (gateSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)gateSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)gateSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (gateSoap != null)
      ((javax.xml.rpc.Stub)gateSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public GateSoap getGateSoap() {
    if (gateSoap == null)
      _initGateSoapProxy();
    return gateSoap;
  }
  
  public ProcessResult send(java.lang.String fileName, java.lang.String senderEMail, byte[] data) throws java.rmi.RemoteException{
    if (gateSoap == null)
      _initGateSoapProxy();
    return gateSoap.send(fileName, senderEMail, data);
  }
  
  public void getMessages(byte[] signedEDRPOU, ProcessResultHolder getMessagesResult, ArrayOfStringHolder messagesList) throws java.rmi.RemoteException{
    if (gateSoap == null)
      _initGateSoapProxy();
    gateSoap.getMessages(signedEDRPOU, getMessagesResult, messagesList);
  }
  
  public void getMessagesEx(byte[] signedEDRPOU, java.lang.String senderEmail, ProcessResultHolder getMessagesExResult, ArrayOfStringHolder messagesList) throws java.rmi.RemoteException{
    if (gateSoap == null)
      _initGateSoapProxy();
    gateSoap.getMessagesEx(signedEDRPOU, senderEmail, getMessagesExResult, messagesList);
  }
  
  public void receive(byte[] signedMsgId, ProcessResultHolder receiveResult, javax.xml.rpc.holders.StringHolder fileName, javax.xml.rpc.holders.ByteArrayHolder messageData) throws java.rmi.RemoteException{
    if (gateSoap == null)
      _initGateSoapProxy();
    gateSoap.receive(signedMsgId, receiveResult, fileName, messageData);
  }
  
  public void receiveAll(byte[] signedEmail, org.apache.axis.types.UnsignedByte needDelete, ProcessResultHolder receiveAllResult, ArrayOfMessageHolder messages, org.apache.axis.holders.UnsignedByteHolder complete) throws java.rmi.RemoteException{
    if (gateSoap == null)
      _initGateSoapProxy();
    gateSoap.receiveAll(signedEmail, needDelete, receiveAllResult, messages, complete);
  }
  
  public ProcessResult delete(byte[] signedMsgId) throws java.rmi.RemoteException{
    if (gateSoap == null)
      _initGateSoapProxy();
    return gateSoap.delete(signedMsgId);
  }
  
  
}