/**
 * DocumentOutData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class DocumentOutData  implements java.io.Serializable {
    private java.util.Calendar deliver;

    private int docCd;

    private java.lang.String docId;

    private org.igov.util.swind.DocumentType docType;

    private byte[] document;

    private java.util.Calendar income;

    private java.lang.String originalDocId;

    private int recipientDept;

    private java.lang.String recipientEDRPOU;

    private int senderDept;

    private java.lang.String senderEDRPOU;

    private int task;

    private byte[] ticket;

    public DocumentOutData() {
    }

    public DocumentOutData(
           java.util.Calendar deliver,
           int docCd,
           java.lang.String docId,
           org.igov.util.swind.DocumentType docType,
           byte[] document,
           java.util.Calendar income,
           java.lang.String originalDocId,
           int recipientDept,
           java.lang.String recipientEDRPOU,
           int senderDept,
           java.lang.String senderEDRPOU,
           int task,
           byte[] ticket) {
           this.deliver = deliver;
           this.docCd = docCd;
           this.docId = docId;
           this.docType = docType;
           this.document = document;
           this.income = income;
           this.originalDocId = originalDocId;
           this.recipientDept = recipientDept;
           this.recipientEDRPOU = recipientEDRPOU;
           this.senderDept = senderDept;
           this.senderEDRPOU = senderEDRPOU;
           this.task = task;
           this.ticket = ticket;
    }


    /**
     * Gets the deliver value for this DocumentOutData.
     * 
     * @return deliver
     */
    public java.util.Calendar getDeliver() {
        return deliver;
    }


    /**
     * Sets the deliver value for this DocumentOutData.
     * 
     * @param deliver
     */
    public void setDeliver(java.util.Calendar deliver) {
        this.deliver = deliver;
    }


    /**
     * Gets the docCd value for this DocumentOutData.
     * 
     * @return docCd
     */
    public int getDocCd() {
        return docCd;
    }


    /**
     * Sets the docCd value for this DocumentOutData.
     * 
     * @param docCd
     */
    public void setDocCd(int docCd) {
        this.docCd = docCd;
    }


    /**
     * Gets the docId value for this DocumentOutData.
     * 
     * @return docId
     */
    public java.lang.String getDocId() {
        return docId;
    }


    /**
     * Sets the docId value for this DocumentOutData.
     * 
     * @param docId
     */
    public void setDocId(java.lang.String docId) {
        this.docId = docId;
    }


    /**
     * Gets the docType value for this DocumentOutData.
     * 
     * @return docType
     */
    public org.igov.util.swind.DocumentType getDocType() {
        return docType;
    }


    /**
     * Sets the docType value for this DocumentOutData.
     * 
     * @param docType
     */
    public void setDocType(org.igov.util.swind.DocumentType docType) {
        this.docType = docType;
    }


    /**
     * Gets the document value for this DocumentOutData.
     * 
     * @return document
     */
    public byte[] getDocument() {
        return document;
    }


    /**
     * Sets the document value for this DocumentOutData.
     * 
     * @param document
     */
    public void setDocument(byte[] document) {
        this.document = document;
    }


    /**
     * Gets the income value for this DocumentOutData.
     * 
     * @return income
     */
    public java.util.Calendar getIncome() {
        return income;
    }


    /**
     * Sets the income value for this DocumentOutData.
     * 
     * @param income
     */
    public void setIncome(java.util.Calendar income) {
        this.income = income;
    }


    /**
     * Gets the originalDocId value for this DocumentOutData.
     * 
     * @return originalDocId
     */
    public java.lang.String getOriginalDocId() {
        return originalDocId;
    }


    /**
     * Sets the originalDocId value for this DocumentOutData.
     * 
     * @param originalDocId
     */
    public void setOriginalDocId(java.lang.String originalDocId) {
        this.originalDocId = originalDocId;
    }


    /**
     * Gets the recipientDept value for this DocumentOutData.
     * 
     * @return recipientDept
     */
    public int getRecipientDept() {
        return recipientDept;
    }


    /**
     * Sets the recipientDept value for this DocumentOutData.
     * 
     * @param recipientDept
     */
    public void setRecipientDept(int recipientDept) {
        this.recipientDept = recipientDept;
    }


    /**
     * Gets the recipientEDRPOU value for this DocumentOutData.
     * 
     * @return recipientEDRPOU
     */
    public java.lang.String getRecipientEDRPOU() {
        return recipientEDRPOU;
    }


    /**
     * Sets the recipientEDRPOU value for this DocumentOutData.
     * 
     * @param recipientEDRPOU
     */
    public void setRecipientEDRPOU(java.lang.String recipientEDRPOU) {
        this.recipientEDRPOU = recipientEDRPOU;
    }


    /**
     * Gets the senderDept value for this DocumentOutData.
     * 
     * @return senderDept
     */
    public int getSenderDept() {
        return senderDept;
    }


    /**
     * Sets the senderDept value for this DocumentOutData.
     * 
     * @param senderDept
     */
    public void setSenderDept(int senderDept) {
        this.senderDept = senderDept;
    }


    /**
     * Gets the senderEDRPOU value for this DocumentOutData.
     * 
     * @return senderEDRPOU
     */
    public java.lang.String getSenderEDRPOU() {
        return senderEDRPOU;
    }


    /**
     * Sets the senderEDRPOU value for this DocumentOutData.
     * 
     * @param senderEDRPOU
     */
    public void setSenderEDRPOU(java.lang.String senderEDRPOU) {
        this.senderEDRPOU = senderEDRPOU;
    }


    /**
     * Gets the task value for this DocumentOutData.
     * 
     * @return task
     */
    public int getTask() {
        return task;
    }


    /**
     * Sets the task value for this DocumentOutData.
     * 
     * @param task
     */
    public void setTask(int task) {
        this.task = task;
    }


    /**
     * Gets the ticket value for this DocumentOutData.
     * 
     * @return ticket
     */
    public byte[] getTicket() {
        return ticket;
    }


    /**
     * Sets the ticket value for this DocumentOutData.
     * 
     * @param ticket
     */
    public void setTicket(byte[] ticket) {
        this.ticket = ticket;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DocumentOutData)) return false;
        DocumentOutData other = (DocumentOutData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.deliver==null && other.getDeliver()==null) || 
             (this.deliver!=null &&
              this.deliver.equals(other.getDeliver()))) &&
            this.docCd == other.getDocCd() &&
            ((this.docId==null && other.getDocId()==null) || 
             (this.docId!=null &&
              this.docId.equals(other.getDocId()))) &&
            ((this.docType==null && other.getDocType()==null) || 
             (this.docType!=null &&
              this.docType.equals(other.getDocType()))) &&
            ((this.document==null && other.getDocument()==null) || 
             (this.document!=null &&
              java.util.Arrays.equals(this.document, other.getDocument()))) &&
            ((this.income==null && other.getIncome()==null) || 
             (this.income!=null &&
              this.income.equals(other.getIncome()))) &&
            ((this.originalDocId==null && other.getOriginalDocId()==null) || 
             (this.originalDocId!=null &&
              this.originalDocId.equals(other.getOriginalDocId()))) &&
            this.recipientDept == other.getRecipientDept() &&
            ((this.recipientEDRPOU==null && other.getRecipientEDRPOU()==null) || 
             (this.recipientEDRPOU!=null &&
              this.recipientEDRPOU.equals(other.getRecipientEDRPOU()))) &&
            this.senderDept == other.getSenderDept() &&
            ((this.senderEDRPOU==null && other.getSenderEDRPOU()==null) || 
             (this.senderEDRPOU!=null &&
              this.senderEDRPOU.equals(other.getSenderEDRPOU()))) &&
            this.task == other.getTask() &&
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
        if (getDeliver() != null) {
            _hashCode += getDeliver().hashCode();
        }
        _hashCode += getDocCd();
        if (getDocId() != null) {
            _hashCode += getDocId().hashCode();
        }
        if (getDocType() != null) {
            _hashCode += getDocType().hashCode();
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
        if (getIncome() != null) {
            _hashCode += getIncome().hashCode();
        }
        if (getOriginalDocId() != null) {
            _hashCode += getOriginalDocId().hashCode();
        }
        _hashCode += getRecipientDept();
        if (getRecipientEDRPOU() != null) {
            _hashCode += getRecipientEDRPOU().hashCode();
        }
        _hashCode += getSenderDept();
        if (getSenderEDRPOU() != null) {
            _hashCode += getSenderEDRPOU().hashCode();
        }
        _hashCode += getTask();
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
        new org.apache.axis.description.TypeDesc(DocumentOutData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://swined/", "DocumentOutData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deliver");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "deliver"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("document");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "document"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("income");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "income"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
        elemField.setFieldName("recipientDept");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "recipientDept"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recipientEDRPOU");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "recipientEDRPOU"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("senderDept");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "senderDept"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("senderEDRPOU");
        elemField.setXmlName(new javax.xml.namespace.QName("http://swined/", "senderEDRPOU"));
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
