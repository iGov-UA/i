package org.igov.io.mail;

import javax.mail.Multipart;
import org.springframework.beans.factory.annotation.Value;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public abstract class Abstract_Mail {

    @Value("${general.Mail.sAddressDefaultFrom}")
    private String sFrom; //"noreplay@gmail.com";
    @Value("${general.Mail.sAddressNoreply}")
    private String saTo; //"noreplay@gmail.com";
    private String sToName; //"Андрій Хветкевич";
    private String sHead = "Subject";
    private String sBody = "Text";
    @Value("${general.Mail.sUsername}")
    private String sAuthUser; //"user";
    @Value("${general.Mail.sPassword}")
    private String sAuthPassword; //"password";
    @Value("${general.Mail.sHost}")
    private String sHost; //"gmail.com";
    @Value("${general.Mail.nPort}")
    private Integer nPort; //Integer.valueOf(mailServerPort);
    @Value("${general.Mail.bUseSSL}")
    private boolean bSSL;
    @Value("${general.Mail.bUseTLS}")
    private boolean bTLS;
    private Multipart oMultiparts;

    public Abstract_Mail() {
    }

    public String getFrom() {
        return sFrom;
    }

    public Abstract_Mail _From(String sFrom) {
        this.sFrom = sFrom;
        return this;
    }

    public String getTo() {
        String[] adress = saTo.split("\\,");
        if (adress.length == 1 && sToName != null && !"null".equalsIgnoreCase(sToName)) {
//            return String.format("<%s <%s>>", sToName, saTo); // "John Dow <email@adress.com>"
            return sToName + "<" + saTo + ">";
        }
        return saTo;
    }

    public Abstract_Mail _To(String saTo) {
        this.saTo = saTo;
        return this;
    }

    public String getToName() {
        return sToName;
    }

    public Abstract_Mail _ToName(String... saToName) {
        String fullName = saToName[0];
        if (saToName.length > 1) {
            for (int i = 1; i < saToName.length; i++) {
                fullName = fullName + " " + saToName[i];
            }
        }
        this.sToName = fullName;
        return this;
    }

    public String getHead() {
        return sHead;
    }

    public Abstract_Mail _Head(String sHead) {
        this.sHead = sHead;
        return this;
    }

    public String getBody() {
        return sBody;
    }

    public Abstract_Mail _Body(String sBody) {
        this.sBody = sBody;
        return this;
    }

    public String getAuthUser() {
        return sAuthUser;
    }

    public Abstract_Mail _AuthUser(String sAuthUser) {
        this.sAuthUser = sAuthUser;
        return this;
    }

    public String getAuthPassword() {
        return sAuthPassword;
    }

    public Abstract_Mail _AuthPassword(String sAuthPassword) {
        this.sAuthPassword = sAuthPassword;
        return this;
    }

    public String getHost() {
        return sHost;
    }

    public Abstract_Mail _Host(String sHost) {
        this.sHost = sHost;
        return this;
    }

    public Integer getPort() {
        return nPort;
    }

    public Abstract_Mail _Port(Integer nPort) {
        this.nPort = nPort;
        return this;
    }

    public boolean isSSL() {
        return bSSL;
    }

    public Abstract_Mail _SSL(boolean bSSL) {
        this.bSSL = bSSL;
        return this;
    }

    public boolean isTLS() {
        return bTLS;
    }

    public Abstract_Mail _TLS(boolean bTLS) {
        this.bTLS = bTLS;
        return this;
    }

    public Multipart getoMultiparts() {
        return oMultiparts;
    }

    public Multipart _oMultiparts(Multipart oMultiparts) {
        this.oMultiparts = oMultiparts;
        return oMultiparts;
    }

    //abstract public void init() throws Exception;
    abstract public void send() throws Exception;

}
