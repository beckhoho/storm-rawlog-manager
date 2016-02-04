/**
 * @see apache storm storm-redis
 */
package com.nl.util.redis.mapper;

/**
 * RedisMapper is for defining data type for querying / storing from / to Redis.
 */
public interface RedisMapper {
    /**
     * Returns descriptor which defines data type.
     * @return data type descriptor
     */
    RedisDataDescription getDataDescription();
}
