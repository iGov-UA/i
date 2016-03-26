package org.igov.util.swind;

public class SWinEDSoapProxy implements org.igov.util.swind.SWinEDSoap {
  private String _endpoint = null;
  private org.igov.util.swind.SWinEDSoap sWinEDSoap = null;
  
  public SWinEDSoapProxy() {
    _initSWinEDSoapProxy();
  }
  
  public SWinEDSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initSWinEDSoapProxy();
  }
  
  private void _initSWinEDSoapProxy() {
    try {
      sWinEDSoap = (new org.igov.util.swind.SWinEDLocator()).getSWinEDSoap();
      if (sWinEDSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sWinEDSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sWinEDSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sWinEDSoap != null)
      ((javax.xml.rpc.Stub)sWinEDSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.igov.util.swind.SWinEDSoap getSWinEDSoap() {
    if (sWinEDSoap == null)
      _initSWinEDSoapProxy();
    return sWinEDSoap;
  }
  
  public void post(java.lang.String senderEDRPOU, int senderDept, org.igov.util.swind.DocumentType docsType, org.igov.util.swind.DocumentInData[] docs, org.igov.util.swind.holders.ProcessResultHolder postResult, javax.xml.rpc.holders.IntHolder errorDocIdx) throws java.rmi.RemoteException{
    if (sWinEDSoap == null)
      _initSWinEDSoapProxy();
    sWinEDSoap.post(senderEDRPOU, senderDept, docsType, docs, postResult, errorDocIdx);
  }
  
  public void receive(java.lang.String recipientEDRPOU, int recipientDept, boolean procAllDepts, java.lang.String caName, byte[] cert, org.igov.util.swind.holders.ProcessResultHolder receiveResult, javax.xml.rpc.holders.BooleanHolder restPresent, org.igov.util.swind.holders.ArrayOfDocumentOutDataHolder docs) throws java.rmi.RemoteException{
    if (sWinEDSoap == null)
      _initSWinEDSoapProxy();
    sWinEDSoap.receive(recipientEDRPOU, recipientDept, procAllDepts, caName, cert, receiveResult, restPresent, docs);
  }
  
  public void mark(org.igov.util.swind.ProcessedDocument[] docs, org.igov.util.swind.holders.ProcessResultHolder markResult, javax.xml.rpc.holders.IntHolder errorDocIdx) throws java.rmi.RemoteException{
    if (sWinEDSoap == null)
      _initSWinEDSoapProxy();
    sWinEDSoap.mark(docs, markResult, errorDocIdx);
  }
  
  public void list(java.lang.String recipientEDRPOU, int recipientDept, boolean procAllDepts, org.igov.util.swind.holders.ProcessResultHolder listResult, org.igov.util.swind.holders.ArrayOfDocumentIdHolder list) throws java.rmi.RemoteException{
    if (sWinEDSoap == null)
      _initSWinEDSoapProxy();
    sWinEDSoap.list(recipientEDRPOU, recipientDept, procAllDepts, listResult, list);
  }
  
  public void load(java.lang.String recipientEDRPOU, org.igov.util.swind.DocumentId[] list, java.lang.String caName, byte[] cert, org.igov.util.swind.holders.ProcessResultHolder loadResult, javax.xml.rpc.holders.BooleanHolder restPreset, org.igov.util.swind.holders.ArrayOfDocumentOutDataHolder docs) throws java.rmi.RemoteException{
    if (sWinEDSoap == null)
      _initSWinEDSoapProxy();
    sWinEDSoap.load(recipientEDRPOU, list, caName, cert, loadResult, restPreset, docs);
  }
  
  public void check(java.lang.String recipientEDRPOU, int recipientDept, boolean procAllDepts, org.igov.util.swind.holders.ProcessResultHolder checkResult, javax.xml.rpc.holders.IntHolder qtDocs) throws java.rmi.RemoteException{
    if (sWinEDSoap == null)
      _initSWinEDSoapProxy();
    sWinEDSoap.check(recipientEDRPOU, recipientDept, procAllDepts, checkResult, qtDocs);
  }
  
  public void checkAcquired(java.lang.String senderEDRPOU, org.igov.util.swind.DocumentIdAcq[] list, org.igov.util.swind.holders.ProcessResultHolder checkAcquiredResult, org.igov.util.swind.holders.ArrayOfBooleanHolder acquired) throws java.rmi.RemoteException{
    if (sWinEDSoap == null)
      _initSWinEDSoapProxy();
    sWinEDSoap.checkAcquired(senderEDRPOU, list, checkAcquiredResult, acquired);
  }
  
  public void getCertificate(java.lang.String caName, org.igov.util.swind.holders.ProcessResultHolder getCertificateResult, org.igov.util.swind.holders.ArrayOfCertificateHolder certs) throws java.rmi.RemoteException{
    if (sWinEDSoap == null)
      _initSWinEDSoapProxy();
    sWinEDSoap.getCertificate(caName, getCertificateResult, certs);
  }
  
  
}