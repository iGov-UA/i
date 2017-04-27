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
    public ObjectCustoms setObjectCustoms(Map<String, String> args) {

        ObjectCustoms pcode;

        //если задан nID, то обновляем запись, если таковая найдена, иначе создаем новую запись
        if (args.containsKey("nID")) {
            pcode = findById(Long.valueOf(args.get("nID"))).orNull();
            if (args.containsKey("sID_UA")) {
                pcode.setsID_UA(args.get("sID_UA").trim());
            }
            if (args.containsKey("sName_UA")) {
                pcode.setsName_UA(args.get("sName_UA").trim());
            }
            if (args.containsKey("sMeasure_UA")) {
                pcode.setsMeasure_UA(args.get("sMeasure_UA"));
            }
        } else {
            pcode = new ObjectCustoms();
            pcode.setsID_UA(args.get("sID_UA").trim());
            pcode.setsName_UA(args.get("sName_UA").trim());
            pcode.setsMeasure_UA(args.get("sMeasure_UA"));
        }

        this.saveOrUpdate(pcode);
        LOG.info("ObjectCustoms {} is updated or set", pcode);

        if (args.containsKey("nID")) {
            pcode = this.findById(Long.valueOf(args.get("nID"))).orNull();
        } else {
            pcode = this.findBy("sID_UA", args.get("sID_UA")).orNull();
        }
        return pcode;
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
