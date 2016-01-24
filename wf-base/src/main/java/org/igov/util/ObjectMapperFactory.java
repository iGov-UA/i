package org.igov.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.FactoryBean;

/**
 * User: goodg_000
 * Date: 17.01.2016
 * Time: 14:17
 */
public class ObjectMapperFactory implements FactoryBean<ObjectMapper> {

    @Override
    public ObjectMapper getObject() throws Exception {
        // To avoid instantiating and configuring the mapper everywhere
        ObjectMapper mapper = new ObjectMapper();

        /*15.03.2015 maxtmn add some features*/
        mapper.disable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        return mapper;
    }

    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
