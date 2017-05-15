package org.igov.model.object;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

@Repository
public class ObjectCustomsDaoImpl extends GenericEntityDao<Long, ObjectCustoms> implements ObjectCustomsDao {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectCustomsDaoImpl.class);

    public ObjectCustomsDaoImpl() {
        super(ObjectCustoms.class);
    }

    /*
       отдает список записей сущности при выполнении критериев
     */
    @Override
    public List<ObjectCustoms> getObjectCustoms(Map<String, String> args) {

        Criteria criteria = getSession().createCriteria(ObjectCustoms.class);

        for (String key : args.keySet()) {
            criteria.add(Restrictions.like(key, "%" + args.get(key) + "%"));
        }

        List<ObjectCustoms> pcode_list = criteria.list();

        return pcode_list;
    }

    /*
       обновляет или вставляет новую запись в сущность
     */
    @Override
    public ObjectCustoms setObjectCustoms(Map<String, String> mParam) {

        ObjectCustoms oObjectCustoms;

        //если задан nID, то обновляем запись, если таковая найдена, иначе создаем новую запись
        if (mParam.containsKey("nID")) {
            oObjectCustoms = findById(Long.valueOf(mParam.get("nID"))).orNull();
            if (mParam.containsKey("sID_UA")) {
                oObjectCustoms.setsID_UA(mParam.get("sID_UA").trim());
            }
            if (mParam.containsKey("sName_UA")) {
                oObjectCustoms.setsName_UA(mParam.get("sName_UA").trim());
            }
            if (mParam.containsKey("sMeasure_UA")) {
                oObjectCustoms.setsMeasure_UA(mParam.get("sMeasure_UA"));
            }
        } else {
            oObjectCustoms = new ObjectCustoms();
            oObjectCustoms.setsID_UA(mParam.get("sID_UA").trim());
            oObjectCustoms.setsName_UA(mParam.get("sName_UA").trim());
            oObjectCustoms.setsMeasure_UA(mParam.get("sMeasure_UA"));
        }

        oObjectCustoms = saveOrUpdate(oObjectCustoms);
        LOG.info("ObjectCustoms {} is updated or set", oObjectCustoms);

        /*if (mParam.containsKey("nID")) {
            oObjectCustoms = findById(Long.valueOf(mParam.get("nID"))).orNull();
        } else {
            oObjectCustoms = findBy("sID_UA", mParam.get("sID_UA")).orNull();
        }*/
        return oObjectCustoms;
    }

    /*
      удаляет запись сущности, если такая существует
     */
    @Override
    public void removeObjectCustoms(Map<String, String> args) throws Exception {
        if (args.containsKey("nID")) {

            if (this.exists(Long.valueOf(args.get("nID")))) {
                this.delete(Long.valueOf(args.get("nID")));
                LOG.info("ObjectCustoms with (nID={}) is deleted", args.get("nID"));
            } else {
                throw new Exception("ObjectCustoms with nID " + args.get("nID") + "hasn't been found, therefore it couldn't been removed");
            }

        } else {
            ObjectCustoms pcode = this.findBy("sID_UA", args.get("sID_UA")).orNull();
            if (pcode != null) {
                this.deleteBy("sID_UA", args.get("sID_UA"));
                LOG.info("ObjectCustoms with (sID_UA={}) is deleted", args.get("sID_UA"));
            } else {
                throw new Exception("ObjectCustoms with sID_UA " + args.get("sID_UA") + "hasn't been found, therefore it couldn't been removed");
            }
        }
    }

}
