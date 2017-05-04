/**
 * Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.bjust;

public interface Service extends javax.xml.rpc.Service {
    public java.lang.String getBinding_IServiceAddress();

    public org.igov.bjust.IService getBinding_IService() throws javax.xml.rpc.ServiceException;

    public org.igov.bjust.IService getBinding_IService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
