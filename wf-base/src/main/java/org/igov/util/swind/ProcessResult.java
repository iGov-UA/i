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

    public static final java.lang.String _GATE_OK = "GATE_OK";
    public static final java.lang.String _GATE_SEND_FAILED = "GATE_SEND_FAILED";
    public static final java.lang.String _GATE_EMPTY_FILENAME = "GATE_EMPTY_FILENAME";
    public static final java.lang.String _GATE_EMPTY_MESSAGE = "GATE_EMPTY_MESSAGE";
    public static final java.lang.String _GATE_FILENAME_TOOLONG = "GATE_FILENAME_TOOLONG";
    public static final java.lang.String _GATE_FILENAME_INVALID = "GATE_FILENAME_INVALID";
    public static final java.lang.String _GATE_PARSESIGN_FAILED = "GATE_PARSESIGN_FAILED";
    public static final java.lang.String _GATE_DB_INTERNAL = "GATE_DB_INTERNAL";
    public static final java.lang.String _GATE_MSGID_INVALID = "GATE_MSGID_INVALID";
    public static final java.lang.String _GATE_MSGID_ABSENT = "GATE_MSGID_ABSENT";
    public static final java.lang.String _GATE_EMPTY_EMAIL = "GATE_EMPTY_EMAIL";
    public static final java.lang.String _GATE_TEMPORARY_UNAVAIL = "GATE_TEMPORARY_UNAVAIL";
    public static final java.lang.String _GATE_INVALID_PARAMS = "GATE_INVALID_PARAMS";
    public static final ProcessResult GATE_OK = new ProcessResult(_GATE_OK);
    public static final ProcessResult GATE_SEND_FAILED = new ProcessResult(_GATE_SEND_FAILED);
    public static final ProcessResult GATE_EMPTY_FILENAME = new ProcessResult(_GATE_EMPTY_FILENAME);
    public static final ProcessResult GATE_EMPTY_MESSAGE = new ProcessResult(_GATE_EMPTY_MESSAGE);
    public static final ProcessResult GATE_FILENAME_TOOLONG = new ProcessResult(_GATE_FILENAME_TOOLONG);
    public static final ProcessResult GATE_FILENAME_INVALID = new ProcessResult(_GATE_FILENAME_INVALID);
    public static final ProcessResult GATE_PARSESIGN_FAILED = new ProcessResult(_GATE_PARSESIGN_FAILED);
    public static final ProcessResult GATE_DB_INTERNAL = new ProcessResult(_GATE_DB_INTERNAL);
    public static final ProcessResult GATE_MSGID_INVALID = new ProcessResult(_GATE_MSGID_INVALID);
    public static final ProcessResult GATE_MSGID_ABSENT = new ProcessResult(_GATE_MSGID_ABSENT);
    public static final ProcessResult GATE_EMPTY_EMAIL = new ProcessResult(_GATE_EMPTY_EMAIL);
    public static final ProcessResult GATE_TEMPORARY_UNAVAIL = new ProcessResult(_GATE_TEMPORARY_UNAVAIL);
    public static final ProcessResult GATE_INVALID_PARAMS = new ProcessResult(_GATE_INVALID_PARAMS);
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
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://govgate/", "ProcessResult"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
