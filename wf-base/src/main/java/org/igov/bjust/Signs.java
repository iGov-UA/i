/**
 * Signs.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.bjust;

public class Signs  implements java.io.Serializable {
    private java.lang.String file_name;

    private java.lang.String file_sign;

    private java.lang.String file_stamp;

    public Signs() {
    }

    public Signs(
           java.lang.String file_name,
           java.lang.String file_sign,
           java.lang.String file_stamp) {
           this.file_name = file_name;
           this.file_sign = file_sign;
           this.file_stamp = file_stamp;
    }


    /**
     * Gets the file_name value for this Signs.
     * 
     * @return file_name
     */
    public java.lang.String getFile_name() {
        return file_name;
    }


    /**
     * Sets the file_name value for this Signs.
     * 
     * @param file_name
     */
    public void setFile_name(java.lang.String file_name) {
        this.file_name = file_name;
    }


    /**
     * Gets the file_sign value for this Signs.
     * 
     * @return file_sign
     */
    public java.lang.String getFile_sign() {
        return file_sign;
    }


    /**
     * Sets the file_sign value for this Signs.
     * 
     * @param file_sign
     */
    public void setFile_sign(java.lang.String file_sign) {
        this.file_sign = file_sign;
    }


    /**
     * Gets the file_stamp value for this Signs.
     * 
     * @return file_stamp
     */
    public java.lang.String getFile_stamp() {
        return file_stamp;
    }


    /**
     * Sets the file_stamp value for this Signs.
     * 
     * @param file_stamp
     */
    public void setFile_stamp(java.lang.String file_stamp) {
        this.file_stamp = file_stamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Signs)) return false;
        Signs other = (Signs) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.file_name==null && other.getFile_name()==null) || 
             (this.file_name!=null &&
              this.file_name.equals(other.getFile_name()))) &&
            ((this.file_sign==null && other.getFile_sign()==null) || 
             (this.file_sign!=null &&
              this.file_sign.equals(other.getFile_sign()))) &&
            ((this.file_stamp==null && other.getFile_stamp()==null) || 
             (this.file_stamp!=null &&
              this.file_stamp.equals(other.getFile_stamp())));
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
        if (getFile_name() != null) {
            _hashCode += getFile_name().hashCode();
        }
        if (getFile_sign() != null) {
            _hashCode += getFile_sign().hashCode();
        }
        if (getFile_stamp() != null) {
            _hashCode += getFile_stamp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Signs.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("BJUST", "Signs"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("file_name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("file_sign");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file_sign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("file_stamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "file_stamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
