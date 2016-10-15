package org.igov.model.core;

import java.io.Serializable;

/**
 *
 * T - type of primary key
 * User: goodg_000
 * Date: 30.04.2016
 * Time: 17:30
 */
public interface Entity<P extends Serializable> extends Serializable {

    P getId();

    void setId(P id);
}
