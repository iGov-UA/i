package org.activiti.redis.service.impl;

import org.activiti.redis.client.RedisClient;
import org.activiti.redis.exception.RedisException;
import org.activiti.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Запись/вычитка redis.
 *
 * @author inna
 */
@Service("redisService")
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisClient jedisClient;

    @Override
    public String putAttachments(byte[] file) throws RedisException {
        String key;
        try {
            key = getJedisClient().putAttachments(file);

        } catch (Exception e) {
            throw new RedisException(RedisException.CODE_REDIS_EXCEPTION_ERROR, e.getMessage(), e);
        }
        return key;
    }

    @Override
    public byte[] getAttachments(String key) throws RedisException {
        byte[] byteFile;
        try {
            byteFile = getJedisClient().getAttachments(key);

        } catch (Exception e) {
            throw new RedisException(RedisException.CODE_REDIS_EXCEPTION_ERROR, e.getMessage());
        }
        return byteFile;
    }


    public RedisClient getJedisClient() {
        return jedisClient;
    }
}
