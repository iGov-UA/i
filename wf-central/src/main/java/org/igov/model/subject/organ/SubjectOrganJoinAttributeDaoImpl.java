package org.igov.model.subject.organ;

import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

import java.util.List;

/**
 * Created by D.Zh. on 08.12.15.
 * dmitry.zhuravlyov@yahoo.com
 */
@Repository
public class SubjectOrganJoinAttributeDaoImpl extends GenericEntityDao<SubjectOrganJoinAttribute> implements SubjectOrganJoinAttributeDao{

    protected SubjectOrganJoinAttributeDaoImpl() {
        super(SubjectOrganJoinAttribute.class);
    }

    @Override
    public List<SubjectOrganJoinAttribute> getSubjectOrganJoinAttributes(SubjectOrganJoin subjectOrganJoin) {
       //return findAllBy("subjectOrganJoinId", subjectOrganJoin.getId());
        return getSubjectOrganJoinAttributesByParent(subjectOrganJoin.getId());
    }
    
    @Override
    public List<SubjectOrganJoinAttribute> getSubjectOrganJoinAttributesByParent(Long nID_SubjectOrganJoin) {
       return findAllBy("subjectOrganJoinId", nID_SubjectOrganJoin);
    }
    
}
