/**
 * ProcessResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class ProcessResult implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ProcessResult(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _SWINED_OK = "SWINED_OK";
    public static final java.lang.String _SWINED_TEMPORARY_UNAVAILABLE = "SWINED_TEMPORARY_UNAVAILABLE";
    public static final java.lang.String _SWINED_EDRPOU_INVALID = "SWINED_EDRPOU_INVALID";
    public static final java.lang.String _SWINED_EMPTY_DATA = "SWINED_EMPTY_DATA";
    public static final java.lang.String _SWINED_CRYPT_ERROR_UNSIGN = "SWINED_CRYPT_ERROR_UNSIGN";
    public static final java.lang.String _SWINED_CRYPT_ERROR_DECRYPT = "SWINED_CRYPT_ERROR_DECRYPT";
    public static final java.lang.String _SWINED_CRYPT_ERROR_INTERNAL = "SWINED_CRYPT_ERROR_INTERNAL";
    public static final java.lang.String _SWINED_SIGN_ABSENT = "SWINED_SIGN_ABSENT";
    public static final java.lang.String _SWINED_SIGN_EDRPOU_MISCOMPARE = "SWINED_SIGN_EDRPOU_MISCOMPARE";
    public static final java.lang.String _SWINED_DOC_EDRPOU_INVALID = "SWINED_DOC_EDRPOU_INVALID";
    public static final java.lang.String _SWINED_DOC_ID_INVALID = "SWINED_DOC_ID_INVALID";
    public static final java.lang.String _SWINED_DOC_EMPTY = "SWINED_DOC_EMPTY";
    public static final java.lang.String _SWINED_DOC_ALREADY_REGISTERED = "SWINED_DOC_ALREADY_REGISTERED";
    public static final java.lang.String _SWINED_ORIG_DOC_ID_INVALID = "SWINED_ORIG_DOC_ID_INVALID";
    public static final java.lang.String _SWINED_ORIG_DOC_ABSENT = "SWINED_ORIG_DOC_ABSENT";
    public static final java.lang.String _SWINED_DOC_TYPE_INVALID = "SWINED_DOC_TYPE_INVALID";
    public static final java.lang.String _SWINED_DOC_TOO_BIG = "SWINED_DOC_TOO_BIG";
    public static final java.lang.String _SWINED_TICKET_INVALID_SIGNS_SEQUENCE = "SWINED_TICKET_INVALID_SIGNS_SEQUENCE";
    public static final java.lang.String _SWINED_TICKET_INVALID_CONTENT = "SWINED_TICKET_INVALID_CONTENT";
    public static final java.lang.String _SWINED_TICKET_DOC_ABSENT = "SWINED_TICKET_DOC_ABSENT";
    public static final java.lang.String _SWINED_CRYPTOLIB_ABSENT = "SWINED_CRYPTOLIB_ABSENT";
    public static final java.lang.String _SWINED_CERTIFICATE_INVALID = "SWINED_CERTIFICATE_INVALID";
    public static final ProcessResult SWINED_OK = new ProcessResult(_SWINED_OK);
    public static final ProcessResult SWINED_TEMPORARY_UNAVAILABLE = new ProcessResult(_SWINED_TEMPORARY_UNAVAILABLE);
    public static final ProcessResult SWINED_EDRPOU_INVALID = new ProcessResult(_SWINED_EDRPOU_INVALID);
    public static final ProcessResult SWINED_EMPTY_DATA = new ProcessResult(_SWINED_EMPTY_DATA);
    public static final ProcessResult SWINED_CRYPT_ERROR_UNSIGN = new ProcessResult(_SWINED_CRYPT_ERROR_UNSIGN);
    public static final ProcessResult SWINED_CRYPT_ERROR_DECRYPT = new ProcessResult(_SWINED_CRYPT_ERROR_DECRYPT);
    public static final ProcessResult SWINED_CRYPT_ERROR_INTERNAL = new ProcessResult(_SWINED_CRYPT_ERROR_INTERNAL);
    public static final ProcessResult SWINED_SIGN_ABSENT = new ProcessResult(_SWINED_SIGN_ABSENT);
    public static final ProcessResult SWINED_SIGN_EDRPOU_MISCOMPARE = new ProcessResult(_SWINED_SIGN_EDRPOU_MISCOMPARE);
    public static final ProcessResult SWINED_DOC_EDRPOU_INVALID = new ProcessResult(_SWINED_DOC_EDRPOU_INVALID);
    public static final ProcessResult SWINED_DOC_ID_INVALID = new ProcessResult(_SWINED_DOC_ID_INVALID);
    public static final ProcessResult SWINED_DOC_EMPTY = new ProcessResult(_SWINED_DOC_EMPTY);
    public static final ProcessResult SWINED_DOC_ALREADY_REGISTERED = new ProcessResult(_SWINED_DOC_ALREADY_REGISTERED);
    public static final ProcessResult SWINED_ORIG_DOC_ID_INVALID = new ProcessResult(_SWINED_ORIG_DOC_ID_INVALID);
    public static final ProcessResult SWINED_ORIG_DOC_ABSENT = new ProcessResult(_SWINED_ORIG_DOC_ABSENT);
    public static final ProcessResult SWINED_DOC_TYPE_INVALID = new ProcessResult(_SWINED_DOC_TYPE_INVALID);
    public static final ProcessResult SWINED_DOC_TOO_BIG = new ProcessResult(_SWINED_DOC_TOO_BIG);
    public static final ProcessResult SWINED_TICKET_INVALID_SIGNS_SEQUENCE = new ProcessResult(_SWINED_TICKET_INVALID_SIGNS_SEQUENCE);
    public static final ProcessResult SWINED_TICKET_INVALID_CONTENT = new ProcessResult(_SWINED_TICKET_INVALID_CONTENT);
    public static final ProcessResult SWINED_TICKET_DOC_ABSENT = new ProcessResult(_SWINED_TICKET_DOC_ABSENT);
    public static final ProcessResult SWINED_CRYPTOLIB_ABSENT = new ProcessResult(_SWINED_CRYPTOLIB_ABSENT);
    public static final ProcessResult SWINED_CERTIFICATE_INVALID = new ProcessResult(_SWINED_CERTIFICATE_INVALID);
    public java.lang.String getValue() { return _value_;}
    public static ProcessResult fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        ProcessResult enumeration = (ProcessResult)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static ProcessResult fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProcessResult.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://swined/", "ProcessResult"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
