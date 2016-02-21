package org.igov.service.controller.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author tasman edited by Olga Turenko & Belyavtsev Vladimir (BW)
 */
public class AccessKeyAuthenticationToken extends AbstractAuthenticationToken {

    private transient final Logger LOG = LoggerFactory.getLogger(AccessKeyAuthenticationToken.class);
    private String sID;
    private String sSecret;

    public AccessKeyAuthenticationToken(String sID, String sSecret) {
        super(null);
        this.sID = sID;
        this.sSecret = sSecret;
        //LOG.info("[AccessKeyAuthenticationToken]:sID="+sID+",sSecret="+sSecret);
        setAuthenticated(false);
    }

    public AccessKeyAuthenticationToken(String sID, String sSecret,
            Collection<? extends GrantedAuthority> aGrantedAuthority) {
        super(aGrantedAuthority);
        this.sID = sID;
        this.sSecret = sSecret;
        //LOG.info("[AccessKeyAuthenticationToken]:sID="+sID+",sSecret="+sSecret+",aGrantedAuthority!=null:"+(aGrantedAuthority!=null));
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        //LOG.info("[getCredentials]:sSecret=" + sSecret);
        return sSecret;
    }

    @Override
    public Object getPrincipal() {
        //LOG.info("[getPrincipal]:sID="+sID);
        return sID;
    }

    public boolean isNotEmpty() {
        return StringUtils.isNotBlank(sID) && StringUtils.isNotBlank(sSecret);
    }
}
