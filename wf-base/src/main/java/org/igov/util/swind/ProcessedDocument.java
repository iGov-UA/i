/**
 * ProcessedDocument.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class ProcessedDocument  implements java.io.Serializable {
    private int docCd;

    private java.lang.String docId;

    private org.igov.util.swind.DocumentType docType;

    private byte[] ticket;

    public ProcessedDocument() {
    }

    public ProcessedDocument(
           int docCd,
           java.lang.String docId,
           org.igov.util.swind.DocumentType docType,
           byte[] ticket) {
           this.docCd = docCd;
           this.docId = docId;
           this.docType = docType;
           this.ticket = ticket;
    }


    /**
     * Gets the docCd value for this ProcessedDocument.
     * 
     * @return docCd
     */
    public int getDocCd() {
        return docCd;
    }


    /**
     * Sets the docCd value for this ProcessedDocument.
     * 
     * @param docCd
     */
    public void setDocCd(int docCd) {
        this.docCd = docCd;
    }


    /**
     * Gets the docId value for this ProcessedDocument.
     * 
     * @return docId
     */
    public java.lang.String getDocId() {
        return docId;
    }


    /**
     * Sets the docId value for this ProcessedDocument.
     * 
     * @param docId
     */
    public void setDocId(java.lang.String docId) {
        this.docId = docId;
    }


    /**
     * Gets the docType value for this ProcessedDocument.
     * 
     * @return docType
     */
    public org.igov.util.swind.DocumentType getDocType() {
        return docType;
    }


    /**
     * Sets the docType value for this ProcessedDocument.
     * 
     * @param docType
     */
    public void setDocType(org.igov.util.swind.DocumentType docType) {
        this.docType = docType;
    }


    /**
     * Gets the ticket value for this ProcessedDocument.
     * 
     * @return ticket
     */
    public byte[] getTicket() {
        return ticket;
    }


    /**
     * Sets the ticket value for this ProcessedDocument.
     * 
     * @param ticket
     */
    public void setTicket(byte[] ticket) {
        this.ticket = ticket;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProcessedDocument)) return false;
        ProcessedDocument other = (ProcessedDocument) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.docCd == other.getDocCd() &&
            ((this.docId==null && other.getDocId()==null) || 
             (this.docId!=null &&
              this.docId.equals(other.getDocId()))) &&
            ((this.docType==null && other.getDocType()==null) || 
             (this.docType!=null &&
              this.docType.equals(other.getDocType()))) &&
            ((this.ticket==null && other.getTicket()==null) || 
             (this.ticket!=null &&
              java.util.Arrays.equals(this.ticket, other.getTicket())));
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
        if (getDocId() != null) {
            _hashCode += getDocId().hashCode();
        }
        if (getDocType() != null) {
            _hashCode += getDocType().hashCode();
        }
        if (getTicket() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTicket());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTicket(), i);
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
        new org.apache.axis.description.TypeDesc(ProcessedDocument.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://swined/", "ProcessedDocument"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "docCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "docId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "docType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://swined/", "DocumentType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ticket");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "ticket"));
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
