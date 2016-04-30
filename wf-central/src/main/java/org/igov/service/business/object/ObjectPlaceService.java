/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.object;

import com.google.common.base.Optional;

import java.util.List;

import org.igov.model.core.AbstractEntity;
import org.igov.model.core.EntityDao;
import org.igov.model.object.place.City;
import org.igov.model.object.place.Region;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ObjectPlaceService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ObjectPlaceService.class);
    
    public static ResponseEntity regionsToJsonResponse(List<Region> aRegion) {
        for (Region oRegion : aRegion) {
            for (City oCity : oRegion.getCities()) {
                oCity.setRegion(null);
            }
        }

        return JsonRestUtils.toJsonResponse(aRegion);
    }    

    /**
     * This method allows to swap two entities by Primary Key (PK).
     *
     * @param entity          - entity with new parameters
     * @param persistedEntity - persisted entity with registered PK in DB
     * @param dao             - type-specific dao implementation
     **/
    @SuppressWarnings("unchecked")
    public static <T extends AbstractEntity> boolean swap(T entity, Optional<T> persistedEntity, EntityDao dao) {
        if (persistedEntity.isPresent()) {
            entity.setId(persistedEntity.get().getId());
            dao.saveOrUpdate(entity);
            return true;
        }
        return false;
    }    
}
