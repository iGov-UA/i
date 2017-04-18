package org.igov.bjust;

public class IServiceProxy implements org.igov.bjust.IService {
  private String _endpoint = null;
  private org.igov.bjust.IService iService = null;
  
  public IServiceProxy() {
    _initIServiceProxy();
  }
  
  public IServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initIServiceProxy();
  }
  
  private void _initIServiceProxy() {
    try {
      iService = (new org.igov.bjust.ServiceLocator()).getBinding_IService();
      if (iService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iService != null)
      ((javax.xml.rpc.Stub)iService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.igov.bjust.IService getIService() {
    if (iService == null)
      _initIServiceProxy();
    return iService;
  }
  
  public java.lang.String test(java.lang.String service_code) throws java.rmi.RemoteException{
    if (iService == null)
      _initIServiceProxy();
    return iService.test(service_code);
  }
  
  public java.lang.String getServiceURL(java.lang.String service_code, int application_id, java.lang.String session_id) throws java.rmi.RemoteException{
    if (iService == null)
      _initIServiceProxy();
    return iService.getServiceURL(service_code, application_id, session_id);
  }
  
  public org.igov.bjust.ResultModel uploadSigns(org.igov.bjust.UploadSignsModel uploadSignsModel) throws java.rmi.RemoteException{
    if (iService == null)
      _initIServiceProxy();
    return iService.uploadSigns(uploadSignsModel);
  }
  
  
}