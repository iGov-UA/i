package org.igov.model.access;

import org.igov.model.core.NamedEntity;
import org.igov.service.business.access.handler.AccessServiceLoginRightHandler;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gives right to access to specified service with specified method.
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 17:26
 */
@Entity
public class AccessServiceRight extends NamedEntity implements Comparable<AccessServiceRight> {

    private static final String METHOD_SEPARATOR = "/";

    /**
     * URL mask to service to provide access right to it.
     */
    @Column
    private String sService;

    /**
     * Apply to method of access GET / POST / PUT - etc. If 'null' then its for all method. '/' - separator
     * if you need to list few methods.
     */
    @Column
    private String saMethod;

    /**
     * Optional name of bean of type {@link AccessServiceLoginRightHandler} which could check the access
     * to service.
     */
    @Column
    private String sHandlerBean;

    /**
     * Is this right represents condition to <b>deny</b> access? By default and in most cases it's <i>false</i>
     */
    @Column
    private boolean bDeny;

    /**
     * Order in which rights should be checked. Its done in case if we have some 'heavy' checks, to put this
     * checks last in checks order.
     */
    @Column
    private Integer nOrder;

    public String getsService() {
        return sService;
    }
    public void setsService(String sService) {
        this.sService = sService;
    }

    public String getSaMethod() {
        return saMethod;
    }
    public void setSaMethod(String saMethod) {
        this.saMethod = saMethod;
    }

    public String getsHandlerBean() {
        return sHandlerBean;
    }
    public void setsHandlerBean(String sHandlerBean) {
        this.sHandlerBean = sHandlerBean;
    }

    public boolean isbDeny() {
        return bDeny;
    }
    public void setbDeny(boolean bDeny) {
        this.bDeny = bDeny;
    }

    public Integer getnOrder() {
        return nOrder;
    }
    public void setnOrder(Integer nOrder) {
        this.nOrder = nOrder;
    }

    public Set<String> resolveSupportedMethods() {
        if (saMethod == null) {
            return null;
        }

        return Arrays.stream(saMethod.split(METHOD_SEPARATOR)).map(String::trim).collect(Collectors.toSet());
    }

    @Override
    public int compareTo(AccessServiceRight other) {
        final int defaultOrder = 0;
        int order1 = nOrder != null ? nOrder : defaultOrder;
        int order2 = other.nOrder != null ? other.nOrder : defaultOrder;
        return Integer.compare(order1, order2);
    }
}
