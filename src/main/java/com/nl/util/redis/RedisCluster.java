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
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.nl.util.GlobalConst;
import com.nl.util.config.RedisClusterCfg;

/**
 * http://redis.io/topics/cluster-tutorial The startup nodes don't need to be
 * all the nodes of the cluster. The important thing is that at least one node
 * is reachable. Also note that redis-rb-cluster updates this list of startup
 * nodes as soon as it is able to connect with the first node. You should expect
 * such a behavior with any other serious client 
 * 
 */
public class RedisCluster {

	private static JedisCluster redisCluster;
	private static final Logger LOG = LoggerFactory.getLogger(RedisCluster.class);
	private final static  RedisCluster INSTANCE= new RedisCluster();
	
	public static RedisCluster getRedisCluster() {
		return INSTANCE ;
	}
	private  RedisClusterCfg cfg;
	
	public RedisClusterCfg getCfg() {
		return cfg;
	}

	public void setCfg(RedisClusterCfg cfg) {
		this.cfg = cfg;
	}

	public RedisCluster() {
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
		jedisClusterNodes.add(new HostAndPort(clusterNode[0], 
				clusterNode[1].matches("^[0-9]*$") ? Integer.parseInt(clusterNode[1]): 6379));
		redisCluster = new JedisCluster(jedisClusterNodes, cfg.getTimeOut(),cfg.getMaxRedirections(),poolConfig);
		/*if (redisCluster !=null){
		int offset = 0;
		// 连接异常
		while (redisCluster.getClusterNodes().size() < hostAndport.size()
				&& offset < hostAndport.size()) {
			LOG.warn("集群节点数量异常:{},尝试重新建立连接{},第{}次", redisCluster.getClusterNodes().size(), hostAndport.get(offset),offset + 1);
			jedisClusterNodes.clear();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clusterNode = hostAndport.get(offset).split(":");
			jedisClusterNodes.add(new HostAndPort(clusterNode[0],
					clusterNode[1].matches("^[0-9]*$") ? Integer
							.parseInt(clusterNode[1]) : 6379));
			redisCluster = new JedisCluster(jedisClusterNodes, cfg.getTimeOut(),cfg.getMaxRedirections(),poolConfig);
			offset++;
		}
		if (redisCluster.getClusterNodes().size() == hostAndport.size()) {
			LOG.info("连接成功.集群节点数量:{}", redisCluster.getClusterNodes().size());
			for (final Entry<String, JedisPool> e : redisCluster.getClusterNodes()
					.entrySet()) {
				LOG.info("节点 " + e.getKey());
			}
		} else {
			LOG.error("连接失败.集群节点数量:{}", redisCluster.getClusterNodes().size());
		}
		}*/
		return redisCluster;
	}
}


/*public RedisCluster(RedisClusterCfg cfg) {
	this.cfg = cfg;
	LOG.info("instancing...");
	LOG.info(GlobalConst.MARK_STRING + "\n" + "PARAMETERS:\n"
			+ "hostAndport:" + this.cfg.getHostAndport() + "\n"
			+ "timeOut:" + this.cfg.getTimeOut() + "\n"
			+ "maxRedirections:" + this.cfg.getMaxRedirections() + "\n"
			+ "poolConfigMaxIdle:" + this.cfg.getPoolConfigMaxIdle() + "\n"
			+ "poolConfigMinIdle:" + this.cfg.getPoolConfigMinIdle() + "\n"
			+ "poolConfigMaxTotal:" + this.cfg.getPoolConfigMaxTotal()
			+ "\n" + "poolConfigMaxWaitMillis:"
			+ this.cfg.getPoolConfigMaxWaitMillis() + "\n"
			+ "poolConfigMinEvictableIdleTimeMillis:"
			+ this.cfg.getPoolConfigMinEvictableIdleTimeMillis() + "\n"
			+ "poolConfigTimeBetweenEvictionRunsMillis:"
			+ this.cfg.getPoolConfigTimeBetweenEvictionRunsMillis() + "\n"
			+ GlobalConst.MARK_STRING + "\n");
}*/
