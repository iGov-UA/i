package org.igov.model.subject.organ;

import org.igov.model.core.EntityDao;

import java.util.List;

/**
 * Created by D.Zh. on 07.12.15.
 * dmitry.zhuravlyov@yahoo.com
 */
public interface SubjectOrganJoinAttributeDao extends EntityDao<SubjectOrganJoinAttribute> {

    List<SubjectOrganJoinAttribute> getSubjectOrganJoinAttributes(SubjectOrganJoin subjectOrganJoin);

    List<SubjectOrganJoinAttribute> getSubjectOrganJoinAttributesByParent(Long nID_SubjectOrganJoin);
    
}
