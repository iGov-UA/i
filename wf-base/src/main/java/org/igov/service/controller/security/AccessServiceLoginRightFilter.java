package org.igov.service.controller.security;

import org.igov.service.business.access.AccessService;
import org.igov.service.exception.HandlerBeanValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Provide check access to service using AccessServiceLoginRight entities.
 */
@Component
public class AccessServiceLoginRightFilter extends GenericFilterBean {

    private final Logger LOG = LoggerFactory.getLogger(AccessServiceLoginRightFilter.class);

    @Value("${general.auth.login}")
    private String generalAuthLogin;

    @Autowired
    private AccessService accessService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        String service = null;
        String parameters = null;

        boolean hasAccessToService = true;
        if (!generalAuthLogin.equals(userName)) {
//TODO:temp
/*            
            try {
                service = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
                parameters = httpServletRequest.getQueryString();
                hasAccessToService = accessService.hasAccessToService(userName, service, parameters);
            } catch (HandlerBeanValidationException e) {
                hasAccessToService = false;
                LOG.error("Exception during call to [accessService.hasAccessToService]", e);
            }
*/        
        }

        if (hasAccessToService) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, String.format("User [%s] has no access " +
                    "to service [%s] with parameters [%s]", userName, service, parameters));
        }
    }
}
