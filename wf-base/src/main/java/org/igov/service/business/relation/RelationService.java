package org.igov.service.business.relation;

import java.util.List;
import java.util.ArrayList;
import org.igov.model.action.vo.Relation_VO;
import org.igov.model.relation.ObjectGroup;
import org.igov.model.relation.ObjectGroupDao;
import org.igov.model.relation.Relation;
import org.igov.model.relation.Relation_ObjectGroupDao;
import org.igov.model.relation.RelationClassDao;
import org.igov.model.relation.RelationDao;
import org.igov.model.relation.Relation_ObjectGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kovilin
 */
@Service
public class RelationService {
    
    @Autowired
    RelationDao oRelationDao;
    
    @Autowired
    RelationClassDao oRelationClassDao;
    
    @Autowired
    ObjectGroupDao oObjectGroupDao;
    
    @Autowired
    Relation_ObjectGroupDao oRelation_ObjectGroupDao;
    
    public List<Relation_VO> getRelations(String sID_Relation, Long nID_Parent){
        
       List<Relation_VO> aRelation_VO = new ArrayList<>();
               
        Relation oRelation = oRelationDao.findByExpected("sID", sID_Relation);
        Long nID_RelationClass = oRelation.getnID_RelationClass();
        
        if(oRelationClassDao.findByExpected("id", nID_RelationClass).getsClass().equals("ObjectGroup")){
            
            List<Relation_ObjectGroup> aRelation_ObjectGroup = new ArrayList<>();
            
            aRelation_ObjectGroup.addAll(oRelation_ObjectGroupDao.getRelation_ObjectGroups(oRelation.getId(), nID_Parent));
            
            for(Relation_ObjectGroup oRelation_ObjectGroup : aRelation_ObjectGroup){
                ObjectGroup oObjectGroup = oRelation_ObjectGroup.getoObjectGroup();
                        //oObjectGroupDao.findByExpected("id", oRelation_ObjectGroup.getnID_ObjectGroup_Child());
                
                Relation_VO oRelation_VO = new Relation_VO();
                oRelation_VO.setnID(oObjectGroup.getId());
                oRelation_VO.setsID_Private_Source(oObjectGroup.getsID_Private_Source());
                oRelation_VO.setsName(oObjectGroup.getsName());
                
                aRelation_VO.add(oRelation_VO);
            }
        }
        
        return aRelation_VO;
    } 
}
