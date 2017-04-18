/**
 * IService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.bjust;

public interface IService extends java.rmi.Remote {
    public java.lang.String test(java.lang.String service_code) throws java.rmi.RemoteException;
    public java.lang.String getServiceURL(java.lang.String service_code, int application_id, java.lang.String session_id) throws java.rmi.RemoteException;
    public org.igov.bjust.ResultModel uploadSigns(org.igov.bjust.UploadSignsModel uploadSignsModel) throws java.rmi.RemoteException;
}
