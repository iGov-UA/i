/**
 * SWinEDSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public interface SWinEDSoap extends java.rmi.Remote {

    /**
     * Передати документи
     */
    public void post(java.lang.String senderEDRPOU, int senderDept, org.igov.util.swind.DocumentType docsType, org.igov.util.swind.DocumentInData[] docs, org.igov.util.swind.holders.ProcessResultHolder postResult, javax.xml.rpc.holders.IntHolder errorDocIdx) throws java.rmi.RemoteException;

    /**
     * Отримати вхідну кореспонденцію
     */
    public void receive(java.lang.String recipientEDRPOU, int recipientDept, boolean procAllDepts, java.lang.String caName, byte[] cert, org.igov.util.swind.holders.ProcessResultHolder receiveResult, javax.xml.rpc.holders.BooleanHolder restPresent, org.igov.util.swind.holders.ArrayOfDocumentOutDataHolder docs) throws java.rmi.RemoteException;

    /**
     * Позначити документи як оброблені
     */
    public void mark(org.igov.util.swind.ProcessedDocument[] docs, org.igov.util.swind.holders.ProcessResultHolder markResult, javax.xml.rpc.holders.IntHolder errorDocIdx) throws java.rmi.RemoteException;

    /**
     * Отримати список вхідних документів
     */
    public void list(java.lang.String recipientEDRPOU, int recipientDept, boolean procAllDepts, org.igov.util.swind.holders.ProcessResultHolder listResult, org.igov.util.swind.holders.ArrayOfDocumentIdHolder list) throws java.rmi.RemoteException;

    /**
     * Отримати вхідну кореспонденцію за списком документів
     */
    public void load(java.lang.String recipientEDRPOU, org.igov.util.swind.DocumentId[] list, java.lang.String caName, byte[] cert, org.igov.util.swind.holders.ProcessResultHolder loadResult, javax.xml.rpc.holders.BooleanHolder restPreset, org.igov.util.swind.holders.ArrayOfDocumentOutDataHolder docs) throws java.rmi.RemoteException;

    /**
     * Перевірити наявність вхідної кореспонденції
     */
    public void check(java.lang.String recipientEDRPOU, int recipientDept, boolean procAllDepts, org.igov.util.swind.holders.ProcessResultHolder checkResult, javax.xml.rpc.holders.IntHolder qtDocs) throws java.rmi.RemoteException;

    /**
     * Отримати підтвердження отримання документів одержувачем
     */
    public void checkAcquired(java.lang.String senderEDRPOU, org.igov.util.swind.DocumentIdAcq[] list, org.igov.util.swind.holders.ProcessResultHolder checkAcquiredResult, org.igov.util.swind.holders.ArrayOfBooleanHolder acquired) throws java.rmi.RemoteException;

    /**
     * Отримати сертифікат Центрального Поштамта
     */
    public void getCertificate(java.lang.String caName, org.igov.util.swind.holders.ProcessResultHolder getCertificateResult, org.igov.util.swind.holders.ArrayOfCertificateHolder certs) throws java.rmi.RemoteException;
}
