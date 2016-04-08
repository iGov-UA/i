/**
 * DocumentId.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class DocumentId  implements java.io.Serializable {
    private int docCd;

    private org.igov.util.swind.DocumentType docType;

    public DocumentId() {
    }

    public DocumentId(
           int docCd,
           org.igov.util.swind.DocumentType docType) {
           this.docCd = docCd;
           this.docType = docType;
    }


    /**
     * Gets the docCd value for this DocumentId.
     * 
     * @return docCd
     */
    public int getDocCd() {
        return docCd;
    }


    /**
     * Sets the docCd value for this DocumentId.
     * 
     * @param docCd
     */
    public void setDocCd(int docCd) {
        this.docCd = docCd;
    }


    /**
     * Gets the docType value for this DocumentId.
     * 
     * @return docType
     */
    public org.igov.util.swind.DocumentType getDocType() {
        return docType;
    }


    /**
     * Sets the docType value for this DocumentId.
     * 
     * @param docType
     */
    public void setDocType(org.igov.util.swind.DocumentType docType) {
        this.docType = docType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DocumentId)) return false;
        DocumentId other = (DocumentId) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.docCd == other.getDocCd() &&
            ((this.docType==null && other.getDocType()==null) || 
             (this.docType!=null &&
              this.docType.equals(other.getDocType())));
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
        _hashCode += getDocCd();
        if (getDocType() != null) {
            _hashCode += getDocType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DocumentId.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://swined/", "DocumentId"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "docCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "docType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://swined/", "DocumentType"));
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
