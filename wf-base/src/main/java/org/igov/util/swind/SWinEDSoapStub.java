/**
 * SWinEDSoapStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public class SWinEDSoapStub extends org.apache.axis.client.Stub implements org.igov.util.swind.SWinEDSoap {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[8];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Post");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "senderEDRPOU"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "senderDept"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "docsType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://swined/", "DocumentType"), org.igov.util.swind.DocumentType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "docs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentInData"), org.igov.util.swind.DocumentInData[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://swined/", "DocumentInData"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "PostResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ProcessResult"), org.igov.util.swind.ProcessResult.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "errorDocIdx"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Receive");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "recipientEDRPOU"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "recipientDept"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "procAllDepts"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "caName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "cert"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), byte[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "ReceiveResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ProcessResult"), org.igov.util.swind.ProcessResult.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "restPresent"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "docs"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentOutData"), org.igov.util.swind.DocumentOutData[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://swined/", "DocumentOutData"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Mark");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "docs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://swined/", "ArrayOfProcessedDocument"), org.igov.util.swind.ProcessedDocument[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://swined/", "ProcessedDocument"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "MarkResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ProcessResult"), org.igov.util.swind.ProcessResult.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "errorDocIdx"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("List");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "recipientEDRPOU"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "recipientDept"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "procAllDepts"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "ListResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ProcessResult"), org.igov.util.swind.ProcessResult.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "list"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentId"), org.igov.util.swind.DocumentId[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://swined/", "DocumentId"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Load");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "recipientEDRPOU"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "list"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentId"), org.igov.util.swind.DocumentId[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://swined/", "DocumentId"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "caName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "cert"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), byte[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "LoadResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ProcessResult"), org.igov.util.swind.ProcessResult.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "restPreset"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "docs"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentOutData"), org.igov.util.swind.DocumentOutData[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://swined/", "DocumentOutData"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Check");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "recipientEDRPOU"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "recipientDept"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "procAllDepts"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "CheckResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ProcessResult"), org.igov.util.swind.ProcessResult.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "qtDocs"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CheckAcquired");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "senderEDRPOU"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "list"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentIdAcq"), org.igov.util.swind.DocumentIdAcq[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://swined/", "DocumentIdAcq"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "CheckAcquiredResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ProcessResult"), org.igov.util.swind.ProcessResult.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "acquired"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ArrayOfBoolean"), boolean[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://swined/", "boolean"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetCertificate");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "caName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "GetCertificateResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ProcessResult"), org.igov.util.swind.ProcessResult.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://swined/", "certs"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://swined/", "ArrayOfCertificate"), org.igov.util.swind.Certificate[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://swined/", "Certificate"));
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

    }

    public SWinEDSoapStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public SWinEDSoapStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public SWinEDSoapStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://swined/", "ArrayOfBoolean");
            cachedSerQNames.add(qName);
            cls = boolean[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean");
            qName2 = new javax.xml.namespace.QName("http://swined/", "boolean");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://swined/", "ArrayOfCertificate");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.Certificate[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://swined/", "Certificate");
            qName2 = new javax.xml.namespace.QName("http://swined/", "Certificate");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentId");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.DocumentId[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://swined/", "DocumentId");
            qName2 = new javax.xml.namespace.QName("http://swined/", "DocumentId");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentIdAcq");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.DocumentIdAcq[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://swined/", "DocumentIdAcq");
            qName2 = new javax.xml.namespace.QName("http://swined/", "DocumentIdAcq");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentInData");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.DocumentInData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://swined/", "DocumentInData");
            qName2 = new javax.xml.namespace.QName("http://swined/", "DocumentInData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://swined/", "ArrayOfDocumentOutData");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.DocumentOutData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://swined/", "DocumentOutData");
            qName2 = new javax.xml.namespace.QName("http://swined/", "DocumentOutData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://swined/", "ArrayOfProcessedDocument");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.ProcessedDocument[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://swined/", "ProcessedDocument");
            qName2 = new javax.xml.namespace.QName("http://swined/", "ProcessedDocument");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://swined/", "Certificate");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.Certificate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://swined/", "DocumentId");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.DocumentId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://swined/", "DocumentIdAcq");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.DocumentIdAcq.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://swined/", "DocumentInData");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.DocumentInData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://swined/", "DocumentOutData");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.DocumentOutData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://swined/", "DocumentType");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.DocumentType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://swined/", "ProcessedDocument");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.ProcessedDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://swined/", "ProcessResult");
            cachedSerQNames.add(qName);
            cls = org.igov.util.swind.ProcessResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public void post(java.lang.String senderEDRPOU, int senderDept, org.igov.util.swind.DocumentType docsType, org.igov.util.swind.DocumentInData[] docs, org.igov.util.swind.holders.ProcessResultHolder postResult, javax.xml.rpc.holders.IntHolder errorDocIdx) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://swined/Post");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://swined/", "Post"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {senderEDRPOU, new java.lang.Integer(senderDept), docsType, docs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                postResult.value = (org.igov.util.swind.ProcessResult) _output.get(new javax.xml.namespace.QName("http://swined/", "PostResult"));
            } catch (java.lang.Exception _exception) {
                postResult.value = (org.igov.util.swind.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "PostResult")), org.igov.util.swind.ProcessResult.class);
            }
            try {
                errorDocIdx.value = ((java.lang.Integer) _output.get(new javax.xml.namespace.QName("http://swined/", "errorDocIdx"))).intValue();
            } catch (java.lang.Exception _exception) {
                errorDocIdx.value = ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "errorDocIdx")), int.class)).intValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void receive(java.lang.String recipientEDRPOU, int recipientDept, boolean procAllDepts, java.lang.String caName, byte[] cert, org.igov.util.swind.holders.ProcessResultHolder receiveResult, javax.xml.rpc.holders.BooleanHolder restPresent, org.igov.util.swind.holders.ArrayOfDocumentOutDataHolder docs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://swined/Receive");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://swined/", "Receive"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {recipientEDRPOU, new java.lang.Integer(recipientDept), new java.lang.Boolean(procAllDepts), caName, cert});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                receiveResult.value = (org.igov.util.swind.ProcessResult) _output.get(new javax.xml.namespace.QName("http://swined/", "ReceiveResult"));
            } catch (java.lang.Exception _exception) {
                receiveResult.value = (org.igov.util.swind.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "ReceiveResult")), org.igov.util.swind.ProcessResult.class);
            }
            try {
                restPresent.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("http://swined/", "restPresent"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                restPresent.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "restPresent")), boolean.class)).booleanValue();
            }
            try {
                docs.value = (org.igov.util.swind.DocumentOutData[]) _output.get(new javax.xml.namespace.QName("http://swined/", "docs"));
            } catch (java.lang.Exception _exception) {
                docs.value = (org.igov.util.swind.DocumentOutData[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "docs")), org.igov.util.swind.DocumentOutData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void mark(org.igov.util.swind.ProcessedDocument[] docs, org.igov.util.swind.holders.ProcessResultHolder markResult, javax.xml.rpc.holders.IntHolder errorDocIdx) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://swined/Mark");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://swined/", "Mark"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {docs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                markResult.value = (org.igov.util.swind.ProcessResult) _output.get(new javax.xml.namespace.QName("http://swined/", "MarkResult"));
            } catch (java.lang.Exception _exception) {
                markResult.value = (org.igov.util.swind.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "MarkResult")), org.igov.util.swind.ProcessResult.class);
            }
            try {
                errorDocIdx.value = ((java.lang.Integer) _output.get(new javax.xml.namespace.QName("http://swined/", "errorDocIdx"))).intValue();
            } catch (java.lang.Exception _exception) {
                errorDocIdx.value = ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "errorDocIdx")), int.class)).intValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void list(java.lang.String recipientEDRPOU, int recipientDept, boolean procAllDepts, org.igov.util.swind.holders.ProcessResultHolder listResult, org.igov.util.swind.holders.ArrayOfDocumentIdHolder list) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://swined/List");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://swined/", "List"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {recipientEDRPOU, new java.lang.Integer(recipientDept), new java.lang.Boolean(procAllDepts)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                listResult.value = (org.igov.util.swind.ProcessResult) _output.get(new javax.xml.namespace.QName("http://swined/", "ListResult"));
            } catch (java.lang.Exception _exception) {
                listResult.value = (org.igov.util.swind.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "ListResult")), org.igov.util.swind.ProcessResult.class);
            }
            try {
                list.value = (org.igov.util.swind.DocumentId[]) _output.get(new javax.xml.namespace.QName("http://swined/", "list"));
            } catch (java.lang.Exception _exception) {
                list.value = (org.igov.util.swind.DocumentId[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "list")), org.igov.util.swind.DocumentId[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void load(java.lang.String recipientEDRPOU, org.igov.util.swind.DocumentId[] list, java.lang.String caName, byte[] cert, org.igov.util.swind.holders.ProcessResultHolder loadResult, javax.xml.rpc.holders.BooleanHolder restPreset, org.igov.util.swind.holders.ArrayOfDocumentOutDataHolder docs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://swined/Load");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://swined/", "Load"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {recipientEDRPOU, list, caName, cert});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                loadResult.value = (org.igov.util.swind.ProcessResult) _output.get(new javax.xml.namespace.QName("http://swined/", "LoadResult"));
            } catch (java.lang.Exception _exception) {
                loadResult.value = (org.igov.util.swind.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "LoadResult")), org.igov.util.swind.ProcessResult.class);
            }
            try {
                restPreset.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("http://swined/", "restPreset"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                restPreset.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "restPreset")), boolean.class)).booleanValue();
            }
            try {
                docs.value = (org.igov.util.swind.DocumentOutData[]) _output.get(new javax.xml.namespace.QName("http://swined/", "docs"));
            } catch (java.lang.Exception _exception) {
                docs.value = (org.igov.util.swind.DocumentOutData[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "docs")), org.igov.util.swind.DocumentOutData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void check(java.lang.String recipientEDRPOU, int recipientDept, boolean procAllDepts, org.igov.util.swind.holders.ProcessResultHolder checkResult, javax.xml.rpc.holders.IntHolder qtDocs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://swined/Check");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://swined/", "Check"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {recipientEDRPOU, new java.lang.Integer(recipientDept), new java.lang.Boolean(procAllDepts)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                checkResult.value = (org.igov.util.swind.ProcessResult) _output.get(new javax.xml.namespace.QName("http://swined/", "CheckResult"));
            } catch (java.lang.Exception _exception) {
                checkResult.value = (org.igov.util.swind.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "CheckResult")), org.igov.util.swind.ProcessResult.class);
            }
            try {
                qtDocs.value = ((java.lang.Integer) _output.get(new javax.xml.namespace.QName("http://swined/", "qtDocs"))).intValue();
            } catch (java.lang.Exception _exception) {
                qtDocs.value = ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "qtDocs")), int.class)).intValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void checkAcquired(java.lang.String senderEDRPOU, org.igov.util.swind.DocumentIdAcq[] list, org.igov.util.swind.holders.ProcessResultHolder checkAcquiredResult, org.igov.util.swind.holders.ArrayOfBooleanHolder acquired) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://swined/CheckAcquired");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://swined/", "CheckAcquired"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {senderEDRPOU, list});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                checkAcquiredResult.value = (org.igov.util.swind.ProcessResult) _output.get(new javax.xml.namespace.QName("http://swined/", "CheckAcquiredResult"));
            } catch (java.lang.Exception _exception) {
                checkAcquiredResult.value = (org.igov.util.swind.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "CheckAcquiredResult")), org.igov.util.swind.ProcessResult.class);
            }
            try {
                acquired.value = (boolean[]) _output.get(new javax.xml.namespace.QName("http://swined/", "acquired"));
            } catch (java.lang.Exception _exception) {
                acquired.value = (boolean[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "acquired")), boolean[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void getCertificate(java.lang.String caName, org.igov.util.swind.holders.ProcessResultHolder getCertificateResult, org.igov.util.swind.holders.ArrayOfCertificateHolder certs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://swined/GetCertificate");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://swined/", "GetCertificate"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {caName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                getCertificateResult.value = (org.igov.util.swind.ProcessResult) _output.get(new javax.xml.namespace.QName("http://swined/", "GetCertificateResult"));
            } catch (java.lang.Exception _exception) {
                getCertificateResult.value = (org.igov.util.swind.ProcessResult) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "GetCertificateResult")), org.igov.util.swind.ProcessResult.class);
            }
            try {
                certs.value = (org.igov.util.swind.Certificate[]) _output.get(new javax.xml.namespace.QName("http://swined/", "certs"));
            } catch (java.lang.Exception _exception) {
                certs.value = (org.igov.util.swind.Certificate[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://swined/", "certs")), org.igov.util.swind.Certificate[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
