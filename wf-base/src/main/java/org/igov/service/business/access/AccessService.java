package org.igov.service.business.access;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.mail.EmailException;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.model.access.*;
import org.igov.model.access.vo.*;
import org.igov.service.business.access.handler.AccessServiceLoginRightHandler;
import org.igov.service.exception.HandlerBeanValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;
import java.util.stream.Collectors;

import org.igov.io.mail.NotificationPatterns;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 21:47
 */
@Service
public class AccessService implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(AccessService.class);

    private ApplicationContext applicationContext;

    @Autowired
    private AccessServiceLoginRoleDao accessServiceLoginRoleDao;

    @Autowired
    private AccessServiceRoleDao accessServiceRoleDao;

    @Autowired
    private AccessServiceRoleRightDao accessServiceRoleRightDao;

    @Autowired
    private AccessServiceRoleRightIncludeDao accessServiceRoleRightIncludeDao;

    @Autowired
    private AccessServiceRightDao accessServiceRightDao;

    @Autowired
    private NotificationPatterns oNotificationPatterns;
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // -------------- AccessServiceLoginRoles --------------------------------------------------------------------------

    public List<AccessLoginRoleVO> getAccessServiceLoginRoles(String sLogin) {
        List<AccessServiceLoginRole> roles = accessServiceLoginRoleDao.getUserRoles(sLogin);
        return roles.stream().map(AccessLoginRoleVO::new).collect(Collectors.toList());
    }

    public AccessLoginRoleVO setAccessServiceLoginRole(Long nID, String sLogin, Long nID_AccessServiceRole) {
        AccessServiceLoginRole loginRole = nID != null ? accessServiceLoginRoleDao.findByIdExpected(nID) :
                new AccessServiceLoginRole();
        loginRole.setsLogin(sLogin);
        loginRole.setAccessServiceRole(accessServiceRoleDao.findByIdExpected(nID_AccessServiceRole));
        accessServiceLoginRoleDao.saveOrUpdate(loginRole);
        return new AccessLoginRoleVO(loginRole);
    }

    public void removeAccessServiceLoginRole(Long nID) {
        accessServiceLoginRoleDao.delete(nID);
    }

    public void removeAccessServiceLoginRole(String sLogin, Long nID_AccessServiceRole) {
        AccessServiceLoginRole loginRole = accessServiceLoginRoleDao.findLoginRole(sLogin, nID_AccessServiceRole);
        if (loginRole != null) {
            accessServiceLoginRoleDao.delete(loginRole);
        }
    }

    // -------------- AccessServiceRole --------------------------------------------------------------------------------

    public List<AccessRoleVO> getAccessServiceRoleRights(Long roleId) {
        List<AccessRoleVO> res = new ArrayList<>();
        if (roleId != null) {
            res.add(new AccessRoleVO(accessServiceRoleDao.findByIdExpected(roleId)));
        }
        else {
            res.addAll(accessServiceRoleDao.findAll().stream().map(AccessRoleVO::new).collect(Collectors.toList()));
        }

        return res;
    }

    public AccessRoleVO setAccessServiceRole(Long roleId, String sName) {
        AccessServiceRole role = roleId != null
                ? accessServiceRoleDao.findByIdExpected(roleId) : new AccessServiceRole();

        role.setName(sName);
        accessServiceRoleDao.saveOrUpdate(role);
        return new AccessRoleVO(role, false);
    }

    public void removeAccessServiceRole(Long roleId) {
        accessServiceRoleDao.delete(roleId);
    }

    // -------------- AccessServiceRoleRight ---------------------------------------------------------------------------

    public AccessRoleRightVO setAccessServiceRoleRight(Long nID, Long nID_AccessServiceRole,
                                                       Long nID_AccessServiceRight) {

        AccessServiceRoleRight roleRight = nID != null ? accessServiceRoleRightDao.findByIdExpected(nID) :
                new AccessServiceRoleRight();

        roleRight.setAccessServiceRole(accessServiceRoleDao.findByIdExpected(nID_AccessServiceRole));
        roleRight.setAccessServiceRight(accessServiceRightDao.findByIdExpected(nID_AccessServiceRight));
        accessServiceRoleRightDao.saveOrUpdate(roleRight);
        return new AccessRoleRightVO(roleRight);
    }

    public void removeAccessServiceRoleRight(Long nID) {
        accessServiceRoleRightDao.delete(nID);
    }

    // -------------- AccessServiceRoleRightInclude --------------------------------------------------------------------

    public AccessRoleIncludeVO setAccessServiceRoleRightInclude(Long nID, Long nID_AccessServiceRole,
                                                       Long nID_AccessServiceRole_Include) {

        AccessServiceRoleRightInclude roleInclude = nID != null ? accessServiceRoleRightIncludeDao.findByIdExpected(nID) :
                new AccessServiceRoleRightInclude();

        roleInclude.setAccessServiceRole(accessServiceRoleDao.findByIdExpected(nID_AccessServiceRole));
        roleInclude.setAccessServiceRoleInclude(accessServiceRoleDao.findByIdExpected(nID_AccessServiceRole_Include));
        accessServiceRoleRightIncludeDao.saveOrUpdate(roleInclude);
        return new AccessRoleIncludeVO(roleInclude);
    }

    public void removeAccessServiceRoleRightInclude(Long nID) {
        accessServiceRoleRightIncludeDao.delete(nID);
    }

    // -------------- AccessServiceRight -------------------------------------------------------------------------------

    public List<AccessRightVO> getAccessServiceRights(Long nID, String sService, String saMethod, String sHandlerBean) {
        return accessServiceRightDao.getAccessServiceRights(nID, sService, saMethod, sHandlerBean).stream().map(
                AccessRightVO::new).collect(Collectors.toList());
    }

    public AccessRightVO setAccessServiceRight(AccessRightVO accessRightVO) {
        AccessServiceRight accessServiceRight = accessRightVO.getnID() != null ? accessServiceRightDao.findByIdExpected(
                accessRightVO.getnID()) : new AccessServiceRight();

        accessServiceRight.setName(accessRightVO.getsName());
        accessServiceRight.setsService(accessRightVO.getsService());
        accessServiceRight.setSaMethod(accessRightVO.getSaMethod());
        accessServiceRight.setnOrder(accessRightVO.getnOrder());
        accessServiceRight.setsHandlerBean(accessRightVO.getsHandlerBean());
        accessServiceRight.setbDeny(accessRightVO.isbDeny());
        accessServiceRightDao.saveOrUpdate(accessServiceRight);

        return new AccessRightVO(accessServiceRight);
    }

    public void removeAccessServiceRight(Long rightId) {
        accessServiceRightDao.delete(rightId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    public boolean hasAccessToService(String sLogin, String sService, String sData, String sMethod)
            throws HandlerBeanValidationException {

        boolean res = false;

        List<AccessServiceLoginRole> roles = accessServiceLoginRoleDao.getUserRoles(sLogin);
        if (roles.isEmpty()) {
            final String allUsersName = "*";
            roles = accessServiceLoginRoleDao.getUserRoles(allUsersName);
        }

        A: for (AccessServiceLoginRole role : roles) {
            List<AccessServiceRight> rights = role.getAccessServiceRole().resolveAllRightsSorted();

            for (AccessServiceRight right : rights) {
                if (isApplicableToServiceAndMethod(right, sService, sMethod)) {
                    res = hasAccess(right, sData);
                    break A;
                }
            }
        }

        return res;
    }


    private boolean hasAccess(AccessServiceRight right, String sData) throws HandlerBeanValidationException {
        boolean res;

        String handlerBeanName = right.getsHandlerBean();
        if (handlerBeanName != null) {
            AccessServiceLoginRightHandler handler = getHandlerBean(handlerBeanName);
            res = handler.hasAccessToService(sData);
        } else {
            res = true;
        }

        return !right.isbDeny() == res;
    }

    private boolean isApplicableToServiceAndMethod(AccessServiceRight accessServiceRight,
                                                   String sService, String sMethod) {
        if (!isServiceMatchedPattern(sService, accessServiceRight.getsService())) {
            return false;
        }

        Set<String> supportedMethods = accessServiceRight.resolveSupportedMethods();
        return supportedMethods == null || supportedMethods.contains(sMethod);

    }

    /**
     * Checks if service matched to <b>accessServicePattern</b>? accessServicePattern can contain * in beginning or
     * end of pattern. For example to match service="TestService" valid patterns are "TestService",
     * "Test*", "*Service" or "*".
     * @param service current service string
     * @param accessServicePattern field sService of entity AccessServiceRight.
     * @return is service matched to accessServicePattern?
     */
    private boolean isServiceMatchedPattern(String service, String accessServicePattern) {
        final String star = "*";

        if (accessServicePattern.equals(star)) {
            return true;
        }
        else if (accessServicePattern.startsWith(star)) {
            final String suffix = accessServicePattern.substring(star.length());
            return service.endsWith(suffix);
        }
        else if (accessServicePattern.endsWith(star)) {
            final String prefix = accessServicePattern.substring(0, accessServicePattern.length() - star.length());
            return service.startsWith(prefix);
        }

        return service.equals(accessServicePattern);
    }

    private AccessServiceLoginRightHandler getHandlerBean(String sHandlerBean) throws HandlerBeanValidationException {
        Object bean = applicationContext.getBean(sHandlerBean);
        if (bean == null) {
            throw new HandlerBeanValidationException(String.format(
                    "AccessServiceLoginRightHandler bean with name '%s' is not found!", sHandlerBean));
        } else if (!(bean instanceof AccessServiceLoginRightHandler)) {
            throw new HandlerBeanValidationException(String.format(
                    "Bean with name '%s' should implement interface %s, but actual class is %s", sHandlerBean,
                    AccessServiceLoginRightHandler.class, bean.getClass()));
        }

        return (AccessServiceLoginRightHandler) bean;
    }

    public Map<String, String> getVerifyContactEmail(String sQuestion, String sAnswer) throws AddressException, EmailException,
            RecordInmemoryException {
        Map<String, String> res = new HashMap<String, String>();
        
        InternetAddress emailAddr = new InternetAddress(sQuestion);
        emailAddr.validate();
        if (sAnswer == null || sAnswer.isEmpty()){
            String sToken = RandomStringUtils.randomAlphanumeric(15);
            oBytesDataInmemoryStorage.putString(sQuestion, sToken);
            oNotificationPatterns.sendVerifyEmail(sQuestion, sToken);
            LOG.info("Send email with token:{} to the address:{} and saved token ", sToken, sQuestion);
            res.put("bVerified", "true");
        } else {
            String sToken = oBytesDataInmemoryStorage.getString(sQuestion);
            LOG.info("Got token from Redis:{}", sToken);
            if (sAnswer.equals(sToken)){
                res.put("bVerified", "true");
            } else {
                res.put("bVerified", "false");
            }
        }
        return res;
    }
}
