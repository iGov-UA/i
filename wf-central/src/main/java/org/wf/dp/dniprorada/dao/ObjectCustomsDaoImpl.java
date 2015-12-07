
package org.wf.dp.dniprorada.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.ObjectCustoms;

@Repository
public class ObjectCustomsDaoImpl extends GenericEntityDao<ObjectCustoms> implements ObjectCustomsDao
{
    private static final Logger log = Logger.getLogger(ObjectCustomsDaoImpl.class);
   
    
    public ObjectCustomsDaoImpl()
    {
        super(ObjectCustoms.class);
    }
    /*
       отдает список записей сущности при выполнении критериев
    */
    @Override
    public List<ObjectCustoms> getObjectCustoms(Map<String, String> args) {
       
        Criteria criteria = getSession().createCriteria(ObjectCustoms.class);
         
            for(String key : args.keySet())
            {
              criteria.add(Restrictions.like(key, "%"+args.get(key)+"%"));
            }
       
        List<ObjectCustoms> pcode_list = criteria.list();
        
        return pcode_list;
    }
    /*
       обновляет или вставляет новую запись в сущность
    */
    @Override
    public ObjectCustoms setObjectCustoms(Map<String, String> args) 
    {
       
        ObjectCustoms pcode = null;
        
  //если задан nID, то обновляем запись, если таковая найдена, иначе создаем новую запись
        
        if(args.containsKey("nID"))
        {
            pcode = findById(Long.valueOf(args.get("nID"))).orNull();
            if(args.containsKey("sID_UA"))
                pcode.setsID_UA(args.get("sID_UA"));
            if(args.containsKey("sName_UA"))
                pcode.setsName_UA(args.get("sName_UA"));
            if(args.containsKey("sMeasure_UA"))
                pcode.setsMeasure_UA(args.get("sMeasure_UA"));
        }
        else
        {
           pcode = new ObjectCustoms();
           pcode.setsID_UA(args.get("sID_UA"));
           pcode.setsName_UA(args.get("sName_UA"));
           pcode.setsMeasure_UA(args.get("sMeasure_UA"));
        }
       
        this.saveOrUpdate(pcode);
        log.info("ObjectCustoms " + pcode + "is updated or set");
        
        if(args.containsKey("nID"))
           pcode = this.findById(Long.valueOf(args.get("nID"))).orNull();
        else
        {
           pcode = this.findBy("sID_UA", args.get("sID_UA")).orNull();
        }
        return pcode;
    }
    /*
      удаляет запись сущности, если такая существует
    */
    @Override
    public void removeObjectCustoms(Map<String, String> args) throws Exception 
    {
        if(args.containsKey("nID"))
        {
           
           if(this.exists(Long.valueOf(args.get("nID"))))
           {
            this.delete(Long.valueOf(args.get("nID")));
            log.info("ObjectCustoms with nID " + args.get("nID") + "is deleted");
           }
           else
           {
               throw new Exception("ObjectCustoms with nID " +args.get("nID")+ "hasn't been found, therefore it couldn't been removed");
           }
        
           
        }
        else
        {
           ObjectCustoms pcode = this.findBy("sID_UA", args.get("sID_UA")).orNull();
           if(pcode != null)
           {
            this.deleteBy("sID_UA", args.get("sID_UA"));
            log.info("ObjectCustoms with sID_UA " + args.get("sID_UA") + "is deleted");
           }
           else
           {
             throw new Exception("ObjectCustoms with sID_UA " +args.get("sID_UA")+ "hasn't been found, therefore it couldn't been removed");
           }
        }
    }
    
   
}
