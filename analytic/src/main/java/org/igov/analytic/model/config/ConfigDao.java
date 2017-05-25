/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.config;

import org.igov.model.core.EntityDao;

/**
 * @author olga
 */
public interface ConfigDao extends EntityDao<Long, Config> {
    Config findLatestConfig();
}
