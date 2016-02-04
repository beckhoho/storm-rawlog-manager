/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年7月30日 上午10:53:25
 * @Description: 无
 */
package com.nl.util.redis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.nl.util.config.RedisClusterCfg;

public class RedisClusterUtil {
	//private static final Logger LOG = LoggerFactory.getLogger(RedisClusterUtil.class);
	private final static  RedisClusterUtil INSTANCE= new RedisClusterUtil();
	private static JedisCluster redisCluster;
	public static RedisClusterUtil getRedisCluster() {
		return INSTANCE ;
	}
	private  RedisClusterCfg cfg;
	
	public RedisClusterCfg getCfg() {
		return cfg;
	}

	public void setCfg(RedisClusterCfg cfg) {
		this.cfg = cfg;
	}

	public RedisClusterUtil() {
	}

	public  JedisCluster getInstance() {
		final GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();	 
		poolConfig.setMaxIdle(cfg.getPoolConfigMaxIdle());//最大能够保持idel状态的对象数
		poolConfig.setMinIdle(cfg.getPoolConfigMinIdle());
		poolConfig.setMaxTotal(cfg.getPoolConfigMaxTotal());//最大分配的对象数
		poolConfig.setMaxWaitMillis(cfg.getPoolConfigMaxWaitMillis());	
		poolConfig.setMinEvictableIdleTimeMillis(cfg.getPoolConfigMinEvictableIdleTimeMillis());//空闲连接多长时间后会被收回 
		poolConfig.setTimeBetweenEvictionRunsMillis(cfg.getPoolConfigTimeBetweenEvictionRunsMillis());//多长时间检查一次空闲的连接 
		// 只给集群里一个实例就可以
		final Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		final List<String> hostAndport=cfg.getHostAndport();
		String[] clusterNode;
		clusterNode = hostAndport.get(0).split(":");
		jedisClusterNodes.add(new HostAndPort(clusterNode[0],clusterNode[1].matches("^[0-9]*$") ? Integer.parseInt(clusterNode[1]): 6379));
		redisCluster = new JedisCluster(jedisClusterNodes, cfg.getTimeOut(),cfg.getMaxRedirections(),poolConfig);	
		return redisCluster;
	}
}
