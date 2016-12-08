package org.igov.model.action.execute.item;

import org.hibernate.Session;

/**
 * Created by dpekach on 29.11.16.
 */
public interface Sessionable {
    Session getSessionForService();
}
