/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.object;

import com.google.common.base.Optional;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.igov.model.document.Document;
import org.igov.model.document.DocumentDao;
import org.igov.model.action.event.HistoryEventDao;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectDao;
import org.igov.model.subject.organ.SubjectOrganDao;
import org.igov.model.action.event.HistoryEventMessage;
import org.igov.model.action.event.HistoryEventType;
import org.igov.model.core.Entity;
import org.igov.model.core.EntityDao;
import org.igov.model.object.place.City;
import org.igov.model.object.place.Region;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public static <T extends Entity> boolean swap(T entity, Optional<T> persistedEntity, EntityDao dao) {
        if (persistedEntity.isPresent()) {
            entity.setId(persistedEntity.get().getId());
            dao.saveOrUpdate(entity);
            return true;
        }
        return false;
    }    
}
