package org.igov.service.business.access;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.mail.EmailException;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.mail.Mail;
import org.igov.service.business.access.handler.AccessServiceLoginRightHandler;
import org.igov.service.controller.AccessCommonController;
import org.igov.service.exception.HandlerBeanValidationException;
import org.igov.model.access.AccessServiceLoginRight;
import org.igov.model.access.AccessServiceLoginRightDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private AccessServiceLoginRightDao accessServiceLoginRightDao;
    @Autowired
    private NotificationPatterns oNotificationPatterns;
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public boolean hasAccessToService(String sLogin, String sService, String sData)
            throws HandlerBeanValidationException {

        boolean res = false;

        AccessServiceLoginRight access = accessServiceLoginRightDao.getAccessServiceLoginRight(sLogin, sService);
        if (access != null) {

            String handlerBeanName = access.getsHandlerBean();
            if (handlerBeanName != null) {
                AccessServiceLoginRightHandler handler = getHandlerBean(handlerBeanName);
                res = handler.hasAccessToService(sData);
            } else {
                res = true;
            }

        }

        return res;
    }

    public void saveOrUpdateAccessServiceLoginRight(String sLogin, String sService, String sHandlerBean)
            throws HandlerBeanValidationException {
        AccessServiceLoginRight access = accessServiceLoginRightDao.getAccessServiceLoginRight(sLogin, sService);
        if (access == null) {
            access = new AccessServiceLoginRight();
        }

        if (sHandlerBean != null) {
            getHandlerBean(sHandlerBean);
        }

        access.setsLogin(sLogin);
        access.setsService(sService);
        access.setsHandlerBean(sHandlerBean);

        accessServiceLoginRightDao.saveOrUpdate(access);
    }

    public boolean removeAccessServiceLoginRight(String sLogin, String sService) {
        boolean removed = false;
        AccessServiceLoginRight access = accessServiceLoginRightDao.getAccessServiceLoginRight(sLogin, sService);

        if (access != null) {
            accessServiceLoginRightDao.delete(access);
            removed = true;
        }

        return removed;
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

    public List<String> getAccessibleServices(String sLogin) {
        return accessServiceLoginRightDao.getAccessibleServices(sLogin);
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
