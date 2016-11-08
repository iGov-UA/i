/**
 * GateSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.igov.util.swind;

public interface GateSoap extends java.rmi.Remote {

    /**
     * <b>Відправити документ на шлюз</b><br />
     * fileName - ім'я файлу документа<br />
     * senderEMail - адреса електронної пошти відправника документа<br />
     * на яку буде відправлено відповіді, якщо за певний час користувач іх
     * не вилучив викликом методу <b>Delete</b><br />
     * data - зміст документа в форматі електронного конверта шлюзу<br />
     */
    public org.igov.util.swind.ProcessResult send(java.lang.String fileName, java.lang.String senderEMail, byte[] data) throws java.rmi.RemoteException;

    /**
     * <b>Отримати список кодів повідомлень для одержувача</b><br
     * />
     * signedEDRPOU - ЄДРПОУ/ДРФО одержувача<br />
     * messagesList - перелік кодів повідомлень, у вигляді тексту, кожен
     * код з нового рядку<br />
     */
    public void getMessages(byte[] signedEDRPOU, org.igov.util.swind.holders.ProcessResultHolder getMessagesResult, org.igov.util.swind.holders.ArrayOfStringHolder messagesList) throws java.rmi.RemoteException;

    /**
     * <b>Отримати список кодів повідомлень для одержувача з визначенням
     * адреси електронної пошти</b><br />
     * signedEDRPOU - ЄДРПОУ/ДРФО одержувача<br />
     * senderEmail - адреса електронної пошти відправника документа (така
     * сама як у виклику <b>Send</b>)<br />
     * messagesList - перелік кодів повідомлень, у вигляді тексту, кожен
     * код з нового рядку<br />
     */
    public void getMessagesEx(byte[] signedEDRPOU, java.lang.String senderEmail, org.igov.util.swind.holders.ProcessResultHolder getMessagesExResult, org.igov.util.swind.holders.ArrayOfStringHolder messagesList) throws java.rmi.RemoteException;

    /**
     * <b>Отримати повідомлення</b><br />
     * signedMsgId - строка з кодом повідомлення<br />
     * fileName - ім'я файлу повідомлення<br />
     * messageData - зміст повідомлення в форматі електронного конверта шлюзу<br
     * />
     */
    public void receive(byte[] signedMsgId, org.igov.util.swind.holders.ProcessResultHolder receiveResult, javax.xml.rpc.holders.StringHolder fileName, javax.xml.rpc.holders.ByteArrayHolder messageData) throws java.rmi.RemoteException;

    /**
     * <b>Отримати всі повідомлення</b><br />
     * signedEmail - адреса електронної пошти відправника документа (така
     * сама як у виклику <b>Send</b>), підписана ЕЦП (блок XXX_SIGN)<br />
     * needDelete - ознака необхідності вилучення повідомлень (0/1)<br />
     * messages - повідомлення<br />
     * complete - ознака відсутності на час виклику повідомлень, що не повернуті
     * користувачу<br />
     */
    public void receiveAll(byte[] signedEmail, org.apache.axis.types.UnsignedByte needDelete, org.igov.util.swind.holders.ProcessResultHolder receiveAllResult, org.igov.util.swind.holders.ArrayOfMessageHolder messages, org.apache.axis.holders.UnsignedByteHolder complete) throws java.rmi.RemoteException;

    /**
     * <b>Вилучити повідомлення</b><br />
     * signedMsgId - перелік кодів повідомлень, у вигляді тексту, кожен код
     * з нового рядку, підписаний ЕЦП (блок XXX_SIGN)<br />
     */
    public org.igov.util.swind.ProcessResult delete(byte[] signedMsgId) throws java.rmi.RemoteException;
}
