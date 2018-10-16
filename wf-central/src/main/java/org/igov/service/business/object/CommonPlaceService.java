package org.igov.service.business.object;

import com.google.common.base.Optional;
import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.object.place.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private PlaceBranchTypeDao oPlaceBranchTypeDao;

    @Autowired
    private PlaceBuildTypeDao oPlaceBuildTypeDao;

    @Autowired
    private PlaceBuildPartTypeDao oPlaceBuildPartTypeDao;

    @Autowired
    private PlaceBuildPartCellTypeDao oPlaceBuildPartCellTypeDao;

    @Autowired
    private PlaceBranchDao oPlaceBranchDao;

    @Autowired
    private PlaceBuildDao oPlaceBuildDao;

    @Autowired
    private PlaceBuildPartDao oPlaceBuildPartDao;

    @Autowired
    private PlaceBuildPartCellDao oPlaceBuildPartCellDao;
    
    public Place getPlaceByProcess(Long nID_Process, Integer nID_Server) 
    {
        	Place result = null;
            try {
            	HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByProcessID(nID_Process, nID_Server);
            	LOG.info("Found history event by process ID "+ nID_Process);
                
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
                                result.setOriginalName(oPlaceTypeParent.getName() + " " + oParentPlace.getName() +
                                        " " + oPlaceTypeResult.getName() + " " + result.getName());
                                LOG.info("fullName: {}", result.getOriginalName());
                            }
                        }
                    }
                }
            } catch (RuntimeException e) {
                LOG.error("Error: {}", e.getMessage());
            } 
            return result;
        }

    public PlaceBranchType getPlaceBranchType(Long nID) {
        PlaceBranchType oPlaceBranchType = oPlaceBranchTypeDao.findById(nID).orNull();
        if (oPlaceBranchType == null) {
            throw new RuntimeException("PlaceBranchType with nID "+nID+" not found" );
        }
        return oPlaceBranchType;
    }

    public PlaceBranchType setPlaceBranchType(String sKey, String sName) {
        PlaceBranchType oPlaceBranchType = oPlaceBranchTypeDao.findBy("sKey", sKey).orNull();
        if (oPlaceBranchType == null) {
            oPlaceBranchType = new PlaceBranchType();
        }
        oPlaceBranchType.setSKey(sKey);
        oPlaceBranchType.setSName(sName);
        return oPlaceBranchTypeDao.saveOrUpdate(oPlaceBranchType);
    }

    public void removePlaceBranchType(Long nID) {
        PlaceBranchType placeBranchType = oPlaceBranchTypeDao.findById(nID).orNull();
        if (placeBranchType != null) {
            oPlaceBranchTypeDao.delete(placeBranchType);
        } else {
            throw new RuntimeException("PlaceBranchType with nID "+nID+" not found" );
        }

    }

    public PlaceBuildType getPlaceBuildType(Long nID) {
        PlaceBuildType oPlaceBuildType = oPlaceBuildTypeDao.findById(nID).orNull();
        if (oPlaceBuildType == null) {
            throw new RuntimeException("PlaceBuildType with nID "+nID+" not found" );
        }
        return oPlaceBuildType;
    }

    public PlaceBuildType setPlaceBuildType(String sKey, String sName) {
        PlaceBuildType oPlaceBuildType = oPlaceBuildTypeDao.findBy("sKey", sKey).orNull();
        if (oPlaceBuildType == null) {
            oPlaceBuildType = new PlaceBuildType();
        }
        oPlaceBuildType.setSKey(sKey);
        oPlaceBuildType.setSName(sName);
        return oPlaceBuildTypeDao.saveOrUpdate(oPlaceBuildType);
    }

    public void removePlaceBuildType(Long nID) {
        PlaceBuildType placeBuildType = oPlaceBuildTypeDao.findById(nID).orNull();
        if (placeBuildType != null) {
            oPlaceBuildTypeDao.delete(placeBuildType);
        } else {
            throw new RuntimeException("PlaceBuildType with nID "+nID+" not found" );
        }

    }

    public PlaceBuildPartType getPlaceBuildPartType(Long nID) {
        PlaceBuildPartType oPlaceBuildPartType = oPlaceBuildPartTypeDao.findById(nID).orNull();
        if (oPlaceBuildPartType == null) {
            throw new RuntimeException("PlaceBuildPartType with nID "+nID+" not found" );

        }
        return oPlaceBuildPartType;
    }

    public PlaceBuildPartType setPlaceBuildPartType(String sKey, String sName) {
        PlaceBuildPartType oPlaceBuildPartType = oPlaceBuildPartTypeDao.findBy("sKey", sKey).orNull();
        if (oPlaceBuildPartType == null) {
            oPlaceBuildPartType = new PlaceBuildPartType();
        }
        oPlaceBuildPartType.setSKey(sKey);
        oPlaceBuildPartType.setSName(sName);
        return oPlaceBuildPartTypeDao.saveOrUpdate(oPlaceBuildPartType);
    }

    public void removePlaceBuildPartType(Long nID) {
        PlaceBuildPartType placeBuildPartType = oPlaceBuildPartTypeDao.findById(nID).orNull();
        if (placeBuildPartType != null) {
            oPlaceBuildPartTypeDao.delete(placeBuildPartType);
        } else {
            throw new RuntimeException("PlaceBuildPartType with nID "+nID+" not found" );
        }
    }

    public PlaceBuildPartCellType getPlaceBuildPartCellType(Long nID) {
        PlaceBuildPartCellType oPlaceBuildPartCellType = oPlaceBuildPartCellTypeDao.findById(nID).orNull();
        if (oPlaceBuildPartCellType == null) {
            throw new RuntimeException("PlaceBuildPartCellType with nID "+nID+" not found" );
        }
        return oPlaceBuildPartCellType;
    }

    public PlaceBuildPartCellType setPlaceBuildPartCellType(String sKey, String sName) {
        PlaceBuildPartCellType oPlaceBuildPartCellType = oPlaceBuildPartCellTypeDao.findBy("sKey", sKey).orNull();
        if (oPlaceBuildPartCellType == null) {
            oPlaceBuildPartCellType = new PlaceBuildPartCellType();
        }
        oPlaceBuildPartCellType.setSKey(sKey);
        oPlaceBuildPartCellType.setSName(sName);
        return oPlaceBuildPartCellTypeDao.saveOrUpdate(oPlaceBuildPartCellType);
    }

    public void removePlaceBuildPartCellType(Long nID) {
        PlaceBuildPartCellType placeBuildPartCellType = oPlaceBuildPartCellTypeDao.findById(nID).orNull();
        if (placeBuildPartCellType != null) {
            oPlaceBuildPartCellTypeDao.delete(placeBuildPartCellType);
        } else {
            throw new RuntimeException("PlaceBuildPartCellType with nID "+nID+" not found" );
        }
    }

    public PlaceBranch getPlaceBranch(Long nID) {
        PlaceBranch oPlaceBranch = oPlaceBranchDao.findById(nID).orNull();
        if (oPlaceBranch == null) {
            throw new RuntimeException("PlaceBranch with nID "+nID+" not found" );
        }
        return oPlaceBranch;
    }

    public PlaceBranch setPlaceBranch(String sKey, String sName, String sName_Old, Long nID_PlaceBranchType, Long nID_Place) {
        PlaceBranch oPlaceBranch = oPlaceBranchDao.findBy("sKey", sKey).orNull();
        if (oPlaceBranch == null) {
            oPlaceBranch = new PlaceBranch();
        }
        PlaceBranchType oPlaceBranchType = oPlaceBranchTypeDao.findByIdExpected(nID_PlaceBranchType);
        Place oPlace = placeDao.findByIdExpected(nID_Place);
        oPlaceBranch.setSKey(sKey);
        oPlaceBranch.setSName(sName);
        oPlaceBranch.setSName_Old(sName_Old);
        oPlaceBranch.setOPlaceBranchType(oPlaceBranchType);
        oPlaceBranch.setOPlace(oPlace);

        return oPlaceBranchDao.saveOrUpdate(oPlaceBranch);
    }

    public void removePlaceBranch(Long nID) {
        PlaceBranch placeBranch = oPlaceBranchDao.findById(nID).orNull();
        if (placeBranch != null) {
            oPlaceBranchDao.delete(placeBranch);
        } else {
            throw new RuntimeException("PlaceBranch with nID "+nID+" not found" );
        }

    }

    public PlaceBuild getPlaceBuild(Long nID) {
        PlaceBuild oPlaceBuild = oPlaceBuildDao.findByIdExpected(nID);
        if (oPlaceBuild == null) {
            throw new RuntimeException("PlaceBuild with nID "+nID+" not found" );
        }
        return oPlaceBuild;
    }

    public PlaceBuild setPlaceBuild(String sKey, Long nID_PlaceBuildType, Long nID_PlaceBranch, String sKey_MailIndex) {
        PlaceBuild oPlaceBuild = oPlaceBuildDao.findBy("sKey", sKey).orNull();
        if (oPlaceBuild == null) {
            oPlaceBuild = new PlaceBuild();
        }
        PlaceBuildType oPlaceBuildType = oPlaceBuildTypeDao.findByIdExpected(nID_PlaceBuildType);
        PlaceBranch oPlaceBranch = oPlaceBranchDao.findByIdExpected(nID_PlaceBranch);
        oPlaceBuild.setSKey(sKey);
        oPlaceBuild.setOPlaceBuildType(oPlaceBuildType);
        oPlaceBuild.setOPlaceBranch(oPlaceBranch);
        oPlaceBuild.setSKey_MailIndex(sKey_MailIndex);
        return oPlaceBuildDao.saveOrUpdate(oPlaceBuild);
    }

    public void removePlaceBuild(Long nID) {
        PlaceBuild placeBuild = oPlaceBuildDao.findById(nID).orNull();
        if (placeBuild != null) {
            oPlaceBuildDao.delete(placeBuild);
        } else {
            throw new RuntimeException("PlaceBuild with nID "+nID+" not found" );
        }

    }

    public PlaceBuildPart getPlaceBuildPart(Long nID) {
        PlaceBuildPart oPlaceBuildPart = oPlaceBuildPartDao.findByIdExpected(nID);
        if (oPlaceBuildPart == null) {
            throw new RuntimeException("PlaceBuildPart with nID "+nID+" not found" );
        }
        return oPlaceBuildPart;
    }

    public PlaceBuildPart setPlaceBuildPart(String sKey, Long nID_PlaceBuildPartType, Long nID_PlaceBuild) {
        PlaceBuildPart oPlaceBuildPart = oPlaceBuildPartDao.findBy("sKey", sKey).orNull();
        if (oPlaceBuildPart == null) {
            oPlaceBuildPart = new PlaceBuildPart();
        }
        PlaceBuildPartType oPlaceBuildPartType = oPlaceBuildPartTypeDao.findByIdExpected(nID_PlaceBuildPartType);
        PlaceBuild oPlaceBuild = oPlaceBuildDao.findByIdExpected(nID_PlaceBuild);

        oPlaceBuildPart.setSKey(sKey);
        oPlaceBuildPart.setOPlaceBuildPartType(oPlaceBuildPartType);
        oPlaceBuildPart.setOPlaceBuild(oPlaceBuild);

        return oPlaceBuildPartDao.saveOrUpdate(oPlaceBuildPart);
    }


    public void removePlaceBuildPart(Long nID) {
        PlaceBuildPart placeBuildPart = oPlaceBuildPartDao.findById(nID).orNull();
        if (placeBuildPart != null) {
            oPlaceBuildPartDao.delete(placeBuildPart);
        } else {
            throw new RuntimeException("PlaceBuildPart with nID "+nID+" not found" );
        }

    }


    public PlaceBuildPartCell getPlaceBuildPartCell(Long nID) {
        PlaceBuildPartCell oPlaceBuildPartCell = oPlaceBuildPartCellDao.findByIdExpected(nID);
        if (oPlaceBuildPartCell == null) {
            throw new RuntimeException("PlaceBuildPartCell with nID "+nID+" not found" );
        }
        return oPlaceBuildPartCell;
    }

    public PlaceBuildPartCell setPlaceBuildPartCell(String sKey, String sNote, Long nID_PlaceBuildPartCellType, Long nID_PlaceBuildPart) {
        PlaceBuildPartCell oPlaceBuildPartCell = oPlaceBuildPartCellDao.findBy("sKey", sKey).orNull();
        if (oPlaceBuildPartCell == null) {
            oPlaceBuildPartCell = new PlaceBuildPartCell();
        }
        PlaceBuildPartCellType oPlaceBuildPartCellType = oPlaceBuildPartCellTypeDao.findByIdExpected(nID_PlaceBuildPartCellType);
        PlaceBuildPart oPlaceBuildPart = oPlaceBuildPartDao.findByIdExpected(nID_PlaceBuildPart);
        oPlaceBuildPartCell.setSNote(sNote);
        oPlaceBuildPartCell.setSKey(sKey);
        oPlaceBuildPartCell.setOPlaceBuildPartCellType(oPlaceBuildPartCellType);
        oPlaceBuildPartCell.setOPlaceBuildPart(oPlaceBuildPart);
        return oPlaceBuildPartCellDao.saveOrUpdate(oPlaceBuildPartCell);
    }


    public void removePlaceBuildPartCell(Long nID) {
        PlaceBuildPartCell placeBuildPartCell = oPlaceBuildPartCellDao.findById(nID).orNull();
        if (placeBuildPartCell != null) {
            oPlaceBuildPartCellDao.delete(placeBuildPartCell);
        } else {
            throw new RuntimeException("PlaceBuildPartCell with nID "+nID+" not found" );
        }

    }

}
