package org.wf.dp.dniprorada.dao;

import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.SubjectOrganJoin;
import org.wf.dp.dniprorada.model.SubjectOrganJoinAttribute;

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
       return findAllBy("subjectOrganJoinId", subjectOrganJoin.getId());
    }
}
