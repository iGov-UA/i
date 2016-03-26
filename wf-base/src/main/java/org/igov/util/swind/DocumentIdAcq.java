/**
 * DocumentIdAcq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class DocumentIdAcq  implements java.io.Serializable {
    private java.lang.String docId;

    private org.igov.util.swind.DocumentType docType;

    private java.lang.String originalDocId;

    public DocumentIdAcq() {
    }

    public DocumentIdAcq(
           java.lang.String docId,
           org.igov.util.swind.DocumentType docType,
           java.lang.String originalDocId) {
           this.docId = docId;
           this.docType = docType;
           this.originalDocId = originalDocId;
    }


    /**
     * Gets the docId value for this DocumentIdAcq.
     * 
     * @return docId
     */
    public java.lang.String getDocId() {
        return docId;
    }


    /**
     * Sets the docId value for this DocumentIdAcq.
     * 
     * @param docId
     */
    public void setDocId(java.lang.String docId) {
        this.docId = docId;
    }


    /**
     * Gets the docType value for this DocumentIdAcq.
     * 
     * @return docType
     */
    public org.igov.util.swind.DocumentType getDocType() {
        return docType;
    }


    /**
     * Sets the docType value for this DocumentIdAcq.
     * 
     * @param docType
     */
    public void setDocType(org.igov.util.swind.DocumentType docType) {
        this.docType = docType;
    }


    /**
     * Gets the originalDocId value for this DocumentIdAcq.
     * 
     * @return originalDocId
     */
    public java.lang.String getOriginalDocId() {
        return originalDocId;
    }


    /**
     * Sets the originalDocId value for this DocumentIdAcq.
     * 
     * @param originalDocId
     */
    public void setOriginalDocId(java.lang.String originalDocId) {
        this.originalDocId = originalDocId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DocumentIdAcq)) return false;
        DocumentIdAcq other = (DocumentIdAcq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.docId==null && other.getDocId()==null) || 
             (this.docId!=null &&
              this.docId.equals(other.getDocId()))) &&
            ((this.docType==null && other.getDocType()==null) || 
             (this.docType!=null &&
              this.docType.equals(other.getDocType()))) &&
            ((this.originalDocId==null && other.getOriginalDocId()==null) || 
             (this.originalDocId!=null &&
              this.originalDocId.equals(other.getOriginalDocId())));
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
        if (getDocId() != null) {
            _hashCode += getDocId().hashCode();
        }
        if (getDocType() != null) {
            _hashCode += getDocType().hashCode();
        }
        if (getOriginalDocId() != null) {
            _hashCode += getOriginalDocId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DocumentIdAcq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://swined/", "DocumentIdAcq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("originalDocId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "originalDocId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
