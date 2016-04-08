/**
 * Certificate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class Certificate  implements java.io.Serializable {
    private java.lang.String caName;

    private byte[] cert;

    public Certificate() {
    }

    public Certificate(
           java.lang.String caName,
           byte[] cert) {
           this.caName = caName;
           this.cert = cert;
    }


    /**
     * Gets the caName value for this Certificate.
     * 
     * @return caName
     */
    public java.lang.String getCaName() {
        return caName;
    }


    /**
     * Sets the caName value for this Certificate.
     * 
     * @param caName
     */
    public void setCaName(java.lang.String caName) {
        this.caName = caName;
    }


    /**
     * Gets the cert value for this Certificate.
     * 
     * @return cert
     */
    public byte[] getCert() {
        return cert;
    }


    /**
     * Sets the cert value for this Certificate.
     * 
     * @param cert
     */
    public void setCert(byte[] cert) {
        this.cert = cert;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Certificate)) return false;
        Certificate other = (Certificate) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.caName==null && other.getCaName()==null) || 
             (this.caName!=null &&
              this.caName.equals(other.getCaName()))) &&
            ((this.cert==null && other.getCert()==null) || 
             (this.cert!=null &&
              java.util.Arrays.equals(this.cert, other.getCert())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCaName() != null) {
            _hashCode += getCaName().hashCode();
        }
        if (getCert() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCert());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCert(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Certificate.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://swined/", "Certificate"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("caName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "caName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cert");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "cert"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
