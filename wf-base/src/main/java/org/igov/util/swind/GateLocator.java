/**
 * GateLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class GateLocator extends org.apache.axis.client.Service implements org.igov.util.swind.Gate {

/**
 * <h1>WEB-сервіс обміну документами та квитанціями з приймальним
 * шлюзом</h1>
 * <span>
 * <h3>Сценарій подання звітності</h3>
 * 1. Відправити документ на шлюз, використовуючи метод <b>Send</b><br
 * />
 * 2. Зачекати певний час (3-5 секунд)<br />
 * 3. Отримати список кодів повідомлень, використовуючи метод <b>GetMessages</b>
 * або <b>GetMessagesEx</b><br />
 * 4. Якщо список порожній, зачекати певний час (30-60 секунд) і перейти
 * до шагу 3<br />
 * 5. Отримати повідомлення, використовуючи метод <b>Receive</b><br />
 * 6. Вилучити отримане повідомлення, використовуючи метод <b>Delete</b><br
 * />
 * 7. Якщо повідомлення не останнє в списку, перейти до шагу 5 для наступного
 * повідомлення<br />
 * 8. Якщо отримано не всі очікувані квитанції, перейти до шагу 3
 * <h3>Примітка</h3>
 * Рекомендується періодично (1-2 рази на добу) виконувати перевірку
 * наявності повідомлень
 * для отримання документів, що відправлено за ініціативою шлюзу.
 * </span>
 * <span>
 * <h3>Коди повернення функцій</h3>
 * GATE_OK (0) - Успішно<br />
 * GATE_SEND_FAILED (1) - Помилка збереження вхідного повідомлення<br
 * />
 * GATE_EMPTY_FILENAME (2) - Не визначено ім'я файлу<br />
 * GATE_EMPTY_MESSAGE (3) - Блок документу не визначено<br />
 * GATE_FILENAME_TOOLONG (4) - Некоректне ім'я файлу<br />
 * GATE_FILENAME_INVALID (5) - Недопустимі символи в імені файлу<br />
 * GATE_PARSESIGN_FAILED (6) - Помилка перевірки підпису<br />
 * GATE_DB_INTERNAL (7) - Помилка роботи з базою повідомлень<br />
 * GATE_MSGID_INVALID (8) - Некоректний код повідомлення<br />
 * GATE_MSGID_ABSENT (9) - Відсутнє запитане повідомлення<br />
 * GATE_EMPTY_EMAIL (10) - Не визначено адресу електронної пошти<br />
 * GATE_TEMPORARY_UNAVAIL (11) - Сервіс тимчасово недоступний<br />
 * </span>
 */

    public GateLocator() {
    }


    public GateLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GateLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GateSoap
    private java.lang.String GateSoap_address = "http://obmen.sfs.gov.ua:1220/gate.asmx";

    public java.lang.String getGateSoapAddress() {
        return GateSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GateSoapWSDDServiceName = "GateSoap";

    public java.lang.String getGateSoapWSDDServiceName() {
        return GateSoapWSDDServiceName;
    }

    public void setGateSoapWSDDServiceName(java.lang.String name) {
        GateSoapWSDDServiceName = name;
    }

    public org.igov.util.swind.GateSoap getGateSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GateSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGateSoap(endpoint);
    }

    public org.igov.util.swind.GateSoap getGateSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.igov.util.swind.GateSoapStub _stub = new org.igov.util.swind.GateSoapStub(portAddress, this);
            _stub.setPortName(getGateSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGateSoapEndpointAddress(java.lang.String address) {
        GateSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.igov.util.swind.GateSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.igov.util.swind.GateSoapStub _stub = new org.igov.util.swind.GateSoapStub(new java.net.URL(GateSoap_address), this);
                _stub.setPortName(getGateSoapWSDDServiceName());
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
        if ("GateSoap".equals(inputPortName)) {
            return getGateSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://govgate/", "Gate");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://govgate/", "GateSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GateSoap".equals(portName)) {
            setGateSoapEndpointAddress(address);
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
