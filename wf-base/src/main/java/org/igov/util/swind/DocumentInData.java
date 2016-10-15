/**
 * DocumentInData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class DocumentInData  implements java.io.Serializable {
    private java.lang.String EDRPOU;

    private int dept;

    private java.lang.String docId;

    private byte[] document;

    private java.lang.String originalDocId;

    private int task;

    public DocumentInData() {
    }

    public DocumentInData(
           java.lang.String EDRPOU,
           int dept,
           java.lang.String docId,
           byte[] document,
           java.lang.String originalDocId,
           int task) {
           this.EDRPOU = EDRPOU;
           this.dept = dept;
           this.docId = docId;
           this.document = document;
           this.originalDocId = originalDocId;
           this.task = task;
    }


    /**
     * Gets the EDRPOU value for this DocumentInData.
     * 
     * @return EDRPOU
     */
    public java.lang.String getEDRPOU() {
        return EDRPOU;
    }


    /**
     * Sets the EDRPOU value for this DocumentInData.
     * 
     * @param EDRPOU
     */
    public void setEDRPOU(java.lang.String EDRPOU) {
        this.EDRPOU = EDRPOU;
    }


    /**
     * Gets the dept value for this DocumentInData.
     * 
     * @return dept
     */
    public int getDept() {
        return dept;
    }


    /**
     * Sets the dept value for this DocumentInData.
     * 
     * @param dept
     */
    public void setDept(int dept) {
        this.dept = dept;
    }


    /**
     * Gets the docId value for this DocumentInData.
     * 
     * @return docId
     */
    public java.lang.String getDocId() {
        return docId;
    }


    /**
     * Sets the docId value for this DocumentInData.
     * 
     * @param docId
     */
    public void setDocId(java.lang.String docId) {
        this.docId = docId;
    }


    /**
     * Gets the document value for this DocumentInData.
     * 
     * @return document
     */
    public byte[] getDocument() {
        return document;
    }


    /**
     * Sets the document value for this DocumentInData.
     * 
     * @param document
     */
    public void setDocument(byte[] document) {
        this.document = document;
    }


    /**
     * Gets the originalDocId value for this DocumentInData.
     * 
     * @return originalDocId
     */
    public java.lang.String getOriginalDocId() {
        return originalDocId;
    }


    /**
     * Sets the originalDocId value for this DocumentInData.
     * 
     * @param originalDocId
     */
    public void setOriginalDocId(java.lang.String originalDocId) {
        this.originalDocId = originalDocId;
    }


    /**
     * Gets the task value for this DocumentInData.
     * 
     * @return task
     */
    public int getTask() {
        return task;
    }


    /**
     * Sets the task value for this DocumentInData.
     * 
     * @param task
     */
    public void setTask(int task) {
        this.task = task;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DocumentInData)) return false;
        DocumentInData other = (DocumentInData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.EDRPOU==null && other.getEDRPOU()==null) || 
             (this.EDRPOU!=null &&
              this.EDRPOU.equals(other.getEDRPOU()))) &&
            this.dept == other.getDept() &&
            ((this.docId==null && other.getDocId()==null) || 
             (this.docId!=null &&
              this.docId.equals(other.getDocId()))) &&
            ((this.document==null && other.getDocument()==null) || 
             (this.document!=null &&
              java.util.Arrays.equals(this.document, other.getDocument()))) &&
            ((this.originalDocId==null && other.getOriginalDocId()==null) || 
             (this.originalDocId!=null &&
              this.originalDocId.equals(other.getOriginalDocId()))) &&
            this.task == other.getTask();
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
        if (getEDRPOU() != null) {
            _hashCode += getEDRPOU().hashCode();
        }
        _hashCode += getDept();
        if (getDocId() != null) {
            _hashCode += getDocId().hashCode();
        }
        if (getDocument() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDocument());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDocument(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOriginalDocId() != null) {
            _hashCode += getOriginalDocId().hashCode();
        }
        _hashCode += getTask();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DocumentInData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://swined/", "DocumentInData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EDRPOU");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "EDRPOU"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dept");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "dept"));
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
        elemField.setFieldName("document");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "document"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("originalDocId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "originalDocId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("task");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "task"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
