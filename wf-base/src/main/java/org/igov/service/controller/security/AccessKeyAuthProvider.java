package org.igov.service.controller.security;

import com.google.gwt.dev.asm.commons.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.igov.service.business.access.AccessDataService;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tasman edited by Olga Turenko & Belyavtsev Vladimir (BW)
 */
@Component
public class AccessKeyAuthProvider implements AuthenticationProvider {

    private static final String GENERAL_ROLE = "ROLE_USER";
    private final Logger LOG = LoggerFactory.getLogger(AccessKeyAuthProvider.class);
    @Value("${general.auth.login}")
    private String sAccessLogin; // = sGeneralUsername; // == null ? "anonymous" : sGeneralUsername;
    private AccessDataService oAccessDataService;

    @Autowired
    public AccessKeyAuthProvider(AccessDataService oAccessDataDao) {
        this.oAccessDataService = oAccessDataDao;
    }

    public void setAccessLoginDefault(String sAccessLogin) {
        this.sAccessLogin = sAccessLogin;
    }

    @Override
    public Authentication authenticate(Authentication oAuthentication) throws AuthenticationException {
        checkAuthByAccessKeyAndData(oAuthentication);
        return createTokenByAccessKeyAndData(oAuthentication);
    }

    private void checkAuthByAccessKeyAndData(Authentication oAuthentication) {
        String sAccessKey = oAuthentication.getName();
        String sAccessData = oAccessDataService.getAccessData(sAccessKey);
        LOG.info("(sAccessKey={},sAccessData(Storage)={})",sAccessKey,sAccessData);
        if (sAccessData == null) {
            LOG.warn("sAccessData == null");
            throw new BadAccessKeyCredentialsException("Error custom authorization - key is absent");
        }
        
        /*sAccessData = sAccessData
                .replace("&" + AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                        + AccessContract.Request.name(), "")
                .replace("" + AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                        + AccessContract.Request.name() + "&", "");*/

        boolean bContractAndLoginUnlimited = sAccessData.contains(AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                + AccessContract.RequestAndLoginUnlimited.name())
                ;
        boolean bContractAndLogin = sAccessData.contains(AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                + AccessContract.RequestAndLogin.name())
                ;
        boolean bContract = sAccessData.contains(AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                + AccessContract.Request.name())
                ;
        
        //TODO: do protection in future - only if "bContract*"
        sAccessData = sAccessData.replace("?&", "?").replace("&&", "&");
        
        
        if (bContractAndLoginUnlimited) {
            sAccessData = sAccessData
                    .replace(AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                    + AccessContract.RequestAndLoginUnlimited.name(), "")
                    ;
        }
        if (bContractAndLogin) {
            sAccessData = sAccessData
                    .replace(AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                    + AccessContract.RequestAndLogin.name(), "")
                    ;
        }
        if (bContract) {
            sAccessData = sAccessData
                    .replace(AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                    + AccessContract.Request.name(), "")
                    ;
        }
        sAccessData = sAccessData.replace("?&", "?").replace("&&", "&");
        
        if (sAccessData.contains(AuthenticationTokenSelector.ACCESS_LOGIN)) {
            sAccessData = sAccessData.substring(0, sAccessData
                    .indexOf("&" + AuthenticationTokenSelector.ACCESS_LOGIN)); //&sAccessLogin=activiti-master
        }

        String sAccessDataGenerated = oAuthentication.getCredentials() + "";
        String sAccessDataGeneratedDecoded = null;
        try {
            sAccessDataGeneratedDecoded = URLDecoder.decode(sAccessDataGenerated);
            if (bContractAndLogin || bContractAndLoginUnlimited) {
                String sStartWith = AuthenticationTokenSelector.ACCESS_LOGIN + "=";
                String[] as = sAccessDataGeneratedDecoded.split("\\&");
                for (String s : as) {
                    if (s.startsWith(sStartWith)) {
                        String[] asWord = s.split("\\=");
                        sAccessLogin = asWord[1];
                        break;
                    }
                }
            }
        } catch (Exception oException) {
            LOG.error("Fail 'URLDecoder.decode': {} (sAccessDataGenerated={})", oException.getMessage(), sAccessDataGenerated);
            throw oException;
        }

        if(sAccessData.endsWith("&")){
            sAccessData=sAccessData.substring(0,sAccessData.length()-1);
        }
        if(sAccessDataGeneratedDecoded.endsWith("&")){
            sAccessDataGeneratedDecoded=sAccessDataGeneratedDecoded.substring(0,sAccessDataGeneratedDecoded.length()-1);
        }
        if (!sAccessData.equals(sAccessDataGeneratedDecoded)) {
            //TODO: temporary for back-compatibility
            /*if(sAccessData.indexOf("/setMessageRate") >= 0
                    || sAccessData.indexOf("/cancelTask") >= 0
                    ){
                LOG.warn("!sAccessData.equals(sAccessDataGenerated) (sAccessData(FromStorage)=\n{}, sAccessDataGeneratedDecoded=\n{}"
                         , sAccessData, sAccessDataGeneratedDecoded);
            }else{*/
                LOG.error("!sAccessData.equals(sAccessDataGeneratedDecoded) (sAccessData(FromStorage)=\n{}, sAccessDataGeneratedDecoded=\n{}"
                         , sAccessData, sAccessDataGeneratedDecoded);
                throw new BadAccessKeyCredentialsException("Error custom authorization - key data is wrong");
            //}
        }

        if(!bContractAndLoginUnlimited){
            oAccessDataService.removeAccessData(sAccessKey);
        }
        LOG.info("Removed key! (sAccessLogin={},bContractAndLogin={},sAccessKey={})", sAccessLogin,bContractAndLogin,sAccessKey);
    }

    private Authentication createTokenByAccessKeyAndData(Authentication oAuthentication) {
        LOG.info("(sAccessLogin={})", sAccessLogin); //+",oAuthentication.getName()="+oAuthentication.getName()//+",authentication.getCredentials().toString()="+oAuthentication.getCredentials().toString());
        List<GrantedAuthority> aGrantedAuthority = new ArrayList<>(); //Arrays.asList(new SimpleGrantedAuthority(GENERAL_ROLE))
        aGrantedAuthority.add(new SimpleGrantedAuthority(GENERAL_ROLE));
        return new AccessKeyAuthenticationToken(sAccessLogin,
                //sAccessLogin == null ? oAuthentication.getName() : sAccessLogin
                oAuthentication.getCredentials().toString(), aGrantedAuthority);
    }

    @Override
    public boolean supports(Class<?> oAuthentication) {
        boolean bSupport = AccessKeyAuthenticationToken.class.equals(oAuthentication);
        //LOG.info("[supports]:bEquals="+bSupport);
        return bSupport;
    }
}
