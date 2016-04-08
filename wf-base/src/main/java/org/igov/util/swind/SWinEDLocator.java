/**
 * SWinEDLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class SWinEDLocator extends org.apache.axis.client.Service implements org.igov.util.swind.SWinED {

/**
 * <h1>Single Window of Electronic Documents - SWinED</h1>
 */

    public SWinEDLocator() {
    }


    public SWinEDLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SWinEDLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SWinEDSoap
    private java.lang.String SWinEDSoap_address = "http://109.237.89.107/SWinEd_Doc/SwinEd.asmx";

    public java.lang.String getSWinEDSoapAddress() {
        return SWinEDSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SWinEDSoapWSDDServiceName = "SWinEDSoap";

    public java.lang.String getSWinEDSoapWSDDServiceName() {
        return SWinEDSoapWSDDServiceName;
    }

    public void setSWinEDSoapWSDDServiceName(java.lang.String name) {
        SWinEDSoapWSDDServiceName = name;
    }

    public org.igov.util.swind.SWinEDSoap getSWinEDSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SWinEDSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSWinEDSoap(endpoint);
    }

    public org.igov.util.swind.SWinEDSoap getSWinEDSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.igov.util.swind.SWinEDSoapStub _stub = new org.igov.util.swind.SWinEDSoapStub(portAddress, this);
            _stub.setPortName(getSWinEDSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSWinEDSoapEndpointAddress(java.lang.String address) {
        SWinEDSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.igov.util.swind.SWinEDSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.igov.util.swind.SWinEDSoapStub _stub = new org.igov.util.swind.SWinEDSoapStub(new java.net.URL(SWinEDSoap_address), this);
                _stub.setPortName(getSWinEDSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SWinEDSoap".equals(inputPortName)) {
            return getSWinEDSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://swined/", "SWinED");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://swined/", "SWinEDSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SWinEDSoap".equals(portName)) {
            setSWinEDSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
