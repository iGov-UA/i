package org.igov.service.business.object.place;

import org.springframework.stereotype.Service;

import com.google.common.base.Optional;

import io.swagger.annotations.*;

import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.object.place.*;
import org.igov.service.business.core.EntityService;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.EntityNotFoundException;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.igov.service.business.object.ObjectPlaceService.regionsToJsonResponse;
import static org.igov.service.business.object.ObjectPlaceService.swap;
import static org.igov.util.Tool.bNullArgsAll;

@Service
public class CommonPlaceService {
    
    private static final Logger LOG = LoggerFactory.getLogger(CommonPlaceService.class);
    
    @Autowired
    private PlaceDao placeDao;

    @Autowired
    private PlaceTypeDao placeTypeDao;
    
    @Autowired
    private PlaceTreeDao placeTreeDao;
    
    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;
    
    public Place getPlaceByProcess(Long nID_Process, Integer nID_Server) 
    {
        	Place result = null;
            try {
            	HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByProcessID(nID_Process, nID_Server);
            	LOG.info("Found history event by process ID {}", nID_Process);
                
                Optional<Place> place = null;
                
                if(oHistoryEvent_Service.getnID_Region() != null){
                    place = placeDao.findById(oHistoryEvent_Service.getnID_Region());
                }else{
                    place = placeDao.findBy("sID_UA", oHistoryEvent_Service.getsID_UA());
                }
                
                if (place.isPresent()) {
                	LOG.info("Found place {} for process by ID_UA {}", place.get().getName(), oHistoryEvent_Service.getsID_UA());
                	result = place.get();
                }
                
                if (result != null){
                    Long placeId = Long.parseLong(result.getsID_UA());
                    Long resultId = result.getId();
                    
                    LOG.info("placeId: " + placeId);
                    LOG.info("resultId: " + resultId);
                    
                    //Optional<PlaceTree> oPlaceTree = placeTreeDao.findBy("placeId", placeId);
                    
                    Optional<PlaceTree> oPlaceTree = placeTreeDao.findBy("placeId", resultId);        
                            
                    if (oPlaceTree.isPresent()){
                        LOG.info("oPlaceTree id is {}", oPlaceTree.get().getId());
                        PlaceTree oPlaceTreeResult = oPlaceTree.get();
                        Long parentId = oPlaceTreeResult.getParentId();
                        
                        
                        if(parentId != null && !parentId.equals(resultId)){
                            LOG.info("Place has parent");
                            Place oParentPlace = placeDao.findByIdExpected(parentId);
                            if (oParentPlace != null)
                            {
                                PlaceType oPlaceTypeParent = placeTypeDao.findByIdExpected(oParentPlace.getPlaceTypeId());
                                PlaceType oPlaceTypeResult = placeTypeDao.findByIdExpected(result.getPlaceTypeId());
                                result.setFullName(oPlaceTypeParent.getName() + " " + oParentPlace.getName() + 
                                        " " + oPlaceTypeResult.getName() + " " + result.getName());
                                LOG.info("fullName: {}", result.getFullName());
                            }
                        }
                    }
                }
            } catch (RuntimeException e) {
                LOG.warn("Error: {}", e.getMessage());
                LOG.trace("FAIL:",  e);
                //response.setStatus(HttpStatus.FORBIDDEN.value());
                //response.setHeader("Reason", e.getMessage());
            } 
            return result;
        }
    
}
