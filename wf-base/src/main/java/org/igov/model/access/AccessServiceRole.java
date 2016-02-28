package org.igov.model.access;

import org.apache.commons.collections.CollectionUtils;
import org.igov.model.core.NamedEntity;

import javax.persistence.*;
import java.util.*;

/**
 * Role which hold collection of rights of type {@link AccessServiceRight} + optional includes of rights from
 * other roles.
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 17:38
 */
@Entity
public class AccessServiceRole extends NamedEntity {

    /**
     * Rights directly linked to this role.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="AccessServiceRoleRight",
            joinColumns = {@JoinColumn(name = "nID_AccessServiceRole", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "nID_AccessServiceRight",
                    nullable = false, updatable = false) })
    private List<AccessServiceRight> rights;

    /**
     * Another roles which are included by current role. Currently role will have all rights from included roles.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="AccessServiceRoleRightInclude",
            joinColumns = {@JoinColumn(name = "nID_AccessServiceRole", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "nID_AccessServiceRole_Include",
                    nullable = false, updatable = false) })
    private List<AccessServiceRole> includes;

    public List<AccessServiceRight> getRights() {
        return rights;
    }
    public void setRights(List<AccessServiceRight> rights) {
        this.rights = rights;
    }

    public List<AccessServiceRole> getIncludes() {
        return includes;
    }
    public void setIncludes(List<AccessServiceRole> includes) {
        this.includes = includes;
    }

    /**
     * @return all rights from current role and from including roles recursively. The result is sorted by nOrder column
     * of {@link AccessServiceRight}.
     */
    public List<AccessServiceRight> resolveAllRightsSorted() {
        Set<AccessServiceRight> resultSet = new HashSet<>();

        Set<AccessServiceRole> currentRoles = new HashSet<>();
        currentRoles.add(this);

        Set<AccessServiceRole> visitedRoles = new HashSet<>();

        while (currentRoles.size() > 0) {
            final Iterator<AccessServiceRole> i = currentRoles.iterator();
            AccessServiceRole role = i.next();
            i.remove();
            visitedRoles.add(role);

            resultSet.addAll(role.getRights());
            if (!CollectionUtils.isEmpty(role.getIncludes())) {
                Set<AccessServiceRole> newRoles = new HashSet<>(role.getIncludes());
                newRoles.removeAll(visitedRoles);
                currentRoles.addAll(newRoles);
            }
        }

        List<AccessServiceRight> res = new ArrayList<>(resultSet);
        Collections.sort(res);
        return res;
    }
}
