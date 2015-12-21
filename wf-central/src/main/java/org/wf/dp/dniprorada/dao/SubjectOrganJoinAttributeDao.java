package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.SubjectOrganJoin;
import org.wf.dp.dniprorada.model.SubjectOrganJoinAttribute;

import java.util.List;

/**
 * Created by D.Zh. on 07.12.15.
 * dmitry.zhuravlyov@yahoo.com
 */
public interface SubjectOrganJoinAttributeDao extends EntityDao<SubjectOrganJoinAttribute> {

    List<SubjectOrganJoinAttribute> getSubjectOrganJoinAttributes(SubjectOrganJoin subjectOrganJoin);

}
