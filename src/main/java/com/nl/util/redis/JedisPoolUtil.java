/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月29日 下午4:52:00
 * @Description: 无
 */
package com.nl.util.redis;

import java.util.ArrayList;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.nl.util.config.RedisClusterCfg;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisPoolUtil{
	private static JedisPool pool = null;
	private static final JedisPoolUtil INSTANCE = new JedisPoolUtil();

	public static JedisPoolUtil getInstance() {
		return INSTANCE;
	}

	private RedisClusterCfg cfg;

	public RedisClusterCfg getCfg() {
		return cfg;
	}

	public void setCfg(RedisClusterCfg cfg) {
		this.cfg = cfg;
	}

	public JedisPool getPool() {
		if (pool == null) {
			final GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
			poolConfig.setMaxIdle(cfg.getPoolConfigMaxIdle());//最大能够保持idel状态的对象数
			poolConfig.setMinIdle(cfg.getPoolConfigMinIdle());
			poolConfig.setMaxTotal(cfg.getPoolConfigMaxTotal());//最大分配的对象数
			poolConfig.setMaxWaitMillis(cfg.getPoolConfigMaxWaitMillis());	
			poolConfig.setMinEvictableIdleTimeMillis(cfg.getPoolConfigMinEvictableIdleTimeMillis());//空闲连接多长时间后会被收回 
			poolConfig.setTimeBetweenEvictionRunsMillis(cfg.getPoolConfigTimeBetweenEvictionRunsMillis());//多长时间检查一次空闲的连接 
			poolConfig.setTestOnBorrow(true);
			pool = new JedisPool(poolConfig, cfg.getHostAndport().get(0).split(":")[0], Integer.parseInt(cfg.getHostAndport().get(0).split(":")[1]));
		}
		return pool;
	}  

	public static void main(String[] args) {
final RedisClusterCfg cfg=new RedisClusterCfg();
		
		ArrayList<String> places = new ArrayList<String>();
		places.add("10.1.2.149:7001");	
		cfg.setHostAndport(places);
		JedisPoolUtil.getInstance().setCfg(cfg);
		JedisPool pool =JedisPoolUtil.getInstance().getPool();
		Jedis jedis=pool.getResource();
		System.out.println(jedis.get("*"));
		System.out.println(jedis.keys("*"));
		pool.returnResource(jedis);
		
	}
}
