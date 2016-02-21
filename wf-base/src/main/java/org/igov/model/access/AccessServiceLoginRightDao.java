package org.igov.model.access;


import java.util.List;
import org.igov.model.core.EntityDao;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 22:31
 */
public interface AccessServiceLoginRightDao extends EntityDao<AccessServiceLoginRight> {

    AccessServiceLoginRight getAccessServiceLoginRight(String sLogin, String sService);

    List<String> getAccessibleServices(String sLogin);
}
